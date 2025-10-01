package com.abdulmunim.apik8s.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {

    PENDING,
    IN_PROGRESS,
    COMPLETED;

    @JsonCreator
    public static Status fromString(String value) {
        if (value != null) {
            for (Status status : Status.values()) {
                if (status.name().equalsIgnoreCase(value)) {
                    return status;
                }
            }
        }
        return PENDING; // Default value
    }

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
