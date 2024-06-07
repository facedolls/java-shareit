package ru.practicum.shareit.booking;

import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Should serialize")
    @Test
    @SneakyThrows
    void shouldSerialize() {
        ItemDto itemDto = ItemDto.builder()
                .name("Angle grinder")
                .description("grinding-wheel")
                .available(true)
                .build();

        JsonContent<ItemDto> itemDtoJson = this.json.write(itemDto);

        assertThat(itemDtoJson).hasJsonPathValue("$.name");
        assertThat(itemDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("Angle grinder");

        assertThat(itemDtoJson).hasJsonPathValue("$.description");
        assertThat(itemDtoJson).extractingJsonPathStringValue("$.description").isEqualTo("grinding-wheel");

        assertThat(itemDtoJson).hasJsonPathValue("$.available");
        assertThat(itemDtoJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    void shouldDeserialize() {
        ItemDto itemDto = new ItemDto(null, "Angle grinder", "grinding-wheel", true, null);

        var resource = new ClassPathResource("itemDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemDto);
    }
}
