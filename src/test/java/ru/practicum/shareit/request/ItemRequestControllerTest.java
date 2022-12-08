package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    private final LocalDateTime created = LocalDateTime.now();

    private final ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput(
            1L,
            "item request",
            created
    );

    private final ItemRequest itemRequest = new ItemRequest(
            1L,
            "item request",
            new User(1L, "user", "user@email.ru"),
            created
    );

    private final ItemRequestDtoWithItems itemRequestDtoWithItems = new ItemRequestDtoWithItems(
            1L,
            "item request",
            created,
            List.of(new ItemDto(1L, "item", "item description", true, 1L))
    );

    @Test
    void testAdd() throws Exception {
        when(itemRequestService.add(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemRequestDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoInput.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoInput.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestDtoInput.getCreated()
                        .format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void testGetByUserId() throws Exception {
        when(itemRequestService.findByUserId(anyLong()))
                .thenReturn(List.of(itemRequestDtoWithItems));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoWithItems.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoWithItems.getDescription()))
                .andExpect(jsonPath("$[0].created").value(created
                        .format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }

    @Test
    void testGetByRequestId() throws Exception {
        when(itemRequestService.findByRequestId(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoWithItems);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemRequestDtoWithItems.getId()))
                .andExpect(jsonPath("$.description").value(itemRequestDtoWithItems.getDescription()))
                .andExpect(jsonPath("$.created").value(itemRequestDtoWithItems.getCreated()
                        .format(DateTimeFormatter.ISO_DATE_TIME)));
    }

    @Test
    void testGetAll() throws Exception {
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemRequestDtoWithItems));

        mvc.perform(get("/requests/all?from={from}&size={size}", 0, 10)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemRequestDtoWithItems.getId()))
                .andExpect(jsonPath("$[0].description").value(itemRequestDtoWithItems.getDescription()))
                .andExpect(jsonPath("$[0].created").value(created
                        .format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].items", hasSize(1)));
    }
}