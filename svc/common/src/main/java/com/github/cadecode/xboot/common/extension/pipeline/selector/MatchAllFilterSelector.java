package com.github.cadecode.xboot.common.extension.pipeline.selector;

import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 匹配任何 filter 的选择器
 * <p>
 * 用于 YAML 未配置的 type 降级场景，或开发者明确希望所有 filter 都执行的场景
 *
 * @author Cade Li
 * @since 2025/12/23
 */
@NoArgsConstructor
public class MatchAllFilterSelector implements FilterSelector {

    @Override
    public boolean matchFilter(String currFilterName) {
        return true;
    }

    @Override
    public List<String> getFilterNames() {
        return Collections.emptyList();
    }
}
