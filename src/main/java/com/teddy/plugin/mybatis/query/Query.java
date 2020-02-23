package com.teddy.plugin.mybatis.query;

import com.teddy.plugin.mybatis.helper.QueryHelper;
import lombok.Getter;
import lombok.Setter;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.query
 * @Description: 查询相关参数，包含分页、过滤、排序
 * @date 2018-5-4 18:10
 */
@Getter
@Setter
public class Query<E> extends ArrayList<E> implements Closeable {

    /** 过滤信息 */
    private AbstractFilter filter;

    /** 分页信息 */
    private Page page;

    /** 排序信息 */
    private String order;

    /** 排序对象Class */
    private Class sortClass;

    @Override
    public void close() throws IOException {
        QueryHelper.clearQuery();
    }
}
