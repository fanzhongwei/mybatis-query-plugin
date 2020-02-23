package com.teddy.plugin.mybatis.test;

import com.teddy.plugin.mybatis.TestApplication;
import com.teddy.plugin.mybatis.entity.TestEntity;
import com.teddy.plugin.mybatis.helper.QueryHelper;
import com.teddy.plugin.mybatis.mapper.TestMapper;
import com.teddy.plugin.mybatis.query.Query;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@ActiveProfiles("mysql")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class)
public class MysqlTest {

    @Resource
    private TestMapper testMapper;

    @Test
    public void test_table_should_have_6_recodes() {
        Assert.assertEquals(6, testMapper.selectAll().size());
    }


    @Test
    public void test_limit_3_should_have_2_pages() {
        QueryHelper.startPage(0, 3);
        Query<TestEntity> result = (Query<TestEntity>) testMapper.selectAll();
        Assert.assertEquals(2, result.getPage().getPages());
    }

    @Test
    public void test_limit_4_should_have_2_pages() {
        QueryHelper.startPage(0, 4);
        Query<TestEntity> result = (Query<TestEntity>) testMapper.selectAll();
        Assert.assertEquals(2, result.getPage().getPages());
    }

    @Test
    public void test_age_sort() {
        QueryHelper.startSort("age", TestEntity.class);
        List<Integer> ages = testMapper.selectAges();
        Assert.assertArrayEquals(new Integer[]{21, 22, 23, 26, 27, 28}, ages.toArray());
    }

    @Test
    public void test_age_desc_sort() {
        QueryHelper.startSort("-age", TestEntity.class);
        List<Integer> ages = testMapper.selectAges();
        Assert.assertArrayEquals(new Integer[]{28, 27, 26, 23, 22, 21}, ages.toArray());
    }

    @Test
    public void test_limit_3_age_sort() {
        QueryHelper.startSort("age", TestEntity.class);
        QueryHelper.startPage(0, 3);
        List<Integer> ages = testMapper.selectAges();
        Assert.assertArrayEquals(new Integer[]{21, 22, 23}, ages.toArray());
    }

    @Test
    public void test_limit_3_age_desc_sort() {
        QueryHelper.startSort("-age", TestEntity.class);
        QueryHelper.startPage(0, 3);
        List<Integer> ages = testMapper.selectAges();
        Assert.assertArrayEquals(new Integer[]{28, 27, 26}, ages.toArray());
    }


}
