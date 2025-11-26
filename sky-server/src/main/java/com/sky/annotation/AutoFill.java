package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * projectName: sky-take-out
 *
 * @author: 张轩鸣
 * description: 自定义注解：用于标识需要填充公共字段的方法
 */
@Target(ElementType.METHOD) // 方法级别
@Retention(RetentionPolicy.RUNTIME) // 运行时
public @interface AutoFill {
    OperationType value(); // 设置填充数据的操作类型
}
