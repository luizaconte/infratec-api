package br.com.infratec.repository;

import br.com.infratec.model.TbChamado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChamadoRepository extends JpaRepository<TbChamado, Long>, JpaSpecificationExecutor<TbChamado> {
}
