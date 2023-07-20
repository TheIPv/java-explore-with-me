package ru.yandex.practicum.comment.mapper;

import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.event.model.Event;
import ru.yandex.practicum.user.mapper.UserMapper;
import ru.yandex.practicum.user.model.User;

import java.time.LocalDateTime;

public class CommentMapper {
    public static Comment toModel(Event event, User user, NewCommentDto newCommentDto) {
        return Comment.builder()
                .text(newCommentDto.getText())
                .createdOn(LocalDateTime.now())
                .event(event)
                .author(user)
                .build();
    }

    public static CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .author(UserMapper.toUserShortDto(comment.getAuthor()))
                .eventId(comment.getEvent().getId())
                .createdOn(comment.getCreatedOn())
                .build();
    }
}
