package ru.practicum.shareit.error;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.exception.controller.ErrorHandler;
import ru.practicum.shareit.item.dto.ItemFullDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ErrorHandler.class)
public class ErrorControllerTest {
    private final String authenticationHeader = "X-Sharer-User-Id";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    @SneakyThrows
    void handleValidationException() {
        BookingRequestDto dto = BookingRequestDto.builder()
                .itemId(1)
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now())
                .build();

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void methodArgumentNotValidException() {
        ItemFullDto dto = ItemFullDto.builder()
                .name("name")
                .available(true)
                .build();

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(dto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @SneakyThrows
    void notFoundException() {
        mvc.perform(get("/bookings/10")
                        .header(authenticationHeader, 3))
                .andExpect(status().isNotFound());
    }
}
