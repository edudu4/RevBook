package com.revbook.reviewservice.dto;

/** Usado tanto para criar (POST) quanto para editar (PUT) um comentário — só o "content". */
public record CommentContentRequest(String content) {
}
