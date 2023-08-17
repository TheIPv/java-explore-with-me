package ru.yandex.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.comment.service.CommentAdminService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/comments")
@Validated
@Slf4j
public class CommentAdminController {

    private final CommentAdminService commentService;

    @DeleteMapping("/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long commentId) {
        log.info("Received DELETE /admin/comments/{commentId} request");
        commentService.delete(commentId);
    }
}
