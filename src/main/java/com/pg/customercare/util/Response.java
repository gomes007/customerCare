package com.pg.customercare.util;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Response<T> {
    private static final long FIRST_PAGE = 1;

    private List<T> items;
    private Long itemsPerPage;
    private Long currentPage;
    private Long totalRecordsQuantity;

    public Long getTotalPages() {
        long pages = this.totalRecordsQuantity / this.itemsPerPage;

        if (this.totalRecordsQuantity % this.itemsPerPage > 0) {
            pages++;
        }

        return pages;
    }

    public Long getPreviousPage() {
        if (this.currentPage > FIRST_PAGE) {
            return this.currentPage - 1;
        }
        return null;
    }
}
