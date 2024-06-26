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
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> json;
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
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .description("need angle grinder")
                .build();

        JsonContent<ItemRequestDto> itemRequestDtoJson = this.json.write(itemRequestDto);

        assertThat(itemRequestDtoJson).hasJsonPathValue("$.description");
        assertThat(itemRequestDtoJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("need angle grinder");
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    void shouldDeserialize() {
        ItemRequestDto itemRequestDto = new ItemRequestDto("need angle grinder");

        var resource = new ClassPathResource("itemRequestDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(itemRequestDto);
    }
}
