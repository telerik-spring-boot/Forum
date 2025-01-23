package com.telerik.forum.exceptions;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(String type, String attribute, String value) {
        super(String.format("%s with %s %s not found", type, attribute, value));
    }

}
