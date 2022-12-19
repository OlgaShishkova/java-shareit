package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingDtoInput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingDtoInput> json;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime start = LocalDateTime.now().plusDays(5);
        LocalDateTime end = LocalDateTime.now().plusDays(10);
        BookingDtoInput bookingDtoInput = new BookingDtoInput(
                1L,
                start,
                end
        );

        JsonContent<BookingDtoInput> result = json.write(bookingDtoInput);

        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ISO_DATE_TIME));
        assertThat(result).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ISO_DATE_TIME));
    }
}
