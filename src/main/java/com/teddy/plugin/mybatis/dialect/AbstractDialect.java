package com.teddy.plugin.mybatis.dialect;

import com.teddy.plugin.mybatis.annotation.Sort;
import com.teddy.plugin.mybatis.query.AbstractFilter;
import com.teddy.plugin.mybatis.query.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * 方言抽象
 *
 * @author teddy Created on 2018/5/9 20:05.
 */
@Slf4j
public abstract class AbstractDialect implements Dialect {
    /**
     * "\r\n"
     */
    protected static final String LINE_BREAK = "\r\n";

    /**
     * SELECT count(*) FROM (%s) t__
     */
    protected static final String COUNT_SQL_FORMAT = "SELECT count(*) FROM (%s) t__";

    /**
     * 拼接排序
     *
     * @param query query object
     * @return 拼接排序sql
     */
    protected String buildSortSql(Query query) {
        String order = query.getOrder();
        Class sortClass = query.getSortClass();
        if (StringUtils.isBlank(order)) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();

        String[] orders = order.split(",");
        List<String> orderList = new ArrayList<>();
        for (String s : orders) {
            try {
                Field field = sortClass.getDeclaredField(s.replace("-", ""));
                Sort sort = field.getAnnotation(Sort.class);
                if (null == sort) {
                    continue;
                }
                String sortName = sort.value();
                // 倒序
                if (s.startsWith("-")) {
                    sortName = sortName.concat(" desc ");
                }
                orderList.add(sortName);
            } catch (NoSuchFieldException e) {
                log.error("获取排序字段失败", e);
                continue;
            }
        }
        sb.append(" ORDER BY ").append(StringUtils.join(orderList, ","));
        return sb.toString();
    }

    /**
     * 拼接过滤
     *
     * @param query query object
     * @param sql sql
     * @return 拼接过滤 sql
     */
    protected String buildFilterSql(Query query, String sql) {
        AbstractFilter filter = query.getFilter();
        if (null == filter) {
            return sql;
        }
        String filterSql = filter.getFilterSql();
        if (StringUtils.isBlank(filterSql)) {
            return sql;
        }
        StringBuilder sb = new StringBuilder();
        sb.append("SELECT t_.* FROM (").append(LINE_BREAK);
        sb.append(sql).append(LINE_BREAK);
        sb.append(") t_").append(LINE_BREAK);
        sb.append("WHERE ").append(filterSql);
        return sb.toString();
    }
}
