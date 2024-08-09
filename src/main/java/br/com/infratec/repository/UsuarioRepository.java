package br.com.infratec.repository;

import br.com.infratec.model.TbUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<TbUsuario, Integer>, JpaSpecificationExecutor<TbUsuario> {

    Optional<TbUsuario> findByLogin(String login);
}
