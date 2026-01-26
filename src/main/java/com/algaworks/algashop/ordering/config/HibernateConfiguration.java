package com.algaworks.algashop.ordering.config;

import org.hibernate.boot.model.naming.ImplicitNamingStrategy;
import org.hibernate.boot.model.naming.ImplicitNamingStrategyComponentPathImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class HibernateConfiguration {
    @Bean
    public ImplicitNamingStrategy implicit() {
        return new ImplicitNamingStrategyComponentPathImpl();
    }
}