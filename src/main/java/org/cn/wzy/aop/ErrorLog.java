package org.cn.wzy.aop;

import lombok.extern.log4j.Log4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.cn.wzy.model.ResultModel;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.cn.wzy.model.ResultModel.ERROR;

/**
 * Created by Wzy
 * on 2018/5/13
 */
@Log4j
public class ErrorLog {

    public void serviceError(JoinPoint joinPoint, Throwable e) {
        //获取目标函数的类信息
        String clazzName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        log.error("**************************************************************");
        log.error("method info: " + clazzName + "的" + methodName + "方法报错");
        log.error("Exception class: " + e.getClass().getName());
        log.error("Exception msg:" + sw.toString());
        log.error("**************************************************************");
    }

    public Object controllerError(ProceedingJoinPoint joinPoint) {
        //获取目标函数的类信息
        String clazzName = joinPoint.getTarget().getClass().getName();
        String methodName = joinPoint.getSignature().getName();
        Object rs = null;
        try {
            rs = joinPoint.proceed();
            return rs;
        } catch (Throwable throwable) {
            StringWriter sw = new StringWriter();
            throwable.printStackTrace(new PrintWriter(sw));
            log.error("**************************************************************");
            log.error("method info: " + clazzName + "的" + methodName + "方法报错");
            log.error("Exception class: " + throwable.getClass().getName());
            log.error("Exception msg:" + sw.toString());
            log.error("**************************************************************");
            return ResultModel.builder().code(ERROR).build();
        }
    }
}
