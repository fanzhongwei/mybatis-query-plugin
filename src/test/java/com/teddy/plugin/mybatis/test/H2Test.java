package com.teddy.plugin.mybatis.test;

import com.teddy.plugin.mybatis.TestApplication;
import com.teddy.plugin.mybatis.mapper.TestMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@RunWith(SpringRunner.class)
@ActiveProfiles("mysql")
@SpringBootTest(classes = TestApplication.class)
public class H2Test {

    @Resource
    private TestMapper testMapper;

    @Test
    public void test_table_should_have_6_recodes(){
        Assert.assertEquals(6, testMapper.selectAll().size());
    }
}
