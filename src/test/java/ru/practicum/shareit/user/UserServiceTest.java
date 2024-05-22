package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class UserServiceTest {
    private final UserService userService;
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private UserDto userDtoOne;
    private UserDto userDtoCreateDuplicateEmail;
    private UserDto userDtoOneUpdate;
    private UserDto userDtoOneUpdateDuplicateEmail;

    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "John", "john@ya.ru");
        userDtoTwoCreate = new UserDto(null, "Amy", "amy@ya.ru");
        userDtoOne = new UserDto(1L, "John", "john@ya.ru");
        userDtoCreateDuplicateEmail = new UserDto(null, "Crystal", "john@ya.ru");
        userDtoOneUpdate = new UserDto(1L, "John Doe", "johndoe@ya.ru");
        userDtoOneUpdateDuplicateEmail = new UserDto(1L, "John Doe", "amy@ya.ru");
    }

    @DisplayName("Should return user by ID")
    @Test
    public void shouldGetUserById() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoResult = userService.getUserById(userDtoCreated.getId());

        assertThat(userDtoCreated, is(equalTo(userDtoResult)));
    }

    @DisplayName("Should throw exception when get user with wrong ID")
    @Test
    public void shouldNotGetUserById() {
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(9L)
        );
        assertEquals("User with id=9 not found", exception.getMessage());
    }

    @DisplayName("Should return users page by page")
    @Test
    public void shouldGetAllUser() {
        UserDto userDtoCreatedOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoCreatedTwo = userService.createUser(userDtoTwoCreate);

        Collection<UserDto> allUsers = List.of(userDtoCreatedOne, userDtoCreatedTwo);
        Collection<UserDto> allUsersResult = userService.getAllUsers(0, 2);

        assertThat(allUsers, is(equalTo(allUsersResult)));
        assertThat(allUsersResult, hasSize(2));
    }

    @DisplayName("Should create user")
    @Test
    public void shouldCreateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);

        assertThat(userDtoCreated, is(equalTo(userDtoOne)));
    }

    @DisplayName("Shouldn't create user with same email")
    @Test
    public void shouldNotCreateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);

        assertThat(userDtoCreated, is(equalTo(userDtoOne)));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.createUser(userDtoCreateDuplicateEmail)
        );

        assertEquals("User with email: " + userDtoCreateDuplicateEmail.getEmail()
                + " exists", exception.getMessage());
    }

    @DisplayName("Should update user")
    @Test
    public void shouldUpdateUser() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoUpdated = userService.updateUser(userDtoCreated.getId(), userDtoOneUpdate);

        assertThat(userDtoUpdated, is(equalTo(userDtoOneUpdate)));
    }

    @DisplayName("Should throw exception when update user with wrong ID")
    @Test
    public void shouldNotUpdateUser() {
        long id = 1;

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> userService.updateUser(id, userDtoOne)
        );
        assertEquals("User with id=" + id + " not exists", exception.getMessage());
    }

    @DisplayName("Should throw exception when update user with duplicate email")
    @Test
    public void shouldNotUpdateUserEmail() {
        UserDto userOne = userService.createUser(userDtoOneCreate);
        userService.createUser(userDtoTwoCreate);

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> userService.updateUser(userOne.getId(), userDtoOneUpdateDuplicateEmail)
        );
        assertEquals("User with email: " + userDtoOneUpdateDuplicateEmail.getEmail()
                + " exists", exception.getMessage());
    }

    @DisplayName("Should delete user")
    @Test
    public void shouldDeleteUserById() {
        UserDto userDtoCreated = userService.createUser(userDtoOneCreate);
        UserDto userDtoResultOne = userService.getUserById(userDtoCreated.getId());

        assertThat(userDtoCreated, is(equalTo(userDtoResultOne)));

        userService.deleteUserById(userDtoCreated.getId());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> userService.getUserById(userDtoCreated.getId())
        );
        assertEquals("User with id=" + userDtoCreated.getId() + " not found", exception.getMessage());
    }
}
