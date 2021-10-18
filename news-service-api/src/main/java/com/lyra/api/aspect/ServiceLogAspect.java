package com.lyra.api.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Modifier;


@Aspect
@Component
public class ServiceLogAspect {
    private final Logger logger = LoggerFactory.getLogger(ServiceLogAspect.class);

    @Around(value = "execution(* com.lyra.*.service..*.*(..))")
    public Object recordTimeOfService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        // target 为切入点所在目标对象
        logger.info("====开始执行 {}, {}", proceedingJoinPoint.getTarget().getClass(),
                proceedingJoinPoint.getSignature().getName());



        Signature signature = proceedingJoinPoint.getSignature();

        // 获取调用方法返回值 包名类名 方法名以及参数
        logger.info(signature.toString());

        // 返回方法名称
        System.out.println(signature.getName());
        // 返回类的全限定名
        System.out.println(signature.getDeclaringType());
        // 返回包名
        System.out.println(signature.getDeclaringTypeName());
        // 返回修饰 如public private final ...
        System.out.println((Modifier.toString(signature.getModifiers())));

        long beginCurrenTime = System.currentTimeMillis();

        // 调用方法链
        Object result = proceedingJoinPoint.proceed();
        long overCurrentTime = System.currentTimeMillis();

        long takeTime = overCurrentTime - beginCurrenTime + 3000;


        if (takeTime > 3000) {
            logger.error("当前执行耗时:{}", takeTime);
        } else if (takeTime > 2000) {
            logger.warn("当前执行耗时:{}", takeTime);
        } else {
            logger.info("当前执行耗时:{}", takeTime);
        }
        return result;
    }
}
