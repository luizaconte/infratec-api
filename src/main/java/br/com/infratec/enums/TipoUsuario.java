package br.com.infratec.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum TipoUsuario {
    ADMIN(0, "Administrador"),
    SIMPLES(1, "Acesso Simples"),
    SUPORTE(2, "Suporte");

    private final Integer id;
    private final String descricao;

    TipoUsuario(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public static TipoUsuario of(final Integer id) {
        return Arrays.stream(TipoUsuario.values()).filter(prioridade -> prioridade.getId() == id).findFirst().orElse(SIMPLES);
    }
}
