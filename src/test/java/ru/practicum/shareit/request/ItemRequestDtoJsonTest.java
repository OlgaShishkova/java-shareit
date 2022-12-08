package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.request.dto.ItemRequestDtoInput;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestDtoInput> json;

    @Test
    void testBookingDto() throws Exception {
        LocalDateTime created = LocalDateTime.now();
        ItemRequestDtoInput itemRequestDtoInput = new ItemRequestDtoInput(
                1L,
                "item request description",
                created
        );
        JsonContent<ItemRequestDtoInput> result = json.write(itemRequestDtoInput);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.description")
                .isEqualTo("item request description");
        assertThat(result).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd,HH:mm:ss")));
    }
}
