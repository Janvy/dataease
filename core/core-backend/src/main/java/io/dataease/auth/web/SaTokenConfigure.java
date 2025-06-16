package io.dataease.auth.web;

import io.dataease.auth.xtoken.ModuleAuthProperties;
import io.dataease.auth.xtoken.UserSessionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author 31788
 * @date 2025/6/13 17:44
 * @description: SaToken拦截
 */
@Configuration
public class SaTokenConfigure  {

    @Bean
    public AuthInterceptor moduleAuthInterceptor(
            UserSessionRepository userSessionRepository, ModuleAuthProperties config) {
        return new AuthInterceptor(userSessionRepository, config);
    }

    @Bean
    public WebMvcConfigurer addInterceptors(AuthInterceptor authInterceptor) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                registry.addInterceptor(authInterceptor)
                        .addPathPatterns("/**");
            }
        };
    }
}