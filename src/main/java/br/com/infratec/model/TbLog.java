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
@SuperBuilder

@Entity
@Table(name = "tb_log")
public class TbLog extends Identifiable {

    @Size(max = 100)
    @Column(name = "evento")
    private String evento;

    @Column(name = "data_evento")
    private LocalDateTime dataEvento;

    @Size(max = 120)
    @Column(name = "ip")
    private String ip;

    @Size(max = 4096)
    @Column(name = "mensagem")
    private String mensagem;

    public TbLog() {
        this.dataEvento = LocalDateTime.now();
    }
}
