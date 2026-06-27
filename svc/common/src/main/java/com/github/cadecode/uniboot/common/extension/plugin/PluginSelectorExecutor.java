package com.github.cadecode.uniboot.common.extension.plugin;

import cn.hutool.core.util.ObjUtil;
import com.github.cadecode.uniboot.common.exception.ExtensionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.plugin.core.PluginRegistry;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 插件执行器
 *
 * @author Cade Li
 * @since 2023/6/23
 */
@Slf4j
@RequiredArgsConstructor
public class PluginSelectorExecutor extends AbstractPluginExecutor {

    private final PluginRegistry<PluginService, PluginContext> pluginRegistry;


    /**
     * 执行匹配的第一个插件，不需要返回值
     *
     * @param clazz    PluginService 实现类
     * @param context  扩展上下文
     * @param consumer consumer
     */
    @Override
    public <S extends PluginService> void execute(Class<S> clazz, PluginContext context, Consumer<S> consumer) {
        Optional<S> serviceOpt = selectService(clazz, context);
        if (serviceOpt.isPresent()) {
            consumer.accept(serviceOpt.get());
            return;
        }
        throw new ExtensionException("Strategy service not found, {}, {}", clazz, context.getPluginType());
    }

    /**
     * 执行匹配的所有插件，不需要返回值
     *
     * @param clazz    PluginService 实现类
     * @param context  扩展上下文
     * @param consumer consumer
     */
    @Override
    public <S extends PluginService> void executeAll(Class<S> clazz, PluginContext context, Consumer<S> consumer) {
        List<S> services = selectServices(clazz, context);
        if (ObjUtil.isNotEmpty(services)) {
            services.forEach(consumer);
            return;
        }
        throw new ExtensionException("Strategy service not found, {}, {}", clazz, context.getPluginType());
    }

    /**
     * 执行匹配的第一个插件，需要返回值
     *
     * @param clazz    PluginService 实现类
     * @param context  扩展上下文
     * @param function function
     * @return 返回值
     */
    @Override
    public <R, S extends PluginService> R submit(Class<S> clazz, PluginContext context, Function<S, R> function) {
        Optional<S> serviceOpt = selectService(clazz, context);
        if (serviceOpt.isPresent()) {
            return function.apply(serviceOpt.get());
        }
        throw new ExtensionException("Strategy service not found, {}, {}", clazz, context.getPluginType());
    }

    /**
     * 执行匹配的所有插件，需要返回值
     *
     * @param clazz    PluginService 实现类
     * @param context  扩展上下文
     * @param function function
     * @return 返回值
     */
    @Override
    public <R, S extends PluginService> List<R> submitAll(Class<S> clazz, PluginContext context, Function<S, R> function) {
        List<S> services = selectServices(clazz, context);
        if (ObjUtil.isNotEmpty(services)) {
            return services.stream()
                    .map(function)
                    .collect(Collectors.toList());
        }
        throw new ExtensionException("Strategy service not found, {}, {}", clazz, context.getPluginType());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> Optional<S> selectService(Class<S> clazz, PluginContext context) {
        return (Optional<S>) pluginRegistry.getPlugins()
                .stream()
                .filter(o -> clazz.isAssignableFrom(o.getClass()) && o.supports(context))
                .findFirst();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <S> List<S> selectServices(Class<S> clazz, PluginContext context) {
        return (List<S>) pluginRegistry.getPlugins()
                .stream()
                .filter(o -> clazz.isAssignableFrom(o.getClass()) && o.supports(context))
                .collect(Collectors.toList());
    }
}
