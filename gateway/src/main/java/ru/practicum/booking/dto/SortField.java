package ru.practicum.booking.dto;

import java.util.Optional;

public enum SortField {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    REJECTED,
    WAITING;

    public static Optional<SortField> from(String stringState) {
        for (SortField state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
