package com.teddy.plugin.mybatis.query.builder;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.exception.QueryException;
import com.teddy.plugin.mybatis.query.AbstractFilter;
import com.teddy.plugin.mybatis.query.IFilterBuilder;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * DefaultFilterBuilder
 */
public class DefaultFilterBuilder implements IFilterBuilder {

    @Override
    public String buildSql(Field field, AbstractFilter filterParam) {
        Filter filter = field.getAnnotation(Filter.class);
        return filter.name() + " " + filter.operate() + " ?";
    }

    @Override
    public void putParam(Field field, AbstractFilter filterParam, Map paramMap) {
        try {
            Filter filter = field.getAnnotation(Filter.class);
            Object value = field.get(filterParam);
            // 字符串类型加上前后缀
            if (value.getClass() == String.class) {
                // 处理postgress数据库用字符串参数查询日期类型
                if (StringUtils.isNotBlank(filter.dateFormat())) {
                    value = DateUtils.parseDate(ObjectUtils.toString(value), filter.dateFormat());
                } else {
                    value = filter.prefix() + value + filter.suffix();
                }
            }
            paramMap.put(field.getName(), value);
        } catch (Exception e) {
            throw new QueryException(e);
        }
    }
}
