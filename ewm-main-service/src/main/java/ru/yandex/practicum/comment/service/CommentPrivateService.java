package ru.yandex.practicum.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;

import java.util.List;

public interface CommentPrivateService {
    List<CommentDto> get(Long userId, PageRequest pageable);
    CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto);
    CommentDto update(Long userId, Long commentId, NewCommentDto newCommentDto);
    void delete(Long userId, Long commentId);
}
