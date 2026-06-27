package com.github.cadecode.uniboot.common.extension.plugin;

import java.util.List;
import java.util.Optional;

/**
 * 插件执行器抽象类
 *
 * @author Cade Li
 * @since 2023/6/24
 */
public abstract class AbstractPluginExecutor implements PluginExecutor {

    public abstract <S> Optional<S> selectService(Class<S> clazz, PluginContext context);

    public abstract <S> List<S> selectServices(Class<S> clazz, PluginContext context);
}
