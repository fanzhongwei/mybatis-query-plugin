package com.teddy.plugin.mybatis.dialect;

import com.teddy.plugin.mybatis.query.Page;
import com.teddy.plugin.mybatis.query.Query;
import lombok.extern.slf4j.Slf4j;

/**
 * abase 数据库方言
 *
 * @author teddy Created on 2018/5/9 20:00.
 */
@Slf4j
public class PostgresqlDialect extends AbstractDialect {

    @Override
    public String getCountSql(Query query, String sql) {
        return String.format(COUNT_SQL_FORMAT, buildFilterSql(query, sql));
    }

    @Override
    public String getQuerySql(Query query, String sql) {
        sql = buildFilterSql(query, sql);
        String sortSql = buildSortSql(query);
        StringBuilder sb = new StringBuilder();
        sb.append(sql).append(LINE_BREAK).append(sortSql);
        Page page = query.getPage();
        if (null == page) {
            return sb.toString();
        }
        sb.append(LINE_BREAK).append(" offset ").append(query.getPage().getOffset()).append(" limit  ")
            .append(query.getPage().getLimit());
        return sb.toString();
    }
}
