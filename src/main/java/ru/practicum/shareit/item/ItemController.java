package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDtoWithBookings> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                 @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                                 @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemService.findByUserId(userId, from, size);
    }

    @GetMapping("{itemId}")
    public ItemDtoWithBookings getByItemId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long itemId) {
        return itemService.findByItemId(userId, itemId);
    }

    @GetMapping("search")
    public List<ItemDto> search(@RequestParam String text,
                                @PositiveOrZero @RequestParam(required = false, defaultValue = "0") Integer from,
                                @Positive @RequestParam(required = false, defaultValue = "10") Integer size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return ItemMapper.toItemDto(itemService.search(text, from, size));
    }

    @PostMapping
    public ItemDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                    @Valid @RequestBody ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.getOwner().setId(userId);
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

    @PostMapping("{itemId}/comment")
    public CommentDto add(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody CommentDto commentDto,
                          @PathVariable Long itemId) {
        return CommentMapper.toCommentDto(itemService.addComment(userId, itemId, CommentMapper.toComment(commentDto)));
    }

    @DeleteMapping("{itemId}")
    public void delete(@RequestHeader("X-Sharer-User-Id") Long userId,
                           @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }
}
