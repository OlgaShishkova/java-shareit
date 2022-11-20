package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotAuthorisedRequestException;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    @Transactional
    @Override
    public Item add(Item item) {
        userService.findById(item.getOwner().getId());
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Long userId, Item item) {
        userService.findById(userId);
        Item itemToUpdate = itemRepository.findById(item.getId()).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
        if (!Objects.equals(itemToUpdate.getOwner().getId(), userId)) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (item.getName() != null && !item.getName().isBlank()) {
            itemToUpdate.setName(item.getName());
        }
        if (item.getDescription() != null && !item.getDescription().isBlank()) {
            itemToUpdate.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.setAvailable(item.getAvailable());
        }
        return itemToUpdate;
    }

    @Override
    public List<ItemDtoWithBookings> findByUserId(Long userId) {
        userService.findById(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDtoWithBookings> itemsWithBookings = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        List<Comment> comments = commentRepository.findAllByItemIn(items);
        List<Booking> bookings = bookingRepository.findAllByItemIn(items);
        for (Item item : items) {
            ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
            List<Comment> commentsForItem = comments.stream()
                    .filter(comment -> item.getId().equals(comment.getItem().getId()))
                    .collect(Collectors.toList());
            itemDtoWithBookings.setComments(CommentMapper.toCommentDto(commentsForItem));
            List<Booking> bookingsForItem = bookings.stream()
                    .filter(booking -> item.getId().equals(booking.getItem().getId()))
                    .collect(Collectors.toList());
            findNearestBookings(itemDtoWithBookings, currentTime, bookingsForItem);
            itemsWithBookings.add(itemDtoWithBookings);
        }
        return itemsWithBookings;
    }

    @Override
    public Item findByItemId(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
    }

    @Override
    public ItemDtoWithBookings findByItemId(Long userId, Long itemId) {
        userService.findById(userId);
        Item item = findByItemId(itemId);
        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
        List<Comment> comments = commentRepository.findCommentsByItemId(itemId);
        itemDtoWithBookings.setComments(CommentMapper.toCommentDto(comments));
        if (Objects.equals(item.getOwner().getId(), userId)) {
            LocalDateTime currentTime = LocalDateTime.now();
            List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
            findNearestBookings(itemDtoWithBookings, currentTime, bookings);
        }
        return itemDtoWithBookings;
    }

    @Override
    public List<Item> search(String text) {
        return itemRepository.search(text);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long itemId) {
        userService.findById(userId);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public Comment add(Long userId, Long itemId, Comment comment) {
        User author = userService.findById(userId);
        checkIfItemWasBookedByUser(itemId, userId);
        comment.setItem(findByItemId(itemId));
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void findNearestBookings(ItemDtoWithBookings itemDtoWithBookings, LocalDateTime currentTime, List<Booking> bookings) {
        if (bookings.size() > 0) {
            Optional<Booking> lastBooking = bookings.stream()
                    .filter(booking -> currentTime.isAfter(booking.getEnd()))
                    .min(Collections.reverseOrder());
            Optional<Booking> nextBooking = bookings.stream()
                    .filter(booking -> currentTime.isBefore(booking.getStart()))
                    .sorted()
                    .findFirst();
            lastBooking.ifPresent(booking -> itemDtoWithBookings.setLastBooking(BookingMapper.toBookingDtoForItem(booking)));
            nextBooking.ifPresent(booking -> itemDtoWithBookings.setNextBooking(BookingMapper.toBookingDtoForItem(booking)));
        }
    }

    private void checkIfItemWasBookedByUser(Long itemId, Long userId) {
        List<Booking> bookings = bookingRepository.findAllByItemId(itemId);
        Optional<Booking> anyBooking = bookings.stream()
                .filter(booking -> userId.equals(booking.getBooker().getId()))
                .filter(booking -> LocalDateTime.now().isAfter(booking.getEnd()))
                .findAny();
        if (anyBooking.isEmpty()) {
            throw new NotAuthorisedRequestException("Невозможно оставить отзыв");
        }
    }
}
