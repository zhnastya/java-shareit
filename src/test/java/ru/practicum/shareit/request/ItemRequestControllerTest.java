package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.request.controllers.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestService requestService;

    private final User user = User.builder()
            .id(1)
            .name("username")
            .email("email@email.com")
            .build();

    private final ItemRequestDto requestDto = ItemRequestDto.builder()
            .id(1)
            .description("description")
            .created(LocalDateTime.now())
            .items(List.of())
            .build();

    @Test
    void createRequest() throws Exception {
        when(requestService.create(any(), any())).thenReturn(requestDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.post("/requests")
                        .content(objectMapper.writeValueAsString(requestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requestDto), result);
    }

    @Test
    void getUserRequests() throws Exception {
        when(requestService.getAllByUser(user.getId())).thenReturn(List.of(requestDto));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), result);
    }

    @Test
    void getAllRequests() throws Exception {
        int from = 0;
        int size = 10;
        when(requestService.getAll(from, size, user.getId())).thenReturn(List.of(requestDto));

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(requestDto)), result);
    }

    @Test
    void get() throws Exception {
        int requestId = 1;

        when(requestService.getById(user.getId(), requestId)).thenReturn(requestDto);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/requests/{requestId}", requestId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(requestDto), result);
    }
}
