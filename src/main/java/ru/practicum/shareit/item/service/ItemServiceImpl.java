package ru.practicum.shareit.item.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.booking.exeptions.BookingException;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemFullDto;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToDto;
import static ru.practicum.shareit.item.mapper.ItemMapper.mapperToModel;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    public ItemFullDto createItem(int userId, ItemFullDto item) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        Item item1 = mapperToModel(item);
        item1.setOwner(user);
        item1.setBookings(new ArrayList<>());
        repository.save(item1);
        return mapperToDto(userId, item1);
    }

    @Transactional
    @Override
    public CommentDto saveComment(int userId, int itemId, CommentDto commentDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + itemId + "не найдена"));
        List<Booking> bookings = bookingRepository.findAllByBookerAndItemAndStatus(user, item, Status.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new BookingException("У пользователя id - " + userId + " нет завершенных бронирований");
        }
        Comment comment = commentRepository.save(CommentMapper.dtoToComment(commentDto));
        item.saveComment(comment);
        user.saveComment(comment);
        return CommentMapper.commentToDto(comment);
    }

    @Transactional
    @Override
    public ItemFullDto updateItem(int userId, int id, ItemFullDto item) {
        User owner = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        Item itemUpdate = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + id + "не найдена"));
        if (itemUpdate.getOwner() != owner) {
            throw new NotFoundException("Пользователь - " + userId + "не является владельцем вещи - " + id);
        }
        if (item.getAvailable() != null) itemUpdate.setAvailable(item.getAvailable());
        if (item.getDescription() != null) itemUpdate.setDescription(item.getDescription());
        if (item.getName() != null) itemUpdate.setName(item.getName());
        return mapperToDto(userId, itemUpdate);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemFullDto getByItemId(int userId, int id) {
        return mapperToDto(userId, repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + id + "не найдена")));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemFullDto> getAllByUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        return repository.findByOwnerIdOrderById(userId).stream()
                .map(x -> mapperToDto(userId, x))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemFullDto> getByName(int userId, String name) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        if (name.isEmpty()) return new ArrayList<>();
        return repository.findItemByAvailableAndQueryContainWithIgnoreCase(name).stream()
                .map(x -> mapperToDto(userId, x))
                .collect(Collectors.toList());
    }
}