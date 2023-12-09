package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.practicum.shareit.booking.dto.BookingToGetDto;
import ru.practicum.shareit.booking.dto.BookingToReturnDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.practicum.shareit.utils.ResourcePool.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @MockBean
    private BookingService bookingService;

    @Test
    void createBooking() throws Exception {
        BookingToGetDto saveBookingDto = read(saveBookingGetDto, BookingToGetDto.class);
        BookingToReturnDto savedBookingDto = read(savedBookingReturnDto, BookingToReturnDto.class);

        Mockito
                .when(bookingService.add(saveBookingDto, 1L, saveBookingDto.getItemId()))
                .thenReturn(savedBookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings/")
                                .content(objectMapper.writeValueAsString(saveBookingDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedBookingDto)));
        Mockito.verify(bookingService, Mockito.times(1)).add(saveBookingDto,
                                                                                   1L,
                                                                                   saveBookingDto.getItemId());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createBookingWithoutBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings/")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createBookingWithWrongBody() throws Exception {

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings/1")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
    }

    @Test
    void createBookingWithoutUserIdHeader() throws Exception {
        BookingToGetDto saveBookingDto = read(saveBookingGetDto, BookingToGetDto.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings/")
                                .content(objectMapper.writeValueAsString(saveBookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void createBookingWithWrongUserId() throws Exception {
        BookingToGetDto saveBookingDto = read(saveBookingGetDto, BookingToGetDto.class);

        Mockito
                .when(bookingService.add(saveBookingDto, 99L, saveBookingDto.getItemId()))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/bookings/")
                                .content(objectMapper.writeValueAsString(saveBookingDto))
                                .header("X-Sharer-User-Id", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1)).add(saveBookingDto,
                99L,
                saveBookingDto.getItemId());
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void updateBookingWithoutApproved() throws Exception {
        BookingToGetDto toUpdateBookingDto = read(toUpdateBookingGetDto, BookingToGetDto.class);
        BookingToReturnDto updatedBookingDto = read(updatedBookingReturnDto, BookingToReturnDto.class);

        Mockito
                .when(bookingService.update(toUpdateBookingDto, 1L, 1L, null))
                .thenReturn(updatedBookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1")
                                .content(objectMapper.writeValueAsString(toUpdateBookingDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedBookingDto)));
        Mockito.verify(bookingService, Mockito.times(1)).update(toUpdateBookingDto,
                1L,
                1L,
                null);
        Mockito.verifyNoMoreInteractions(bookingService);

    }

    @Test
    void updateBookingWithApproved() throws Exception {
        BookingToGetDto toUpdateBookingDto = read(toUpdateBookingGetDto, BookingToGetDto.class);
        BookingToReturnDto updatedBookingDto = read(updatedBookingReturnDto, BookingToReturnDto.class);

        Mockito
                .when(bookingService.update(toUpdateBookingDto, 1L, 1L, true))
                .thenReturn(updatedBookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1?approved=true")
                                .content(objectMapper.writeValueAsString(toUpdateBookingDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedBookingDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .update(toUpdateBookingDto, 1L, 1L, true);
        Mockito.verifyNoMoreInteractions(bookingService);

    }

    @Test
    void updateBookingWithRejected() throws Exception {
        BookingToGetDto toUpdateBookingDto = read(toUpdateBookingGetDto, BookingToGetDto.class);
        BookingToReturnDto updatedBookingDto = read(updatedBookingReturnDto, BookingToReturnDto.class);

        Mockito
                .when(bookingService.update(toUpdateBookingDto, 1L, 1L, false))
                .thenReturn(updatedBookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1?approved=false")
                                .content(objectMapper.writeValueAsString(toUpdateBookingDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedBookingDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .update(toUpdateBookingDto, 1L, 1L, false);
        Mockito.verifyNoMoreInteractions(bookingService);

    }

    @Test
    void updateBookingWithoutId() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
    }

    @Test
    void updateBookingWithWrongUrl() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateBookingWithWrongId() throws Exception {
        BookingToGetDto updateBookingDto = read(toUpdateBookingGetDto, BookingToGetDto.class);

        Mockito
                .when(bookingService.update(updateBookingDto, 1L, 99L, null))
                .thenThrow(ItemNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/99")
                                .content(objectMapper.writeValueAsString(updateBookingDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    void updateBookingWithoutBody() throws Exception {

        Mockito
                .when(bookingService.update(null, 1L, 1L, null))
                .thenThrow(UnavailableException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateBookingWithoutUserIdHeader() throws Exception {
        BookingToGetDto toUpdateBookingDto = read(toUpdateBookingGetDto, BookingToGetDto.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1")
                                .content(objectMapper.writeValueAsString(toUpdateBookingDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void updateBookingWithWrongBody() throws Exception {

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/bookings/1")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getBookingById() throws Exception {
        BookingToReturnDto savedBookingDto = read(savedBookingReturnDto, BookingToReturnDto.class);

        Mockito
                .when(bookingService.getById(1L, 1L))
                .thenReturn(savedBookingDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedBookingDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getById(1L, 1L);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingByWrongBookingId() throws Exception {
        Mockito
                .when(bookingService.getById(99L, 1L))
                .thenThrow(BookingNotFoundException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/99")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(bookingService, Mockito.times(1))
                .getById(99L, 1L);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingWithWrongUserId() throws Exception {
        Mockito
                .when(bookingService.getById(99L, 1L))
                .thenThrow(BookingNotFoundException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/99")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(bookingService, Mockito.times(1))
                .getById(99L, 1L);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBookingWithWrongURL() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    void getUserBookingsWithoutParams() throws Exception {
        ArrayList<BookingToReturnDto> savedUserBookingsDto = read(savedUserBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getBookingsByState("ALL", 1L, 0, 10))
                .thenReturn(savedUserBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserBookingsDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsByState("ALL", 1L, 0, 10);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserBookingsWithAllParams() throws Exception {
        ArrayList<BookingToReturnDto> savedUserBookingsDto = read(savedUserBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getBookingsByState("CURRENT", 1L, 1, 8))
                .thenReturn(savedUserBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings?state=CURRENT&from=1&size=8")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserBookingsDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsByState("CURRENT", 1L, 1, 8);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserBookingsWithTwoParams() throws Exception {
        ArrayList<BookingToReturnDto> savedUserBookingsDto = read(savedUserBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getBookingsByState("ALL", 1L, 1, 8))
                .thenReturn(savedUserBookingsDto.subList(1, 3));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings?from=1&size=8")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(
                                objectMapper.writeValueAsString(savedUserBookingsDto.subList(1, 3))));
        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsByState("ALL", 1L, 1, 8);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserBookingsWithNegativeFromParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings?from=-1&")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(bookingService);
    }

    @Test
    void getUserBookingsWithZeroSizeParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings?size=0")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(bookingService);
    }

    @Test
    void getUserBookingWithNonexistentParameter() throws Exception {
        ArrayList<BookingToReturnDto> savedUserBookingsDto = read(savedUserBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getBookingsByState("ALL", 1L, 0, 10))
                .thenReturn(savedUserBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings?param=abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserBookingsDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsByState("ALL", 1L, 0, 10);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserItemsBookingsWithoutParams() throws Exception {
        ArrayList<BookingToReturnDto> savedUserItemsBookingsDto = read(savedUserItemsBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getUserItemsBookingsByState("ALL", 1L, 0, 10))
                .thenReturn(savedUserItemsBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserItemsBookingsDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getUserItemsBookingsByState("ALL", 1L, 0, 10);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserItemsBookingsWithAllParams() throws Exception {
        ArrayList<BookingToReturnDto> savedUserItemsBookingsDto = read(savedUserItemsBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getUserItemsBookingsByState("CURRENT", 1L, 1, 8))
                .thenReturn(savedUserItemsBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner?state=CURRENT&from=1&size=8")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserItemsBookingsDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getUserItemsBookingsByState("CURRENT", 1L, 1, 8);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserItemsBookingsWithTwoParams() throws Exception {
        ArrayList<BookingToReturnDto> savedUserItemsBookingsDto = read(savedUserItemsBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getUserItemsBookingsByState("ALL", 1L, 1, 8))
                .thenReturn(savedUserItemsBookingsDto.subList(1, 3));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner?from=1&size=8")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(
                                objectMapper.writeValueAsString(savedUserItemsBookingsDto.subList(1, 3))));
        Mockito.verify(bookingService, Mockito.times(1))
                .getUserItemsBookingsByState("ALL", 1L, 1, 8);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getUserItemsBookingsWithNegativeFromParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner?from=-1&")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(bookingService);
    }

    @Test
    void getUserItemsBookingsWithZeroSizeParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner?size=0")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(bookingService);
    }

    @Test
    void getUserItemsBookingsWithNonexistentParameter() throws Exception {
        ArrayList<BookingToReturnDto> savedUserItemsBookingsDto = read(savedUserItemsBookingsListReturnDto,
                new TypeReference<>() {});

        Mockito
                .when(bookingService.getUserItemsBookingsByState("ALL", 1L, 0, 10))
                .thenReturn(savedUserItemsBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/bookings/owner?param=abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserItemsBookingsDto)));
        Mockito.verify(bookingService, Mockito.times(1))
                .getUserItemsBookingsByState("ALL", 1L, 0, 10);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void badMethodCall() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/bookings")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
        Mockito.verifyNoInteractions(bookingService);
    }
}
