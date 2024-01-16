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
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;
import static ru.practicum.shareit.item.mapper.ItemMapper.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final UserRepository userRepository;
    private final ItemRepository repository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final RequestRepository requestRepository;

    private ItemFullDto setBookings(ItemFullDto dto, List<Booking> bookings) {
        LocalDateTime dateTime = LocalDateTime.now();
        if (bookings == null) return dto;
        Booking last = bookings.stream()
                .filter(x -> x.getEnd().isBefore(dateTime)
                        || x.getStart().isBefore(dateTime)
                        && x.getEnd().isAfter(dateTime))
                .max(Comparator.comparing(Booking::getEnd))
                .orElse(null);
        Booking next = bookings.stream()
                .filter(x -> x.getStart().isAfter(dateTime))
                .min(Comparator.comparing(Booking::getStart))
                .orElse(null);
        dto.setLastBooking(bookingDtoForItem(last));
        dto.setNextBooking(bookingDtoForItem(next));
        return dto;
    }

    private ItemFullDto setComments(ItemFullDto dto, List<Comment> comments) {
        if (comments == null) {
            dto.setComments(new ArrayList<>());
            return dto;
        }
        dto.setComments(comments
                .stream()
                .map(CommentMapper::commentToDto)
                .collect(Collectors.toList()));
        return dto;
    }

    @Transactional
    @Override
    public ItemFullDto createItem(int userId, ItemFullDto item) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        Item item1 = mapperToModel(item);
        if (item.getRequestId() != null) {
            ItemRequest request = requestRepository.findById(item.getRequestId())
                    .orElseThrow(() -> new NotFoundException("Запрос с id - " + item.getRequestId() + " не найден"));
            item1.setRequest(request);
        }
        item1.setOwner(user);
        return mapperToDto(repository.save(item1));
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
        comment.setItem(item);
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

        return mapperToDto(itemUpdate);
    }

    @Transactional(readOnly = true)
    @Override
    public ItemFullDto getByItemId(int userId, int id) {
        Item item = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь с id - " + id + "не найдена"));
        ItemFullDto dto = setComments(mapperToDto(item), commentRepository.findAllByItem_Id(id));
        if (item.getOwner().getId() == userId) {
            return setBookings(dto, bookingRepository.findAllByStatusAndItem_Id(Status.APPROVED, id));
        }
        return dto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemFullDto> getAllByUser(int userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        List<Item> items = repository.findByOwnerIdOrderById(userId);
        List<Integer> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Integer, List<Comment>> comments = commentRepository.findAllByItem_IdIn(itemsIds)
                .stream()
                .collect(groupingBy(x -> x.getItem().getId()));
        Map<Integer, List<Booking>> bookings = bookingRepository.findAllByStatusAndItem_IdIn(Status.APPROVED, itemsIds)
                .stream()
                .collect(groupingBy(x -> x.getItem().getId()));

        return items.stream()
                .map(ItemMapper::mapperToDto)
                .peek(x -> {
                    setComments(x, comments.get(x.getId()));
                    setBookings(x, bookings.get(x.getId()));
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemFullDto> getByName(int userId, String name) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь id - " + userId + " не найден"));
        if (name.isEmpty()) return new ArrayList<>();
        List<Item> items = repository.findItemByAvailableAndQueryContainWithIgnoreCase(name);
        List<Integer> itemsIds = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        List<Integer> itemForOwner = items.stream()
                .filter(x -> x.getOwner().getId() == userId)
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Integer, List<Comment>> comments = commentRepository.findAllByItem_IdIn(itemsIds)
                .stream()
                .collect(groupingBy(x -> x.getItem().getId()));
        Map<Integer, List<Booking>> bookings = bookingRepository.findAllByStatusAndItem_IdIn(Status.APPROVED, itemsIds)
                .stream()
                .collect(groupingBy(x -> x.getItem().getId()));
        return items.stream()
                .map(ItemMapper::mapperToDto)
                .peek(x -> {
                    setComments(x, comments.get(x.getId()));
                    if (itemForOwner.contains(x.getId())) {
                        setBookings(x, bookings.get(x.getId()));
                    }
                })
                .collect(Collectors.toList());
    }
}