package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequest add(ItemRequest itemRequest) {
        itemRequest.setRequestor(userService.findById(itemRequest.getRequestor().getId()));
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDtoWithItems> findByUserId(Long userId) {
        userService.findById(userId);
        Sort sort = Sort.by(Sort.Direction.DESC, "created");
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorId(userId, sort);
        return insertItems(itemRequests);
    }

    @Override
    public ItemRequest findByRequestId(Long requestId) {
        return itemRequestRepository.findById(requestId).orElseThrow(() ->
                new ItemNotFoundException("Вещь не найдена"));
    }

    @Override
    public ItemRequestDtoWithItems findByRequestId(Long userId, Long requestId) {
        userService.findById(userId);
        ItemRequest itemRequest = findByRequestId(requestId);
        ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest);
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());
        itemRequestDtoWithItems.setItems(ItemMapper.toItemDto(items));
        return itemRequestDtoWithItems;
    }

    @Override
    public List<ItemRequestDtoWithItems> findAll(Long userId, int from, int size) {
        int page = from / size;
        Pageable itemRequestsPage = PageRequest.of(page, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorIdNot(userId, itemRequestsPage);
        return insertItems(itemRequests);
    }

    private List<ItemRequestDtoWithItems> insertItems(List<ItemRequest> itemRequests) {
        List<ItemRequestDtoWithItems> itemRequestsDtoWithItems = new ArrayList<>();
        Map<ItemRequest, List<Item>> itemsByItemRequest = itemRepository.findAllByRequestIn(itemRequests)
                .stream()
                .collect(groupingBy(Item::getRequest, toList()));
        for (ItemRequest itemRequest : itemRequests) {
            ItemRequestDtoWithItems itemRequestDtoWithItems = ItemRequestMapper.toItemRequestDtoWithItems(itemRequest);
            itemRequestDtoWithItems.setItems(ItemMapper.toItemDto(
                    itemsByItemRequest.getOrDefault(itemRequest, Collections.emptyList())));
            itemRequestsDtoWithItems.add(itemRequestDtoWithItems);
        }
        return itemRequestsDtoWithItems;
    }
}
