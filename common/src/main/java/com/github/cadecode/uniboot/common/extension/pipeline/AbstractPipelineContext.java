package com.github.cadecode.uniboot.common.extension.pipeline;

import com.github.cadecode.uniboot.common.enums.ExtensionType;
import com.github.cadecode.uniboot.common.extension.pipeline.selector.FilterSelector;
import lombok.Getter;

/**
 * pipeline 上下文抽象类
 *
 * @author Cade Li
 * @since 2023/6/25
 */
@Getter
public abstract class AbstractPipelineContext implements PipelineContext {

    private final ExtensionType pipelineType;
    private final FilterSelector filterSelector;

    public AbstractPipelineContext(ExtensionType pipelineType, FilterSelector filterSelector) {
        this.pipelineType = pipelineType;
        this.filterSelector = filterSelector;
    }

}
