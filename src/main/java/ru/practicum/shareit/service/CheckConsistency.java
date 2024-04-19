package ru.practicum.shareit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@RequiredArgsConstructor
@Service
public class CheckConsistency {
    private final UserService userService;
    private final ItemService itemService;

    public boolean isExistUser(Long userId) {
        boolean exist = userService.getUserById(userId) != null;
        return exist;
    }

    public void deleteItemsByUser(Long userId) {
        itemService.deleteItemsByOwner(userId);
    }
}
