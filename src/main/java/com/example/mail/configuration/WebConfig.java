package com.example.mail.configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

/**
 * 描述：
 *
 * @author LT
 * @date 2018/2/23
 */
@Configuration
public class WebConfig {

    public static boolean sendFlag = false;

    public static boolean stopFlag = false;

    @Bean
    public ExecutorService executorService(){
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("thread-poll-%d").build();
        return new ThreadPoolExecutor(5, 5,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),threadFactory);
    }
}
