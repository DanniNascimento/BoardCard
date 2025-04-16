package com.danni.BoardCard.service;

import com.danni.BoardCard.persistence.dao.BoardColumnDAO;
import com.danni.BoardCard.persistence.entity.BoardColumnEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardColumnQueryService {

    private final BoardColumnDAO boardColumnDAO;

    public Optional<BoardColumnEntity> findById(final Long id) throws SQLException {
        return boardColumnDAO.findById(id);
    }
}