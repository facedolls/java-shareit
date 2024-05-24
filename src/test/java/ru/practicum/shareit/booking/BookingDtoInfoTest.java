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
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class BookingDtoInfoTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);

    @Autowired
    private JacksonTester<BookingDtoInfo> json;

    @DisplayName("Should serialize")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        BookingDtoInfo bookingDtoInfo = BookingDtoInfo.builder()
                .bookerId(1L)
                .start(SOME_TIME)
                .end(SOME_TIME.plusDays(3))
                .status(WAITING)
                .itemId(1L)
                .build();

        JsonContent<BookingDtoInfo> bookingDtoInfoJson = this.json.write(bookingDtoInfo);

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.bookerId");
        assertThat(bookingDtoInfoJson).extractingJsonPathValue("$.bookerId").isEqualTo(1);

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.start");
        assertThat(bookingDtoInfoJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(SOME_TIME.format(DATE_FORMAT));

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.end");
        assertThat(bookingDtoInfoJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(SOME_TIME.plusDays(3).format(DATE_FORMAT));

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.status");
        assertThat(bookingDtoInfoJson).extractingJsonPathStringValue("$.status")
                .isEqualTo(String.valueOf(WAITING));

        assertThat(bookingDtoInfoJson).hasJsonPathValue("$.itemId");
        assertThat(bookingDtoInfoJson).extractingJsonPathValue("$.itemId").isEqualTo(1);
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        BookingDtoInfo bookingDtoInfo = new BookingDtoInfo(null, 1L, SOME_TIME, SOME_TIME.plusDays(3),
                WAITING, 1L);

        var resource = new ClassPathResource("bookingDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(bookingDtoInfo);
    }
}
