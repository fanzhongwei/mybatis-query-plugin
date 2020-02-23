package com.teddy.plugin.mybatis.annotation;

import java.lang.annotation.*;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.annotation
 * @Description: 排序
 * @date 2018-5-8 20:29
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface Sort {
    String value();
}
