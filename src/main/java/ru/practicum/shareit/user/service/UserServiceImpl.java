package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NoPermissionException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.dto.UserToGetDto;
import ru.practicum.shareit.user.dto.UserToReturnDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.JpaUserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    final JpaUserRepository userRepository;
    final UserMapper userMapper;

    @Override
    public UserToReturnDto getById(Long userId) {
        return userMapper.toUserToReturnDto(checkUserExistence(userId));
    }

    @Override
    public List<UserToReturnDto> getAll() {
        return userMapper.toUserToReturnDtoList(userRepository.findAll());
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public UserToReturnDto add(UserToGetDto user) {
        try {
            return userMapper.toUserToReturnDto(userRepository.save(userMapper.toUser(user)));
        } catch (DataIntegrityViolationException e) {
            throw new EmailAlreadyExistException(user.getEmail());
        }
    }


    @Override
    public UserToReturnDto update(UserToGetDto newUser, Long userId) {
        if (!newUser.getId().equals(userId)) {
            throw new NoPermissionException(userId);
        }
        User oldUser = checkUserExistence(newUser.getId());
        if (newUser.getName() != null) {
            oldUser.setName(newUser.getName());
        }
        if (newUser.getEmail() != null) {
            oldUser.setEmail(newUser.getEmail());
        }
        return userMapper.toUserToReturnDto(userRepository.save(oldUser));
    }



    private User checkUserExistence(Long userId) {
        try {
            User user = userRepository.getReferenceById(userId);
            System.out.println(user);
            return user;
        } catch (EntityNotFoundException e) {
            throw new UserNotFoundException(userId);
        }

    }
}
