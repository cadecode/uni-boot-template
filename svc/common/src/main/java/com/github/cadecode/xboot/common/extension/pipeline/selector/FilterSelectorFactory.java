package com.github.cadecode.xboot.common.extension.pipeline.selector;

import com.github.cadecode.xboot.common.exception.ExtensionException;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * FilterSelector 工厂
 * <p>
 * 根据业务提供的 filter-selectors Map 创建对应的 FilterSelector：
 * - YAML 有 filter 列表 → LocalListFilterSelector（精确启用）
 * - YAML 值为 [on] → MatchAllFilterSelector（全部启用）
 * - YAML 值为 [off] → DummyFilterSelector（全部禁用）
 * - YAML 无此 type 配置 → 抛出 ExtensionException
 *
 * @author Cade Li
 * @since 2026/6/28
 */
public final class FilterSelectorFactory {

    private static final List<String> ON = List.of("on");
    private static final List<String> OFF = List.of("off");

    private FilterSelectorFactory() {
    }

    /**
     * 根据 typeName 和业务配置创建 FilterSelector
     *
     * @param typeName        ExtensionType.getType() 值
     * @param filterSelectors 业务 YAML 配置的 filter-selectors Map
     * @throws ExtensionException 如果 type 未在配置中找到
     */
    public static FilterSelector createFilterSelector(String typeName, Map<String, List<String>> filterSelectors) {
        if (filterSelectors == null) {
            throw new ExtensionException("Pipeline filter-selectors not configured");
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
        return new LocalListFilterSelector(enabledNames);
    }
}
