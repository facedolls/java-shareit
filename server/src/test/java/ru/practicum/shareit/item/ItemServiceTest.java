package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoInfo;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.sql.DataSource;
import javax.validation.ValidationException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class ItemServiceTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final DataSource dataSource;
    private final ItemRequestService itemRequestService;
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private ItemRequestDto itemRequestDtoCreateOne;
    private ItemDto itemDtoOneCreate;
    private ItemDto itemDtoTwoCreate;
    private ItemDto itemDtoThreeCreate;
    private ItemDto itemDto;
    private ItemDto itemDtoUpdate;
    private CommentDto commentDtoCreate;
    private BookingDtoCreate bookingDtoTwoCreate;
    private BookingDtoCreate bookingDtoCreate;


    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "John", "john@ya.ru");
        userDtoTwoCreate = new UserDto(null, "Amy", "amy@ya.ru");
        itemRequestDtoCreateOne = new ItemRequestDto("need rotary hammer");
        itemDtoOneCreate = new ItemDto(null, "Rotor hammer", "rotary hammer for concrete", true, null);
        itemDtoTwoCreate = new ItemDto(null, "Vacuum cleaner", "industrial vacuum cleaner", true, null);
        itemDtoThreeCreate = new ItemDto(null, "Vacuum cleaner", "industrial vacuum cleaner", true, 1L);
        itemDto = new ItemDto(null, "Rotor hammer", "rotary hammer for concrete", true, null);
        itemDtoUpdate = new ItemDto(null, "Rotor hammer", "good rotor hammer", true, null);
        commentDtoCreate = new CommentDto(null, "cool", null, null, null);
        bookingDtoTwoCreate = new BookingDtoCreate(null, SOME_TIME.plusNanos(1), SOME_TIME.plusNanos(2));
        bookingDtoCreate = new BookingDtoCreate(null,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
    }

    @AfterEach
    public void reinitDatabase() throws SQLException {
        var connection = dataSource.getConnection();
        var statement = connection.createStatement();

        statement.execute("SET REFERENTIAL_INTEGRITY FALSE; " +
                "TRUNCATE TABLE USERS; " +
                "TRUNCATE TABLE ITEMS; " +
                "SET REFERENTIAL_INTEGRITY FALSE;");
        statement.close();
        connection.close();
    }

    @DisplayName("Should not get item by ID")
    @Test
    public void shouldNotGetItemDtoById() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        long itemId = 10L;

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.getItemDtoById(itemId, userDtoOne.getId())
        );
        assertEquals("Item with this id=" + itemId + " not found", exception.getMessage());
    }

    @DisplayName("Should create item")
    @Test
    public void shouldCreateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));
    }

    @DisplayName("Should create item for user request")
    @Test
    public void shouldCreateItemForTheUserRequest() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);

        ItemRequestDtoInfo requestDtoCreated = itemRequestService
                .createItemRequest(itemRequestDtoCreateOne, userDtoTwo.getId());

        itemDtoOneCreate.setRequestId(requestDtoCreated.getId());
        itemDto.setRequestId(requestDtoCreated.getId());
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));
    }

    @DisplayName("Should not create item")
    @Test
    public void shouldNotCreateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.createItem(itemDtoThreeCreate, userDtoOne.getId())
        );
        assertEquals("Request id=" + itemDtoThreeCreate.getRequestId() + " not found",
                exception.getMessage());
    }

    @DisplayName("Should update item")
    @Test
    public void shouldUpdateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());
        itemDto.setId(itemDtoOne.getId());

        assertThat(itemDtoOne, is(equalTo(itemDto)));

        ItemDto itemDtoOneUpdated = itemService.updateItem(itemDtoUpdate, itemDtoOne.getId(), userDtoOne.getId());
        itemDtoUpdate.setId(itemDtoOneUpdated.getId());

        assertThat(itemDtoOneUpdated, is(equalTo(itemDtoUpdate)));
    }

    @DisplayName("Should not update item")
    @Test
    public void shouldNotUpdateItem() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> itemService.updateItem(itemDtoUpdate, itemDtoOne.getId(), userDtoTwo.getId())
        );
        assertEquals("Item with this id=" + itemDtoOne.getId() + " not found", exception.getMessage());
    }

    @DisplayName("Should find items by name or descr.")
    @Test
    public void shouldSearchItems() {
        String textOne = "hammer";
        String textTwo = "cleaner";

        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        Collection<ItemDto> items = itemService.searchItems(textOne, 0, 2);
        Collection<ItemDto> itemsTwo = itemService.searchItems(textTwo, 0, 2);

        assertThat(items, is(hasSize(1)));
        assertThat(items, is(contains(itemDtoOne)));
        assertThat(itemsTwo, is(hasSize(0)));
    }

    @DisplayName("Should not create comment when booking time has not expired")
    @Test
    public void shouldNotCreateCommentIfTheBookingTimeHasNotExpired() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(commentDtoCreate, userDtoTwo.getId(), itemDtoOne.getId())
        );
        assertEquals("Only users whose booking has expired can leave comments", exception.getMessage());
    }

    @DisplayName("Should not create comment if item not exist")
    @Test
    public void shouldNotCreateCommentOnItemNotExist() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true);

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> itemService.createComment(commentDtoCreate, userDtoTwo.getId(), 54321L)
        );
        assertEquals("Item doesn't exist yet", exception.getMessage());
    }
}
