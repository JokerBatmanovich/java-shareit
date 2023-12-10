package ru.practicum.shareit.user.controller;

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
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static ru.practicum.shareit.utils.ResourcePool.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;

    @Test
    void createUser() throws Exception {
        UserToGetDto saveUserDto = read(saveUserGetDto, UserToGetDto.class);
        UserToReturnDto savedUserDto = read(savedUserReturnDto, UserToReturnDto.class);

        Mockito
                .when(userService.add(saveUserDto))
                .thenReturn(savedUserDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserDto)));

        Mockito.verify(userService, Mockito.times(1)).add(saveUserDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void createUserWithoutName() throws Exception {
        UserToGetDto saveUserDto = read(saveUserGetDto, UserToGetDto.class);
        saveUserDto.setName("");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void createUserWrongEmail() throws Exception {
        UserToGetDto saveUserDto = read(saveUserGetDto, UserToGetDto.class);
        saveUserDto.setEmail("abc");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void createUserWithExistentEmail() throws Exception {
        UserToGetDto saveUserDto = read(saveUserGetDto, UserToGetDto.class);

        Mockito
                .when(userService.add(saveUserDto))
                .thenThrow(EmailAlreadyExistException.class);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isConflict());

        Mockito.verify(userService, Mockito.times(1)).add(saveUserDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void createUserWithEmptyBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void createUserWithWrongBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.post("/users")
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8));

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void updateUser() throws Exception {
        UserToGetDto saveUserDto = read(updateUserGetDto, UserToGetDto.class);
        UserToGetDto saveUserDtoWithId = saveUserDto;
        saveUserDtoWithId.setId(1L);
        UserToReturnDto savedUserDto = read(updatedUserReturnDto, UserToReturnDto.class);

        Mockito
                .when(userService.update(saveUserDtoWithId, 1L))
                .thenReturn(savedUserDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserDto)));

        Mockito.verify(userService, Mockito.times(1)).update(saveUserDto, 1L);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUserWithWrongBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(objectMapper.writeValueAsString("{\"lokll\": 12}"))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void updateUserWithNullBody() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(objectMapper.writeValueAsString(null))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void updateUserWithWrongIdI() throws Exception {
        UserToGetDto saveUserDto = read(updateUserGetDto, UserToGetDto.class);
        saveUserDto.setId(99L);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void updateUserWithNonExistentId() throws Exception {
        UserToGetDto saveUserDto = read(updateUserGetDto, UserToGetDto.class);
        saveUserDto.setId(99L);

        Mockito
                .when(userService.update(saveUserDto, 99L))
                .thenThrow(UserNotFoundException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/99")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(userService, Mockito.times(1)).update(saveUserDto, 99L);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUserWithWrongEmail() throws Exception {
        UserToGetDto saveUserDto = read(updateUserGetDto, UserToGetDto.class);
        saveUserDto.setEmail("abc");

        mockMvc
                .perform(
                        MockMvcRequestBuilders.patch("/users/1")
                                .content(objectMapper.writeValueAsString(saveUserDto))
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void getUserById() throws Exception {
        UserToReturnDto savedUserDto = read(savedUserReturnDto, UserToReturnDto.class);

        Mockito
                .when(userService.getById(1L))
                .thenReturn(savedUserDto);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserDto)));

        Mockito.verify(userService, Mockito.times(1)).getById(1L);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByWrongId() throws Exception {
        Mockito
                .when(userService.getById(99L))
                .thenThrow(UserNotFoundException.class);

        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users/99")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isNotFound());

        Mockito.verify(userService, Mockito.times(1)).getById(99L);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void getUserByWrongUrl() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users/abc")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void getUsersList() throws Exception {
        ArrayList<UserToReturnDto> savedUserBookingsDto = read(savedUsersReturnDto, new TypeReference<>() {});
        Mockito
                .when(userService.getAll())
                .thenReturn(savedUserBookingsDto);
        mockMvc
                .perform(
                        MockMvcRequestBuilders.get("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(
                        MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(savedUserBookingsDto)));

        Mockito.verify(userService, Mockito.times(1)).getAll();
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void useWrongMethod() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders.put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(MockMvcResultMatchers.status().isMethodNotAllowed());

    }
}
