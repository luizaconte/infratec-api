package br.com.infratec.controller;

import br.com.infratec.controller.mapper.DepartamentoDTOMapper;
import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.dto.PageResponseDTO;
import br.com.infratec.dto.DepartamentoDTO;
import br.com.infratec.exception.NotFoundException;
import br.com.infratec.model.TbDepartamento;
import br.com.infratec.service.DepartamentoService;
import br.com.infratec.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Departamentos")
@RestController
@RequestMapping(path = "/api/v1/departamento")
public class DepartamentoController {

    private final DepartamentoService departamentoService;

    private final DepartamentoDTOMapper mapper;

    @Autowired
    public DepartamentoController(DepartamentoService departamentoService, DepartamentoDTOMapper mapper) {
        this.departamentoService = departamentoService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO> getAll(@Valid PageRequestDTO pageRequestDTO) {
        Page<TbDepartamento> result = departamentoService.findAll(pageRequestDTO);

        final List<DepartamentoDTO> dtos = result
                .getContent()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseBuilder.build(result, dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> getById(@PathVariable Long id) {
        final var dto = mapper.toDto(departamentoService.findById(id).orElseThrow(NotFoundException::new));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoDTO> update(@PathVariable("id") Integer id,
                                                  @RequestBody @Valid DepartamentoDTO departamentoDTO) {
        departamentoService.atualizar(mapper.toEntity(id, departamentoDTO));
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid DepartamentoDTO departamentoDTO) {
        departamentoService.salvar(mapper.toEntity(departamentoDTO));
        return ResponseEntity.ok().build();
    }


}
