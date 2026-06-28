package com.github.cadecode.xboot.admin.pipeline;

import com.github.cadecode.xboot.common.extension.pipeline.filter.AbstractPipelineFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class PipelineTestFilterB extends AbstractPipelineFilter<PipelineTestContext> {

    @Override
    public void handle(PipelineTestContext context) {
        log.info("filterB executed");
    }
}
