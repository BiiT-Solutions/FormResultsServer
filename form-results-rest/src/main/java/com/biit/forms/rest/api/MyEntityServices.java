package com.biit.forms.rest.api;

import com.biit.forms.core.controllers.MyEntityController;
import com.biit.forms.core.converters.MyEntityConverter;
import com.biit.forms.core.converters.models.MyEntityConverterRequest;
import com.biit.forms.core.providers.MyEntityProvider;
import com.biit.forms.core.models.MyEntityDTO;
import com.biit.forms.persistence.entities.MyEntity;
import com.biit.forms.persistence.repositories.MyEntityRepository;
import com.biit.server.rest.ElementServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/entities")
public class MyEntityServices extends ElementServices<MyEntity, Long, MyEntityDTO, MyEntityRepository,
        MyEntityProvider, MyEntityConverterRequest, MyEntityConverter, MyEntityController> {

    public MyEntityServices(MyEntityController controller) {
        super(controller);
    }
}
