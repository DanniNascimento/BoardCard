package com.danni.BoardCard.persistence.dao;

import com.danni.BoardCard.persistence.entity.BoardEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class BoardDAO {
    private final DataSource dataSource;

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        if (entity == null || entity.getName() == null) {
            throw new IllegalArgumentException("BoardEntity and name must not be null");
        }

        var sql = "INSERT INTO BOARDS (name) values (?);";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, entity.getName());
            statement.executeUpdate();

            try (var generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    entity.setId(generatedKeys.getLong(1));
                }
            }
        }
        return entity;
    }

    public void delete(final Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        var sql = "DELETE FROM BOARDS WHERE id = ?;";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            statement.executeUpdate();
        }
    }

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        var sql = "SELECT id, name FROM BOARDS WHERE id = ?;";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var entity = new BoardEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setName(resultSet.getString("name"));
                    return Optional.of(entity);
                }
            }
        }
        return Optional.empty();
    }

    public boolean exists(final Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("ID must not be null");
        }

        var sql = "SELECT 1 FROM BOARDS WHERE id = ?;";
        try (Connection connection = dataSource.getConnection();
             var statement = connection.prepareStatement(sql)) {

            statement.setLong(1, id);
            try (var resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
}