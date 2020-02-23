package com.teddy.plugin.mybatis.query;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.exception.QueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.query
 * @Description: 过滤接口
 * @date 2018-5-4 18:14
 */
@Slf4j
public abstract class AbstractFilter {

    private static final String BLANK_SPACE = " ";

    private static final String PARAM_SPACE = "?";

    /**
     * 自定义过滤SQL
     *
     * @return SQL
     */
    protected String customCondition() {
        return null;
    }

    /**
     * 自定义过滤参数
     *
     * @return Map
     */
    protected Map customConditionParam() {
        return null;
    }

    /***
     * 获取过滤的SQL，其中参数采用占位符代替
     *
     * @return String
     */
    public String getFilterSql() {
        String customSql = customCondition();
        if (StringUtils.isNotBlank(customSql)) {
            return customSql;
        }
        StringBuilder sql = new StringBuilder();

        getNeedFilterField().forEach(field -> {
            Filter filter = field.getAnnotation(Filter.class);
            sql.append(" and ");
            sql.append(filter.name()).append(BLANK_SPACE);
            sql.append(filter.operate()).append(BLANK_SPACE);
            sql.append(PARAM_SPACE);
        });
        if (sql.length() == 0) {
            return StringUtils.EMPTY;
        }
        // 截取第一个and
        sql.delete(0,5);
        return sql.toString();
    }

    /**
     * 获取过滤参数
     *
     * @return Map
     */
    public Map getFilterMap() {
        Map customParamMap = customConditionParam();
        if (null != customParamMap && !customParamMap.isEmpty()) {
            return customParamMap;
        }
        List<Field> needFilterFields = getNeedFilterField();
        Map paramMap = new HashMap(needFilterFields.size());
        needFilterFields.forEach(field -> {
            try {
                paramMap.put(field.getName(), getFieldValue(field));
            } catch (Exception e) {
                log.error("处理过滤字段：{}失败", field, e);
                throw new QueryException("处理过滤字段：" + field + "失败", e);
            }
        });
        return paramMap;
    }

    /**
     * Get field value object.
     *
     * @param field the field
     * @return the object
     */
    protected Object getFieldValue(Field field) throws IllegalAccessException, ParseException {
        Class type = field.getType();
        Object value = field.get(this);
        Filter filter = field.getAnnotation(Filter.class);
        // 字符串类型加上前后缀
        if (type == String.class) {
            // 处理abase数据库用字符串参数查询日期类型
            if (StringUtils.isNotBlank(filter.dateFormat())) {
                return DateUtils.parseDate(ObjectUtils.toString(field.get(this)), filter.dateFormat());
            }
            return filter.prefix() + value + filter.suffix();
        }
        return value;
    }

    /**
     * Get need filter field list.
     *
     * @return the list
     */
    protected List<Field> getNeedFilterField() {
        Field[] fields = this.getClass().getDeclaredFields();

        List<Field> reuslt = Arrays.asList(fields).stream().filter(field -> {
            try {
                field.setAccessible(true);
                Object value = field.get(this);
                return field.isAnnotationPresent(Filter.class) && null != value
                    && StringUtils.isNotBlank(ObjectUtils.toString(value));
            } catch (Exception e) {
                log.error("处理过滤字段：{}失败", field, e);
                return false;
            }
        }).collect(Collectors.toList());
        // 获取的Field默认是没有排序的，为了保证参数映射顺序一致，在这里做一下排序
        reuslt.sort(Comparator.comparing(Field::getName));
        return reuslt;
    }
}
