package br.com.infratec.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.jackson.Jacksonized;
import org.springframework.data.domain.Sort;

import java.util.Objects;

@Getter
@Setter
@Builder
@Jacksonized
public class PageRequestDTO {

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int DEFAULT_PAGE_INDEX = 0;

    private String query;
    private Integer pageSize;
    private Integer pageIndex;

    private final String sort;

    public Integer getPageSize() {
        return Objects.isNull(pageSize) ? DEFAULT_PAGE_SIZE : pageSize;
    }

    public Integer getPageIndex() {
        return Objects.isNull(pageIndex) ? DEFAULT_PAGE_INDEX : pageIndex;
    }

    public Sort getSortArgument() {
        String sortArguments = this.sort;
        Sort toReturn = Sort.unsorted();
        if (sortArguments == null || sortArguments.isEmpty())
            return toReturn;
        else {
            sortArguments = sortArguments.replaceAll("'", "").replaceAll("\"", "");
            for (String sortArgument : sortArguments.split("(?=[@$])")) {
                if (sortArgument.charAt(0) == '$')
                    toReturn = toReturn.and(Sort.by(sortArgument.substring(1)).descending());
                else if (sortArgument.charAt(0) == '@')
                    toReturn = toReturn.and(Sort.by(sortArgument.substring(1)).ascending());
                else
                    throw new RuntimeException();
            }
            return toReturn;
        }
    }
}
