package com.danni.BoardCard.persistence.entity;

import java.util.Arrays;
import java.util.NoSuchElementException;

public enum BoardColumnKindEnum {
    INITIAL, FINAL, CANCEL, PENDING;

    public static BoardColumnKindEnum findByName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        return Arrays.stream(values())
                .filter(b -> b.name().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException("No enum constant with name: " + name));
    }
}