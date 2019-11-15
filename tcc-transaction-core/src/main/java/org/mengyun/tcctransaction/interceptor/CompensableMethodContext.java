package org.mengyun.tcctransaction.interceptor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.mengyun.tcctransaction.api.*;
import org.mengyun.tcctransaction.common.MethodRole;
import org.mengyun.tcctransaction.support.FactoryBuilder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * Created by changming.xie on 04/04/19.
 */
public class CompensableMethodContext {

    ProceedingJoinPoint pjp = null;
    Method method = null;
    Compensable compensable = null;
    Propagation propagation = null;
    TransactionContext transactionContext = null;

    public CompensableMethodContext(ProceedingJoinPoint pjp) {
        this.pjp = pjp;
        this.method = getCompensableMethod();
        this.compensable = method.getAnnotation(Compensable.class);
        this.propagation = compensable.propagation();
        FactoryBuilder.SingeltonFactory<? extends TransactionContextEditor> singletonFactory = FactoryBuilder.factoryOf(compensable.transactionContextEditor());
        this.transactionContext = singletonFactory .getInstance().get(pjp.getTarget(), method, pjp.getArgs());

    }


    public Compensable getAnnotation() {
        return compensable;
    }


    public Propagation getPropagation() {
        return propagation;
    }


    public TransactionContext getTransactionContext() {
        return transactionContext;
    }


    public Method getMethod() {
        return method;
    }


    /**
     * 获取当前事务唯一约束
     * @return
     */
    public Object getUniqueIdentity() {
        //获取方法上的所有注解
        Annotation[][] annotations = this.getMethod().getParameterAnnotations();

        for (int i = 0; i < annotations.length; i++) {
            for (Annotation annotation : annotations[i]) {
                //获取对应注解
                if (annotation.annotationType().equals(UniqueIdentity.class)) {
                    //获取注解上的参数
                    Object[] params = pjp.getArgs();
                    Object unqiueIdentity = params[i];

                    return unqiueIdentity;
                }
            }
        }

        return null;
    }


    private Method getCompensableMethod() {
        Method method = ((MethodSignature) (pjp.getSignature())).getMethod();

        if (method.getAnnotation(Compensable.class) == null) {
            try {
                method = pjp.getTarget().getClass().getMethod(method.getName(), method.getParameterTypes());
            } catch (NoSuchMethodException e) {
                return null;
            }
        }
        return method;
    }

    /**
     * 	事务传播性，包含REQUIRED（必须存在事务，不存在，创建），
     * 	SUPPORTS（有事务的话在事务内运行），
     * 	MANDATORY（必须存在事务），
     * 	REQUIRES_NEW（不管是否存在，创建新的事务）
     * @param isTransactionActive
     * @return
     */

    public MethodRole getMethodRole(boolean isTransactionActive) {
        if ((propagation.equals(Propagation.REQUIRED) && !isTransactionActive && transactionContext == null) ||
                propagation.equals(Propagation.REQUIRES_NEW)) {
            return MethodRole.ROOT;
        } else if ((propagation.equals(Propagation.REQUIRED) || propagation.equals(Propagation.MANDATORY)) && !isTransactionActive && transactionContext != null) {
            return MethodRole.PROVIDER;
        } else {
            return MethodRole.NORMAL;
        }
    }


    public Object proceed() throws Throwable {
        return this.pjp.proceed();
    }

    @Override
    public String toString() {
        return "CompensableMethodContext{" +
            "pjp=" + pjp.getSignature().getDeclaringTypeName() +
            ", method=" + method.getName() +
            ", compensable=" + compensable +
            ", propagation=" + propagation +
            ", transactionContext=" + transactionContext +
            '}';
    }
}
