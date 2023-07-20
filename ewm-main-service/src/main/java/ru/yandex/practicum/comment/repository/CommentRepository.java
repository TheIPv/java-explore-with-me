package ru.yandex.practicum.comment.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yandex.practicum.comment.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthor_Id(Long authorId, Pageable pageable);

    List<Comment> findAllByEventId(Long id, Pageable pageable);

    @Query(" SELECT COUNT(c) > 0 FROM comments c " +
            "WHERE c.id = :commentId " +
            "AND c.author.id = :userId ")
    boolean isAuthor(Long commentId, Long userId);
}
