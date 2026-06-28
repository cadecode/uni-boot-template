package com.github.cadecode.xboot.admin.pipeline;

import com.github.cadecode.xboot.common.extension.pipeline.AbstractPipelineContext;
import com.github.cadecode.xboot.common.extension.pipeline.selector.FilterSelector;
import lombok.Getter;

@Getter
public class PipelineTestContext extends AbstractPipelineContext {

    public PipelineTestContext(PipelineTestType pipelineType, FilterSelector filterSelector) {
        super(pipelineType, filterSelector);
    }

    @Override
    public boolean continueChain() {
        return true;
    }
}
