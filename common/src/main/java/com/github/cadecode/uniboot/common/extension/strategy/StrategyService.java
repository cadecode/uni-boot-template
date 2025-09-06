package com.github.cadecode.uniboot.common.extension.strategy;

import org.springframework.plugin.core.Plugin;

/**
 * 策略模式统一服务接口
 * <p>可使用执行器调用的通用策略服务接口
 *
 * @author Cade Li
 * @since 2023/6/23
 */
public interface StrategyService extends Plugin<StrategyContext> {

}
