package br.com.infratec.util;

import br.com.infratec.dto.PageResponseDTO;
import org.springframework.data.domain.Page;

import java.util.List;

public class ResponseBuilder {

    public static PageResponseDTO build(final Page<?> result, final List<?> data) {
        return PageResponseDTO.builder()
                .result(data)
                .count((int) result.getTotalElements())
                .currentPage(result.getNumber() + 1)
                .totalPages(result.getTotalPages())
                .build();
    }

}
