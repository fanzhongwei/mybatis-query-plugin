package com.teddy.plugin.mybatis.mapper;

import com.teddy.plugin.mybatis.entity.TestEntity;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TestMapper {


    List<TestEntity> selectAll();

    @Select("select age from teddy.t_test")
    List<Integer> selectAges();
}
