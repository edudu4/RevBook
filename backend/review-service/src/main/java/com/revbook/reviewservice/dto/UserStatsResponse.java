package com.revbook.reviewservice.dto;

public record UserStatsResponse(long reviewCount, long commentCount, long totalRatingsReceived) {
}
