package com.biit.forms.core;

/*-
 * #%L
 * Form Results Server (Core)
 * %%
 * Copyright (C) 2022 - 2025 BiiT Sourcing Solutions S.L.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

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
