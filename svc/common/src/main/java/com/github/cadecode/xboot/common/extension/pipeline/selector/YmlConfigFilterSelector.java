package com.github.cadecode.xboot.common.extension.pipeline.selector;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 基于 YAML 配置的过滤器选择器
 * <p>
 * 从 YAML 读取每个 type 下启用的 filter 名列表
 *
 * @author Cade Li
 * @since 2026/6/28
 */
@NoArgsConstructor
@AllArgsConstructor
public class YmlConfigFilterSelector implements FilterSelector {

    private List<String> filterNames = new ArrayList<>();

    @Override
    public boolean matchFilter(String currFilterName) {
        return filterNames.stream().anyMatch(o -> Objects.equals(currFilterName, o));
    }

    @Override
    public List<String> getFilterNames() {
        return filterNames;
    }
}
