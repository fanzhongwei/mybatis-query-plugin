package com.teddy.plugin.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.annotation
 * @Description: 参数过滤注解
 * @date 2018-5-8 14:56
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Filter {

    /** 过滤字段的名字 */
    String name();

    /** 过滤字段的操作 */
    String operate();

    /** 过滤字段值的前缀 仅用于String类型的字段 */
    String prefix() default "";

    /** 过滤字段值的后缀 仅用于String类型的字段 */
    String suffix() default "";

    /** 查询的日期格式化*/
    String dateFormat() default "";
}
