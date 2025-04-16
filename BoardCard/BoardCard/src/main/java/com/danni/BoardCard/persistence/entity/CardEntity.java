package com.danni.BoardCard.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "cards") // Opcional: define o nome da tabela no banco de dados
public class CardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    @ManyToOne
    @JoinColumn(name = "board_column_id") // Nome da coluna na tabela 'cards' que referencia a coluna
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private BoardColumnEntity boardColumn;

    @OneToMany(mappedBy = "card", cascade = CascadeType.ALL, orphanRemoval = true) // Supondo relação um-para-muitos com BlockEntity
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BlockEntity> blocks = new ArrayList<>();

    public void setBoardColumn(BoardColumnEntity boardColumn) {
        if (this.boardColumn != null) {
            this.boardColumn.getCards().remove(this);
        }
        this.boardColumn = boardColumn;
        if (boardColumn != null && !boardColumn.getCards().contains(this)) {
            boardColumn.getCards().add(this);
        }
    }

    // Método auxiliar para adicionar um bloco
    public void addBlock(BlockEntity block) {
        this.blocks.add(block);
        block.setCard(this);
    }

    // Método auxiliar para remover um bloco
    public void removeBlock(BlockEntity block) {
        this.blocks.remove(block);
        block.setCard(null);
    }
}