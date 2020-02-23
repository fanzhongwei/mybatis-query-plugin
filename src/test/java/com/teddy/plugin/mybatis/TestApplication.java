package com.teddy.plugin.mybatis;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;

@SpringBootApplication
@ActiveProfiles("mysql")
@ComponentScan("com.teddy.plugin.mybatis")
@MapperScan(basePackages = {"com.teddy.plugin.mybatis"})
public class TestApplication {
}