package br.com.infratec.repository.sales;

import br.com.infratec.model.sales.TbMunicipio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MunicipioRepository extends JpaRepository<TbMunicipio, Long>, JpaSpecificationExecutor<TbMunicipio> {


    Optional<TbMunicipio> findByNomeIgnoreCase(String nome);

    Optional<TbMunicipio> findByUf(String uf);

    Optional<TbMunicipio> findByNomeIgnoreCaseAndUf(String nome, String uf);
}
