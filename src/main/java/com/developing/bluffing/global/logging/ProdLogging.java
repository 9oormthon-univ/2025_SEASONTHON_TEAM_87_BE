package com.developing.bluffing.global.logging;

import com.developing.bluffing.global.exception.GlobalBaseException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@Profile("prod")
public class ProdLogging {

    @Around("execution(* com.developing.bluffing..*Controller.*(..))")
    public Object logExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().toShortString();

        log.info("[START] {}", methodName);

        try {
            Object result = joinPoint.proceed();
            long end = System.currentTimeMillis();
            log.info("[END] {} ({}ms)", methodName, end - start);
            return result;
        } catch (GlobalBaseException e) {
            long end = System.currentTimeMillis();
            log.error("[GLOBAL_EXCEPTION] 메서드: {} (소요시간: {}ms)", methodName, end - start);
            log.error("[GLOBAL_EXCEPTION] 예외코드: {}\n예외상세: {}",e.getErrorCode(), e.getMessage());
            throw e;
        } catch (Throwable e) {
            long end = System.currentTimeMillis();
            log.error("[EXCEPTION] {} ({}ms)", methodName, end - start);
            throw e;
        }
    }
}
