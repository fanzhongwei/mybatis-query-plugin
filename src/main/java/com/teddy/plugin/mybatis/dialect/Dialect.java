package com.teddy.plugin.mybatis.dialect;

import com.teddy.plugin.mybatis.query.Query;

/**
 * The interface Dialect.
 *
 * @author teddy
 * @Package com.teddy.plugin.mybatis.dialect
 * @Description: 数据库方言
 * @date 2018 -5-9 10:05
 */
public interface Dialect {

    /**
     * 获取查询总数的SQL
     *
     * @param query the query
     * @param sql the sql
     * @return the count sql
     */
    String getCountSql(Query query, String sql);

    /**
     * 获取最终查询的SQL
     *
     * @param query the query
     * @param sql the sql
     * @return the query sql
     */
    String getQuerySql(Query query, String sql);

}
