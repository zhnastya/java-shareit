package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;


    private final User user = User.builder()
            .id(1)
            .name("username")
            .email("my@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1)
            .name("item name")
            .description("description")
            .owner(user)
            .available(true)
            .build();


    @Test
    public void createItemWhenItemIsValid() throws Exception {
        int userId = 1;
        ItemFullDto itemDtoToCreate = ItemFullDto.builder()
                .description("description")
                .name("item name")
                .available(true)
                .build();

        when(itemService.createItem(userId, itemDtoToCreate)).thenReturn(ItemMapper.mapperToDto(item));

        String result = mockMvc.perform(post("/items")
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemFullDto resultItemDto = objectMapper.readValue(result, ItemFullDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }


    @Test
    public void updateWhenItemIsValidShouldReturnStatusIsOk() throws Exception {
        int itemId = 0;
        int userId = 0;
        ItemFullDto itemDtoToCreate = ItemFullDto.builder()
                .description("some item description")
                .name("some item name")
                .available(true)
                .build();

        when(itemService.updateItem(userId, itemId, itemDtoToCreate)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        ItemFullDto resultItemDto = objectMapper.readValue(result, ItemFullDto.class);
        assertEquals(itemDtoToCreate.getDescription(), resultItemDto.getDescription());
        assertEquals(itemDtoToCreate.getName(), resultItemDto.getName());
        assertEquals(itemDtoToCreate.getAvailable(), resultItemDto.getAvailable());
    }

    @Test
    public void getShouldReturnStatusOk() throws Exception {
        int itemId = 0;
        int userId = 0;
        ItemFullDto itemDtoToCreate = ItemFullDto.builder()
                .id(itemId)
                .description("")
                .name("")
                .available(true)
                .build();

        when(itemService.getByItemId(userId, itemId)).thenReturn(itemDtoToCreate);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/{itemId}", itemId)
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(itemDtoToCreate)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDtoToCreate), result);
    }

    @Test
    public void getAllShouldReturnStatusOk() throws Exception {
        int userId = 0;
        int from = 0;
        int size = 10;
        List<ItemFullDto> itemsDtoToExpect = List.of(ItemFullDto.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemService.getAllByUser(userId)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items", from, size)
                        .header("X-Sharer-User-Id", userId))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }

    @Test
    public void searchItemsShouldReturnStatusOk() throws Exception {
        int userId = 0;
        int from = 0;
        int size = 10;
        String text = "find";
        List<ItemFullDto> itemsDtoToExpect = List.of(ItemFullDto.builder()
                .name("some item name")
                .description("some item description")
                .available(true)
                .build());

        when(itemService.getByName(userId, text)).thenReturn(itemsDtoToExpect);

        String result = mockMvc.perform(MockMvcRequestBuilders.get("/items/search", from, size)
                        .header("X-Sharer-User-Id", userId)
                        .param("text", text))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemsDtoToExpect), result);
    }


    @Test
    public void createCommentWhenCommentIsValidShouldReturnStatusIsOk() throws Exception {
        CommentDto commentToAdd = CommentDto.builder()
                .text("some comment")
                .build();
        CommentDto commentDtoOut = CommentDto.builder()
                .id(1)
                .itemId(item.getId())
                .text(commentToAdd.getText())
                .build();
        when(itemService.saveComment(user.getId(), item.getId(), commentToAdd)).thenReturn(commentDtoOut);

        String result = mockMvc.perform(post("/items/{itemId}/comment", item.getId())
                        .contentType("application/json")
                        .header("X-Sharer-User-Id", user.getId())
                        .content(objectMapper.writeValueAsString(commentToAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(commentDtoOut), result);
    }
}
