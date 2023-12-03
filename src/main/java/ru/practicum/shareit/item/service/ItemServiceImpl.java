package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToModel;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDto createItem(int userId, ItemDto item) {
        User user = userService.getByIdModel(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        Item item1 = mapperToModel(item);
        item1.setOwner(user);
        item1.setBookings(new ArrayList<>());
        repository.save(item1);
        return mapperToDto(userId, item1);
    }

    @Transactional
    @Override
    public CommentDto saveComment(int userId, int itemId, CommentDto commentDto) {
        User user = userService.getByIdModel(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        try {
            Item item = repository.getReferenceById(itemId);
            if (item.getOwner().equals(user)) {
                throw new NotFoundException("Пользователь - " + userId + " не может оставлять комментарии на вещь - " + itemId);
            }
            if (!item.getBookings().isEmpty() && !repository.findCustomStoryBookers(itemId, LocalDateTime.now(),
                    Status.APPROVED).contains(user)) {
                throw new BookingException("У пользователя id - " + userId + " нет завершенных бронирований");
            }
            Comment comment = commentRepository.save(CommentMapper.dtoToComment(commentDto));
            item.saveComments(comment);
            user.saveComment(comment);
            return CommentMapper.commentToDto(comment);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Вещь с id - " + itemId + "не найдена");
        }
    }

    @Transactional
    @Override
    public ItemDto updateItem(int userId, int id, ItemDto item) {
        User owner = userService.getByIdModel(userId).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        try {
            Item itemUpdate = repository.getReferenceById(id);
            if (itemUpdate.getOwner() != owner) {
                throw new NotFoundException("Пользователь - " + userId + "не является владельцем вещи - " + id);
            }
            if (item.getAvailable() != null) itemUpdate.setAvailable(item.getAvailable());
            if (item.getDescription() != null) itemUpdate.setDescription(item.getDescription());
            if (item.getName() != null) itemUpdate.setName(item.getName());
            return mapperToDto(userId, itemUpdate);

        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Вещь с id - " + id + "не найдена");
        }
    }

    @Override
    public ItemDto getByItemId(int userId, int id) {
        try {
            return mapperToDto(userId, repository.getReferenceById(id));
        } catch (EntityNotFoundException e) {
            throw new NotFoundException("Вещь с id - " + id + "не найдена");
        }
    }

    @Override
    public List<ItemDto> getAllByUser(int userId) {
        userService.getById(userId);
        return repository.findByOwnerIdOrderById(userId).stream()
                .map(x -> mapperToDto(userId, x))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByName(int userId, String name) {
        userService.getById(userId);
        if (name.isEmpty()) return new ArrayList<>();
        return repository.findItemByAvailableAndQueryContainWithIgnoreCase(name).stream()
                .map(x -> mapperToDto(userId, x))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Item> getItemForBooking(int id) {
        return repository.findById(id);
    }
}