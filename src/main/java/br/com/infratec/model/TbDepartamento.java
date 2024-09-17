package br.com.infratec.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter

@Entity
@Table(name = "tb_departamento")
public class TbDepartamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Size(max = 100)
    @Column(name = "nome")
    private String nome;
}
