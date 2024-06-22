package com.pg.customercare.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public class PaginationUtil {
    private static final int MAX_PAGE_SIZE = 20;

    public static Pageable createPageRequest(int page, int size) {
        size = Math.min(size, MAX_PAGE_SIZE);
        return PageRequest.of(page, size);
    }
}
