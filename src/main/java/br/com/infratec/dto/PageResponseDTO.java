package br.com.infratec.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Getter
@Builder
@Jacksonized
public class PageResponseDTO {

    private final Integer currentPage;
    private final Integer totalPages;
    private final Integer count;
    private final List<?> result;

}
