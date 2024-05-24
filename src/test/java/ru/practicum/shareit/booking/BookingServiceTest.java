package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.booking.BookingState.WAITING;
import static ru.practicum.shareit.booking.BookingStatus.REJECTED;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class BookingServiceTest {
    private final UserService userService;
    private final ItemService itemService;
    private final BookingService bookingService;
    private final LocalDateTime current = LocalDateTime.now();
    private UserDto userDtoOneCreate;
    private UserDto userDtoTwoCreate;
    private ItemDto itemDtoOneCreate;
    private ItemDto itemDtoCreate;
    private BookingDtoCreate bookingDtoCreate;
    private BookingDtoCreate bookingDtoTwoCreate;

    @BeforeEach
    public void setUp() {
        userDtoOneCreate = new UserDto(null, "John", "john@ya.ru");
        userDtoTwoCreate = new UserDto(null, "Amy", "amy@ya.ru");
        itemDtoOneCreate = new ItemDto(null, "Rotor hammer", "rotary hammer for concrete", true, null);
        itemDtoCreate = new ItemDto(null, "Vacuum cleaner", "industrial vacuum cleaner", false, null);
        bookingDtoCreate = new BookingDtoCreate(null, current.plusDays(1), current.plusDays(5));
        bookingDtoTwoCreate = new BookingDtoCreate(null, current.minusHours(20), current.minusHours(2));
    }

    @DisplayName("Should create booking")
    @Test
    public void shouldCreateBooking() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDtoOne = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoCreate.setItemId(itemDtoOne.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoCreate, userDtoTwo.getId());

        assertThat(bookingDtoCreated.getItem(), is(equalTo(itemDtoOne)));
        assertThat(bookingDtoCreated.getBooker(), is(equalTo(userDtoTwo)));
        assertThat(bookingDtoCreated.getStart(), is(equalTo(bookingDtoCreate.getStart())));
        assertThat(bookingDtoCreated.getEnd(), is(equalTo(bookingDtoCreate.getEnd())));
        assertThat(bookingDtoCreated.getStatus(), is(equalTo(BookingStatus.WAITING)));
    }

    @DisplayName("Should update booking")
    @Test
    public void shouldUpdateBooking() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());

        assertThat(bookingDtoCreated.getStatus(), is(equalTo(BookingStatus.WAITING)));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);

        assertThat(bookingDtoUpdated.getStatus(), is(equalTo(REJECTED)));
    }

    @DisplayName("Should not update booking when status not WAITING")
    @Test
    public void shouldNotUpdateBookingIfStatusNotWaiting() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());

        assertThat(BookingStatus.WAITING, is(bookingDtoCreated.getStatus()));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);

        assertThat(bookingDtoUpdated.getStatus(), is(equalTo(REJECTED)));

        ValidationException exception = assertThrows(
                ValidationException.class,
                () -> bookingService.updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), true)
        );
        assertEquals("Booking status is not WAITING", exception.getMessage());
    }

    @DisplayName("Should not update booking when not exist")
    @Test
    public void shouldNotUpdateBookingNotExists() {
        long bookingId = 1025L;
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userDtoOne.getId(), bookingId, false)
        );
        assertEquals("Booking with id=" + bookingId + " not found", exception.getMessage());
    }

    @DisplayName("Should get exception when not an owner item try to change booking status")
    @Test
    public void shouldNotUpdateBooking() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());

        assertThat(bookingDtoCreated.getStatus(), equalTo(BookingStatus.WAITING));

        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> bookingService.updateBooking(userDtoTwo.getId(), bookingDtoCreated.getId(), false)
        );
        assertEquals("Booking id=" + bookingDtoCreated.getId() + " not found", exception.getMessage());
    }

    @DisplayName("Should get user booking by ID")
    @Test
    public void shouldGetOneBookingUser() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        BookingDto result = bookingService.getOneBookingUser(bookingDtoCreated.getId(), userDtoTwo.getId());

        assertThat(result).isEqualTo(bookingDtoCreated);
    }

    @DisplayName("Should get all user bookings")
    @Test
    public void shouldGetAllBookingsBooker() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        Collection<BookingDto> result = bookingService
                .getAllBookingsBooker(userDtoTwo.getId(), BookingState.ALL, 0, 2);

        assertThat(result, contains(bookingDtoCreated));
    }

    @DisplayName("Should get all bookings by booker with states: waiting, rejected")
    @Test
    public void shouldGetAllBookingsBookerWaitingAndRejected() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        Collection<BookingDto> resultWaiting = bookingService
                .getAllBookingsBooker(userDtoTwo.getId(), WAITING, 0, 2);

        assertThat(resultWaiting, contains(bookingDtoCreated));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);
        Collection<BookingDto> resultRejected = bookingService
                .getAllBookingsBooker(userDtoTwo.getId(), BookingState.REJECTED, 0, 2);

        assertThat(resultRejected, contains(bookingDtoUpdated));
    }

    @DisplayName("Should get all bookings for user with diff states")
    @Test
    public void shouldGetAllBookingsBookerCurrentAndFutureAndPast() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        bookingDtoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreatedPast = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        BookingDto bookingDtoCreatedFuture = bookingService.createBooking(bookingDtoCreate, userDtoTwo.getId());

        Collection<BookingDto> resultPast = bookingService
                .getAllBookingsBooker(userDtoTwo.getId(), BookingState.PAST, 0, 2);
        Collection<BookingDto> resultFuture = bookingService
                .getAllBookingsBooker(userDtoTwo.getId(), BookingState.FUTURE, 0, 2);
        Collection<BookingDto> resultCurrent = bookingService
                .getAllBookingsBooker(userDtoTwo.getId(), BookingState.CURRENT, 0, 2);

        assertThat(resultCurrent, empty());
        assertThat(resultPast, contains(bookingDtoCreatedPast));
        assertThat(resultFuture, contains(bookingDtoCreatedFuture));
    }

    @DisplayName("Should get all bookings by owner states: waiting, rejected")
    @Test
    public void shouldGetAllBookingsOwnerWaitingAndRejected() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        Collection<BookingDto> resultWaiting = bookingService
                .getAllBookingsOwner(userDtoOne.getId(), WAITING, 0, 2);

        assertThat(resultWaiting, contains(bookingDtoCreated));

        BookingDto bookingDtoUpdated = bookingService
                .updateBooking(userDtoOne.getId(), bookingDtoCreated.getId(), false);
        Collection<BookingDto> resultRejected = bookingService
                .getAllBookingsOwner(userDtoOne.getId(), BookingState.REJECTED, 0, 2);

        assertThat(resultRejected, contains(bookingDtoUpdated));
    }

    @DisplayName("Should get all bookings for owner with diff states")
    @Test
    public void shouldGetAllBookingsOwnerCurrentAndFutureAndPast() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        bookingDtoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreatedPast = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        BookingDto bookingDtoCreatedFuture = bookingService.createBooking(bookingDtoCreate, userDtoTwo.getId());

        Collection<BookingDto> resultPast = bookingService
                .getAllBookingsOwner(userDtoOne.getId(), BookingState.PAST, 0, 2);
        Collection<BookingDto> resultFuture = bookingService
                .getAllBookingsOwner(userDtoOne.getId(), BookingState.FUTURE, 0, 2);
        Collection<BookingDto> resultCurrent = bookingService
                .getAllBookingsOwner(userDtoOne.getId(), BookingState.CURRENT, 0, 2);

        assertThat(resultCurrent, empty());
        assertThat(resultPast, contains(bookingDtoCreatedPast));
        assertThat(resultFuture, contains(bookingDtoCreatedFuture));
    }

    @DisplayName("Should get all bookings by owner")
    @Test
    public void shouldGetAllBookingsOwner() {
        UserDto userDtoOne = userService.createUser(userDtoOneCreate);
        UserDto userDtoTwo = userService.createUser(userDtoTwoCreate);
        ItemDto itemDto = itemService.createItem(itemDtoOneCreate, userDtoOne.getId());

        bookingDtoTwoCreate.setItemId(itemDto.getId());
        BookingDto bookingDtoCreated = bookingService.createBooking(bookingDtoTwoCreate, userDtoTwo.getId());
        Collection<BookingDto> result = bookingService
                .getAllBookingsOwner(userDtoOne.getId(), BookingState.ALL, 0, 2);

        assertThat(result, contains(bookingDtoCreated));
    }
}
