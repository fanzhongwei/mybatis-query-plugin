package com.teddy.plugin.mybatis.query;

import com.teddy.plugin.mybatis.annotation.Filter;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 过滤条件生成器
 */
public interface IFilterBuilder {

    /**
     * SQL生成
     *
     * @param field 过滤字段
     * @param filterParam 过滤条件
     * @return 组装的sql
     */
    String buildSql(Field field, AbstractFilter filterParam);

    /**
     * param 生成
     *
     * @param field 过滤字段
     * @param filterParam 过滤条件
     * @param paramMap 参数map
     */
    void putParam(Field field, AbstractFilter filterParam, Map paramMap);
}
