package com.danni.BoardCard.persistence.dao;

import org.springframework.stereotype.Repository;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.OffsetDateTime;

import static com.danni.BoardCard.persistence.converter.OffsetDateTimeConverter.toTimestamp;

@Repository
public class BlockDAO {
    private final DataSource dataSource;

    // 1. SOLUÇÃO: Construtor explícito para injeção de dependência
    public BlockDAO(DataSource dataSource) {
        if (dataSource == null) {
            throw new IllegalArgumentException("DataSource cannot be null");
        }
        this.dataSource = dataSource;
    }

    public void block(final String reason, final Long cardId) throws SQLException {
        if (reason == null || cardId == null) {
            throw new IllegalArgumentException("Reason and cardId must not be null");
        }

        var sql = "INSERT INTO BLOCKS (blocked_at, block_reason, card_id) VALUES (?, ?, ?);";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }

    public void unblock(final String reason, final Long cardId) throws SQLException {
        if (reason == null || cardId == null) {
            throw new IllegalArgumentException("Reason and cardId must not be null");
        }

        var sql = "UPDATE BLOCKS SET unblocked_at = ?, unblock_reason = ? WHERE card_id = ? AND unblock_reason IS NULL;";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            var i = 1;
            statement.setTimestamp(i++, toTimestamp(OffsetDateTime.now()));
            statement.setString(i++, reason);
            statement.setLong(i, cardId);
            statement.executeUpdate();
        }
    }
}