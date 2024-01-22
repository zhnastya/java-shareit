package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class UserControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void createUserWhenUserIsValid() throws Exception {
        UserDto userDtoToCreate = UserDto.builder()
                .email("email@email.com")
                .name("name")
                .build();

        when(userService.saveUser(userDtoToCreate)).thenReturn(userDtoToCreate);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToCreate)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToCreate), result);
    }


    @Test
    void updateUserWhenUserIsValid() throws Exception {
        int userId = 0;
        UserDto userDtoToUpdate = UserDto.builder()
                .email("update@update.com")
                .name("update")
                .build();

        when(userService.updateUser(userId, userDtoToUpdate)).thenReturn(userDtoToUpdate);

        String result = mockMvc.perform(patch("/users/{userId}", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDtoToUpdate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDtoToUpdate), result);
    }


    @Test
    void get() throws Exception {
        int userId = 1;
        when(userService.getById(anyInt())).thenReturn(new UserDto(1, "name", "email@email.ru"));

        mockMvc.perform(MockMvcRequestBuilders.get("/users/{userId}", userId))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getById(userId);
    }

    @Test
    void findAll() throws Exception {
        List<UserDto> usersDtoToExpect = List.of(UserDto.builder()
                .name("name")
                .email("email@email.com")
                .build());

        when(userService.getAll()).thenReturn(usersDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(usersDtoToExpect), result);
    }

    @Test
    void delete() throws Exception {
        int userId = 0;

        mockMvc.perform(MockMvcRequestBuilders.delete("/users/{id}", userId))
                .andExpect(status().isNoContent());

        verify(userService, times(1)).deleteUser(userId);
    }
}
