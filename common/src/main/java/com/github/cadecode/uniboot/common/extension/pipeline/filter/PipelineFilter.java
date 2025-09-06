package com.github.cadecode.uniboot.common.extension.pipeline.filter;

import com.github.cadecode.uniboot.common.extension.pipeline.PipelineContext;
import com.github.cadecode.uniboot.common.extension.pipeline.PipelineFilterChain;

/**
 * 过滤器接口
 *
 * @author Cade Li
 * @date 2023/6/20
 */
public interface PipelineFilter<T extends PipelineContext> {

    void doFilter(T context, PipelineFilterChain<T> filterChain);
}
