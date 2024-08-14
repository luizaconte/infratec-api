package br.com.infratec.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)

@Entity
@Table(name = "tb_departamento")
public class TbDepartamento extends Identifiable {

    @Size(max = 100)
    @Column(name = "nome")
    private String nome;
}
