package br.com.infratec.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DepartamentoDTO {

    private Long id;
    private String nome;
}
