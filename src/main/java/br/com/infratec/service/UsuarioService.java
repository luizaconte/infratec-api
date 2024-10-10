package br.com.infratec.service;

import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.exception.ZCException;
import br.com.infratec.model.TbUsuario;
import br.com.infratec.repository.UsuarioRepository;
import br.com.infratec.util.JwtService;
import br.com.infratec.util.rsql.CustomRsqlVisitor;
import cz.jirutka.rsql.parser.RSQLParser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final RSQLParser rsqlParser;
    private final CustomRsqlVisitor<TbUsuario> usuarioCustomRsqlVisitor = new CustomRsqlVisitor<>();
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository, RSQLParser rsqlParser, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rsqlParser = rsqlParser;
        this.passwordEncoder = passwordEncoder;
    }

    public Optional<TbUsuario> findById(final Integer id) {
        return usuarioRepository.findById(id);
    }

    public Page<TbUsuario> findAll(PageRequestDTO pageRequestDTO) {
        Sort sort = pageRequestDTO.getSortArgument();
        if (StringUtils.isBlank(pageRequestDTO.getQuery())) {
            return usuarioRepository.findAll(PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
        }
        Specification<TbUsuario> specification = rsqlParser.parse(pageRequestDTO.getQuery()).accept(usuarioCustomRsqlVisitor);
        return usuarioRepository.findAll(specification, PageRequest.of(pageRequestDTO.getPageIndex(), pageRequestDTO.getPageSize(), sort));
    }

    public void atualizar(TbUsuario tbUsuario) {
        tbUsuario.setDataAlteracao(Instant.now());
        // TODO - ver senha
        tbUsuario.setLoginAlteracao(JwtService.getLogin());
        usuarioRepository.save(tbUsuario);
    }

    public void salvar(TbUsuario tbUsuario)  {
        Optional<TbUsuario> optionalTbUsuario = usuarioRepository.findByLogin(tbUsuario.getLogin());
        if (optionalTbUsuario.isEmpty()) {
            tbUsuario.setDataInclusao(Instant.now());
            tbUsuario.setSenha(passwordEncoder.encode(tbUsuario.getSenha()));
            tbUsuario.setLoginInclusao(JwtService.getLogin());
            usuarioRepository.save(tbUsuario);
        } else {
            throw new ZCException("O login fornecido já existe. Verifique!");
        }
    }

    public void excluir(Integer idUsuario) {
        usuarioRepository.deleteById(idUsuario);
    }
}
