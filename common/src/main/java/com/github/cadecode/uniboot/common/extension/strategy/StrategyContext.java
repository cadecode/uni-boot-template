package com.github.cadecode.uniboot.common.extension.strategy;

import com.github.cadecode.uniboot.common.enums.ExtensionType;

/**
 * 策略上下文
 *
 * @author Cade Li
 * @since 2023/6/25
 */
public interface StrategyContext {

    ExtensionType getStrategyType();

}
