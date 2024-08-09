package br.com.infratec.repository.erp;

import br.com.infratec.model.erp.VendaItem;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface VendaItemRepository extends JpaRepository<VendaItem, Long> {

    @Transactional
    @Modifying
    @Query(value = "    UPDATE VendaItem vdi " +
            "    SET vdi.viProVlBruto = :valorFinal ," +
            "    vdi.viProVlUnit = :valorFinal" +
            "    WHERE vdi.proCodigo = :sku " +
            "    AND EXISTS (" +
            "            SELECT 1" +
            "            FROM Venda vd" +
            "            WHERE vdi.vdCodigo = vd.vdCodigo" +
            "            AND vd.vdPedidoExterno = :idMeli" +
            "    )"
    )
    void updateItemVenda(@Param("idMeli") String idMeli, @Param("sku") String sku, @Param("valorFinal") BigDecimal valorFinal);
}
