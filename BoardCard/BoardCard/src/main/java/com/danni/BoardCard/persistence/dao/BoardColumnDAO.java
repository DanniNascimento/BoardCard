package com.danni.BoardCard.persistence.dao;

import com.danni.BoardCard.dto.BoardColumnDTO;
import com.danni.BoardCard.persistence.entity.BoardColumnEntity;
import com.danni.BoardCard.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.danni.BoardCard.persistence.entity.BoardColumnKindEnum.findByName;

@Repository
@RequiredArgsConstructor
public class BoardColumnDAO {

    private final DataSource dataSource;

    public BoardColumnEntity insert(final BoardColumnEntity entity) throws SQLException {
        if (entity == null || entity.getBoard() == null) {
            throw new IllegalArgumentException("BoardColumnEntity and its board must not be null");
        }

        var sql = "INSERT INTO BOARDS_COLUMNS (name, `order`, kind, board_id) VALUES (?, ?, ?, ?);";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            var i = 1;
            statement.setString(i++, entity.getName());
            statement.setInt(i++, entity.getOrder());
            statement.setString(i++, entity.getKind().name());
            statement.setLong(i, entity.getBoard().getId());
            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
            return entity;
        }
    }

    public List<BoardColumnEntity> findByBoardId(final Long boardId) throws SQLException {
        if (boardId == null) {
            throw new IllegalArgumentException("Board ID must not be null");
        }

        List<BoardColumnEntity> entities = new ArrayList<>();
        var sql = "SELECT id, name, `order`, kind FROM BOARDS_COLUMNS WHERE board_id = ? ORDER BY `order`";

        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var entity = new BoardColumnEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setName(resultSet.getString("name"));
                    entity.setOrder(resultSet.getInt("order"));
                    entity.setKind(findByName(resultSet.getString("kind")));
                    entities.add(entity);
                }
            }
            return entities;
        }
    }

    public List<BoardColumnDTO> findByBoardIdWithDetails(final Long boardId) throws SQLException {
        if (boardId == null) {
            throw new IllegalArgumentException("Board ID must not be null");
        }

        List<BoardColumnDTO> dtos = new ArrayList<>();
        var sql =
                """
                SELECT bc.id,
                       bc.name,
                       bc.kind,
                       (SELECT COUNT(c.id)
                               FROM CARDS c
                              WHERE c.board_column_id = bc.id) cards_amount
                  FROM BOARDS_COLUMNS bc
                 WHERE board_id = ?
                 ORDER BY `order`;
                """;

        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, boardId);
            try (var resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    var dto = new BoardColumnDTO(
                            resultSet.getLong("bc.id"),
                            resultSet.getString("bc.name"),
                            findByName(resultSet.getString("bc.kind")),
                            resultSet.getInt("cards_amount")
                    );
                    dtos.add(dto);
                }
            }
            return dtos;
        }
    }

    public Optional<BoardColumnEntity> findById(final Long columnId) throws SQLException {
        if (columnId == null) {
            throw new IllegalArgumentException("Column ID must not be null");
        }

        var sql =
                """
                SELECT bc.id, bc.name, bc.kind, bc.order,
                       c.id as card_id,
                       c.title,
                       c.description
                  FROM BOARDS_COLUMNS bc
                  LEFT JOIN CARDS c ON c.board_column_id = bc.id
                 WHERE bc.id = ?
                 ORDER BY c.position;
                """;

        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, columnId);
            try (var resultSet = statement.executeQuery()) {
                if (!resultSet.next()) {
                    return Optional.empty();
                }

                var entity = new BoardColumnEntity();
                entity.setId(resultSet.getLong("id"));
                entity.setName(resultSet.getString("name"));
                entity.setKind(findByName(resultSet.getString("kind")));
                entity.setOrder(resultSet.getInt("order"));

                // Process cards
                do {
                    if (resultSet.getLong("card_id") != 0) {
                        var card = new CardEntity();
                        card.setId(resultSet.getLong("card_id"));
                        card.setTitle(resultSet.getString("title"));
                        card.setDescription(resultSet.getString("description"));
                        entity.getCards().add(card);
                    }
                } while (resultSet.next());

                return Optional.of(entity);
            }
        }
    }
}