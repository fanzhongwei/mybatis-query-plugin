package com.teddy.plugin.mybatis.query.builder;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.exception.QueryException;
import com.teddy.plugin.mybatis.query.IFilterBuilder;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * DefaultFilterBuilder
 */
public class DefaultFilterBuilder implements IFilterBuilder {

    @Override
    public String buildSql(Filter filter, Object value) {
        return filter.name() + " " + filter.operate() + " ?";
    }

    @Override
    public Object buildParam(Filter filter, Object value) {
        try {
            // 字符串类型加上前后缀
            if (value.getClass() == String.class) {
                // 处理postgress数据库用字符串参数查询日期类型
                if (StringUtils.isNotBlank(filter.dateFormat())) {
                    return DateUtils.parseDate(ObjectUtils.toString(value), filter.dateFormat());
                }
                return filter.prefix() + value + filter.suffix();
            }
            return value;
        } catch (Exception e) {
            throw new QueryException(e);
        }
    }
}
