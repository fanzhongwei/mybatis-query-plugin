package com.teddy.plugin.mybatis.filter;

import com.teddy.plugin.mybatis.annotation.Filter;
import com.teddy.plugin.mybatis.query.AbstractFilter;
import com.teddy.plugin.mybatis.query.builder.InFilterBuilder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TestFilter extends AbstractFilter {

    @Filter(name = "name", operate = "like", prefix = "%", suffix = "%")
    private String nameLike;

    @Filter(name = "name", builder = InFilterBuilder.class)
    private List<String> nameIn;

    @Filter(name = "age", builder = InFilterBuilder.class)
    private List<Integer> ageIn;

    @Filter(name = "age", operate = ">")
    private Integer ageMoreThan;
}
