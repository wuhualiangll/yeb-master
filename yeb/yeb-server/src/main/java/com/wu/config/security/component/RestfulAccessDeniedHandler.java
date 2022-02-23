package com.wu.config.security.component;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.wu.pojo.RespBean;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 当访问接口没有权限时，自定义返回结果
 * @author cxj
 */
@Component
public class RestfulAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest Request, HttpServletResponse Response, AccessDeniedException e) throws IOException, ServletException {
        Response.setCharacterEncoding("UTF-8");
        Response.setContentType("application/json");
        PrintWriter out= Response.getWriter();
        RespBean error = RespBean.error("权限不足，请联系管理员");
        error.setCode(403);
        out.write(new ObjectMapper().writeValueAsString(error));
        out.flush();
        out.close();
    }
}
