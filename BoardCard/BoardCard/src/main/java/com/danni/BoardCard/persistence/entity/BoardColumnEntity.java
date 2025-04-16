package com.danni.BoardCard.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(name = "board_columns") // Opcional: define o nome da tabela
public class BoardColumnEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    @Column(name = "column_order") // Boa prática explicitar o nome da coluna para evitar conflitos com palavras reservadas
    private Integer order;

    @Enumerated(EnumType.STRING) // Persiste o enum como String no banco
    private BoardColumnKindEnum kind;

    @ManyToOne
    @JoinColumn(name = "board_id")
    private BoardEntity board;

    @OneToMany(mappedBy = "boardColumn", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CardEntity> cards = new ArrayList<>();

    public void addCard(CardEntity card) {
        if (card != null && !cards.contains(card)) {
            cards.add(card);
            card.setBoardColumn(this);
        }
    }

    public void removeCard(CardEntity card) {
        if (card != null) {
            cards.remove(card);
            card.setBoardColumn(null);
        }
    }
}