package com.danni.BoardCard.service;

import com.danni.BoardCard.persistence.dao.BoardColumnDAO;
import com.danni.BoardCard.persistence.dao.BoardDAO;
import com.danni.BoardCard.persistence.entity.BoardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;

    @Transactional(rollbackFor = SQLException.class)
    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        // 1. Insere o board principal
        boardDAO.insert(entity);

        // 2. Insere as colunas associadas
        if (entity.getBoardColumns() != null) {
            for (var column : entity.getBoardColumns()) {
                column.setBoard(entity);  // Garante a relação bidirecional
                boardColumnDAO.insert(column);
            }
        }

        return entity;
    }

    @Transactional(rollbackFor = SQLException.class)
    public boolean delete(final Long id) throws SQLException {
        // Verifica existência antes de deletar
        if (!boardDAO.exists(id)) {
            return false;
        }

        // Delete em cascata (as colunas serão deletadas por ON DELETE CASCADE ou manualmente)
        boardDAO.delete(id);
        return true;
    }
}