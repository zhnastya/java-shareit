package ru.practicum.shareit.booking.enums;

import java.util.Optional;

public enum SortField {
    WAITING,
    APPROVED,
    REJECTED,
    PAST,
    CURRENT,
    FUTURE,
    ALL;

    public static Optional<SortField> from(String stringState) {
        for (SortField state : values()) {
            if (state.name().equalsIgnoreCase(stringState)) {
                return Optional.of(state);
            }
        }
        return Optional.empty();
    }
}
