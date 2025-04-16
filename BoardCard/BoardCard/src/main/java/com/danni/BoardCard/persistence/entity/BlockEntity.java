package com.danni.BoardCard.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.OffsetDateTime;

@Entity
@Data
@Table(name = "blocks") // Opcional: define o nome da tabela
public class BlockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private OffsetDateTime blockedAt;
    private String blockReason;
    private OffsetDateTime unblockedAt;
    private String unblockReason;

    @ManyToOne
    @JoinColumn(name = "card_id") // Nome da coluna na tabela 'blocks' que referencia o card
    private CardEntity card;
}