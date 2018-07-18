package org.cn.wzy.controller;

import io.jsonwebtoken.Claims;
import lombok.extern.log4j.Log4j;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@Log4j
public class BaseController {
    protected static final ThreadLocal<HttpServletRequest> requests = new ThreadLocal();
    protected static final ThreadLocal<HttpServletResponse> responses = new ThreadLocal();

    @ModelAttribute
    public void init(HttpServletRequest request, HttpServletResponse response) {
        this.requests.set(request);
        this.responses.set(response);
    }

    public HttpServletRequest getRequest() {
        return requests.get();
    }

    public HttpServletResponse getResponse() {
        return responses.get();
    }

    public Object getSessionValue(String key) {
        return getRequest().getSession().getAttribute(key);
    }

    public String getRemoteAddr() {
        return getRequest().getRemoteAddr();
    }

    public void save(String key, Object value) {
        getRequest().setAttribute(key, value);
    }

    public Object ValueOfClaims(String key) {
        Claims claims = (Claims) getRequest().getAttribute("claims");
        if (claims == null || claims.get(key) == null)
            return null;
        else
            return claims.get(key);
    }

    public Object getValue(String key) {
        return getRequest().getAttribute(key);
    }
}
