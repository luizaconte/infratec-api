package br.com.infratec.service.sales;


import br.com.infratec.enums.EstadosBrasil;
import br.com.infratec.model.sales.TbMunicipio;
import br.com.infratec.repository.sales.MunicipioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class MunicipioService {

    private final MunicipioRepository municipioRepository;

    @Autowired
    public MunicipioService(MunicipioRepository municipioRepository) {
        this.municipioRepository = municipioRepository;
    }

    public Optional<TbMunicipio> buscarMunicipio(String nome, String uf) {
        if (Objects.nonNull(nome) && Objects.nonNull(uf)) {
            return municipioRepository.findByNomeIgnoreCaseAndUf(nome, EstadosBrasil.of(uf));
        } else if (Objects.nonNull(nome)) {
            return municipioRepository.findByNomeIgnoreCase(nome);
        } else if (Objects.nonNull(uf)) {
            return municipioRepository.findByUf(EstadosBrasil.of(uf));
        }
        return Optional.empty();
    }
}
