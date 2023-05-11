package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.projetodifm.controller.ProdutoController;
import br.com.projetodifm.data.vo.v1.ProdutoVO;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.RequiredObjectIsNullException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;
import br.com.projetodifm.mapper.DozerMapper;
import br.com.projetodifm.model.Produto;
import br.com.projetodifm.repositories.ProdutoRepository;
import br.com.projetodifm.repositories.UserRepository;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.logging.Logger;

@Service
public class ProdutoServices {

    private Logger logger = Logger.getLogger(ProdutoServices.class.getName());

    @Autowired
    private ProdutoRepository repository;

    @Autowired
    private UserRepository userRepository;

    public ResponseEntity<List<ProdutoVO>> findAll(String email) {
        logger.info("Finding all Products!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var produtos = repository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var vo = DozerMapper.parseListObjects(produtos, ProdutoVO.class);

        vo.stream().forEach(
                x -> x.add(linkTo(methodOn(ProdutoController.class).findById(email, x.getKey())).withSelfRel()));

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public ResponseEntity<ProdutoVO> findById(String email, Long idProduct) {
        logger.info("Finding one Product!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var produto = repository.findByUserIdAndId(user.getId(), idProduct)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var vo = DozerMapper.parseObject(produto, ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(email, idProduct)).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public ResponseEntity<ProdutoVO> create(String email, ProdutoVO produto) {
        logger.info("Creating one Product!");

        if (produto == null)
            throw new RequiredObjectIsNullException();

        if (repository.existsByNomeProduto(produto.getNomeProduto()))
            throw new ConflictException("this product already exists");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var produtos = DozerMapper.parseObject(produto, Produto.class);

        produtos.setUser(user);

        var vo = DozerMapper.parseObject(repository.save(produtos), ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(email, vo.getKey())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    public ResponseEntity<ProdutoVO> update(String email, ProdutoVO produto) {
        logger.info("Updating one Product!");

        if (produto == null)
            throw new RequiredObjectIsNullException();

        if (repository.existsByNomeProduto(produto.getNomeProduto()))
            throw new ConflictException("this product already exists");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var userProduto = repository.findByUserIdAndId(user.getId(), produto.getKey())
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        userProduto = DozerMapper.parseObject(produto, Produto.class);

        userProduto.setUser(user);

        var vo = DozerMapper.parseObject(repository.save(userProduto), ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(email, vo.getKey())).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public void delete(String email, Long idProduct) {
        logger.info("Deleting one Product!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        var produto = repository.findByUserIdAndId(user.getId(), idProduct)
                .orElseThrow(() -> new ResourceNotFoundException("No records found for this ID!"));

        repository.delete(produto);
    }
}
