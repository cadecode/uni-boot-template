package com.github.cadecode.uniboot.common.util;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 树形化工具类
 *
 * @author Cade Li
 * @since 2023/11/26
 */
public class TreeUtil {

    /**
     * List 树形化
     *
     * @param list           原数据列表
     * @param rootId         root id
     * @param idGetter       获取 id 的方法
     * @param parentIdGetter 获取 parentId 的方法
     * @param childrenSetter 设置 children 的方法
     * @param <T>            原数据类型
     * @param <L>            数据 ID 类型
     * @return 树形化之后的列表
     */
    public static <T, L> List<T> listToTree(List<T> list,
                                            L rootId,
                                            Function<T, L> idGetter,
                                            Function<T, L> parentIdGetter,
                                            BiConsumer<T, List<T>> childrenSetter) {
        List<T> parentList = list.stream()
                .filter(o -> Objects.equals(rootId, parentIdGetter.apply(o)))
                .collect(Collectors.toList());
        parentList.forEach(p -> {
            List<T> children = list.stream()
                    .filter(c -> Objects.equals(parentIdGetter.apply(c), idGetter.apply(p)))
                    .peek(c -> childrenSetter.accept(c, listToTree(list, idGetter.apply(c), idGetter, parentIdGetter, childrenSetter)))
                    .collect(Collectors.toList());
            childrenSetter.accept(p, children);
        });
        return parentList;
    }
}
