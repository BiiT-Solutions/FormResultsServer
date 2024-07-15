package com.biit.forms.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"myEntityTest"})
@Listeners(TestListener.class)
public class MyEntityTest extends AbstractTestNGSpringContextTests {

}