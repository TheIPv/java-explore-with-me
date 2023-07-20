package ru.yandex.practicum.comment.service;

import org.springframework.data.domain.PageRequest;
import ru.yandex.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentPublicService {
    List<CommentDto> getByEvent(Long eventId, PageRequest pageable);

    CommentDto getById(Long commentId);
}
