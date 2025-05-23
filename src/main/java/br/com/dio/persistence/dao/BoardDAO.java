// Usei o Statement.RETURN_GENERATED_KEYS para obter o ID gerado, evitando dependência de StatementImpl do MySQL.
// Além disso, explicitei o encerramento do ResultSet com try-with-resources, para evitar vazamentos.

package br.com.dio.persistence.dao;

import br.com.dio.persistence.entity.BoardEntity;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Uso do Statement.RETURN_GENERATED_KEYS
import java.util.Optional;

public class BoardDAO {

    private Connection connection;

    public BoardDAO(Connection connection) {
        this.connection = connection;
    };

    public BoardEntity insert(final BoardEntity entity) throws SQLException {
        var sql = "INSERT INTO BOARDS (name) VALUES (?);";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, entity.getName());
            statement.executeUpdate();

            try (ResultSet rs = statement.getGeneratedKeys()) {
                if (rs.next()) {
                    entity.setId(rs.getLong(1));
                }
            }
        }
        return entity;
    };

    public void delete(final Long id) throws SQLException {
        var sql = "DELETE FROM BOARDS WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);
            statement.executeUpdate();
        }
    };

    public Optional<BoardEntity> findById(final Long id) throws SQLException {
        var sql = "SELECT id, name FROM BOARDS WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    var entity = new BoardEntity();
                    entity.setId(resultSet.getLong("id"));
                    entity.setName(resultSet.getString("name"));
                    return Optional.of(entity);
                }
            }
            return Optional.empty();
        }
    };

    public boolean exists(final Long id) throws SQLException {
        var sql = "SELECT 1 FROM BOARDS WHERE id = ?;";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }
};
