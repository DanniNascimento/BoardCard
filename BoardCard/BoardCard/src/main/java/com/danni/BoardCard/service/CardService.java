package com.danni.BoardCard.service;

import com.danni.BoardCard.dto.BoardColumnInfoDTO;
import com.danni.BoardCard.exception.CardBlockedException;
import com.danni.BoardCard.exception.CardFinishedException;
import com.danni.BoardCard.exception.EntityNotFoundException;
import com.danni.BoardCard.persistence.dao.BlockDAO;
import com.danni.BoardCard.persistence.dao.CardDAO;
import com.danni.BoardCard.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.List;

import static com.danni.BoardCard.persistence.entity.BoardColumnKindEnum.CANCEL;
import static com.danni.BoardCard.persistence.entity.BoardColumnKindEnum.FINAL;

@Service
@RequiredArgsConstructor
public class CardService {

    private final CardDAO cardDAO;
    private final BlockDAO blockDAO;

    @Transactional(rollbackFor = SQLException.class)
    public CardEntity create(final CardEntity entity) throws SQLException {
        cardDAO.insert(entity);
        return entity;
    }

    @Transactional(rollbackFor = SQLException.class)
    public void moveToNextColumn(final Long cardId, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        var dto = cardDAO.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
        if (dto.blocked()) {
            throw new CardBlockedException("O card %s está bloqueado, é necesário desbloquea-lo para mover".formatted(cardId));
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card já foi finalizado");
        }
        var nextColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
        cardDAO.moveToColumn(nextColumn.id(), cardId);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void cancel(final Long cardId, final Long cancelColumnId,
                       final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        var dto = cardDAO.findById(cardId)
                .orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(cardId)));
        if (dto.blocked()) {
            throw new CardBlockedException("O card %s está bloqueado, é necesário desbloquea-lo para mover".formatted(cardId));
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("O card informado pertence a outro board"));
        if (currentColumn.kind().equals(FINAL)) {
            throw new CardFinishedException("O card já foi finalizado");
        }
        boardColumnsInfo.stream()
                .filter(bc -> bc.order() == currentColumn.order() + 1)
                .findFirst().orElseThrow(() -> new IllegalStateException("O card está cancelado"));
        cardDAO.moveToColumn(cancelColumnId, cardId);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void block(final Long id, final String reason, final List<BoardColumnInfoDTO> boardColumnsInfo) throws SQLException {
        var dto = cardDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id)));
        if (dto.blocked()) {
            throw new CardBlockedException("O card %s já está bloqueado".formatted(id));
        }
        var currentColumn = boardColumnsInfo.stream()
                .filter(bc -> bc.id().equals(dto.columnId()))
                .findFirst()
                .orElseThrow();
        if (currentColumn.kind().equals(FINAL) || currentColumn.kind().equals(CANCEL)) {
            throw new IllegalStateException("O card está em uma coluna do tipo %s e não pode ser bloqueado"
                    .formatted(currentColumn.kind()));
        }
        blockDAO.block(reason, id);
    }

    @Transactional(rollbackFor = SQLException.class)
    public void unblock(final Long id, final String reason) throws SQLException {
        var dto = cardDAO.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("O card de id %s não foi encontrado".formatted(id)));
        if (!dto.blocked()) {
            throw new CardBlockedException("O card %s não está bloqueado".formatted(id));
        }
        blockDAO.unblock(reason, id);
    }
}