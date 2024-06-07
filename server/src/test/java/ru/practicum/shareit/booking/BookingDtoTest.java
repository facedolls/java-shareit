package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class BookingDtoTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);

    @Autowired
    private JacksonTester<BookingDto> json;

    @DisplayName("Should serialize")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        BookingDto bookingDto = BookingDto.builder()
                .start(SOME_TIME)
                .end(SOME_TIME.plusDays(3))
                .status(WAITING)
                .booker(new UserDto(null, "Paul", "paul@ya.ru"))
                .item(new ItemDto(null, "Angle grinder", "grinding-wheel", true, null))
                .build();

        JsonContent<BookingDto> bookingDtoJson = this.json.write(bookingDto);

        assertThat(bookingDtoJson).hasJsonPathValue("$.start");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(SOME_TIME.format(DATE_FORMAT));

        assertThat(bookingDtoJson).hasJsonPathValue("$.end");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(SOME_TIME.plusDays(3).format(DATE_FORMAT));

        assertThat(bookingDtoJson).hasJsonPathValue("$.status");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.status")
                .isEqualTo(String.valueOf(WAITING));

        assertThat(bookingDtoJson).hasJsonPathValue("$.booker");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.booker.name")
                .isEqualTo("Paul");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.booker.email")
                .isEqualTo("paul@ya.ru");

        assertThat(bookingDtoJson).hasJsonPathValue("$.item");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.item.name").isEqualTo("Angle grinder");
        assertThat(bookingDtoJson).extractingJsonPathStringValue("$.item.description")
                .isEqualTo("grinding-wheel");
        assertThat(bookingDtoJson).extractingJsonPathValue("$.item.available").isEqualTo(true);
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        BookingDto bookingDto = new BookingDto(1L, SOME_TIME, SOME_TIME.plusDays(3), WAITING,
                new UserDto(null, "Paul", "paul@ya.ru"),
                new ItemDto(null, "Angle grinder", "grinding-wheel", true, null));

        var resource = new ClassPathResource("bookingDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(bookingDto);
    }
}
