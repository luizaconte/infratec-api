package br.com.infratec.service.erp;

import br.com.infratec.model.erp.Produto;
import br.com.infratec.repository.erp.ProdutoRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public Optional<Produto> findById(Long id) {
        return produtoRepository.findById(id);
    }
}
