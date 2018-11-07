package org.cn.wzy.aop;

import io.jsonwebtoken.*;
import org.aspectj.lang.ProceedingJoinPoint;
import org.cn.wzy.controller.BaseController;
import org.cn.wzy.model.ResultModel;
import org.cn.wzy.util.TokenUtil;

/**
 * Create by Wzy
 * on 2018/7/18 23:48
 * 不短不长八字刚好
 */
public class JwtAspect {


    public Object checkJwt(ProceedingJoinPoint joinPoint) throws Throwable {
        BaseController controller = (BaseController) joinPoint.getTarget();
        String token = controller.getRequest().getHeader("Authorization");
        if (token == null)
            return joinPoint.proceed();
        Claims claims = null;
        try {
            claims = TokenUtil.parse(token);
        } catch (ExpiredJwtException e) {
            return ResultModel.builder().code("JWT EXPIRE").build();
        } catch (Exception e) {
            return ResultModel.builder().code("JWT WRONG").build();
        }
        controller.set("claims", claims);
        return joinPoint.proceed();
    }
}
