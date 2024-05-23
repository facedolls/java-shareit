package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.ValidationException;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId).stream().findFirst().orElseThrow(() -> {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        });

        log.info("User was received by id={}", userId);
        return userMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    public Collection<UserDto> getAllUsers(Integer from, Integer size) {
        log.info("All users have been received");
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(Sort.Order.asc("id")));
        List<User> allUsers = userRepository.findAll(pageable).getContent();
        return userMapper.toUserDtoCollection(allUsers);
    }

    @Transactional
    public UserDto createUser(UserDto userDto) {
        try {
            User createdUser = userRepository.save(userMapper.toUser(userDto));
            log.info("User has been created={}", createdUser);
            return userMapper.toUserDto(createdUser);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMostSpecificCause().getMessage();
            log.warn("User has not been created due to data integrity violation: {}", errorMessage);
            if (errorMessage != null && errorMessage.contains("PUBLIC.USERS(EMAIL")) {
                throw new ConflictException("Email " + userDto.getEmail() + " already exists");
            } else {
                throw new ConflictException("User has not been created " + userDto + ". Error " + errorMessage);
            }
        }
    }

    @Transactional
    public UserDto updateUser(Long userId, UserDto userDtoNew) {
        User userOld = userRepository.findById(userId).stream().findFirst().orElseThrow(() -> {
            log.warn("User with this id={} not already exists", userId);
            throw new ValidationException("User with this id=" + userId + " not already exists");
        });

        getExceptionIfEmailExistsAndItIsAlien(userDtoNew.getEmail(), userOld.getEmail());
        User updatedUser = userRepository.save(setUser(userOld, userDtoNew));
        log.info("User has been updated={}", updatedUser);
        return userMapper.toUserDto(updatedUser);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        log.info("User with id={} deleted", userId);
        userRepository.deleteById(userId);
    }

    public User findById(Long userId) {
        return userRepository.findById(userId).stream().findFirst().orElseThrow(() -> {
            log.warn("User with id={} not found", userId);
            throw new NotFoundException("User with id=" + userId + " not found");
        });
    }

    private void getExceptionIfEmailExistsAndItIsAlien(String emailNew, String emailOld) {
        if (emailNew == null) {
            return;
        }
        boolean isExistEmail = userRepository.existsByEmail(emailNew);
        if (isExistEmail && !emailNew.equals(emailOld)) {
            log.warn("User with this email={} already exists", emailNew);
            throw new ConflictException("User with this email=" + emailNew + " already exists");
        }
    }

    private User setUser(User userOld, UserDto userDtoNew) {
        if (userDtoNew.getName() != null && !userDtoNew.getName().isEmpty()) {
            userOld.setName(userDtoNew.getName());
        }
        if (userDtoNew.getEmail() != null && !userDtoNew.getEmail().isEmpty()) {
            userOld.setEmail(userDtoNew.getEmail());
        }
        return userOld;
    }
}
