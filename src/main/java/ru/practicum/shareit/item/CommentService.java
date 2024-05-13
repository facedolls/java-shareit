package ru.practicum.shareit.item;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;

    public List<Comment> getCommentsByItemId(Long itemId) {
        return commentRepository.findByItem_Id(itemId);
    }

    public List<Comment> getCommentsByItemIdIn(List<Long> itemsId) {
        return commentRepository.findAllByItem_IdIn(itemsId);
    }

    public Comment saveComment(Comment comment) {
        return commentRepository.save(comment);
    }
}
