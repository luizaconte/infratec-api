package br.com.infratec.model.sales;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_municipio")
public class TbMunicipio extends Identifiable {

    @Column(name = "codigo", nullable = false)
    private String codigo;

    @Size(max = 255)
    @NotNull
    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Size(max = 2)
    @NotNull
    @Column(name = "uf", length = 2)
    private String uf;
}
