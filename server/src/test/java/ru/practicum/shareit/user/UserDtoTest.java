package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.nio.file.Files;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
@AutoConfigureJsonTesters
public class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> json;
    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Should serialize")
    @Test
    @SneakyThrows
    public void shouldSerialize() {
        UserDto userDto = UserDto.builder()
                .name("Paul")
                .email("paul@ya.ru")
                .build();

        JsonContent<UserDto> userDtoJson = this.json.write(userDto);

        assertThat(userDtoJson).hasJsonPathValue("$.name");
        assertThat(userDtoJson).extractingJsonPathStringValue("$.name").isEqualTo("Paul");

        assertThat(userDtoJson).hasJsonPathValue("$.email");
        assertThat(userDtoJson).extractingJsonPathStringValue("$.email").isEqualTo("paul@ya.ru");
    }

    @DisplayName("Should deserialize")
    @Test
    @SneakyThrows
    public void shouldDeserialize() {
        UserDto userDto = new UserDto(null, "Paul", "paul@ya.ru");

        var resource = new ClassPathResource("userDto.json");
        String content = Files.readString(resource.getFile().toPath());

        assertThat(this.json.parse(content)).isEqualTo(userDto);
    }
}
