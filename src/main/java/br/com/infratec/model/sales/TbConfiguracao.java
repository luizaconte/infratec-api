package br.com.infratec.model.sales;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor

@Entity
@Table(name = "tb_configuracao")
public class TbConfiguracao implements Serializable {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "client_id")
    @Size(max = 100)
    private String clientId;

    @Column(name = "client_secret")
    @Size(max = 100)
    private String clientSecret;

    @Size(max = 100)
    @Column(name = "code")
    private String code;

    @Size(max = 255)
    @Column(name = "redirect_uri")
    private String redirectUri;

    @Size(max = 255)
    @Column(name = "access_token")
    private String accessToken;

    @Size(max = 255)
    @Column(name = "refresh_token")
    private String refreshToken;

    @Size(max = 15)
    @Column(name = "seller_id")
    private String sellerId;

    @Column(name = "data_alteracao")
    private LocalDateTime dataAlteracao;

    @Column(name = "token_expiration")
    private LocalDateTime tokenExpiration;

    @Size(max = 14)
    @Column(name = "cnpj")
    private String cnpj;

    @Size(max = 100)
    @Column(name = "nome")
    private String nome;

    @Size(max = 100)
    @Column(name = "nome_fantasia")
    private String nomeFantasia;

    @Size(max = 100)
    @Column(name = "logradouro")
    private String logradouro;

    @Size(max = 10)
    @Column(name = "numero")
    private String numero;

    @Size(max = 35)
    @Column(name = "cpl")
    private String cpl;

    @Size(max = 35)
    @Column(name = "bairro")
    private String bairro;

    @Size(max = 10)
    @Column(name = "cod_municipio")
    private String codMunicipio;

    @Size(max = 35)
    @Column(name = "nome_municipio")
    private String nomeMunicipio;

    @Size(max = 10)
    @Column(name = "cep")
    private String cep;

    @Size(max = 2)
    @Column(name = "uf")
    private String uf;

    @Size(max = 11)
    @Column(name = "telefone")
    private String telefone;

    @Size(max = 30)
    @Column(name = "ie")
    private String ie;

    @Size(max = 5)
    @Column(name = "crt")
    private String crt;

    @Size(max = 50)
    @Column(name = "content_type")
    private String contentType;

    @Column(name = "certificado")
    private byte[] certificado;

    @Size(max = 255)
    @Column(name = "nome_arquivo")
    private String nomeArquivo;

    @Size(max = 35)
    @Column(name = "senha")
    private String senha;

    @Column(name = "num_nfe")
    private Long numNfe;

    @Column(name = "processar_automatico")
    private Boolean processarAutomatico;

    public TbConfiguracao(Integer id) {
        this.id = id;
    }
}
