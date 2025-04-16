package com.biit.forms.core;

import com.biit.form.result.FormResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
@Test(groups = {"receivedFormTest"})
@Listeners(TestListener.class)
public class ReceivedFormTest extends AbstractTestNGSpringContextTests {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testDeserializationSerializationFormResult() throws IOException, URISyntaxException {
        final FormResult formResult = objectMapper.readValue(new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("json/Localization.json").toURI()))), FormResult.class);
        Assert.assertEquals(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(formResult),
                new String(Files.readAllBytes(Paths.get(getClass().getClassLoader().getResource("json/Localization.json").toURI()))));
    }

}