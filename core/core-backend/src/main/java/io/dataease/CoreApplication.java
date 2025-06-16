package io.dataease;

import cn.dev33.satoken.spring.SaTokenContextRegister;
import io.dataease.auth.filter.FilterConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {QuartzAutoConfiguration.class})
@EnableCaching
@EnableScheduling
@Import(SaTokenContextRegister.class)
public class CoreApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }
}
