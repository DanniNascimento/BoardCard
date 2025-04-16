package com.danni.BoardCard.persistence.dao;

import com.danni.BoardCard.dto.CardDetailsDTO;
import com.danni.BoardCard.persistence.entity.CardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

import static com.danni.BoardCard.persistence.converter.OffsetDateTimeConverter.toOffsetDateTime;
import static java.util.Objects.nonNull;

@Repository
@RequiredArgsConstructor
public class CardDAO {
    private final DataSource dataSource;

    public CardEntity insert(final CardEntity entity) throws SQLException {
        if (entity == null || entity.getBoardColumn() == null) {
            throw new IllegalArgumentException("CardEntity and its boardColumn must not be null");
        }

        var sql = "INSERT INTO CARDS (title, description, board_column_id) values (?, ?, ?);";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            var i = 1;
            statement.setString(i++, entity.getTitle());
            statement.setString(i++, entity.getDescription());
            statement.setLong(i, entity.getBoardColumn().getId());
            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
        return entity;
    }

    public void moveToColumn(final Long columnId, final Long cardId) throws SQLException {
        if (columnId == null || cardId == null) {
            throw new IllegalArgumentException("columnId and cardId must not be null");
        }

        var sql = "UPDATE CARDS SET board_column_id = ? WHERE id = ?;";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            var i = 1;
            statement.setLong(i++, columnId);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public Optional<CardDetailsDTO> findById(final Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        var sql = """
                SELECT c.id, c.title, c.description, b.blocked_at, b.block_reason, 
                       c.board_column_id, bc.name,
                       (SELECT COUNT(sub_b.id) FROM BLOCKS sub_b WHERE sub_b.card_id = c.id) blocks_amount
                FROM CARDS c
                LEFT JOIN BLOCKS b ON c.id = b.card_id AND b.unblocked_at IS NULL
                INNER JOIN BOARDS_COLUMNS bc ON bc.id = c.board_column_id
                WHERE c.id = ?;
                """;

        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var dto = new CardDetailsDTO(
                            resultSet.getLong("c.id"),
                            resultSet.getString("c.title"),
                            resultSet.getString("c.description"),
                            nonNull(resultSet.getString("b.block_reason")),
                            toOffsetDateTime(resultSet.getTimestamp("b.blocked_at")),
                            resultSet.getString("b.block_reason"),
                            resultSet.getInt("blocks_amount"),
                            resultSet.getLong("c.board_column_id"),
                            resultSet.getString("bc.name")
                    );
                    return Optional.of(dto);
                }
            }
        }
        return Optional.empty();
    }
}