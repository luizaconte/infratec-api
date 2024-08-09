package br.com.infratec.repository.sales;

import br.com.infratec.model.sales.TbLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LogSistemaRepository extends JpaRepository<TbLog, Long>, JpaSpecificationExecutor<TbLog> {
}
