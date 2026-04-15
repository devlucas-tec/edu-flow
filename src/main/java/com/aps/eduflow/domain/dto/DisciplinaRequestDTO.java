package com.aps.eduflow.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class DisciplinaRequestDTO {

    @NotBlank(message = "O nome é obrigatório")
    private String nome;

    @NotBlank(message = "O código é obrigatório")
    @Pattern(regexp = "^[A-Za-z]{3}[0-9]{3}$", message = "O código deve ter o formato 3 letras e 3 números (ex: ADS101)")
    private String codigo;

    private String descricao;
}
