package br.com.infratec.service;

import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.enums.TipoUsuario;
import br.com.infratec.model.TbChamado;
import br.com.infratec.model.TbUsuario;
import br.com.infratec.repository.ChamadoRepository;
import br.com.infratec.security.InfraTecAuthentication;
import br.com.infratec.util.JwtService;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

@Service
public class ChamadoService {
    private final ChamadoRepository chamadoRepository;
    private final UsuarioService usuarioService;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbChamado> chamadoCustomRsqlVisitor = new CustomRsqlVisitor<>();

    @Autowired
    public ChamadoService(ChamadoRepository chamadoRepository, UsuarioService usuarioService, RSQLParser rsqlParser) {
        this.chamadoRepository = chamadoRepository;
        this.usuarioService = usuarioService;
        this.rsqlParser = rsqlParser;
    }

    public Optional<TbChamado> findById(final Long id) {
        return chamadoRepository.findById(id);
    }

    public Page<TbChamado> findAll(PageRequestDTO pageRequestDTO) {
        // TODO- filtro tipo usuario
        InfraTecAuthentication authentication = (InfraTecAuthentication) SecurityContextHolder.getContext().getAuthentication();

        // validar tipo se tec listar só as dele
        if (JwtService.getType().equals(TipoUsuario.SUPORTE.getId())) {
            String query = (StringUtils.isBlank(pageRequestDTO.getQuery()) ? "" : pageRequestDTO.getQuery() + ";")
                    .concat("(idUsuarioCriacao==" + authentication.getPrincipal().getUserId())
                    .concat(",idUsuarioResponsavel==" + authentication.getPrincipal().getUserId() + ")");
            pageRequestDTO.setQuery(query);
        }
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return chamadoRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbChamado> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(chamadoCustomRsqlVisitor);
        return chamadoRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }

    public void atualizar(TbChamado tbChamado) {
        tbChamado.setDataAlteracao(Instant.now());
        chamadoRepository.save(tbChamado);
    }

    public void salvar(TbChamado tbChamado) {
        processarChamado(tbChamado);
        tbChamado.setDataInclusao(Instant.now());
        chamadoRepository.save(tbChamado);
    }

    private void processarChamado(TbChamado chamado) {
        Optional<TbUsuario> usuario = usuarioService.findById(JwtService.getId());
        if (usuario.isPresent()) {
            chamado.setUsuarioCriacao(usuario.get());
            chamado.setIdUsuarioCriacao(usuario.get().getId());
        }
        chamado.getComentarios().forEach(c -> {
            c.setChamado(chamado);
            if (Objects.isNull(c.getDataInclusao())) {
                c.setDataInclusao(Instant.now());
            } else {
                c.setDataAlteracao(Instant.now());
            }
        });
    }
}
