package com.teddy.plugin.mybatis.query.builder;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.exception.QueryException;
import com.teddy.plugin.mybatis.query.IFilterBuilder;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * in 条件sql组装
 */
public class InFilterBuilder implements IFilterBuilder {
    @Override
    public String buildSql(Filter filter, Object value) {
        if (value instanceof Collection){
            Collection c = (Collection) value;
            return filter.name() + " in (" + c.stream().map(v -> "?").collect(Collectors.joining(",")) + ")";
        }
        return null;
    }

    @Override
    public Object buildParam(Filter filter, Object value) {
        if (value instanceof Collection){
            return value;
        }
        throw new QueryException(value + " is not Collection.");
    }
}
