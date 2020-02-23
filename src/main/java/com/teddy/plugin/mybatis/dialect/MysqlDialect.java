package com.teddy.plugin.mybatis.dialect;

import com.teddy.plugin.mybatis.query.Page;
import com.teddy.plugin.mybatis.query.Query;

/**
 * package com.teddy.plugin.mybatis.dialect
 * description: MysqlDialect
 * Copyright 2018 Teddy, Inc. All rights reserved.
 *
 * @author Teddy
 * @date 2018-9-1 18:04
 */
public class MysqlDialect extends AbstractDialect  {

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
        sb.append(LINE_BREAK)
                .append(" limit ").append(" ")
                .append(query.getPage().getOffset())
                .append(",")
                .append(query.getPage().getLimit());
        return sb.toString();
    }
}
