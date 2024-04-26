package com.yelm.memberpj.utils.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PageInfo {
    private int page;
    private int pageSize;
    private long totalElements;
    private int totalPages;
}
