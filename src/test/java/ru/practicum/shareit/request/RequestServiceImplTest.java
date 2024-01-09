package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.ItemRequestServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ItemRequestServiceImpl requestService;

    private final User user = User.builder()
            .id(1)
            .name("username")
            .email("email@email.com")
            .build();

    private final UserDto userDto = UserDto.builder()
            .id(1)
            .name("username")
            .email("email@email.com")
            .build();

    private final Item item = Item.builder()
            .id(1)
            .name("item name")
            .description("description")
            .available(true)
            .owner(user)
            .build();

    private final ItemRequest request = ItemRequest.builder()
            .id(1)
            .description("request description")
            .build();

    @Test
    void addNewRequest() {
        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(List.of(), request);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(request);

        ItemRequestDto actualRequestDto = requestService.create(user.getId(), requestDto);

        assertEquals(requestDto, actualRequestDto);
    }

    @Test
    void getUserRequests() {
        List<ItemRequestDto> expectedRequestsDto = List.of(ItemRequestMapper.toItemRequestDto(List.of(ItemMapper.mapperToDto(item)), request));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(List.of(item));
        when(requestRepository.findAllByOwnerOrderByCreatedAsc(any(User.class))).thenReturn(List.of(request));

        List<ItemRequestDto> actualRequestsDto = requestService.getAllByUser(userDto.getId());

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getAllRequests() {
        List<ItemRequestDto> expectedRequestsDto = List.of(ItemRequestMapper.toItemRequestDto(List.of(ItemMapper.mapperToDto(item)), request));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(List.of(item));
        when(requestRepository.findByOwnerIsNotLike(any(User.class), any(Pageable.class)))
                .thenReturn(List.of(request));

        List<ItemRequestDto> actualRequestsDto = requestService.getAll(0, 10, userDto.getId());

        assertEquals(expectedRequestsDto, actualRequestsDto);
    }

    @Test
    void getRequestById() {
        ItemRequestDto expectedRequestDto = ItemRequestMapper.toItemRequestDto(List.of(), request);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        ItemRequestDto actualRequestDto = requestService.getById(userDto.getId(), request.getId());

        assertEquals(expectedRequestDto, actualRequestDto);
    }

    @Test
    void getRequestByIdWhenRequestIdIsNotValidShouldThrowObjectNotFoundException() {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(requestRepository.findById(request.getId())).thenReturn(Optional.empty());

        NotFoundException requestNotFoundException = assertThrows(NotFoundException.class,
                () -> requestService.getById(userDto.getId(), request.getId()));

        assertEquals(requestNotFoundException.getMessage(), "Запрос с id - 1 не найден");
    }
}
