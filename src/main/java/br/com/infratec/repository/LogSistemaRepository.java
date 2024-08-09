package br.com.infratec.repository;

import br.com.infratec.model.TbLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogSistemaRepository extends JpaRepository<TbLog, Long>, JpaSpecificationExecutor<TbLog> {
}
