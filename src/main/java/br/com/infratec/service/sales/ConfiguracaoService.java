package br.com.infratec.service.sales;

import br.com.infratec.config.Constants;
import br.com.infratec.dto.ConfiguracaoDTO;
import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.dto.TokenResponse;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.sales.TbConfiguracao;
import br.com.infratec.repository.sales.ConfiguracaoRepository;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ConfiguracaoService {
    private final ConfiguracaoRepository configuracaoRepository;

    private final RSQLParser rsqlParser;

    private final CustomRsqlVisitor<TbConfiguracao> cboCustomRsqlVisitor = new CustomRsqlVisitor<>();

    @Autowired
    public ConfiguracaoService(ConfiguracaoRepository configuracaoRepository, RSQLParser rsqlParser) {
        this.configuracaoRepository = configuracaoRepository;
        this.rsqlParser = rsqlParser;
    }

    public Optional<TbConfiguracao> findById(final Integer id) {
        return configuracaoRepository.findById(id);
    }

    public Page<TbConfiguracao> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return configuracaoRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbConfiguracao> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(cboCustomRsqlVisitor);
        return configuracaoRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }

    public void atualizar(TbConfiguracao tbConfiguracao) {
        TbConfiguracao tbConfiguracaoSaved = get();
        tbConfiguracao.setDataAlteracao(LocalDateTime.now());
        tbConfiguracao.setContentType(tbConfiguracaoSaved.getContentType());
        tbConfiguracao.setCertificado(tbConfiguracaoSaved.getCertificado());
        configuracaoRepository.save(tbConfiguracao);
        loadConfigs();
    }

    public void salvarCertificado(MultipartFile file) {
        try {
            TbConfiguracao tbConfiguracao = get();
            tbConfiguracao.setCertificado(file.getBytes());
            tbConfiguracao.setContentType(file.getContentType());
            tbConfiguracao.setNomeArquivo(file.getOriginalFilename());
            configuracaoRepository.save(tbConfiguracao);
            loadConfigs();
        } catch (IOException ioException) {
            throw new ZCException(ioException.getMessage());
        }
    }

    public TbConfiguracao get() {
        return configuracaoRepository.findById(1).orElse(new TbConfiguracao(1));
    }

    public void loadConfigs() {
        TbConfiguracao tbConfiguracao = get();
        Constants.NUM_NFE = tbConfiguracao.getNumNfe();
        Constants.APP_CONFIG = ConfiguracaoDTO.builder()
                .code(tbConfiguracao.getCode())
                .accessToken(tbConfiguracao.getAccessToken())
                .clientId(tbConfiguracao.getClientId())
                .clientSecret(tbConfiguracao.getClientSecret())
                .redirectUri(tbConfiguracao.getRedirectUri())
                .dataAlteracao(tbConfiguracao.getDataAlteracao())
                .refreshToken(tbConfiguracao.getRefreshToken())
                .sellerId(tbConfiguracao.getSellerId())
                .tokenExpiration(tbConfiguracao.getTokenExpiration())
                .cnpj(tbConfiguracao.getCnpj())
                .nome(tbConfiguracao.getNome())
                .nomeFantasia(tbConfiguracao.getNomeFantasia())
                .cpl(tbConfiguracao.getCpl())
                .logradouro(tbConfiguracao.getLogradouro())
                .numero(tbConfiguracao.getNumero())
                .bairro(tbConfiguracao.getBairro())
                .codMunicipio(tbConfiguracao.getCodMunicipio())
                .nomeMunicipio(tbConfiguracao.getNomeMunicipio())
                .cep(tbConfiguracao.getCep())
                .uf(tbConfiguracao.getUf())
                .telefone(tbConfiguracao.getTelefone())
                .ie(tbConfiguracao.getIe())
                .crt(tbConfiguracao.getCrt())
                .certificado(tbConfiguracao.getCertificado())
                .contentType(tbConfiguracao.getContentType())
                .nomeArquivo(tbConfiguracao.getNomeArquivo())
                .senha(tbConfiguracao.getSenha())
                .numNfe(tbConfiguracao.getNumNfe())
                .processarAutomatico(tbConfiguracao.getProcessarAutomatico())
                .build();
    }

    public Long buscarNumNfe() {
        Constants.NUM_NFE = Constants.NUM_NFE + 1;
        return Constants.NUM_NFE;
    }

    public void atualizarNumNfe() {
        TbConfiguracao tbConfiguracao = get();
        tbConfiguracao.setNumNfe(Constants.NUM_NFE);
        atualizar(tbConfiguracao);
    }

    public void atualizarToken(TokenResponse tokenResponse) {
        TbConfiguracao tbConfiguracao = get();
        tbConfiguracao.setAccessToken(tokenResponse.getAccessToken());
        tbConfiguracao.setRefreshToken(tokenResponse.getRefreshToken());
        tbConfiguracao.setDataAlteracao(LocalDateTime.now());
        tbConfiguracao.setTokenExpiration(LocalDateTime.now().plusSeconds(tokenResponse.getExpiresIn() - 10));
        configuracaoRepository.save(tbConfiguracao);
    }


}
