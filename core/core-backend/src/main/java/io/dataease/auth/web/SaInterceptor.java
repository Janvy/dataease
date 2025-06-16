package io.dataease.auth.web;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.exception.BackResultException;
import cn.dev33.satoken.exception.StopMatchException;
import cn.dev33.satoken.strategy.SaStrategy;
import io.dataease.auth.xtoken.AuthException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.lang.reflect.Method;

/**
 * Sa-Token 综合拦截器，提供注解鉴权和路由拦截鉴权能力<br/>
 * <p>
 * 拷贝自sa-token的cn.dev33.satoken.interceptor.SaInterceptor,修改给前端返回结果的输出方式
 *
 * @author hadix
 */
public class SaInterceptor implements HandlerInterceptor {

    /**
     * 是否打开注解鉴权
     */
    @Getter
    @Setter
    public boolean isAnnotation = true;

    /**
     * 每次请求之前触发的方法
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        try {

            if (isAnnotation && handler instanceof HandlerMethod) {

                // 获取此请求对应的 Method 处理函数
                Method method = ((HandlerMethod) handler).getMethod();

                // 如果此 Method 或其所属 Class 标注了 @SaIgnore，则忽略掉鉴权
                if (SaStrategy.me.isAnnotationPresent.apply(method, SaIgnore.class)) {
                    return true;
                }

                // 注解校验
                SaStrategy.me.checkMethodAnnotation.accept(method);
            }

        } catch (StopMatchException e) {
            // 停止匹配，进入Controller
        } catch (BackResultException e) {
            // 停止匹配，向前端输出结果
            throw new AuthException(e.getMessage(), e);
        }

        // 通过验证
        return true;
    }

}
