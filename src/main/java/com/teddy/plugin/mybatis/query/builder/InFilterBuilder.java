package com.teddy.plugin.mybatis.query.builder;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.exception.QueryException;
import com.teddy.plugin.mybatis.query.AbstractFilter;
import com.teddy.plugin.mybatis.query.IFilterBuilder;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * in 条件sql组装
 */
public class InFilterBuilder implements IFilterBuilder {
    @Override
    public String buildSql(Field field, AbstractFilter filterParam) {
        try {
            Filter filter = field.getAnnotation(Filter.class);
            Object value = field.get(filterParam);
            assertIsCollection(value);
            Collection c = (Collection) value;
            return filter.name() + " in (" + c.stream().map(v -> "?").collect(Collectors.joining(",")) + ")";
        } catch (Exception e) {
            throw new QueryException(e);
        }
    }

    private void assertIsCollection(Object value) {
        if (!(value instanceof Collection)){
            throw new QueryException(value + " is not Collection.");
        }
    }

    @Override
    public void putParam(Field field, AbstractFilter filterParam, Map paramMap) {
        try {
            Object value = field.get(filterParam);
            assertIsCollection(value);
            Collection c = (Collection) value;
            int i = 0;
            Iterator iterator = c.iterator();
            while (iterator.hasNext()) {
                paramMap.put(field.getName() + i++, iterator.next());
            }
        } catch (Exception e) {
            throw new QueryException(e);
        }

    }
}
