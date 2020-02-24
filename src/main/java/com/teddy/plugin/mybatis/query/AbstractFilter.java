package com.teddy.plugin.mybatis.query;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.exception.QueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import java.lang.reflect.Field;
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
        return getNeedFilterField().stream().map(field -> {
            try {
                Filter filter = field.getAnnotation(Filter.class);
                Class<? extends IFilterBuilder> builderClass = filter.builder();
                IFilterBuilder builder = builderClass.newInstance();
                return builder.buildSql(field, this);
            } catch (Exception e) {
                log.error("cannot get field【{}】 value", field, e);
                throw new QueryException("获取处理过滤字段：" + field + "值失败");
            }
        }).collect(Collectors.joining(" and "));
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

        needFilterFields.forEach(field -> putFieldValue(field, paramMap));
        return paramMap;
    }

    /**
     * Get field value object.
     *
     * @param field the field
     * @return the object
     */
    protected void putFieldValue(Field field, Map paramMap) {
        try {
            Filter filter = field.getAnnotation(Filter.class);
            Class<? extends IFilterBuilder> builderClass = filter.builder();
            IFilterBuilder builder = builderClass.newInstance();
            builder.putParam(field, this, paramMap);
        } catch (Exception e) {
            log.error("获取field【{}】值失败", field, e);
            throw new QueryException("处理过滤字段：" + field + "失败");
        }

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
