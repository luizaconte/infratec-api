package br.com.infratec.repository.sales;

import br.com.infratec.model.sales.TbConfiguracao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ConfiguracaoRepository extends JpaRepository<TbConfiguracao, Integer>, JpaSpecificationExecutor<TbConfiguracao> {
}
