package ru.practicum.shareit.request.controller;

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
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestToGetDto;
import ru.practicum.shareit.request.dto.ItemRequestToReturnDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.practicum.shareit.utils.ResourcePool.*;

@WebMvcTest(ItemRequestController.class)
class ItemRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService requestService;

    @Test
    void createRequest() throws Exception {
        ItemRequestToGetDto saveRequestDto = read(saveRequestGetDto, ItemRequestToGetDto.class);
        ItemRequestToReturnDto savedRequestDto = read(savedRequestReturnDto, ItemRequestToReturnDto.class);

        Mockito
                .when(requestService.add(saveRequestDto, 1L))
                .thenReturn(savedRequestDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests")
                                .content(objectMapper.writeValueAsString(saveRequestDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedRequestDto)));
        Mockito.verify(requestService, Mockito.times(1)).add(saveRequestDto, 1L);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void createRequestWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests/")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void createRequestWithWrongBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests/")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void createRequestWithoutUserIdHeader() throws Exception {
        ItemRequestToGetDto saveRequestDto = read(saveRequestGetDto, ItemRequestToGetDto.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests/")
                                .content(objectMapper.writeValueAsString(saveRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void createRequestNullBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests/")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void createRequestWithWrongUserId() throws Exception {
        ItemRequestToGetDto saveRequestDto = read(saveRequestGetDto, ItemRequestToGetDto.class);

        Mockito
                .when(requestService.add(saveRequestDto, 99L))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests/")
                                .header("X-Sharer-User-Id", 99L)
                                .content(objectMapper.writeValueAsString(saveRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(requestService, Mockito.times(1)).add(saveRequestDto, 99L);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void createItemWithNonValidatedItem() throws Exception {
        ItemRequestToGetDto saveRequestDto = read(saveRequestGetDto, ItemRequestToGetDto.class);
        saveRequestDto.setDescription("");
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/requests/")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(saveRequestDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);
    }

    @Test
    void getRequestById() throws Exception {
        ItemRequestToReturnDto savedRequestDto = read(savedRequestReturnDto, ItemRequestToReturnDto.class);

        Mockito
                .when(requestService.getById(1L, 1L))
                .thenReturn(savedRequestDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Mockito.verify(requestService, Mockito.times(1)).getById(1L,  1L);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestWithWrongUserId() throws Exception {
        Mockito
                .when(requestService.getById(1L, 99L))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/1")
                                .header("X-Sharer-User-Id", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(requestService, Mockito.times(1)).getById(1L,  99L);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getRequestWithoutUserIdHeader() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void getRequestByBadUrl() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void getUserRequests() throws Exception {
        ArrayList<ItemRequestToReturnDto> savedUserRequests = read(savedUserRequestReturnDtoList,
                new TypeReference<>() {});

        Mockito
                .when(requestService.getByOwner(1L))
                .thenReturn(savedUserRequests);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserRequests)));
        Mockito.verify(requestService, Mockito.times(1)).getByOwner(1L);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getNonExistentUserRequests() throws Exception {
        Mockito
                .when(requestService.getByOwner(99L))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests")
                                .header("X-Sharer-User-Id", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(requestService, Mockito.times(1)).getByOwner(99L);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getUserRequestsWithoutUserIdHeader() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

//******

    @Test
    void getAllRequestsWithoutParams() throws Exception {
        ArrayList<ItemRequestToReturnDto> savedUserRequests = read(savedUserRequestReturnDtoList,
                new TypeReference<>() {});

        Mockito
                .when(requestService.getAll(1L, 0, 10))
                .thenReturn(savedUserRequests);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserRequests)));
        Mockito.verify(requestService, Mockito.times(1)).getAll(1L, 0, 10);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getAllRequestsWithFromParam() throws Exception {
        ArrayList<ItemRequestToReturnDto> savedUserRequests = read(savedUserRequestReturnDtoList,
                new TypeReference<>() {});

        Mockito
                .when(requestService.getAll(1L, 2, 10))
                .thenReturn(savedUserRequests.subList(1, 3));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all?from=2")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(savedUserRequests.subList(1, 3))));
        Mockito.verify(requestService, Mockito.times(1)).getAll(1L, 2, 10);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getAllRequestsWithSizeParam() throws Exception {
        ArrayList<ItemRequestToReturnDto> savedUserRequests = read(savedUserRequestReturnDtoList,
                new TypeReference<>() {});

        Mockito
                .when(requestService.getAll(1L, 0, 2))
                .thenReturn(savedUserRequests.subList(0, 2));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all?size=2")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(savedUserRequests.subList(0, 2))));
        Mockito.verify(requestService, Mockito.times(1)).getAll(1L, 0, 2);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getAllRequestsWithNegativeFromParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all?from=-1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);
    }

    @Test
    void getAllRequestsWithZeroSizeParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all?size=0")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);
    }

    @Test
    void getAllRequestsWithWrongParam() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all?size=abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);
    }

    @Test
    void getAllRequestsByNonExistentUser() throws Exception {
        Mockito
                .when(requestService.getAll(99L, 0, 10))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all")
                                .header("X-Sharer-User-Id", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(requestService, Mockito.times(1)).getAll(99L, 0, 10);
        Mockito.verifyNoMoreInteractions(requestService);
    }

    @Test
    void getAllRequestsWithoutUserIdHeader() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/requests/all")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(requestService);

    }

    @Test
    void badMethodCall() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/requests")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
        Mockito.verifyNoInteractions(requestService);
    }
}
