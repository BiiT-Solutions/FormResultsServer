package com.biit.forms.core.models;

import com.biit.server.controllers.models.ElementDTO;

public class MyEntityDTO extends ElementDTO<Long> {

    private Long id;

    private String name = "";

    public MyEntityDTO() {
        super();
    }

    public MyEntityDTO(String name) {
        this();
        this.name = name;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
