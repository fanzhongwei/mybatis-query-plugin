package com.teddy.plugin.mybatis.query;

import com.teddy.plugin.mybatis.annotation.Filter;

/**
 * 过滤条件生成器
 */
public interface IFilterBuilder {

    /**
     * SQL生成
     *
     * @param filter 过滤规则
     * @param value 过滤条件的值
     * @return 组装的sql
     */
    String buildSql(Filter filter, Object value);

    /**
     * param 生成
     *
     * @param filter 过滤规则
     * @param value 过滤条件的值
     * @return 组装的sql
     */
    Object buildParam(Filter filter, Object value);
}
