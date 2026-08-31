package com.revbook.reviewservice.dto;

public record CommentContentRequest(String content, Long parentId) {
}
