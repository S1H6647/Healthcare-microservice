package com.healthcare.receptionist.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@Order(1)
public class GatewayAuthFilter implements Filter {

    private static final List<String> REQUIRED_HEADERS = List.of(
            "X-User-Id",
            "X-User-Role",
            "X-User-Email"
    );

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // check all required gateway headers are present
        for (String header : REQUIRED_HEADERS) {
            if (httpRequest.getHeader(header) == null) {
                httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                httpResponse.setContentType("application/json");
                httpResponse.getWriter().write(
                    "{\"message\":\"Direct access not allowed\",\"status\":401}"
                );
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
