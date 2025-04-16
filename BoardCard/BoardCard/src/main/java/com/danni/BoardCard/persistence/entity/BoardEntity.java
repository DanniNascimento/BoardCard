package com.danni.BoardCard.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.danni.BoardCard.persistence.entity.BoardColumnKindEnum.CANCEL;
import static com.danni.BoardCard.persistence.entity.BoardColumnKindEnum.INITIAL;

@Entity
@Data
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<BoardColumnEntity> boardColumns = new ArrayList<>();
    public void addColumn(BoardColumnEntity column) {
        if (column != null && !boardColumns.contains(column)) {
            boardColumns.add(column);
            column.setBoard(this);
        }
    }

    public BoardColumnEntity getInitialColumn() {
        return getFilteredColumn(bc -> INITIAL.equals(bc.getKind()));
    }

    public BoardColumnEntity getCancelColumn() {
        return getFilteredColumn(bc -> CANCEL.equals(bc.getKind()));
    }

    private BoardColumnEntity getFilteredColumn(Predicate<BoardColumnEntity> filter) {
        return boardColumns.stream()
                .filter(filter)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Required column not found"));
    }
}