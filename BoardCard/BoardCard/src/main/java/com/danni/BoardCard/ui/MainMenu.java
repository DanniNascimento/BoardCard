package com.danni.BoardCard.ui;

import com.danni.BoardCard.persistence.dao.*;
import com.danni.BoardCard.persistence.entity.*;
import com.danni.BoardCard.service.*;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainMenu {
    private final Scanner scanner = new Scanner(System.in).useDelimiter("\n");
    private final BoardService boardService;
    private final BoardQueryService boardQueryService;
    private final DataSource dataSource;

    public MainMenu(DataSource dataSource) {
        this.dataSource = dataSource;
        BoardDAO boardDao = new BoardDAO(dataSource);
        BoardColumnDAO boardColumnDao = new BoardColumnDAO(dataSource);
        this.boardService = new BoardService(boardDao, boardColumnDao);
        this.boardQueryService = new BoardQueryService(boardDao, boardColumnDao);
    }

    public void execute() {
        System.out.println("Bem vindo ao gerenciador de boards, escolha a opção desejada");
        var option = -1;
        while (true) {
            try {
                // Menu options
                switch (option) {
                    case 1 -> createBoard();
                    case 2 -> selectBoard();
                    case 3 -> deleteBoard();
                    case 4 -> System.exit(0);
                    default -> System.out.println("Opção inválida");
                }
            } catch (Exception e) {
                System.err.println("Erro: " + e.getMessage());
            }
        }
    }

    private void createBoard() {
        try {
            BoardEntity entity = new BoardEntity();
            // ... (configuração do board)
            boardService.insert(entity);
        } catch (Exception e) {
            System.err.println("Erro ao criar board: " + e.getMessage());
        }
    }

    private void selectBoard() {
        try {
            System.out.println("Informe o id do board que deseja selecionar");
            Long id = scanner.nextLong();
            BoardEntity board = boardQueryService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Board não encontrado"));

            CardService cardService = new CardService(new CardDAO(dataSource), new BlockDAO(dataSource));
            BoardColumnQueryService columnQueryService = new BoardColumnQueryService(new BoardColumnDAO(dataSource));
            CardQueryService cardQueryService = new CardQueryService(new CardDAO(dataSource));

            new BoardMenu(
                    board,
                    cardService,
                    boardQueryService,
                    columnQueryService,
                    cardQueryService
            ).execute();
        } catch (Exception e) {
            System.err.println("Erro ao selecionar board: " + e.getMessage());
        }
    }

    private void deleteBoard() {
        try {
            System.out.println("Informe o id do board que será excluído");
            Long id = scanner.nextLong();
            boolean deleted = boardService.delete(id);
            System.out.println(deleted ? "Board excluído com sucesso" : "Board não encontrado");
        } catch (Exception e) {
            System.err.println("Erro ao excluir board: " + e.getMessage());
        }
    }

    private BoardColumnEntity createColumn(String name, BoardColumnKindEnum kind, int order) {
        BoardColumnEntity column = new BoardColumnEntity();
        column.setName(name);
        column.setKind(kind);
        column.setOrder(order);
        return column;
    }
}