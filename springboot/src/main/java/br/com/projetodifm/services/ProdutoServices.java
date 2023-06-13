package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.projetodifm.controller.ProdutoController;
import br.com.projetodifm.data.vo.v1.ProdutoVO;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.EmailNotFoundException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;
import br.com.projetodifm.mapper.DozerMapper;
import br.com.projetodifm.model.Produto;
import br.com.projetodifm.repositories.ProdutoRepository;
import br.com.projetodifm.repositories.UserRepository;
import br.com.projetodifm.util.ErrorMessages;

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
                .orElseThrow(() -> new EmailNotFoundException(email));

        var produtos = repository.findByUserId(user.getId())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var vo = DozerMapper.parseListObjects(produtos, ProdutoVO.class);

        vo.stream().forEach(
                x -> x.add(linkTo(methodOn(ProdutoController.class).findById(email, x.getKey())).withSelfRel()));

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public ResponseEntity<ProdutoVO> findById(String email, Long idProduct) {
        logger.info("Finding one Product!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        var produto = repository.findByUserIdAndId(user.getId(), idProduct)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var vo = DozerMapper.parseObject(produto, ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(email, idProduct)).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public ResponseEntity<ProdutoVO> create(String email, ProdutoVO produto) {
        logger.info("Creating one Product!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        if (repository.existsByNomeProdutoAndUserId(produto.getNomeProduto(), user.getId()))
            throw new ConflictException(ErrorMessages.PRODUCT_CONFLICT);

        var produtos = DozerMapper.parseObject(produto, Produto.class);

        produtos.setUser(user);

        var vo = DozerMapper.parseObject(repository.save(produtos), ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(email, vo.getKey())).withSelfRel());

        return ResponseEntity.status(HttpStatus.CREATED).body(vo);
    }

    public ResponseEntity<ProdutoVO> update(String email, ProdutoVO produto) {
        logger.info("Updating one Product!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        var userProduto = repository.findByUserIdAndId(user.getId(), produto.getKey())
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        if (repository.existsByNomeProdutoAndUserId(produto.getNomeProduto(), user.getId())
                && !userProduto.getNomeProduto().equals(produto.getNomeProduto()))
            throw new ConflictException(ErrorMessages.PRODUCT_CONFLICT);

        userProduto = DozerMapper.parseObject(produto, Produto.class);

        userProduto.setUser(user);

        var vo = DozerMapper.parseObject(repository.save(userProduto), ProdutoVO.class);

        vo.add(linkTo(methodOn(ProdutoController.class).findById(email, vo.getKey())).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    public void delete(String email, Long idProduct) {
        logger.info("Deleting one Product!");

        var user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        var produto = repository.findByUserIdAndId(user.getId(), idProduct)
                .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        repository.delete(produto);
    }
}
