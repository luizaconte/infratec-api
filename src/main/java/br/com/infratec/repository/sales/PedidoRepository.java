package br.com.infratec.repository.sales;

import br.com.infratec.enums.StatusProcessamento;
import br.com.infratec.model.sales.TbPedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface PedidoRepository extends JpaRepository<TbPedido, Long>, JpaSpecificationExecutor<TbPedido> {

    @Override
    @EntityGraph(value = TbPedido.FETCH_ALL)
    Optional<TbPedido> findById(Long id);

    @Override
    @EntityGraph(value = TbPedido.FETCH_ALL)
    Page<TbPedido> findAll(@Nullable Specification<TbPedido> spec, Pageable pageable);

    @Override
    @EntityGraph(value = TbPedido.FETCH_ALL)
    Page<TbPedido> findAll(Pageable pageable);

    @EntityGraph(value = TbPedido.FETCH_ALL)
    @Query(value = "SELECT p FROM TbPedido p ORDER BY p.lote DESC LIMIT 1")
    Optional<TbPedido> findByLoteMax();

    @EntityGraph(value = TbPedido.FETCH_ALL)
    List<TbPedido> findByIdOrder(Long idOrder);

    @EntityGraph(value = TbPedido.FETCH_ALL)
    @Query(value = "SELECT p FROM TbPedido p WHERE p.lote = :lote and p.status in (:statusList)")
    List<TbPedido> findByLoteAndStatus(@Param("lote") Long lote, @Param("statusList") List<StatusProcessamento> statusProcessamentoList);

    @EntityGraph(value = TbPedido.FETCH_ALL)
    @Query(value = "SELECT p FROM TbPedido p WHERE p.dataCriacao BETWEEN :startDate AND :endDate AND p.status IN (:statusList)")
    List<TbPedido> findByDataCriacaoBetweenAndStatus(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate, @Param("statusList") List<StatusProcessamento> statusProcessamentoList);

    @EntityGraph(value = TbPedido.FETCH_ALL)
    @Query(value = "SELECT p FROM TbPedido p WHERE p.status in (:statusList)")
    List<TbPedido> findByStatus( @Param("statusList") List<StatusProcessamento> statusProcessamentoList);

}
