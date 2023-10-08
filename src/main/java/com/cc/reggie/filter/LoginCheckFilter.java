package com.cc.reggie.filter;

import com.alibaba.fastjson.JSON;
import com.cc.reggie.common.BaseContext;
import com.cc.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查是否登录
 */
@Slf4j
@WebFilter
public class LoginCheckFilter implements Filter {
    // 一个字符匹配的工具，支持通配符
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String requestURI = request.getRequestURI();
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        log.info("拦截到请求: {}", requestURI);

        Boolean check = check(urls, requestURI);

        if (check){
//            log.info("本次请求{}不需要处理", requestURI);
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("employee") != null){
            log.info("用户已登录，用户id为：{}", request.getSession().getAttribute("employee"));
            BaseContext.setCurrentUserId((Long) request.getSession().getAttribute("employee"));
            filterChain.doFilter(request,response);
            return;
        }

        if (request.getSession().getAttribute("user") != null){
            log.info("移动端用户已登录，用户id为：{}", request.getSession().getAttribute("user"));
            BaseContext.setCurrentUserId((Long) request.getSession().getAttribute("user"));
            filterChain.doFilter(request,response);
            return;
        }

        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    public Boolean check(String[] urls, String requestURI) {
        for (String url : urls) {
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match) return true;
        }
        return false;
    }

}
