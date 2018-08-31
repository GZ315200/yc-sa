package com.unistack.tamboo.mgt.handler;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Gyges Zean
 * @date 2018/6/4
 */
@Component
public class EntryPointUnauthorizedHandler implements AuthenticationEntryPoint {


    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        response.getWriter().println("{\"status\":401,\"msg\":\"Unauthorized\",\"data\":\"\"}");
        response.getWriter().flush();

    }
}
