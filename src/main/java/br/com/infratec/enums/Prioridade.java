package br.com.infratec.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Prioridade {
    ALTA(0, "Alta"),
    MEDIA(1, "Média"),
    BAIXA(2, "Média");

    private final int id;
    private final String descricao;

    Prioridade(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public static Prioridade of(final int id) {
        return Arrays.stream(Prioridade.values()).filter(prioridade -> prioridade.getId() == id).findFirst().orElse(BAIXA);
    }
}
