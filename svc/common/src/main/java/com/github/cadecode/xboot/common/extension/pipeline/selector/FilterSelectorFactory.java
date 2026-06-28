package com.github.cadecode.xboot.common.extension.pipeline.selector;

import com.github.cadecode.xboot.common.enums.ExtensionType;
import com.github.cadecode.xboot.common.exception.ExtensionException;
import com.github.cadecode.xboot.common.extension.pipeline.config.PipelineProperties;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * FilterSelector 工厂
 * <p>
 * 根据 ExtensionType 创建对应的 FilterSelector：
 * - YAML 有 filter 列表 → YmlConfigFilterSelector（精确启用）
 * - YAML 值为 [on] → MatchAllFilterSelector（全部启用）
 * - YAML 值为 [off] → DummyFilterSelector（全部禁用）
 * - YAML 无此 type 配置 → 抛出 ExtensionException
 *
 * @author Cade Li
 * @since 2026/6/28
 */
public class FilterSelectorFactory {

    private static final List<String> ON = List.of("on");
    private static final List<String> OFF = List.of("off");

    private final PipelineProperties properties;

    public FilterSelectorFactory(PipelineProperties properties) {
        this.properties = properties;
    }

    /**
     * 根据 type 创建 FilterSelector
     *
     * @throws ExtensionException 如果 type 未在 YAML 中配置
     */
    public FilterSelector createFilterSelector(ExtensionType type) {
        String typeName = type.getType();
        Map<String, List<String>> filterSelectors = properties.getFilterSelectors();
        if (filterSelectors == null) {
            throw new ExtensionException("Pipeline filter-selectors not configured at all");
        }
        List<String> enabledNames = filterSelectors.get(typeName);
        if (CollectionUtils.isEmpty(enabledNames)) {
            throw new ExtensionException("No pipeline selector config for type '{}'", typeName);
        }
        if (enabledNames.equals(ON)) {
            return new MatchAllFilterSelector();
        }
        if (enabledNames.equals(OFF)) {
            return new DummyFilterSelector();
        }
        return new YmlConfigFilterSelector(enabledNames);
    }
}
