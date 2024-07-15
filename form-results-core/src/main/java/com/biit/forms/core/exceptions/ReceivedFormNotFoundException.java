package com.biit.forms.core.exceptions;

import com.biit.logger.ExceptionType;
import com.biit.server.exceptions.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ReceivedFormNotFoundException extends NotFoundException {
    private static final long serialVersionUID = 7132994111678894371L;

    public ReceivedFormNotFoundException(Class<?> clazz, String message, ExceptionType type) {
        super(clazz, message, type);
    }

    public ReceivedFormNotFoundException(Class<?> clazz, String message) {
        super(clazz, message);
    }

    public ReceivedFormNotFoundException(Class<?> clazz) {
        this(clazz, "MyEntity not found");
    }

    public ReceivedFormNotFoundException(Class<?> clazz, Throwable e) {
        super(clazz, e);
    }
}
