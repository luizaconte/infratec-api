package br.com.infratec.repository.sales;

import br.com.infratec.model.sales.TbLogPedidos;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.util.Optional;


@Repository
public interface LogPedidosRepository extends JpaRepository<TbLogPedidos, Long>, JpaSpecificationExecutor<TbLogPedidos> {

    @Override
    @EntityGraph(value = TbLogPedidos.FETCH_ALL)
    Optional<TbLogPedidos> findById(Long id);

    @Override
    @EntityGraph(value = TbLogPedidos.FETCH_ALL)
    Page<TbLogPedidos> findAll(@Nullable Specification<TbLogPedidos> spec, Pageable pageable);

    @Override
    @EntityGraph(value = TbLogPedidos.FETCH_ALL)
    Page<TbLogPedidos> findAll(Pageable pageable);
}
