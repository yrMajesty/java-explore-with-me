package ru.practicum.mainservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.mainservice.dto.user.UserDto;
import ru.practicum.mainservice.entity.User;
import ru.practicum.mainservice.exception.NoFoundObjectException;
import ru.practicum.mainservice.repository.UserRepository;
import ru.practicum.mainservice.service.mapper.UserMapper;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDto createUser(UserDto request) {
        User user = userMapper.fromDto(request);
        User saveUser = userRepository.save(user);

        return userMapper.toDto(saveUser);
    }

    public List<UserDto> getUsersByIds(List<Long> ids, Integer from, Integer size) {
        if (Objects.isNull(ids) || ids.isEmpty()) {
            List<User> users = userRepository.findAll(PageRequest.of(from / size, size)).getContent();
            return userMapper.toDtos(users);
        }

        List<User> users = userRepository.findAllByIdIn(ids, PageRequest.of(from / size, size)).getContent();
        return userMapper.toDtos(users);
    }

    @Transactional
    public void deleteUserById(Long userId) {
        checkExistUserById(userId);
        userRepository.deleteById(userId);
    }

    public User getUserByIdIfExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NoFoundObjectException(String.format("User with id='%s' not found", userId)));
    }

    public void checkExistUserById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NoFoundObjectException(String.format("User with id='%s' not found", userId));
        }
    }
}
