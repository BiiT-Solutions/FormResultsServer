package com.biit.forms.core.controllers;


import com.biit.forms.core.converters.MyEntityConverter;
import com.biit.forms.core.converters.models.MyEntityConverterRequest;
import com.biit.forms.core.exceptions.MyEntityNotFoundException;
import com.biit.forms.core.kafka.MyEntityEventSender;
import com.biit.forms.core.providers.MyEntityProvider;
import com.biit.forms.core.models.MyEntityDTO;
import com.biit.forms.persistence.entities.MyEntity;
import com.biit.forms.persistence.repositories.MyEntityRepository;
import com.biit.kafka.controllers.KafkaElementController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class MyEntityController extends KafkaElementController<MyEntity, Long, MyEntityDTO, MyEntityRepository,
        MyEntityProvider, MyEntityConverterRequest, MyEntityConverter> {

    @Autowired
    protected MyEntityController(MyEntityProvider provider, MyEntityConverter converter, MyEntityEventSender<MyEntityDTO> eventSender) {
        super(provider, converter, eventSender);
    }

    @Override
    protected MyEntityConverterRequest createConverterRequest(MyEntity myEntity) {
        return new MyEntityConverterRequest(myEntity);
    }

    public MyEntityDTO getByName(String name) {
        return getConverter().convert(new MyEntityConverterRequest(getProvider().findByName(name).orElseThrow(() ->
                new MyEntityNotFoundException(this.getClass(),
                        "No MyEntity with name '" + name + "' found on the system."))));
    }
}
