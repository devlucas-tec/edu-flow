package com.aps.eduflow.domain.dto;

import com.aps.eduflow.domain.entity.Disciplina;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DisciplinaResponseDTO {

    private Long id;
    private String nome;
    private String codigo;
    private String descricao;
    private Boolean ativo;

    public DisciplinaResponseDTO(Disciplina d) {
        this(d.getId(), d.getNome(), d.getCodigo(), d.getDescricao(), d.getAtivo());
    }
}
