package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return ItemMapper.toItemDto(itemService.getByUserId(userId));
    }

    @GetMapping("{itemId}")
    public ItemDto getByItemId(@PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.getByItemId(itemId));
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDto(itemService.search(text));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setUserId(userId);
        return ItemMapper.toItemDto(itemService.add(item));
    }

    @PatchMapping("{itemId}")
    public ItemDto update(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto,
                          @PathVariable Long itemId) {
        Item item = ItemMapper.toItem(itemDto);
        item.setId(itemId);
        return ItemMapper.toItemDto(itemService.update(userId, item));
    }

    @DeleteMapping("{itemId}")
    public void deleteItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }
}
