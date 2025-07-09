package com.centralService.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PagedModel<T> {
    private List<T> content;
}