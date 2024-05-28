package ru.practicum.shareit.request;

import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;

import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
public class ItemRequestDtoInfoTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);

    @Autowired
    private JacksonTester<ItemRequestDtoInfo> json;

    @DisplayName("Should serialize")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        ItemRequestDtoInfo itemRequestDtoInfo = ItemRequestDtoInfo
                .builder()
                .description("need angle grinder")
                .created(SOME_TIME)
                .items(List.of(new ItemDto(null, "Angle grinder", "grinding-wheel", true, null)))
                .build();

        JsonContent<ItemRequestDtoInfo> itemRequestDtoInfoJson = this.json.write(itemRequestDtoInfo);

        assertThat(itemRequestDtoInfoJson).hasJsonPathValue("$.description");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("need angle grinder");

        assertThat(itemRequestDtoInfoJson).hasJsonPathValue("$.created");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(SOME_TIME.format(DATE_FORMAT));

        assertThat(itemRequestDtoInfoJson).hasJsonPathValue("$.items");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.items[0].name")
                .isEqualTo("Angle grinder");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathStringValue("$.items[0].description")
                .isEqualTo("grinding-wheel");
        assertThat(itemRequestDtoInfoJson).extractingJsonPathValue("$.items[0].available")
                .isEqualTo(true);
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        ItemRequestDtoInfo itemRequestDtoInfo = new ItemRequestDtoInfo(null, "need angle grinder", SOME_TIME,
                List.of(new ItemDto(null, "Angle grinder", "grinding-wheel", true, null)));

        var resource = new ClassPathResource("itemRequestDtoInfo.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemRequestDtoInfo);
    }
}
