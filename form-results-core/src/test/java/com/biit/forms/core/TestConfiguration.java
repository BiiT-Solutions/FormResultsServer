package com.biit.forms.core;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.TestPropertySource;

@Configuration
@EnableCaching
@TestPropertySource("classpath:application.properties")
@ComponentScan({"com.biit.forms.core"})
public class TestConfiguration {

}
