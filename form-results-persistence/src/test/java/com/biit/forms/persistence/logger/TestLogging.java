package com.biit.forms.persistence.logger;

import com.biit.forms.logger.BasicLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestLogging extends BasicLogger {

    private static Logger logger = LoggerFactory.getLogger(TestLogging.class);

    public static void info(String className, String message) {
        info(logger, className, message);
    }

    public static void info(String message) {
        info(logger, message);
    }

    public static void debug(String className, String message) {
        debug(logger, className, message);
    }

    public static void debug(String message) {
        debug(logger, message);
    }

    public static void severe(String className, String message) {
        severe(logger, className, message);
    }

    public static void errorMessage(String className, Throwable throwable) {
        errorMessageNotification(logger, className, getStackTrace(throwable));
    }

    public static void errorMessage(Class<?> clazz, Throwable throwable) {
        errorMessageNotification(logger, clazz.getName(), getStackTrace(throwable));
    }

    public static void errorMessage(String className, String error) {
        errorMessageNotification(logger, className, error);
    }

    public static void errorMessage(String error) {
        errorMessageNotification(logger, error);
    }

    public static void errorMessage(Object object, Throwable throwable) {
        errorMessageNotification(logger, object.getClass().getName(), getStackTrace(throwable));
    }
}
