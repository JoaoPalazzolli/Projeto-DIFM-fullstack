package br.com.projetodifm.data.vo.v1;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonPropertyOrder({ "id", "nomeProduto", "descricao", "quantidade", "preco" })
public class ProdutoVO extends RepresentationModel<ProdutoVO> implements Serializable{

    private static final long serialVersionUID = 1L;

    @JsonProperty("id")
    @Mapping("id")
    private Long key;
    private String nomeProduto;
    private String descricao;
    private Integer quantidade;
    private Double preco;
}
