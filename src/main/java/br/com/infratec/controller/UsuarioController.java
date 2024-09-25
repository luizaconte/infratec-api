package br.com.infratec.controller;

import br.com.infratec.controller.mapper.UsuarioDTOMapper;
import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.dto.PageResponseDTO;
import br.com.infratec.dto.UsuarioDTO;
import br.com.infratec.exception.NotFoundException;
import br.com.infratec.model.TbUsuario;
import br.com.infratec.service.UsuarioService;
import br.com.infratec.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Usuários")
@RestController
@RequestMapping(path = "/api/v1/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    private final UsuarioDTOMapper mapper;

    @Autowired
    public UsuarioController(UsuarioService usuarioService, UsuarioDTOMapper mapper) {
        this.usuarioService = usuarioService;
        this.mapper = mapper;
    }

    @GetMapping
    @Operation(summary = "Lista todos os usuários de acordo com o filtro")
    public ResponseEntity<PageResponseDTO> getAll(@Valid PageRequestDTO pageRequestDTO) {
        Page<TbUsuario> result = usuarioService.findAll(pageRequestDTO);

        final List<UsuarioDTO> dtos = result
                .getContent()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseBuilder.build(result, dtos));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca o usuário pelo Id")
    public ResponseEntity<UsuarioDTO> getById(@PathVariable Integer id) {
        final var dto = mapper.toDto(usuarioService.findById(id).orElseThrow(NotFoundException::new));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualiza o usuário")
    public ResponseEntity<UsuarioDTO> update(@PathVariable("id") Integer id, @RequestBody @Valid UsuarioDTO usuarioDTO) {
        usuarioService.atualizar(mapper.toEntity(id, usuarioDTO));
        return ResponseEntity.ok().build();
    }

    @PostMapping
    @Operation(summary = "Salva um novo usuário")
    public ResponseEntity<Void> salvar(@RequestBody @Valid UsuarioDTO usuarioDTO) {
        var entity = mapper.toEntity(usuarioDTO.getId(), usuarioDTO);
        usuarioService.salvar(entity);
        return ResponseEntity.ok().build();
    }


}
