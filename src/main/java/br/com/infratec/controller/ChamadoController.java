package br.com.infratec.controller;

import br.com.infratec.controller.mapper.ChamadoDTOMapper;
import br.com.infratec.dto.ChamadoDTO;
import br.com.infratec.dto.PageRequestDTO;
import br.com.infratec.dto.PageResponseDTO;
import br.com.infratec.exception.NotFoundException;
import br.com.infratec.model.TbChamado;
import br.com.infratec.service.ChamadoService;
import br.com.infratec.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Chamados")
@RestController
@RequestMapping(path = "/api/v1/chamado")
public class ChamadoController {

    private final ChamadoService chamadoService;

    private final ChamadoDTOMapper mapper;

    @Autowired
    public ChamadoController(ChamadoService chamadoService, ChamadoDTOMapper mapper) {
        this.chamadoService = chamadoService;
        this.mapper = mapper;
    }

    @GetMapping
    public ResponseEntity<PageResponseDTO> getAll(@Valid PageRequestDTO pageRequestDTO) {
        Page<TbChamado> result = chamadoService.findAll(pageRequestDTO);
        final List<ChamadoDTO> dtos = result
                .getContent()
                .stream()
                .map(mapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ResponseBuilder.build(result, dtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadoDTO> getById(@PathVariable Long id) {
        final var dto = mapper.toDto(chamadoService.findById(id).orElseThrow(NotFoundException::new));
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChamadoDTO> update(@PathVariable("id") Integer id,
                                             @RequestBody @Valid ChamadoDTO chamadoDTO) {
        chamadoService.atualizar(mapper.toEntity(id, chamadoDTO));
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Void> salvar(@RequestBody @Valid ChamadoDTO chamadoDTO) {
        chamadoService.salvar(mapper.toEntity(chamadoDTO));
        return ResponseEntity.ok().build();
    }


}
