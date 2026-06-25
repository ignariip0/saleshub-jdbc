package model.dao.impl;

import db.DB;
import db.DbException;
import db.DbIntegrityException;
import model.dao.DepartmentDao;
import model.entities.Department;
import model.entities.Seller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DepartmentDaoJDBC implements DepartmentDao {

    Connection conn;

    public DepartmentDaoJDBC(Connection conn) {
        this.conn = conn;
    }


    @Override
    public void insert(Department obj) {
        PreparedStatement st = null;

        try {
            // RETURN_GENERATED_KEYS pra pegar o Id depois
            st = conn.prepareStatement(
                    "INSERT INTO department" +
                            "(Name)" +
                            "VALUES" +
                            "(?)",
                    PreparedStatement.RETURN_GENERATED_KEYS);

            st.setString(1, obj.getName());

            int rowsAffected = st.executeUpdate();
            if (rowsAffected > 0) {
                ResultSet rs = st.getGeneratedKeys();
                if (rs.next()) {
                    int id = rs.getInt(1); // só 1 coluna, sem nome
                    obj.setId(id);
                }
                DB.closeResultSet(rs);
            } else {
                throw new DbException("Unexpected error! No rows affected!");
            }

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st); // conn fica aberta, não fecha aqui
        }
    }


    @Override
    public void update(Department obj) {
        PreparedStatement st = null;

        try {
            // Id no WHERE diz qual linha, o resto é o valor novo
            st = conn.prepareStatement(
                    "UPDATE department " +
                            "SET Name = ? " +
                            "WHERE Id = ?");

            st.setString(1, obj.getName());
            st.setInt(2, obj.getId()); // esse é o department que vai ser alterado

            st.executeUpdate(); // não precisei guardar o retorno aqui

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;
        try {
            st = conn.prepareStatement(
                    "DELETE FROM department WHERE Id = ?"
            );
            st.setInt(1, id);

            int rows = st.executeUpdate();
            if (rows == 0){
                throw new DbException("Department Id doesn't exist!"); // rodou, mas não tinha ninguém com esse Id
            }
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                throw new DbIntegrityException(e.getMessage());
            } else {
                throw new DbException(e.getMessage());
            }
        } finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        return null;
    }

    @Override
    public List<Department> findAll() {
        return List.of();
    }
}