package ru.netology.cloudservice.logging;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.DispatcherType;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static java.lang.String.format;

@Component
@RequiredArgsConstructor
public class LoggingInterceptorRest implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        if (DispatcherType.REQUEST.name().equals(request.getDispatcherType().name())) {
            Logback.log(format("Input REST request - %s %s", request.getMethod(), request.getRequestURL()));
        }
        return true;
    }
}
