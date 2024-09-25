package br.com.infratec.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
public class DepartamentoDTO implements Serializable {

    Long id;

    @NotNull
    String nome;
}
