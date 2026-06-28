package com.github.cadecode.xboot.common.extension.pipeline.selector;

import java.util.Collections;
import java.util.List;

/**
 * 空过滤器选择器
 * <p>
 * 不匹配任何 filter，用于禁用所有 filter 的场景
 *
 * @author Cade Li
 * @since 2026/6/28
 */
public class DummyFilterSelector implements FilterSelector {

    @Override
    public boolean matchFilter(String currFilterName) {
        return false;
    }

    @Override
    public List<String> getFilterNames() {
        return Collections.emptyList();
    }
}
