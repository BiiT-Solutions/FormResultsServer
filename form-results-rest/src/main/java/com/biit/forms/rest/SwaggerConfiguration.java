package com.biit.forms.rest;

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
