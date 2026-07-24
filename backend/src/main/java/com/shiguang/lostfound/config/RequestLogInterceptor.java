package com.shiguang.lostfound.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class RequestLogInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(RequestLogInterceptor.class);
    private static final String START_TIME = RequestLogInterceptor.class.getName() + ".startTime";
    private static final String REQUEST_ID = RequestLogInterceptor.class.getName() + ".requestId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String requestId = UUID.randomUUID().toString().substring(0, 8);
        request.setAttribute(START_TIME, System.nanoTime());
        request.setAttribute(REQUEST_ID, requestId);
        response.setHeader("X-Request-Id", requestId);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
        Object started = request.getAttribute(START_TIME);
        long elapsedMs = started instanceof Long start ? (System.nanoTime() - start) / 1_000_000 : -1;
        String requestId = String.valueOf(request.getAttribute(REQUEST_ID));
        String user = currentUser();
        String message = "请求日志 requestId={} method={} path={} user={} ip={} status={} durationMs={}";

        if (exception != null || response.getStatus() >= 500) {
            log.error(message, requestId, request.getMethod(), request.getRequestURI(), user,
                    clientIp(request), response.getStatus(), elapsedMs, exception);
        } else if (response.getStatus() >= 400) {
            log.warn(message, requestId, request.getMethod(), request.getRequestURI(), user,
                    clientIp(request), response.getStatus(), elapsedMs);
        } else {
            log.info(message, requestId, request.getMethod(), request.getRequestURI(), user,
                    clientIp(request), response.getStatus(), elapsedMs);
        }
    }

    private String currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            return "anonymous";
        }
        return authentication.getName();
    }

    private String clientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        if (forwarded != null && !forwarded.isBlank()) return forwarded.split(",", 2)[0].trim();
        String realIp = request.getHeader("X-Real-IP");
        return realIp == null || realIp.isBlank() ? request.getRemoteAddr() : realIp.trim();
    }
}
