package br.com.projetodifm.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.projetodifm.model.Produto;

public interface ProdutoRepository extends JpaRepository<Produto, Long>{
    
    Boolean existsByNomeProduto(String nomeProduto);

    Optional<Produto> findByUserIdAndId(Long userId, Long id);

    Optional<List<Produto>> findByUserId(Long userId);
}
