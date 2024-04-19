package ru.practicum.shareit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

@Service
public class CheckConsistency {
    private final UserService userService;
    private final ItemService itemService;

    @Autowired
    public CheckConsistency(UserService userService, ItemService itemService) {
        this.userService = userService;
        this.itemService = itemService;
    }

    public boolean isExistUser(Long userId) {
        boolean exist = userService.getUserById(userId) != null;
        return exist;
    }

    public void deleteItemsByUser(Long userId) {
        itemService.deleteItemsByOwner(userId);
    }
}
