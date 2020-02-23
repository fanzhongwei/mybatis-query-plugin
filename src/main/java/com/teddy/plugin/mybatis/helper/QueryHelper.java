package com.teddy.plugin.mybatis.helper;

import com.teddy.plugin.mybatis.exception.QueryException;
import com.teddy.plugin.mybatis.query.AbstractFilter;
import com.teddy.plugin.mybatis.query.Page;
import com.teddy.plugin.mybatis.query.Query;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.helper
 * @Description: mybatis查询参数设置入口
 * @date 2018-5-4 17:59
 */
public class QueryHelper {
    private static final ThreadLocal<Query> LOCAL_QUERY = new ThreadLocal();

    private QueryHelper() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * 清楚当前查询信息
     */
    public static void clearQuery() {
        LOCAL_QUERY.remove();
    }

    /**
     * 获取查询参数.
     *
     * @return the query
     */
    public static Query getQuery() {
        return LOCAL_QUERY.get();
    }

    /**
     * 设置分页参数
     *
     * @param offset the offset
     * @param limit the limit
     * @return the query
     */
    public static Query startPage(int offset, int limit) {
        if (limit <= 0) {
            throw new QueryException("limit参数必须大于0.");
        }
        Query query = createQuery();
        query.setPage(new Page(offset, limit));
        return query;
    }

    /**
     * 设置过滤参数.
     *
     * @param filter the filter
     * @return the query
     */
    public static Query startFilter(AbstractFilter filter) {
        if (null == filter) {
            throw new QueryException("过滤参数不允许为空.");
        }
        Query query = createQuery();
        query.setFilter(filter);
        return query;
    }

    /**
     * 设置排序参数
     *
     * @param order the order
     * @return the query
     */
    public static Query startSort(String order, Class sortClass) {
        Query query = createQuery();
        query.setOrder(order);
        query.setSortClass(sortClass);
        return query;
    }

    private static Query createQuery() {
        Query query = LOCAL_QUERY.get();
        if (null == query) {
            query = new Query();
            LOCAL_QUERY.set(query);
        }
        return query;
    }
}
