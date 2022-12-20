package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoInput;
import ru.practicum.shareit.booking.dto.BookingDtoOutput;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    private final LocalDateTime bookingStart = LocalDateTime.now().plusDays(5);
    private final LocalDateTime bookingEnd = LocalDateTime.now().plusDays(10);

    private final Booking booking = new Booking(
            1L,
            bookingStart,
            bookingEnd,
            new Item(
                    1L,
                    "item",
                    "item description",
                    true,
                    new User(1L, "owner", "owner@email.ru"),
                    null
            ),
            new User(2L, "user", "user@email.ru"),
            Status.WAITING
    );

    private final BookingDtoInput bookingDtoInput = new BookingDtoInput(
            null,
            bookingStart,
            bookingEnd
    );

    private final BookingDtoOutput bookingDtoOutput = new BookingDtoOutput(
            1L,
            bookingStart,
            bookingEnd,
            new ItemDto(
                    1L,
                    "item",
                    "item description",
                    true,
                    null),
            new UserDto(
                    2L,
                    "user",
                    "user@email.ru"),
            Status.WAITING
    );

    @Test
    void testAdd() throws Exception {
        when(bookingService.add(any(Booking.class)))
                .thenReturn(booking);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOutput.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoOutput.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoOutput.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoOutput.getStatus().toString())
                );
    }

//    @Test
//    void testAddEndBeforeStartWithException() throws Exception {
//        when(bookingService.add(any(Booking.class)))
//                .thenReturn(booking);
//
//        bookingDtoInput.setEnd(bookingStart.minusDays(2));
//        mvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", 2)
//                        .content(mapper.writeValueAsString(bookingDtoInput))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(400));
//    }

//    @Test
//    void testAddDatesInPastWithException() throws Exception {
//        when(bookingService.add(any()))
//                .thenReturn(booking);
//
//        bookingDtoInput.setEnd(bookingStart.minusDays(10));
//        mvc.perform(post("/bookings")
//                        .header("X-Sharer-User-Id", 2)
//                        .content(mapper.writeValueAsString(bookingDtoInput))
//                        .characterEncoding(StandardCharsets.UTF_8)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().is(400));
//    }

    @Test
    void approve() throws Exception {
        when(bookingService.approve(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(booking);

        mvc.perform(patch("/bookings/{bookingId}?approved={approved}", 1, true)
                        .header("X-Sharer-User-Id", 2)
                        .content(mapper.writeValueAsString(bookingDtoInput))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOutput.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoOutput.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoOutput.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoOutput.getStatus().toString()));
    }

    @Test
    void testGet() throws Exception {
        when(bookingService.get(anyLong(), anyLong()))
                .thenReturn(booking);

        mvc.perform(get("/bookings/{bookingId}", 1)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(bookingDtoOutput.getId()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoOutput.getItem().getId()))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoOutput.getBooker().getId()))
                .andExpect(jsonPath("$.status").value(bookingDtoOutput.getStatus().toString()));
    }

    @Test
    void testGetAll() throws Exception {
        when(bookingService.getAll(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(booking));

        mvc.perform(get("/bookings?state={state}", "FUTURE")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(bookingDtoOutput.getId()))
                .andExpect(jsonPath("$[0].item.id").value(bookingDtoOutput.getItem().getId()))
                .andExpect(jsonPath("$[0].booker.id").value(bookingDtoOutput.getBooker().getId()))
                .andExpect(jsonPath("$[0].status").value(bookingDtoOutput.getStatus().toString()));
    }

   @Test
    void testGetForAllItems() throws Exception {
       when(bookingService.getForAllItems(anyLong(), anyString(), anyInt(), anyInt()))
               .thenReturn(List.of(booking));

       mvc.perform(get("/bookings/owner?state={state}&from={from}&size={size}", "FUTURE", 0, 10)
                       .header("X-Sharer-User-Id", 2))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$", hasSize(1)))
               .andExpect(jsonPath("$[0].id").value(bookingDtoOutput.getId()))
               .andExpect(jsonPath("$[0].item.id").value(bookingDtoOutput.getItem().getId()))
               .andExpect(jsonPath("$[0].booker.id").value(bookingDtoOutput.getBooker().getId()))
               .andExpect(jsonPath("$[0].status").value(bookingDtoOutput.getStatus().toString()));
    }
}