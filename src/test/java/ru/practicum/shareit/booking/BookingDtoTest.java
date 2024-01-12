package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@JsonTest
class BookingDtoTest {

    @Autowired
    private JacksonTester<BookingRequestDto> json;

    private static final String DATE_TIME = "2023-07-23T07:33:00";

    private BookingRequestDto bookingDto = null;

    @BeforeEach
    public void init() {
        bookingDto = BookingRequestDto.builder()
                .itemId(1)
                .start(LocalDateTime.parse("2023-07-23T07:33:00"))
                .end(LocalDateTime.parse("2023-07-23T07:33:00"))
                .build();
    }

    @Test
    public void startSerializes() throws Exception {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.start")
                .isEqualTo(DATE_TIME);
    }

    @Test
    public void endSerializes() throws Exception {
        assertThat(json.write(bookingDto)).extractingJsonPathStringValue("$.end")
                .isEqualTo(DATE_TIME);
    }
}
