package ru.practicum.shareit.item.controller;

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
import ru.practicum.shareit.comment.dto.CommentToGetDto;
import ru.practicum.shareit.comment.dto.CommentToReturnDto;
import ru.practicum.shareit.comment.service.CommentService;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UnavailableException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemToGetDto;
import ru.practicum.shareit.item.dto.ItemToReturnDto;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.practicum.shareit.utils.ResourcePool.*;

@WebMvcTest(ItemController.class)
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private ObjectMapper objectMapper;
    @MockBean
    private ItemService itemService;
    @MockBean
    private CommentService commentService;

    @Test
    void createItem() throws Exception {
        ItemToGetDto saveItemDto = read(saveItemGetDto, ItemToGetDto.class);
        ItemToReturnDto savedItemDto = read(savedItemReturnDto, ItemToReturnDto.class);

        Mockito
                .when(itemService.add(saveItemDto, 1L))
                .thenReturn(savedItemDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")
                                .content(objectMapper.writeValueAsString(saveItemDto))
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedItemDto)));
        Mockito.verify(itemService, Mockito.times(1)).add(saveItemDto, 1L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void createItemWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);

    }

    @Test
    void createItemWithWrongBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);

    }

    @Test
    void createItemWithNullBody() throws Exception {
        ItemToGetDto saveItemDto = null;
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")
                                .content(objectMapper.writeValueAsString(saveItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void createItemWithoutUserIdHeader() throws Exception {
        ItemToGetDto saveItemDto = read(saveItemGetDto, ItemToGetDto.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")
                                .content(objectMapper.writeValueAsString(saveItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void createItemWithWrongUserId() throws Exception {
        ItemToGetDto saveItemDto = read(saveItemGetDto, ItemToGetDto.class);

        Mockito
                .when(itemService.add(saveItemDto, 99L))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")

                                .header("X-Sharer-User-Id", 99L)
                                .content(objectMapper.writeValueAsString(saveItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(itemService, Mockito.times(1)).add(saveItemDto, 99L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void createItemWithNonValidatedItem() throws Exception {
        ItemToGetDto saveItemDto = read(saveItemGetDto, ItemToGetDto.class);
        saveItemDto.setName(null);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/")

                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(saveItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void updateItemWithIdInBody() throws Exception {
        ItemToGetDto updateItemDto = read(updateItemGetDto, ItemToGetDto.class);
        updateItemDto.setId(1L);
        ItemToReturnDto updatedItemDto = read(updatedItemReturnDto, ItemToReturnDto.class);

        Mockito
                .when(itemService.update(updateItemDto, 1L, 1L))
                .thenReturn(updatedItemDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedItemDto)));
        Mockito.verify(itemService, Mockito.times(1)).update(updateItemDto, 1L, 1L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItemWithWrongIdInBody() throws Exception {
        ItemToGetDto updateItemDto = read(updateItemGetDto, ItemToGetDto.class);
        updateItemDto.setId(3L);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void updateItemWithoutIdInBody() throws Exception {
        ItemToGetDto updateItemDto = read(updateItemGetDto, ItemToGetDto.class);
        ItemToReturnDto updatedItemDto = read(updatedItemReturnDto, ItemToReturnDto.class);

        Mockito
                .when(itemService.update(updateItemDto, 1L, 1L))
                .thenReturn(updatedItemDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(updatedItemDto)));
        Mockito.verify(itemService, Mockito.times(1)).update(updateItemDto, 1L,  1L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItemWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void updateItemWithWrongUser() throws Exception {
        ItemToGetDto updateItemDto = read(updateItemGetDto, ItemToGetDto.class);

        Mockito
                .when(itemService.update(updateItemDto, 1L, 99L))
                .thenThrow(UserNotFoundException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .header("X-Sharer-User-Id", 99L)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(itemService, Mockito.times(1)).update(updateItemDto, 1L,  99L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItemWithoutUserIdHeader() throws Exception {
        ItemToGetDto updateItemDto = read(updateItemGetDto, ItemToGetDto.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void updateNonExistentItem() throws Exception {
        ItemToGetDto updateItemDto = read(updateItemGetDto, ItemToGetDto.class);

        Mockito
                .when(itemService.update(updateItemDto, 99L, 1L))
                .thenThrow(ItemNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/99")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(updateItemDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1)).update(updateItemDto, 99L,  1L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItemWithWrongBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/items/1")
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void getItemById() throws Exception {
        ItemToReturnDto savedItemDto = read(savedItemReturnDto, ItemToReturnDto.class);

        Mockito
                .when(itemService.getById(1L, 1L))
                .thenReturn(savedItemDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedItemDto)));
        Mockito.verify(itemService, Mockito.times(1)).getById(1L,  1L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemByWrongId() throws Exception {
        Mockito
                .when(itemService.getById(99L, 1L))
                .thenThrow(ItemNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/99")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(itemService, Mockito.times(1)).getById(99L,  1L);
        Mockito.verifyNoMoreInteractions(itemService);
    }


    @Test
    void getItemByWrongUserId() throws Exception {
        Mockito
                .when(itemService.getById(1L, 99L))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/1")
                                .header("X-Sharer-User-Id", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(itemService, Mockito.times(1)).getById(1L,  99L);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemWithoutUserIdHeader() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void getItemByBadUrl() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/abc")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void getItemsListWithoutParams() throws Exception {
        ArrayList<ItemToReturnDto> savedItemsDto = read(savedItemsListReturnDto, new TypeReference<>() {});

        Mockito
                .when(itemService.getByOwnerId(1L, 0, 10))
                .thenReturn(savedItemsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedItemsDto)));
        Mockito.verify(itemService, Mockito.times(1)).getByOwnerId(1L,  0, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsListWithFromOnly() throws Exception {
        ArrayList<ItemToReturnDto> savedItemsDto = read(savedItemsListReturnDto, new TypeReference<>() {});

        Mockito
                .when(itemService.getByOwnerId(1L, 1, 10))
                .thenReturn(savedItemsDto.subList(1,3));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items?from=1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(savedItemsDto.subList(1,3))));
        Mockito.verify(itemService, Mockito.times(1)).getByOwnerId(1L,  1, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsListWithSizeOnly() throws Exception {
        ArrayList<ItemToReturnDto> savedItemsDto = read(savedItemsListReturnDto, new TypeReference<>() {});

        Mockito
                .when(itemService.getByOwnerId(1L, 0, 2))
                .thenReturn(savedItemsDto.subList(0,3));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items?size=2")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(savedItemsDto.subList(0,3))));
        Mockito.verify(itemService, Mockito.times(1)).getByOwnerId(1L,  0, 2);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsListWithAllParams() throws Exception {
        ArrayList<ItemToReturnDto> savedItemsDto = read(savedItemsListReturnDto, new TypeReference<>() {});

        Mockito
                .when(itemService.getByOwnerId(1L, 2, 1))
                .thenReturn(savedItemsDto.subList(1,2));
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items?from=2&size=1")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(savedItemsDto.subList(1,2))));
        Mockito.verify(itemService, Mockito.times(1)).getByOwnerId(1L,  2, 1);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsListWithWrongParams() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items?from=abc&size=cba")
                                .header("X-Sharer-User-Id", 1L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void getItemsListByNonExistentUser() throws Exception {
        Mockito
                .when(itemService.getByOwnerId(99L, 0, 10))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items")
                                .header("X-Sharer-User-Id", 99L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
        Mockito.verify(itemService, Mockito.times(1)).getByOwnerId(99L,  0, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getItemsListWithoutUserIdHeader() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(itemService);
    }

    @Test
    void searchItemsWithoutTextOnly() throws Exception {
        ArrayList<ItemToReturnDto> foundUserBookingsDto = read(savedItemsListForSearchReturnDto,
                new TypeReference<>() {});
        Mockito
                .when(itemService.search("search", 0, 10))
                .thenReturn(foundUserBookingsDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/search?text=search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(foundUserBookingsDto)));
        Mockito.verify(itemService, Mockito.times(1)).search("search",  0, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchItemsWithFrom() throws Exception {
        ArrayList<ItemToReturnDto> foundUserBookingsDto = read(savedItemsListForSearchReturnDto,
                new TypeReference<>() {});
        Mockito
                .when(itemService.search("search", 2, 10))
                .thenReturn(foundUserBookingsDto.subList(1, 3));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/search?text=search&from=2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(foundUserBookingsDto.subList(1, 3))));
        Mockito.verify(itemService, Mockito.times(1)).search("search",  2, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchItemsWithSize() throws Exception {
        ArrayList<ItemToReturnDto> foundUserBookingsDto = read(savedItemsListForSearchReturnDto,
                new TypeReference<>() {});
        Mockito
                .when(itemService.search("search", 0, 2))
                .thenReturn(foundUserBookingsDto.subList(0, 2));

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/search?text=search&size=2")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(foundUserBookingsDto.subList(0, 2))));
        Mockito.verify(itemService, Mockito.times(1)).search("search",  0, 2);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchItemsWithEmptyText() throws Exception {
        Mockito
                .when(itemService.search("", 0, 10))
                .thenReturn(new ArrayList<>());
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/search?text=")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(new ArrayList<>())));
        Mockito.verify(itemService, Mockito.times(1)).search("",  0, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchItemsWithoutParams() throws Exception {
        Mockito
                .when(itemService.search("", 0, 10))
                .thenReturn(new ArrayList<>());
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/items/search")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(new ArrayList<>())));
        Mockito.verify(itemService, Mockito.times(1)).search("",  0, 10);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void addComment() throws Exception {
        CommentToGetDto saveCommentDto = read(saveCommentGetDto, CommentToGetDto.class);
        CommentToReturnDto savedCommentDto = read(savedCommentReturnDto, CommentToReturnDto.class);
        Mockito
                .when(commentService.add(saveCommentDto, 1L,1L))
                .thenReturn(savedCommentDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(saveCommentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content()
                                .json(objectMapper.writeValueAsString(savedCommentDto)));
        Mockito.verify(commentService, Mockito.times(1)).add(saveCommentDto, 1L,1L);
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    void addCommentByNonExistentUser() throws Exception {
        CommentToGetDto saveCommentDto = read(saveCommentGetDto, CommentToGetDto.class);
        Mockito
                .when(commentService.add(saveCommentDto, 99L,1L))
                .thenThrow(UserNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .header("X-Sharer-User-Id", 99L)
                                .content(objectMapper.writeValueAsString(saveCommentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(commentService, Mockito.times(1)).add(saveCommentDto, 99L,1L);
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    void addCommentByUserWithoutPermission() throws Exception {
        CommentToGetDto saveCommentDto = read(saveCommentGetDto, CommentToGetDto.class);
        Mockito
                .when(commentService.add(saveCommentDto, 2L,1L))
                .thenThrow(UnavailableException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .header("X-Sharer-User-Id", 2L)
                                .content(objectMapper.writeValueAsString(saveCommentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verify(commentService, Mockito.times(1)).add(saveCommentDto, 2L,1L);
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    void addCommentToNonExistentItem() throws Exception {
        CommentToGetDto saveCommentDto = read(saveCommentGetDto, CommentToGetDto.class);
        Mockito
                .when(commentService.add(saveCommentDto, 1L,99L))
                .thenThrow(ItemNotFoundException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/99/comment")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(saveCommentDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(commentService, Mockito.times(1)).add(saveCommentDto, 1L,99L);
        Mockito.verifyNoMoreInteractions(commentService);
    }

    @Test
    void addCommentWithWrongBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(commentService);
    }

    @Test
    void addCommentWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/items/1/comment")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(commentService);
    }

    @Test
    void badMethodCall() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/items")
                                .header("X-Sharer-User-Id", 1L)
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());
        Mockito.verifyNoInteractions(commentService);
    }
}
