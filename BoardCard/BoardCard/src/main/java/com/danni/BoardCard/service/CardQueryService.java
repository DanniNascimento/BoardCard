package com.danni.BoardCard.service;

import com.danni.BoardCard.dto.CardDetailsDTO;
import com.danni.BoardCard.persistence.dao.CardDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CardQueryService {

    private final CardDAO cardDAO;

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        return cardDAO.findById(id);
    }
}