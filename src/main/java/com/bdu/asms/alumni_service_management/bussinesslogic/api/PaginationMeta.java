package com.bdu.asms.alumni_service_management.bussinesslogic.api;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationMeta {
    private int page;              // 0-based page
    private int size;              // page size
    private long totalElements;    // total items
    private int totalPages;        // total pages
    private String sort;           // e.g., "name,asc"
}
