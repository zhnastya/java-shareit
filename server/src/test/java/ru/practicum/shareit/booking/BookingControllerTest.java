package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.enums.SortField;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class BookingControllerTest {

    private final String authenticationHeader = "X-Sharer-User-Id";

    private final ItemFullDto itemDto = ItemFullDto.builder()
            .id(1)
            .name("Name")
            .description("Desc")
            .available(true)
            .build();
    private final User booker = User.builder()
            .id(1)
            .name("name")
            .email("email@email.com")
            .build();

    private Booking booking;

    private LocalDateTime testTime;

    private BookingRequestDto bookingDto;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private BookingServiceImpl bookingService;

    @Autowired
    private MockMvc mvc;

    @BeforeEach
    void setData() {
        testTime = LocalDateTime.now();
        bookingDto = BookingRequestDto.builder()
                .id(1)
                .itemId(itemDto.getId())
                .start(testTime.plusSeconds(1))
                .end(testTime.plusSeconds(12))
                .build();
        booking = BookingMapper.dtoToBooking(bookingDto);
        booking.setId(1);
        booking.setBooker(booker);
        booking.setItem(new Item(1, "name", "desc", true, null, null));
        booking.setStatus(Status.APPROVED);
    }

    @Test
    void add() throws Exception {

        when(bookingService.saveBooking(anyInt(), eq(bookingDto)))
                .thenReturn(BookingMapper.bookingToDto(booking));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, 3)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(booker.getId())));
    }

    @Test
    void approve() throws Exception {
        when(bookingService.updateStatus(anyInt(), anyInt(), anyBoolean()))
                .thenReturn(BookingMapper.bookingToDto(booking));

        mvc.perform(patch("/bookings/1")
                        .param("approved", "true")
                        .content(mapper.writeValueAsString(bookingDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(authenticationHeader, 3L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.status", is(booking.getStatus().toString())));
    }

    @Test
    void getTest() throws Exception {
        when(bookingService.getBooking(anyInt(), anyInt()))
                .thenReturn(BookingMapper.bookingToDto(booking));

        mvc.perform(get("/bookings/1")
                        .header(authenticationHeader, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.booker.id", is(booker.getId())));
    }

    @Test
    void getAllByBooker() throws Exception {
        when(bookingService.getSorted(anyInt(), eq(SortField.ALL), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.bookingToDto(booking)));

        mvc.perform(get("/bookings?from=0&size=5")
                        .param("state", "ALL")
                        .header(authenticationHeader, 3))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())));
    }

    @Test
    void getAllByOwner() throws Exception {
        when(bookingService.getSortedByOwner(anyInt(), eq(SortField.ALL), any(Pageable.class)))
                .thenReturn(List.of(BookingMapper.bookingToDto(booking)));

        mvc.perform(get("/bookings/owner?from=0&size=5")
                        .param("state", "ALL")
                        .header(authenticationHeader, 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id", is(booking.getId()), Integer.class))
                .andExpect(jsonPath("$.[0].status", is(booking.getStatus().toString())));
    }
}