package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoInfo;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.booking.BookingStatus.APPROVED;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private static final LocalDateTime SOME_TIME = LocalDateTime.parse("2020-01-01T01:01:01", DATE_FORMAT);
    private static final String USER_ID = "X-Sharer-User-Id";

    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    @Autowired
    private ObjectMapper mapper;
    private ItemDtoInfo itemDtoInfo;

    @DisplayName("Should get item by ID")
    @Test
    @SneakyThrows
    public void shouldGetItemById() {
        ItemDtoInfo itemDtoInfo = getItemDtoInfo();

        when(itemService.getItemDtoById(anyLong(), anyLong())).thenReturn(itemDtoInfo);

        mvc.perform(get("/items/{itemId}", 1L)
                        .header(USER_ID, 1))
                .andExpect(jsonPath("$.id").value(itemDtoInfo.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoInfo.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoInfo.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoInfo.getAvailable()))
                .andExpect(jsonPath("$.lastBooking.id").value(itemDtoInfo.getLastBooking().getId()))
                .andExpect(jsonPath("$.lastBooking.bookerId")
                        .value(itemDtoInfo.getLastBooking().getBookerId()))
                .andExpect(jsonPath("$.lastBooking.start")
                        .value(itemDtoInfo.getLastBooking().getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.lastBooking.end")
                        .value(itemDtoInfo.getLastBooking().getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.lastBooking.status")
                        .value(String.valueOf(itemDtoInfo.getLastBooking().getStatus())))
                .andExpect(jsonPath("$.lastBooking.itemId")
                        .value(itemDtoInfo.getLastBooking().getItemId()))
                .andExpect(jsonPath("$.nextBooking.id")
                        .value(itemDtoInfo.getNextBooking().getId()))
                .andExpect(jsonPath("$.nextBooking.bookerId")
                        .value(itemDtoInfo.getNextBooking().getBookerId()))
                .andExpect(jsonPath("$.nextBooking.start")
                        .value(itemDtoInfo.getNextBooking().getStart().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.nextBooking.end")
                        .value(itemDtoInfo.getNextBooking().getEnd().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.nextBooking.status")
                        .value(String.valueOf(itemDtoInfo.getNextBooking().getStatus())))
                .andExpect(jsonPath("$.nextBooking.itemId")
                        .value(itemDtoInfo.getNextBooking().getItemId()))
                .andExpect(jsonPath("$.comments[0].id")
                        .value(itemDtoInfo.getComments().get(0).getId()))
                .andExpect(jsonPath("$.comments[0].text")
                        .value(itemDtoInfo.getComments().get(0).getText()))
                .andExpect(jsonPath("$.comments[0].authorName")
                        .value(itemDtoInfo.getComments().get(0).getAuthorName()))
                .andExpect(jsonPath("$.comments[0].created")
                        .value(itemDtoInfo.getComments().get(0).getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.comments[0].itemId")
                        .value(itemDtoInfo.getComments().get(0).getItemId()))
                .andExpect(status().isOk());

        verify(itemService).getItemDtoById(anyLong(), anyLong());
    }

    @DisplayName("Should get back all item to owner")
    @Test
    @SneakyThrows
    public void shouldGetAllItemUser() {
        List<ItemDtoInfo> items = getItemDtoInfoList();

        when(itemService.getAllItemUser(anyLong(), anyInt(), anyInt())).thenReturn(items);

        mvc.perform(get("/items")
                        .header(USER_ID, 1))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(items.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(items.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(items.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(items.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[0].comments").isNotEmpty())
                .andExpect(jsonPath("$[1].id").value(items.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(items.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(items.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(items.get(1).getAvailable()))
                .andExpect(jsonPath("$[1].nextBooking").isNotEmpty())
                .andExpect(jsonPath("$[1].lastBooking").isNotEmpty())
                .andExpect(jsonPath("$[1].comments").isEmpty())
                .andExpect(status().isOk());

        verify(itemService).getAllItemUser(anyLong(), anyInt(), anyInt());
    }

    @DisplayName("Should create item")
    @Test
    @SneakyThrows
    public void shouldCreateItem() {
        ItemDto itemDtoOneCreate = new ItemDto(null, "Rotor hammer", "rotary hammer for concrete", true, null);
        ItemDto itemDto = new ItemDto(1L, "Rotor hammer", "rotary hammer for concrete", true, null);

        when(itemService.createItem(any(), anyLong())).thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDtoOneCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(jsonPath("$.id").value(itemDto.getId()))
                .andExpect(jsonPath("$.name").value(itemDto.getName()))
                .andExpect(jsonPath("$.description").value(itemDto.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDto.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDto.getRequestId()))
                .andExpect(status().is(201));

        verify(itemService).createItem(any(), anyLong());
    }

    @DisplayName("Should update item")
    @Test
    @SneakyThrows
    public void shouldUpdateItem() {
        ItemDto itemDtoUpdate = new ItemDto(1L, "Rotor hammer", "good rotary hammer for concrete", true, null);

        when(itemService.updateItem(any(), anyLong(), anyLong())).thenReturn(itemDtoUpdate);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .content(mapper.writeValueAsString(itemDtoUpdate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1L))
                .andExpect(jsonPath("$.id").value(itemDtoUpdate.getId()))
                .andExpect(jsonPath("$.name").value(itemDtoUpdate.getName()))
                .andExpect(jsonPath("$.description").value(itemDtoUpdate.getDescription()))
                .andExpect(jsonPath("$.available").value(itemDtoUpdate.getAvailable()))
                .andExpect(jsonPath("$.requestId").value(itemDtoUpdate.getRequestId()))
                .andExpect(status().isOk());

        verify(itemService).updateItem(any(), anyLong(), anyLong());
    }

    @DisplayName("Should find items by name or descr.")
    @Test
    @SneakyThrows
    public void shouldSearchItems() {
        String text = "renovation";
        List<ItemDto> itemDtoList = List.of(
                new ItemDto(2L, "Angle grinder", "grinding-wheel", true, 1L),
                new ItemDto(3L, "Vacuum cleaner", "industrial vacuum cleaner", true, 2L)
        );

        when(itemService.searchItems(anyString(), anyInt(), anyInt())).thenReturn(itemDtoList);

        mvc.perform(get("/items/search?text=", text, 10L, 2L))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(itemDtoList.get(0).getId()))
                .andExpect(jsonPath("$[0].name").value(itemDtoList.get(0).getName()))
                .andExpect(jsonPath("$[0].description").value(itemDtoList.get(0).getDescription()))
                .andExpect(jsonPath("$[0].available").value(itemDtoList.get(0).getAvailable()))
                .andExpect(jsonPath("$[0].requestId").value(itemDtoList.get(0).getRequestId()))
                .andExpect(jsonPath("$[1].id").value(itemDtoList.get(1).getId()))
                .andExpect(jsonPath("$[1].name").value(itemDtoList.get(1).getName()))
                .andExpect(jsonPath("$[1].description").value(itemDtoList.get(1).getDescription()))
                .andExpect(jsonPath("$[1].available").value(itemDtoList.get(1).getAvailable()))
                .andExpect(jsonPath("$[1].requestId").value(itemDtoList.get(1).getRequestId()))
                .andExpect(status().isOk());

        verify(itemService).searchItems(anyString(), anyInt(), anyInt());
    }

    @DisplayName("Should create comment")
    @Test
    @SneakyThrows
    public void shouldCreateComment() {
        CommentDto commentDtoCreate = new CommentDto(null, "cool", null, null, null);
        CommentDto commentDto = new CommentDto(1L, "cool", "John", SOME_TIME, 1L);

        when(itemService.createComment(any(), anyLong(), anyLong())).thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .content(mapper.writeValueAsString(commentDtoCreate))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header(USER_ID, 1))
                .andExpect(jsonPath("$.id").value(commentDto.getId()))
                .andExpect(jsonPath("$.text").value(commentDto.getText()))
                .andExpect(jsonPath("$.authorName").value(commentDto.getAuthorName()))
                .andExpect(jsonPath("$.created").value(commentDto.getCreated().format(DATE_FORMAT)))
                .andExpect(jsonPath("$.itemId").value(commentDto.getItemId()))
                .andExpect(status().isOk());

        verify(itemService).createComment(any(), anyLong(), anyLong());
    }

    private ItemDtoInfo getItemDtoInfo() {
        return new ItemDtoInfo(1L, "Rotor hammer", "rotary hammer for concrete", true,
                new BookingDtoInfo(1L, 1L,
                        SOME_TIME.minusDays(2), SOME_TIME.minusDays(1), APPROVED, 1L),
                new BookingDtoInfo(2L, 1L,
                        SOME_TIME.plusDays(1), SOME_TIME.plusDays(2), APPROVED, 1L),
                List.of(
                        new CommentDto(2L, "good", "Jamila", SOME_TIME, 1L),
                        new CommentDto(3L, "All is fine", "Morgana", SOME_TIME, 1L)
                )
        );
    }

    private List<ItemDtoInfo> getItemDtoInfoList() {
        return List.of(
                new ItemDtoInfo(1L, "Angle grinder", "grinding-wheel", true,
                        new BookingDtoInfo(1L, 1L, SOME_TIME.minusDays(2), SOME_TIME.minusDays(1),
                                APPROVED, 1L),
                        new BookingDtoInfo(2L, 1L, SOME_TIME.plusDays(1), SOME_TIME.plusDays(2),
                                APPROVED, 1L),
                        List.of(
                                new CommentDto(2L, "good", "Jamila", SOME_TIME, 1L),
                                new CommentDto(3L, "All is fine", "Morgana", SOME_TIME, 1L)
                        )),
                new ItemDtoInfo(2L, "Vacuum cleaner", "industrial vacuum cleaner", true,
                        new BookingDtoInfo(3L, 1L, SOME_TIME.minusDays(3), SOME_TIME.minusDays(1),
                                APPROVED, 2L
                        ),
                        new BookingDtoInfo(4L, 1L, SOME_TIME.plusDays(1), SOME_TIME.plusDays(2),
                                APPROVED, 2L
                        ),
                        new ArrayList<>())
        );
    }
}
