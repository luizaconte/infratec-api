package br.com.infratec.dto;

import lombok.*;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

@Value
@Builder
@Jacksonized
public class DepartamentoDTO implements Serializable {

    Long id;
    String nome;
}
