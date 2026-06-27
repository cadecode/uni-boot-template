package com.github.cadecode.uniboot.common.extension.pipeline;

import com.github.cadecode.uniboot.common.extension.pipeline.filter.PipelineFilter;
import lombok.Getter;

import java.util.Objects;

/**
 * 管道构建器
 *
 * @author Cade Li
 * @date 2023/6/20
 */
public class PipelineGenerator<A extends PipelineContext> {

    @Getter
    private DefaultPipelineFilterChain<A> firstChain;
    private DefaultPipelineFilterChain<A> lastChain;

    public void appendFilter(PipelineFilter<A> filter) {
        DefaultPipelineFilterChain<A> newChain = new DefaultPipelineFilterChain<>(null, filter);
        if (Objects.isNull(firstChain)) {
            firstChain = newChain;
            lastChain = firstChain;
            return;
        }
        lastChain.setNext(newChain);
        lastChain = newChain;
    }

}
