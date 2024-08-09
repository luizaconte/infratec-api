package br.com.infratec.repository.sales;

import br.com.infratec.enums.TipoPessoa;
import br.com.infratec.model.sales.TbConfigNcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ConfigNcmRepository extends JpaRepository<TbConfigNcm, Integer>, JpaSpecificationExecutor<TbConfigNcm> {

    Optional<TbConfigNcm> findByIdNcmAndTipoPessoaAndUf(Integer idNcm, TipoPessoa tipoPessoa, String uf);
}
