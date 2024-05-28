package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoCreate;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:test",
        webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class BookingValidationTest {
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    public static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    protected MockMvc mvc;
    @Autowired
    protected ObjectMapper mapper;

    @DisplayName("Shouldn't create booking when end time less then start time")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeEndBeforeTimeStart() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                SOME_TIME.plusDays(2), SOME_TIME.plusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Shouldn't create booking when start and end time is null")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeStartOrTimeEndEqualsNull() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L, null, null);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Shouldn't create booking when start and end time same")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeStartAndTimeEndEquals() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                SOME_TIME.plusDays(2), SOME_TIME.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Shouldn't create booking when start time in past")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeStartInPast() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                SOME_TIME.minusDays(5), SOME_TIME.plusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Shouldn't create booking when end time in past")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfTimeEndInPast() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(1L,
                SOME_TIME.minusDays(5), SOME_TIME.minusDays(1));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Shouldn't create booking if item ID < 1")
    @Test
    @SneakyThrows
    public void shouldNotCreateBookingIfItemIdLessOne() {
        BookingDtoCreate bookingDtoCreate = new BookingDtoCreate(-5L,
                SOME_TIME.plusDays(1), SOME_TIME.plusDays(2));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Should get exception booker when unknown state")
    @Test
    @SneakyThrows
    public void shouldReturnExceptionBookerForUnknownState() {
        mvc.perform(get("/bookings?state=ERROR")
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }

    @DisplayName("Should get exception to owner when unknown state")
    @Test
    @SneakyThrows
    public void shouldReturnExceptionOwnerForUnknownState() {
        mvc.perform(get("/bookings/owner?state=ERROR")
                        .header(USER_ID, 1L))
                .andExpect(status().is(400));
    }
}
