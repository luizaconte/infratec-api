package br.com.infratec.service.erp;

import br.com.infratec.repository.erp.VendaItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class VendaService {

    private final VendaItemRepository vendaItemRepository;

    public VendaService(VendaItemRepository vendaItemRepository) {
        this.vendaItemRepository = vendaItemRepository;
    }

    public void aplicarDescontos(String idMeli, String sku, BigDecimal valorFinal) {
        vendaItemRepository.updateItemVenda(idMeli, sku, valorFinal);
    }
}
