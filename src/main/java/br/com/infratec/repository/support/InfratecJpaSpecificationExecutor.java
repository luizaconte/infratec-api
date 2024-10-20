package br.com.infratec.repository.support;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;

@NoRepositoryBean
public interface InfratecJpaSpecificationExecutor<E, ID extends Serializable> extends JpaSpecificationExecutor<E> {

    Page<ID> findEntityIds(Pageable pageable);

    Page<ID> findEntityIds(Specification<E> spec, Pageable pageable);
}
