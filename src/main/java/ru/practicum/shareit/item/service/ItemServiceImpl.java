package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item add(Item item) {
        checkIfUserExists(item.getOwner().getId());
        return itemRepository.save(item);
    }

    @Override
    public Item update(Long userId, Item item) {
        checkIfUserExists(userId);
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
        return itemRepository.save(itemToUpdate);
    }

    @Override
    public List<ItemDtoWithBookings> findByUserId(Long userId) {
        checkIfUserExists(userId);
        List<Item> items = itemRepository.findAllByOwnerId(userId);
        List<ItemDtoWithBookings> itemsWithBookings = new ArrayList<>();
        LocalDateTime currentTime = LocalDateTime.now();
        for (Item item : items) {
            ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
            List<Booking> bookings = bookingRepository.findAllByItemId(item.getId());
            findNearestBookings(itemDtoWithBookings, currentTime, bookings);
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
        checkIfUserExists(userId);
        Item item = findByItemId(itemId);
        ItemDtoWithBookings itemDtoWithBookings = ItemMapper.toItemDtoWithBookings(item);
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

    @Override
    public void delete(Long userId, Long itemId) {
        checkIfUserExists(userId);
        itemRepository.deleteById(itemId);
    }

    private void checkIfUserExists(Long userId) {
        if (userRepository.findById(userId).isEmpty()) {
            throw new UserNotFoundException("Пользователь не найден");
        }
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
}
