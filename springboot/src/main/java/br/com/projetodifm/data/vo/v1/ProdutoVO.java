package br.com.projetodifm.data.vo.v1;

import java.io.Serializable;

import org.springframework.hateoas.RepresentationModel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.dozermapper.core.Mapping;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@JsonPropertyOrder({ "id", "nomeProduto", "descricao", "quantidade", "preco" })
public class ProdutoVO extends RepresentationModel<ProdutoVO> implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String CONTENT_NOT_BLANK = "This Content cannot be Blank";
    private static final String CONTENT_NOT_NULL = "This Content cannot be Null";

    @JsonProperty("id")
    @Mapping("id")
    private Long key;
    @NotBlank(message = CONTENT_NOT_BLANK)
    private String nomeProduto;
    @NotBlank(message = CONTENT_NOT_BLANK)
    private String descricao;
    @NotNull(message = CONTENT_NOT_NULL)
    @Min(value = 0)
    private Integer quantidade;
    @NotNull(message = CONTENT_NOT_NULL)
    @Min(value = 0)
    private Double preco;
}
