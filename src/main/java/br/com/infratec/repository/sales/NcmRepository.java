package br.com.infratec.repository.sales;

import br.com.infratec.model.sales.TbNcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface NcmRepository extends JpaRepository<TbNcm, Integer>, JpaSpecificationExecutor<TbNcm> {

    Optional<TbNcm> findByNumero(String numero);
}
