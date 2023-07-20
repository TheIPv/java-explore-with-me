package ru.yandex.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.comment.repository.CommentRepository;
import ru.yandex.practicum.exception.NotFoundException;


@Service
@RequiredArgsConstructor
public class CommentAdminServiceImpl implements CommentAdminService{

    private final CommentRepository commentRepository;

    @Override
    public void delete(Long commentId) {
        if(!commentRepository.existsById(commentId)) {
            throw new NotFoundException("Comment with ID " + commentId + " wasn't found");
        }
        commentRepository.deleteById(commentId);
    }
}
