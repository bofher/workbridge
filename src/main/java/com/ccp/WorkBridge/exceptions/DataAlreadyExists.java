package com.ccp.WorkBridge.exceptions;

public class DataAlreadyExists extends RuntimeException {
    public DataAlreadyExists(String entityName, String uniqueField, Object value) {
        super(entityName + " with " + uniqueField + " '" + value + "' already exists");
    }
}
