package com.github.cadecode.uniboot.framework.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


/**
 * 线程池设置
 *
 * @author Cade Li
 * @date 2023/3/15
 */
@Slf4j
@RequiredArgsConstructor
@EnableAsync
@EnableScheduling
@Configuration
public class ThreadPoolConfig {

    /**
     * Spring 定时任务线程池 @Scheduled
     */
    @Bean(name = "taskScheduler")
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(Runtime.getRuntime().availableProcessors());
        scheduler.setThreadNamePrefix("taskScheduler-");
        scheduler.setWaitForTasksToCompleteOnShutdown(true);
        scheduler.setAwaitTerminationSeconds(30);
        scheduler.setErrorHandler(throwable -> {
            log.error("Scheduled task execute fail,", throwable);
        });
        return scheduler;
    }

    /**
     * 定时任务配置器
     */
    @Bean
    public SchedulingConfigurer schedulingConfigurer(ThreadPoolTaskScheduler taskScheduler) {
        return taskRegistrar -> taskRegistrar.setTaskScheduler(taskScheduler);
    }

    /**
     * Spring 异步任务线程池 @Async
     */
    @Bean(name = "asyncExecutor")
    public ThreadPoolTaskExecutor asyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(16);
        executor.setMaxPoolSize(32);
        executor.setQueueCapacity(10000);
        executor.setKeepAliveSeconds(60);
        executor.setThreadNamePrefix("async-task-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setAwaitTerminationSeconds(30);
        // 允许核心线程超时销毁
        executor.setAllowCoreThreadTimeOut(true);
        executor.initialize();
        return executor;
    }

    /**
     * 异步任务配置器
     */
    @Bean
    public AsyncConfigurer asyncConfigurer(ThreadPoolTaskExecutor asyncExecutor) {
        return new AsyncConfigurer() {
            @Override
            public Executor getAsyncExecutor() {
                return asyncExecutor;
            }

            @Override
            public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
                return (throwable, method, objects) -> {
                    log.error("Async task execute fail, method {}, params {}", method, objects, throwable);
                };
            }
        };
    }
}
