package com.github.cadecode.uniboot.common.extension.plugin;

import org.springframework.plugin.core.Plugin;

/**
 * 插件模式统一服务接口
 * <p>可使用执行器调用的通用插件服务接口
 *
 * @author Cade Li
 * @since 2023/6/23
 */
public interface PluginService extends Plugin<PluginContext> {

}
