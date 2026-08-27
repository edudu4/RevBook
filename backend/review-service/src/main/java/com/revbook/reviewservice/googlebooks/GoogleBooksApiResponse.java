package com.revbook.reviewservice.googlebooks;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GoogleBooksApiResponse(List<Item> items) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Item(String id, VolumeInfo volumeInfo) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record VolumeInfo(String title, List<String> authors, List<String> categories, ImageLinks imageLinks) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record ImageLinks(String thumbnail) {
    }
}
