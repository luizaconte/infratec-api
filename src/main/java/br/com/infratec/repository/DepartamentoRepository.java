package br.com.infratec.repository;

import br.com.infratec.model.TbDepartamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartamentoRepository extends JpaRepository<TbDepartamento, Long>, JpaSpecificationExecutor<TbDepartamento> {

    Optional<TbDepartamento> findByNomeContainsIgnoreCase(String nome);
}
