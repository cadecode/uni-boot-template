package com.github.cadecode.xboot.common.extension.pipeline;

import com.github.cadecode.xboot.common.exception.ExtensionException;
import com.github.cadecode.xboot.common.extension.pipeline.filter.PipelineFilter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Pipeline 执行器
 * <p>
 * 既是链构建器（appendFilter），也是执行入口（execute）
 *
 * @author Cade Li
 * @date 2023/6/20
 */
@Slf4j
public class PipelineExecutor<A extends PipelineContext> {

    @Getter
    private DefaultPipelineFilterChain<A> firstChain;
    private DefaultPipelineFilterChain<A> lastChain;

    /**
     * 追加 filter 到链尾
     *
     * @param filter filter 实例
     * @param desc   描述信息
     */
    public void appendFilter(PipelineFilter<A> filter, String desc) {
        DefaultPipelineFilterChain<A> newChain = new DefaultPipelineFilterChain<>(null, filter);
        if (Objects.isNull(firstChain)) {
            firstChain = newChain;
            lastChain = firstChain;
            log.info("Pipeline init first filter: [{}] desc: {}", filter.getClass().getSimpleName(), desc);
            return;
        }
        log.info("Pipeline append filter: [{}] desc: {}", filter.getClass().getSimpleName(), desc);
        lastChain.setNext(newChain);
        lastChain = newChain;
    }

    /**
     * 启动责任链执行
     *
     * @param context pipeline 上下文
     */
    public void execute(A context) {
        if (firstChain == null) {
            throw new ExtensionException("Pipeline chain is empty, cannot execute for type '{}'",
                    context != null ? context.getPipelineType().getType() : "null");
        }
        log.info("Pipeline executing for type '{}'", context.getPipelineType().getType());
        firstChain.filter(context);
    }
}
