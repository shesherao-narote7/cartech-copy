package com.cartechindia.util;

import com.cartechindia.dto.PageResponse;
import org.springframework.data.domain.Page;

public class PageResponseMapper {

    public static <T> PageResponse<T> toPageResponse(Page<T> page) {
        PageResponse<T> response = new PageResponse<>();
        response.setContent(page.getContent());
        response.setNumber(page.getNumber());
        response.setSize(page.getSize());
        response.setTotalElements(page.getTotalElements());
        response.setTotalPages(page.getTotalPages());
        response.setFirst(page.isFirst());
        response.setLast(page.isLast());
        return response;
    }
}
