package com.danni.BoardCard.ui;

import com.danni.BoardCard.dto.BoardColumnInfoDTO;
import com.danni.BoardCard.persistence.entity.BoardColumnEntity;
import com.danni.BoardCard.persistence.entity.BoardEntity;
import com.danni.BoardCard.persistence.entity.CardEntity;
import com.danni.BoardCard.service.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

@Transactional
public class BoardMenu {
    private final BoardEntity board;
    private final CardService cardService;
    private final BoardQueryService boardQueryService;
    private final BoardColumnQueryService boardColumnQueryService;
    private final CardQueryService cardQueryService;

    public BoardMenu(BoardEntity board,
                     CardService cardService,
                     BoardQueryService boardQueryService,
                     BoardColumnQueryService boardColumnQueryService,
                     CardQueryService cardQueryService) {
        this.board = board;
        this.cardService = cardService;
        this.boardQueryService = boardQueryService;
        this.boardColumnQueryService = boardColumnQueryService;
        this.cardQueryService = cardQueryService;
    }

    public void execute() {
        try (Scanner scanner = new Scanner(System.in).useDelimiter("\n")) {
            System.out.printf("Bem vindo ao board %s, selecione a operação desejada\n", board.getName());
            int option = -1;
            while (option != 9) {
                printMenu();
                option = scanner.nextInt();
                handleMenuOption(option, scanner);
            }
        } catch (Exception e) {
            System.err.println("Erro inesperado: " + e.getMessage());
        }
    }

    private void printMenu() {
        System.out.println("1 - Criar um card");
        System.out.println("2 - Mover um card");
        System.out.println("3 - Bloquear um card");
        System.out.println("4 - Desbloquear um card");
        System.out.println("5 - Cancelar um card");
        System.out.println("6 - Ver board");
        System.out.println("7 - Ver coluna com cards");
        System.out.println("8 - Ver card");
        System.out.println("9 - Voltar para o menu anterior");
        System.out.println("10 - Sair");
    }

    private void handleMenuOption(int option, Scanner scanner) {
        try {
            switch (option) {
                case 1 -> createCard(scanner);
                case 2 -> moveCardToNextColumn(scanner);
                case 3 -> blockCard(scanner);
                case 4 -> unblockCard(scanner);
                case 5 -> cancelCard(scanner);
                case 6 -> showBoard();
                case 7 -> showColumn(scanner);
                case 8 -> showCard(scanner);
                case 9 -> System.out.println("Voltando para o menu anterior");
                case 10 -> System.exit(0);
                default -> System.out.println("Opção inválida, informe uma opção do menu");
            }
        } catch (Exception ex) {
            System.out.printf("Erro: %s - %s\n", ex.getClass().getSimpleName(), ex.getMessage());
        }
    }

    private void createCard(Scanner scanner) {
        try {
            var card = new CardEntity();
            System.out.println("Informe o título do card");
            card.setTitle(scanner.next());
            System.out.println("Informe a descrição do card");
            card.setDescription(scanner.next());
            card.setBoardColumn(board.getInitialColumn());

            cardService.create(card);
            System.out.println("Card criado com sucesso!");
        } catch (Exception e) {
            System.out.printf("Erro ao criar card: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void moveCardToNextColumn(Scanner scanner) {
        try {
            System.out.println("Informe o id do card que deseja mover para a próxima coluna");
            long cardId = scanner.nextLong();
            List<BoardColumnInfoDTO> boardColumnsInfo = board.getBoardColumns().stream()
                    .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                    .toList();

            cardService.moveToNextColumn(cardId, boardColumnsInfo);
            System.out.println("Card movido com sucesso!");
        } catch (Exception e) {
            System.out.printf("Erro ao mover card: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void blockCard(Scanner scanner) {
        try {
            System.out.println("Informe o id do card que será bloqueado");
            long cardId = scanner.nextLong();
            System.out.println("Informe o motivo do bloqueio do card");
            String reason = scanner.next();
            List<BoardColumnInfoDTO> boardColumnsInfo = board.getBoardColumns().stream()
                    .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                    .toList();

            cardService.block(cardId, reason, boardColumnsInfo);
            System.out.println("Card bloqueado com sucesso!");
        } catch (Exception e) {
            System.out.printf("Erro ao bloquear card: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void unblockCard(Scanner scanner) {
        try {
            System.out.println("Informe o id do card que será desbloqueado");
            long cardId = scanner.nextLong();
            System.out.println("Informe o motivo do desbloqueio do card");
            String reason = scanner.next();

            cardService.unblock(cardId, reason);
            System.out.println("Card desbloqueado com sucesso!");
        } catch (Exception e) {
            System.out.printf("Erro ao desbloquear card: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void cancelCard(Scanner scanner) {
        try {
            System.out.println("Informe o id do card que deseja mover para a coluna de cancelamento");
            long cardId = scanner.nextLong();
            BoardColumnEntity cancelColumn = board.getCancelColumn();
            List<BoardColumnInfoDTO> boardColumnsInfo = board.getBoardColumns().stream()
                    .map(bc -> new BoardColumnInfoDTO(bc.getId(), bc.getOrder(), bc.getKind()))
                    .toList();

            cardService.cancel(cardId, cancelColumn.getId(), boardColumnsInfo);
            System.out.println("Card cancelado com sucesso!");
        } catch (Exception e) {
            System.out.printf("Erro ao cancelar card: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void showBoard() {
        try {
            boardQueryService.showBoardDetails(board.getId())
                    .ifPresent(b -> {
                        System.out.printf("Board [%s, %s]\n", b.id(), b.name());
                        b.columns().forEach(c ->
                                System.out.printf("Coluna [%s] tipo: [%s] tem %s cards\n",
                                        c.name(), c.kind(), c.cardsAmount())
                        );
                    });
        } catch (Exception e) {
            System.out.printf("Erro ao exibir board: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void showColumn(Scanner scanner) {
        try {
            List<Long> columnsIds = board.getBoardColumns().stream()
                    .map(BoardColumnEntity::getId)
                    .toList();
            long selectedColumnId = -1L;

            System.out.printf("Escolha uma coluna do board %s pelo id:\n", board.getName());
            board.getBoardColumns().forEach(c ->
                    System.out.printf("%s - %s [%s]\n", c.getId(), c.getName(), c.getKind()));

            while (!columnsIds.contains(selectedColumnId)) {
                selectedColumnId = scanner.nextLong();
                if (!columnsIds.contains(selectedColumnId)) {
                    System.out.println("Id de coluna inválido. Por favor, escolha um id da lista.");
                }
            }

            boardColumnQueryService.findById(selectedColumnId)
                    .ifPresent(co -> {
                        System.out.printf("Coluna %s tipo %s\n", co.getName(), co.getKind());
                        co.getCards().forEach(ca -> System.out.printf(
                                "Card %s - %s\nDescrição: %s\n",
                                ca.getId(), ca.getTitle(), ca.getDescription()));
                    });
        } catch (Exception e) {
            System.out.printf("Erro ao exibir coluna: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }

    private void showCard(Scanner scanner) {
        try {
            System.out.println("Informe o id do card que deseja visualizar");
            long selectedCardId = scanner.nextLong();

            cardQueryService.findById(selectedCardId)
                    .ifPresentOrElse(
                            c -> {
                                System.out.printf("Card %s - %s.\n", c.id(), c.title());
                                System.out.printf("Descrição: %s\n", c.description());
                                System.out.println(c.blocked() ?
                                        "Está bloqueado. Motivo: " + c.blockReason() :
                                        "Não está bloqueado");
                                System.out.printf("Já foi bloqueado %s vezes\n", c.blocksAmount());
                                System.out.printf("Está no momento na coluna %s - %s\n",
                                        c.columnId(), c.columnName());
                            },
                            () -> System.out.printf("Não existe um card com o id %s\n", selectedCardId));
        } catch (Exception e) {
            System.out.printf("Erro ao exibir card: %s - %s\n", e.getClass().getSimpleName(), e.getMessage());
        }
    }
}