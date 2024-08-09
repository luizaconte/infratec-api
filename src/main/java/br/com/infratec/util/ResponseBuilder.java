package br.com.infratec.util;

import br.com.infratec.dto.PageResponseDTO;
import br.com.infratec.dto.meli.PageResult;
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

    public static PageResponseDTO buildMeli(final PageResult pageResult) {
        return PageResponseDTO.builder()
                .result(pageResult.getResults())
                .count(pageResult.getPaging().getTotal())
                .currentPage(pageResult.getPaging().getOffset() + 1)
                .totalPages(pageResult.getPaging().getLimit())
                .build();
    }

}
