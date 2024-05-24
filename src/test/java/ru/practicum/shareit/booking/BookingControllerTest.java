package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private BookingService bookingService;
    @Autowired
    private ObjectMapper mapper;

    @DisplayName("Should creat booking")
    @Test
    @SneakyThrows
    public void shouldCreateBooking() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2));
        BookingDto bookingDto = getBookingDto();

        when(bookingService.createBooking(any(), anyLong())).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.status").value(String.valueOf(bookingDto.getStatus())))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingDto.getItem().getRequestId()))
                .andExpect(status().is(201));

        verify(bookingService).createBooking(any(), anyLong());
    }

    @DisplayName("Should update booking")
    @Test
    @SneakyThrows
    public void shouldUpdateBooking() {
        BookingDto bookingDtoUpdate = getBookingDtoUpdate();

        when(bookingService.updateBooking(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDtoUpdate);

        mvc.perform(patch("/bookings/{bookingId}?approved=true", 1L)
                        .content(mapper.writeValueAsString(bookingDtoUpdate))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 2L))
                .andExpect(jsonPath("$.id").value(bookingDtoUpdate.getId()))
                .andExpect(jsonPath("$.start").value(bookingDtoUpdate.getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.end").value(bookingDtoUpdate.getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.status").value(String.valueOf(bookingDtoUpdate.getStatus())))
                .andExpect(jsonPath("$.booker.id").value(bookingDtoUpdate.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDtoUpdate.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDtoUpdate.getBooker().getEmail()))
                .andExpect(jsonPath("$.item.id").value(bookingDtoUpdate.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDtoUpdate.getItem().getName()))
                .andExpect(jsonPath("$.item.description")
                        .value(bookingDtoUpdate.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDtoUpdate.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingDtoUpdate.getItem().getRequestId()))
                .andExpect(status().isOk());

        verify(bookingService).updateBooking(anyLong(), anyLong(), anyBoolean());
    }

    @DisplayName("Should get booking by ID to item owner or requester")
    @Test
    @SneakyThrows
    public void shouldGetOneBookingUser() {
        BookingDto bookingDto = getBookingDto();

        when(bookingService.getOneBookingUser(anyLong(), anyLong())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", 1L)
                        .header(USER_ID, 2L))
                .andExpect(jsonPath("$.id").value(bookingDto.getId()))
                .andExpect(jsonPath("$.start").value(bookingDto.getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.end").value(bookingDto.getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.status").value(String.valueOf(bookingDto.getStatus())))
                .andExpect(jsonPath("$.booker.id").value(bookingDto.getBooker().getId()))
                .andExpect(jsonPath("$.booker.name").value(bookingDto.getBooker().getName()))
                .andExpect(jsonPath("$.booker.email").value(bookingDto.getBooker().getEmail()))
                .andExpect(jsonPath("$.item.id").value(bookingDto.getItem().getId()))
                .andExpect(jsonPath("$.item.name").value(bookingDto.getItem().getName()))
                .andExpect(jsonPath("$.item.description").value(bookingDto.getItem().getDescription()))
                .andExpect(jsonPath("$.item.available").value(bookingDto.getItem().getAvailable()))
                .andExpect(jsonPath("$.item.requestId").value(bookingDto.getItem().getRequestId()))
                .andExpect(status().isOk());

        verify(bookingService).getOneBookingUser(anyLong(), anyLong());
    }

    @DisplayName("Should get user bookings")
    @Test
    @SneakyThrows
    public void shouldGetAllBookingsBooker() {
        List<BookingDto> bookings = List.of(getBookingDto());

        when(bookingService.getAllBookingsBooker(anyLong(), any(), anyInt(), anyInt())).thenReturn(bookings);

        mvc.perform(get("/bookings?state=ALL")
                        .header(USER_ID, 1L))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[0].status").value(String.valueOf(bookings.get(0).getStatus())))
                .andExpect(jsonPath("$[0].booker.id").value(bookings.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookings.get(0).getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookings.get(0).getBooker().getEmail()))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookings.get(0).getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookings.get(0).getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookings.get(0).getItem().getRequestId()))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsBooker(anyLong(), any(), anyInt(), anyInt());
    }

    @DisplayName("Should get bookings for item owner")
    @Test
    @SneakyThrows
    public void shouldGetAllBookingsOwner() {
        List<BookingDto> bookings = getBookingDtoList();

        when(bookingService.getAllBookingsOwner(anyLong(), any(), anyInt(), anyInt()))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header(USER_ID, 1L))
                .andExpect(jsonPath("$[0].id").value(bookings.get(0).getId()))
                .andExpect(jsonPath("$[0].start").value(bookings.get(0).getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[0].end").value(bookings.get(0).getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$[0].status").value(String.valueOf(bookings.get(0).getStatus())))
                .andExpect(jsonPath("$[0].booker.id").value(bookings.get(0).getBooker().getId()))
                .andExpect(jsonPath("$[0].booker.name").value(bookings.get(0).getBooker().getName()))
                .andExpect(jsonPath("$[0].booker.email").value(bookings.get(0).getBooker().getEmail()))
                .andExpect(jsonPath("$[0].item.id").value(bookings.get(0).getItem().getId()))
                .andExpect(jsonPath("$[0].item.name").value(bookings.get(0).getItem().getName()))
                .andExpect(jsonPath("$[0].item.description").value(bookings.get(0).getItem().getDescription()))
                .andExpect(jsonPath("$[0].item.available").value(bookings.get(0).getItem().getAvailable()))
                .andExpect(jsonPath("$[0].item.requestId").value(bookings.get(0).getItem().getRequestId()))
                .andExpect(status().isOk());

        verify(bookingService).getAllBookingsOwner(anyLong(), any(), anyInt(), anyInt());
    }

    private BookingDto getBookingDto() {
        return new BookingDto(
                1L,
                SOME_TIME.plusDays(1),
                SOME_TIME.plusDays(2),
                WAITING,
                new UserDto(1L, "John", "john@ya.ru"),
                new ItemDto(1L, "Rotor hammer", "rotary hammer for concrete", true, null));
    }

    private List<BookingDto> getBookingDtoList() {
        return List.of(
                new BookingDto(2L, SOME_TIME.plusDays(3), SOME_TIME.plusDays(4), WAITING,
                        new UserDto(2L, "Amy", "amy@ya.ru"),
                        new ItemDto(2L, "Vacuum cleaner", "industrial vacuum cleaner", true, null))
        );
    }

    private BookingDto getBookingDtoUpdate() {
        return new BookingDto(
                1L,
                SOME_TIME.plusDays(1),
                SOME_TIME.plusDays(2),
                APPROVED,
                new UserDto(1L, "John", "john@ya.ru"),
                new ItemDto(1L, "Rotor hammer", "rotary hammer for concrete", true, null));
    }
}