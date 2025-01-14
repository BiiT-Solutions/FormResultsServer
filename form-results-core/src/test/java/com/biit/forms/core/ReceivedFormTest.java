package com.biit.forms.core;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@SpringBootTest
@Test(groups = {"receivedFormTest"})
@Listeners(TestListener.class)
public class ReceivedFormTest extends AbstractTestNGSpringContextTests {

}