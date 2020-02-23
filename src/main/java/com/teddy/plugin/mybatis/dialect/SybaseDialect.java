package com.teddy.plugin.mybatis.dialect;

import com.teddy.plugin.mybatis.query.Page;
import com.teddy.plugin.mybatis.query.Query;
import lombok.extern.slf4j.Slf4j;

import java.util.Base64;
import java.util.UUID;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.dialect
 * @Description: sybase数据库方言
 * @date 2018-5-9 10:39
 */
@Slf4j
public class SybaseDialect extends AbstractDialect {

    @Override
    public String getCountSql(Query query, String sql) {
        return String.format(COUNT_SQL_FORMAT, buildFilterSql(query, sql));
    }

    @Override
    public String getQuerySql(Query query, String sql) {
        // 首先添加过滤信息
        // 添加排序和分页信息
        return buildPageAndSortSql(query, buildFilterSql(query, sql));
    }

    private String buildPageAndSortSql(Query query, String sql) {
        Page page = query.getPage();
        String sortSql = buildSortSql(query);
        if (null == page) {
            return new StringBuilder().append(sql).append(LINE_BREAK).append(sortSql).toString();
        }

        String tempTableName
            = String.valueOf(Base64.getEncoder().encode((UUID.randomUUID().toString()).getBytes())).replace("[B@", "");

        StringBuilder sb = new StringBuilder();

        int rowCount = query.getPage().getOffset() + query.getPage().getLimit();
        sb.append("SET rowcount ").append(rowCount).append(LINE_BREAK);
        sb.append("SELECT t__.*, rownum__ = IDENTITY (11) INTO #t_").append(tempTableName).append(LINE_BREAK);
        sb.append("FROM(").append(sql).append(" ) t__").append(LINE_BREAK);
        sb.append(sortSql).append(LINE_BREAK);
        sb.append("SET rowcount 0").append(LINE_BREAK);
        sb.append("SELECT * FROM #t_").append(tempTableName).append(LINE_BREAK);
        sb.append("WHERE rownum__ > ").append(query.getPage().getOffset()).append(LINE_BREAK);
        sb.append("DROP TABLE #t_").append(tempTableName);

        return sb.toString();
    }

}
