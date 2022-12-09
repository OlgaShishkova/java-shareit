package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final ItemRequestService itemRequestService;

    @Transactional
    @Override
    public Item add(Item item) {
        User owner = userService.findById(item.getOwner().getId());
        item.setOwner(owner);
        if (item.getRequest() != null) {
            ItemRequest itemRequest = itemRequestService.findByRequestId(item.getRequest().getId());
            item.setRequest(itemRequest);
        }
        return itemRepository.save(item);
    }

    @Transactional
    @Override
    public Item update(Long userId, Item item) {
        userService.findById(userId);
        Item itemToUpdate = findByItemId(item.getId());
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
    public List<ItemDtoWithBookings> findByUserId(Long userId, Integer from, Integer size) {
        userService.findById(userId);
        Pageable itemPage = getPageable(from, size);
        List<Item> items = itemRepository.findAllByOwnerId(userId, itemPage);
        List<ItemDtoWithBookings> itemsWithBookings = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        Map<Item, List<Booking>> bookingsByItem =
                bookingRepository.findAllByItemIn(items)
                        .stream()
                        .collect(groupingBy(Booking::getItem, toList()));
        Map<Item, List<Comment>> commentsByItem =
                commentRepository.findAllByItemIn(items)
                        .stream()
                        .collect(groupingBy(Comment::getItem, toList()));
        for (Item item : items) {
            ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
            itemDtoWithBookings.setComments(CommentMapper.toCommentDto(
                    commentsByItem.getOrDefault(item, Collections.emptyList())));
            findNearestBookings(itemDtoWithBookings, currentTime,
                    bookingsByItem.getOrDefault(item, Collections.emptyList()));
            itemsWithBookings.add(itemDtoWithBookings);
        }
        return itemsWithBookings;
    }

    private static Pageable getPageable(Integer from, Integer size) {
        int page = from / size;
        return PageRequest.of(page, size);
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
    public List<Item> search(String text, Integer from, Integer size) {
        Pageable itemPage = getPageable(from, size);
        return itemRepository.search(text, itemPage);
    }

    @Transactional
    @Override
    public void delete(Long userId, Long itemId) {
        userService.findById(userId);
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public Comment addComment(Long userId, Long itemId, Comment comment) {
        User author = userService.findById(userId);
        checkIfItemWasBookedByUser(itemId, userId);
        comment.setItem(findByItemId(itemId));
        comment.setAuthor(author);
        comment.setCreated(LocalDateTime.now());
        return commentRepository.save(comment);
    }

    private void findNearestBookings(ItemDtoWithBookings itemDtoWithBookings,
                                     LocalDateTime currentTime, List<Booking> bookings) {
        if (bookings.size() > 0) {
            Optional<Booking> lastBooking = bookings.stream()
                    .filter(booking -> currentTime.isAfter(booking.getEnd()))
                    .min((Booking b1, Booking b2) -> b2.getEnd().compareTo(b1.getEnd()));
            Optional<Booking> nextBooking = bookings.stream()
                    .filter(booking -> currentTime.isBefore(booking.getStart()))
                    .min(Comparator.comparing(Booking::getStart));
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
