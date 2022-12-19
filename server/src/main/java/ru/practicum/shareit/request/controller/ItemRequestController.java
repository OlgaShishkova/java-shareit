package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDtoInput add(@RequestHeader("X-Sharer-User-Id") Long userId,
                                   @RequestBody ItemRequestDtoInput requestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toRequest(requestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.getRequestor().setId(userId);
        return ItemRequestMapper.toRequestDtoInput(itemRequestService.add(itemRequest));
    }

    @GetMapping
    public List<ItemRequestDtoWithItems> getByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemRequestService.findByUserId(userId);
    }

    @GetMapping("{requestId}")
    public ItemRequestDtoWithItems getByRequestId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @PathVariable Long requestId) {
        return itemRequestService.findByRequestId(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDtoWithItems> getAll(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                @RequestParam(required = false, defaultValue = "0") Integer from,
                                                @RequestParam(required = false, defaultValue = "10") Integer size) {
        return itemRequestService.findAll(userId, from, size);
    }
}
