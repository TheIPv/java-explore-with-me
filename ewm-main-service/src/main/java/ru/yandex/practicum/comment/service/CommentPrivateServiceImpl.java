package ru.yandex.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comment.dto.CommentDto;
import ru.yandex.practicum.comment.dto.NewCommentDto;
import ru.yandex.practicum.comment.mapper.CommentMapper;
import ru.yandex.practicum.comment.model.Comment;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.event.service.EventPublicService;
import ru.yandex.practicum.exception.ForbiddenException;
import ru.yandex.practicum.exception.NotFoundException;
import ru.yandex.practicum.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentPrivateServiceImpl implements CommentPrivateService {
    private final CommentRepository commentRepository;
    private final EventPublicService eventService;
    private final UserService userService;

    @Override
    public List<CommentDto> get(Long userId, PageRequest pageable) {
        return commentRepository.findAllByAuthor_Id(userId, pageable)
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto create(Long userId, Long eventId, NewCommentDto newCommentDto) {
        Comment comment = CommentMapper.toModel(eventService.getEventById(eventId),
                userService.get(userId), newCommentDto);

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto update(Long userId, Long commentId, NewCommentDto newCommentDto) {
        isAuthor(userId, commentId);

        Comment comment = commentRepository.findById(commentId).get();
        comment.setText(newCommentDto.getText());

        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }

    @Override
    public void delete(Long userId, Long commentId) {
        isAuthor(userId, commentId);
        commentRepository.deleteById(commentId);
    }

    private void isAuthor(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with ID " + commentId
                        + "wasn't found"));

        if (comment.getAuthor().getId() != userId) {
            throw new ForbiddenException("User with ID " + commentId
                    + " isn't author of comment with ID" + commentId);
        }
    }
}
