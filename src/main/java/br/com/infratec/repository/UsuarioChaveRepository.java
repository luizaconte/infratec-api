package br.com.infratec.repository;

import br.com.infratec.model.TbUsuarioChave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioChaveRepository extends JpaRepository<TbUsuarioChave, Integer>, JpaSpecificationExecutor<TbUsuarioChave> {

    TbUsuarioChave findByChavePublica(String chavePublica);

    void deleteTbUsuarioChaveByChavePublica(String chavePublica);
}
