package com.tpintegrador.tecnicas_avanzadas_MP.model;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "resultados_revision")
public class ResultadoRevision {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "puntajes_revision", joinColumns = @JoinColumn(name = "resultado_id"))
    @Column(name = "puntaje", nullable = false)
    private List<Integer> puntajes = new ArrayList<>();

    @Column(nullable = false)
    private Integer puntajeTotal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Calificacion calificacion;

    @Column(length = 1000)
    private String comentarioMecanico;

    @OneToOne
    @JoinColumn(name = "turno_id", unique = true, nullable = false)
    private Turno turno;
}


