package com.github.cadecode.uniboot.common.extension.plugin;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 插件执行器接口
 *
 * @author Cade Li
 * @since 2023/6/24
 */
public interface PluginExecutor {

    <S extends PluginService> void execute(Class<S> clazz, PluginContext context, Consumer<S> consumer);

    <S extends PluginService> void executeAll(Class<S> clazz, PluginContext context, Consumer<S> consumer);

    <R, S extends PluginService> R submit(Class<S> clazz, PluginContext context, Function<S, R> function);

    <R, S extends PluginService> List<R> submitAll(Class<S> clazz, PluginContext context, Function<S, R> function);

}
