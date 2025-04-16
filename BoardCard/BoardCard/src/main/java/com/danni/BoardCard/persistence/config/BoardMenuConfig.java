package com.danni.BoardCard.persistence.config;

import com.danni.BoardCard.persistence.dao.*;
import com.danni.BoardCard.persistence.entity.BoardEntity;
import com.danni.BoardCard.persistence.repository.BoardRepository;
import com.danni.BoardCard.service.*;
import com.danni.BoardCard.ui.BoardMenu;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@Configuration
public class BoardMenuConfig {

    @Bean
    @Transactional
    public BoardMenu boardMenu(DataSource dataSource, BoardRepository boardRepository) {
        // Verifica se existe um board, senão cria um padrão
        BoardEntity boardEntity = boardRepository.findAll().stream().findFirst()
                .orElseGet(() -> {
                    BoardEntity newBoard = new BoardEntity();
                    newBoard.setName("Default Board");
                    return boardRepository.save(newBoard);
                });

        // Configuração dos DAOs
        BoardDAO boardDao = new BoardDAO(dataSource);
        BoardColumnDAO boardColumnDao = new BoardColumnDAO(dataSource);
        CardDAO cardDao = new CardDAO(dataSource);
        BlockDAO blockDao = new BlockDAO(dataSource);

        // Configuração dos serviços
        CardService cardService = new CardService(cardDao, blockDao);
        BoardQueryService boardQueryService = new BoardQueryService(boardDao, boardColumnDao);
        BoardColumnQueryService boardColumnQueryService = new BoardColumnQueryService(boardColumnDao);
        CardQueryService cardQueryService = new CardQueryService(cardDao);

        return new BoardMenu(
                boardEntity,
                cardService,
                boardQueryService,
                boardColumnQueryService,
                cardQueryService
        );
    }
}