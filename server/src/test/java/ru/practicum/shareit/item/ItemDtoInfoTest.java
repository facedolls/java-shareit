package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@JsonTest
@AutoConfigureJsonTesters
public class ItemDtoInfoTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);

    @Autowired
    private JacksonTester<ItemDtoInfo> json;

    @DisplayName("Should serialize")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemDtoInfo itemDtoInfo = ItemDtoInfo.builder()
                .name("Angle grinder")
                .description("grinding-wheel")
                .available(true)
                .lastBooking(new BookingDtoInfo(null, 1L, SOME_TIME, SOME_TIME.plusDays(3),
                        WAITING, 1L))
                .nextBooking(null)
                .comments(List.of(new CommentDto(null, "good", "Paul", SOME_TIME, 1L)))
                .build();

        JsonContent<ItemDtoInfo> itemDtoInfoJson = this.json.write(itemDtoInfo);

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.name");
        assertThat(itemDtoInfoJson).extractingJsonPathStringValue("$.name").isEqualTo("Angle grinder");

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.description");
        assertThat(itemDtoInfoJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("grinding-wheel");

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.available");
        assertThat(itemDtoInfoJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.lastBooking");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.start")
                .isEqualTo(SOME_TIME.format(DATE_FORMAT));
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.end")
                .isEqualTo(SOME_TIME.plusDays(3).format(DATE_FORMAT));
        assertThat(itemDtoInfoJson).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo(String.valueOf(WAITING));
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.lastBooking.itemId").isEqualTo(1);

        assertThat(itemDtoInfoJson).hasJsonPathValue("$.comments");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].text").isEqualTo("good");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].authorName")
                .isEqualTo("Paul");
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].created")
                .isEqualTo(SOME_TIME.format(DATE_FORMAT));
        assertThat(itemDtoInfoJson).extractingJsonPathValue("$.comments[0].itemId").isEqualTo(1);
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemDtoInfo itemDtoInfo = new ItemDtoInfo(null, "Angle grinder", "grinding-wheel", true,
                new BookingDtoInfo(null, 1L, SOME_TIME, SOME_TIME.plusDays(3), WAITING, 1L),
                null, List.of(new CommentDto(null, "good", "Paul", SOME_TIME, 1L)));

        var resource = new ClassPathResource("itemDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemDtoInfo);
    }
}
