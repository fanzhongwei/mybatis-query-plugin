package com.teddy.plugin.mybatis.entity;

import com.teddy.plugin.mybatis.annotation.Sort;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestEntity {

    private Integer id;

    private String name;

    @Sort("age")
    private Integer age;
}
