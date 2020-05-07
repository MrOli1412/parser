package com.xml.parser.config;

import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
//@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class DefaultBatchConfig extends DefaultBatchConfigurer {
    @Override
    public void setDataSource(DataSource dataSource) {
//        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create();
//        dataSourceBuilder.url("jdbc:postgresql://localhost:5432/xmlParser?currentSchema=test");
//        dataSourceBuilder.username("postgres");
//        dataSourceBuilder.password("admin");
//        dataSource = dataSourceBuilder.build();
////        return dataSourceBuilder.build();
    }
}



