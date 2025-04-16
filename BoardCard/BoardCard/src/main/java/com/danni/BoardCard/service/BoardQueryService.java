package com.danni.BoardCard.service;

import com.danni.BoardCard.dto.BoardDetailsDTO;
import com.danni.BoardCard.exception.BoardServiceException;
import com.danni.BoardCard.persistence.dao.BoardColumnDAO;
import com.danni.BoardCard.persistence.dao.BoardDAO;
import com.danni.BoardCard.persistence.entity.BoardEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BoardQueryService {

    private final BoardDAO boardDAO;
    private final BoardColumnDAO boardColumnDAO;

    /**
     * Busca um BoardEntity pelo ID com suas colunas associadas
     * @param id ID do board
     * @return Optional contendo o BoardEntity se encontrado
     * @throws BoardServiceException se ocorrer erro ao acessar o banco de dados
     */
    public Optional<BoardEntity> findById(final Long id) {
        try {
            Optional<BoardEntity> optional = boardDAO.findById(id);
            if (optional.isPresent()) {
                BoardEntity entity = optional.get();
                entity.setBoardColumns(boardColumnDAO.findByBoardId(entity.getId()));
            }
            return optional;
        } catch (SQLException e) {
            log.error("Error fetching board with id: {}", id, e);
            throw new BoardServiceException("Failed to fetch board with id: " + id, e);
        }
    }

    /**
     * Busca detalhes completos de um board incluindo colunas e estatísticas
     * @param id ID do board
     * @return Optional contendo DTO com detalhes se encontrado
     * @throws BoardServiceException se ocorrer erro ao acessar o banco de dados
     */
    public Optional<BoardDetailsDTO> showBoardDetails(final Long id) {
        try {
            Optional<BoardEntity> optional = boardDAO.findById(id);
            if (!optional.isPresent()) {
                return Optional.empty();
            }

            BoardEntity entity = optional.get();
            var columns = boardColumnDAO.findByBoardIdWithDetails(entity.getId());
            return Optional.of(new BoardDetailsDTO(entity.getId(), entity.getName(), columns));
        } catch (SQLException e) {
            log.error("Error fetching board details for id: {}", id, e);
            throw new BoardServiceException("Failed to fetch board details for id: " + id, e);
        }
    }
}