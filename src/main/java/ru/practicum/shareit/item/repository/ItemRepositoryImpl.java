package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, List<Item>> items = new HashMap<>();
    private final Map<Long, Item> itemById = new HashMap<>();

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
        itemById.put(item.getUserId(), item);
        return item;
    }

    @Override
    public Item update(Long userId, Item item) {
        List<Item> userItems = getByUserId(userId);
        Item itemToUpdate = userItems.stream()
                .filter(it -> it.getId().equals(item.getId()))
                .findFirst()
                .orElseThrow(() ->
                        new ItemNotFoundException("Вещь не найдена"));
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
    public List<Item> getByUserId(Long userId) {
        return items.getOrDefault(userId, Collections.emptyList());
    }

    @Override
    public Optional<Item> getByItemId(Long itemId) {
        return Optional.ofNullable(itemById.get(itemId));
    }

    @Override
    public List<Item> search(String text) {
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
            itemById.remove(itemId);
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
