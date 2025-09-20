package com.abdulmunim.apik8s.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Priority {
    LOW,
    MEDIUM,
    HIGH,
    URGENT;

    @JsonCreator
    public static Priority fromString(String value) {
        if (value != null) {
            for (Priority priority : Priority.values()) {
                if (priority.name().equalsIgnoreCase(value)) {
                    return priority;
                }
            }
            // Log invalid value for debugging
            System.out.println("Invalid priority value: " + value + ", defaulting to LOW");
        }
        return LOW; // Default value
    }

    @JsonValue
    public String getValue() {
        return this.name().toLowerCase();
    }
}
