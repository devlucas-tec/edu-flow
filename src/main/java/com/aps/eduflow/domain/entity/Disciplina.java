package com.aps.eduflow.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "disciplinas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 20)
    private String codigo;

    private String descricao;

    @Column(nullable = false)
    private Boolean ativo;

    @OneToMany(mappedBy = "disciplina", fetch = FetchType.LAZY)
    private List<Monitoria> monitorias;

    @OneToMany(mappedBy = "disciplina", fetch = FetchType.LAZY)
    private List<Atendimento> atendimentos;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;
}
