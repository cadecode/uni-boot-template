package com.github.cadecode.xboot.common.extension.pipeline.filter;

import com.github.cadecode.xboot.common.extension.pipeline.PipelineContext;
import com.github.cadecode.xboot.common.extension.pipeline.PipelineFilterChain;
import lombok.extern.slf4j.Slf4j;

/**
 * 过滤器抽象类
 *
 * @author Cade Li
 * @date 2023/6/20
 */
@Slf4j
public abstract class AbstractPipelineFilter<T extends PipelineContext> implements PipelineFilter<T> {

    /**
     * 过滤方法模板
     */
    @Override
    public void doFilter(T context, PipelineFilterChain<T> filterChain) {
        if (context.getFilterSelector().matchFilter(this.getClass().getSimpleName())) {
            handle(context);
        }
        if (context.continueChain()) {
            filterChain.next(context);
        } else {
            log.info("Pipeline chain interrupted by [{}] for type '{}'",
                    this.getClass().getSimpleName(), context.getPipelineType().getType());
        }
    }

    /**
     * 过滤处理主逻辑
     */
    public abstract void handle(T context);
}
