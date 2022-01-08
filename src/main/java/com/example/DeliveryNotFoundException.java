package com.example;

import graphql.*;
import graphql.language.*;

import java.util.*;

/**
 * unfortunately kotlin can not handle extends a class and interface with the same method:
 * Exception.getMessage() and GraphQLError.getMessage(). This is the workaround, writing the Java Class
 */
public class DeliveryNotFoundException extends RuntimeException implements GraphQLError {

    private final String invalidField;

    public DeliveryNotFoundException(String message, String invalidField) {
        super(message);
        this.invalidField = invalidField;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public List<Object> getPath() {
        return null;
    }

    @Override
    public List<SourceLocation> getLocations() {
        return null;
    }

    @Override
    public ErrorClassification getErrorType() {
        return ErrorType.ValidationError;
    }

    @Override
    public Map<String, Object> getExtensions() {
        return Collections.singletonMap("invalidField", invalidField);
    }
}