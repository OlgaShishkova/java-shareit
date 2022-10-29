package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();

    @Override
    public Item add(Item item) {
        item.setId(getId());
        items.compute(item.getUserId(), (userId, userItems) -> {
            if (userItems == null) {
                userItems = new ArrayList<>();
            }
            userItems.add(item);
            return userItems;
        });
        return item;
    }

    @Override
    public Item update(Long userId, Item item) {
        List<Item> userItems = getByUserId(userId);
        Optional<Item> itemToUpdate = userItems.stream()
                .filter(it -> it.getId().equals(item.getId()))
                .findFirst();
        if (itemToUpdate.isEmpty()) {
            throw new ItemNotFoundException("Вещь не найдена");
        }
        if (item.getName() != null) {
            itemToUpdate.get().setName(item.getName());
        }
        if (item.getDescription() != null) {
            itemToUpdate.get().setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemToUpdate.get().setAvailable(item.getAvailable());
        }
        return itemToUpdate.get();
    }

    @Override
    public List<Item> getByUserId(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> getByItemId(Long itemId) {
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getId().equals(itemId))
                .findFirst();
    }

    @Override
    public List<Item> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        String textLowerCase = text.toLowerCase();
        return items.values().stream()
                .flatMap(Collection::stream)
                .filter(item -> (item.getName().toLowerCase().contains(textLowerCase) ||
                        item.getDescription().toLowerCase().contains(textLowerCase)) &&
                        item.getAvailable().equals(true))
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId, Long itemId) {
        if (items.containsKey(userId)) {
            List<Item> userItems = items.get(userId);
            userItems.removeIf(item -> item.getId().equals(itemId));
        }
    }

    private long getId() {
        long lastId = items.values().stream()
                .flatMap(Collection::stream)
                .mapToLong(Item::getId)
                .max()
                .orElse(0);
        return lastId + 1;
    }
}
