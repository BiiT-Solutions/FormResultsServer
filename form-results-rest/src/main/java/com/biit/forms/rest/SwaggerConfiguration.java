package com.biit.forms.rest;

/*-
 * #%L
 * Form Results Server (Rest)
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

import com.biit.server.rest.DefaultSwaggerConfiguration;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfiguration extends DefaultSwaggerConfiguration {
    private static final String SWAGGER_TITLE = "Form Results Server";
    private static final String SWAGGER_DESCRIPTION = "";
    private static final String SWAGGER_GROUP = "rest-public";

    private static final String[] PACKAGES_TO_SCAN = new String[]{"com.biit.forms.rest", "com.biit.server.rest", "com.biit.server.security.rest"};

    @Override
    public String getSwaggerTitle() {
        return SWAGGER_TITLE;
    }

    @Override
    public String getSwaggerDescription() {
        return SWAGGER_DESCRIPTION;
    }

    @Override
    public String getSwaggerGroup() {
        return SWAGGER_GROUP;
    }

    @Override
    public String[] getPackagesToScan() {
        return PACKAGES_TO_SCAN;
    }
}
