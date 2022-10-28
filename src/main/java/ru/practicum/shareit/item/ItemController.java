package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemServiceImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemServiceImpl itemService;

    @GetMapping
    public List<ItemDto> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getByUserId(userId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("{itemId}")
    public ItemDto getByItemId(@PathVariable Long itemId) {
        return ItemMapper.toItemDto(itemService.getByItemId(itemId));
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text) {
        return itemService.search(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
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
