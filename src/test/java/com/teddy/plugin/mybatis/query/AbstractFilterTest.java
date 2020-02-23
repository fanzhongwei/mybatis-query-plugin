package com.teddy.plugin.mybatis.query;

import com.teddy.plugin.mybatis.annotation.Filter;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

/**
 * @author teddy
 * @Package com.teddy.plugin.mybatis.query
 * @Description: 单元测试
 * @date 2018-6-26 18:29
 */
@Slf4j
public class AbstractFilterTest {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    @ToString
    private static class TestFilter extends AbstractFilter {

        @Filter(name = "id", operate = "=")
        private Integer id;

        @Filter(name = "name", operate = "like", prefix = "%", suffix = "%")
        private String name;

        @Filter(name = "age", operate = "=")
        private Integer age;
    }

    private TestFilter filter;

    @Before
    public void before(){
        filter = new TestFilter();
        filter.setId(1)
            .setName("单元测试Name")
            .setAge(123);
    }

    @Test
    public void getFilterSql() {
        String filterSql = filter.getFilterSql();
        String result = "age like ? and id = ? and name like ?";
        Assert.assertEquals(result,filterSql);
    }

    @Test
    public void getEmptyFilterSql(){
        TestFilter emptyFilter = new TestFilter();
        Assert.assertEquals(StringUtils.EMPTY,emptyFilter.getFilterSql());
    }

    @Test
    public void getFilterMap() {
        Map filterMap = filter.getFilterMap();
        Assert.assertArrayEquals(new String[]{"name", "age", "id"}, filterMap.keySet().toArray());
        Assert.assertArrayEquals(new Object[]{"%单元测试Name%", 123, 1}, filterMap.values().toArray());
    }

}