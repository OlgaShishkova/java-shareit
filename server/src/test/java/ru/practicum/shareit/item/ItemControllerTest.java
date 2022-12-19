package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBookings;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    private final Item item = new Item(
            1L,
            "item",
            "item description",
            true,
            new User(),
            new ItemRequest()
    );

    private final ItemDtoWithBookings itemDtoWithBookings = new ItemDtoWithBookings(
            1L,
            "item",
            "item description",
            true,
            null,
            new BookingDtoForItem(),
            new BookingDtoForItem(),
            Collections.emptyList()
    );

    private final ItemDto itemDto = new ItemDto(
            1L,
            "item",
            "item description",
            true,
            null
    );

    @Test
    void testGetByUserId() throws Exception {
        when(itemService.findByUserId(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(itemDtoWithBookings));

        mvc.perform(get("/items?from={from}&size={size}", 0, 10)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDtoWithBookings.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoWithBookings.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDtoWithBookings.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDtoWithBookings.getAvailable()));
    }

    @Test
    void testGetByItemId() throws Exception {
        when(itemService.findByItemId(anyLong(), anyLong()))
                .thenReturn(itemDtoWithBookings);

        mvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDtoWithBookings.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoWithBookings.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoWithBookings.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoWithBookings.getAvailable()));
    }

    @Test
    void testSearch() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search?text={text},from={from}&size={size}", "text", 0, 10)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(itemDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemDto.getName()))
                .andExpect(jsonPath("$[0].description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDto.getAvailable()));
    }

    @Test
    void testSearchBlankText() throws Exception {
        when(itemService.search(anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search?text={text}&from={from}&size={size}", "", 0, 10)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void testAdd() throws Exception {
        when(itemService.add(any(Item.class)))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

//    @Test
//    void testAddBlankNameWithException() throws Exception {
//        itemDto.setName("");
//        mvc.perform(post("/items")
//                .header("X-Sharer-User-Id", 1)
//                .content(mapper.writeValueAsString(itemDto))
//                .characterEncoding(StandardCharsets.UTF_8)
//                .contentType(MediaType.APPLICATION_JSON)
//                .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(400));
//    }

    @Test
    void testAddWrongOwnerWithException() throws Exception {
        when(itemService.add(any(Item.class)))
                .thenThrow(UserNotFoundException.class);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(404));
    }

    @Test
    void testUpdate() throws Exception {
        when(itemService.update(anyLong(), any(Item.class)))
                .thenReturn(item);

        mvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()));
    }

    @Test
    void testDelete() throws Exception {
        doNothing().when(itemService).delete(anyLong(), anyLong());

        mvc.perform(delete("/items/{itemId}", 1)
                .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());
    }

    @Test
    void testAddComment() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(
                1L,
                "comment",
                new Item(),
                new User(),
                created
        );
        CommentDto commentDto = new CommentDto(
                1L,
                "comment",
                "author",
                created
        );
        when(itemService.addComment(anyLong(), anyLong(), any(Comment.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.created")
                        .value(commentDto.getCreated().format(DateTimeFormatter.ISO_DATE_TIME)));
    }
}