package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao {

	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn=conn;
	}

	@Override
	public void insert(Department department) {

		PreparedStatement st = null;
		StringBuilder query = new StringBuilder();

		try {
			query.append("INSERT INTO department ");
			query.append("(Name)");
			query.append("VALUES ");
			query.append("(?)");

			st = conn.prepareStatement(query.toString(), Statement.RETURN_GENERATED_KEYS);

			st.setString(1, department.getName());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);
					department.setId(id);
				}
				DB.closeResultSet(rs);
			} else {
				throw new DbException("Erro inesperado, nenhuma linha foi alterada");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void update(Department department) {

		PreparedStatement st = null;
		StringBuilder query = new StringBuilder();

		try {
			query.append("UPDATE department SET ");
			query.append("Name = ? ");
			query.append("WHERE id = ?");

			st = conn.prepareStatement(query.toString());

			st.setString(1, department.getName());
			st.setInt(2, department.getId());

			int rowsAffected = st.executeUpdate();

			if (rowsAffected > 0) {
				System.out.println("Linhas alteradas : " + rowsAffected);
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null;
		StringBuilder query = new StringBuilder();

		try {
			query.append("DELETE FROM department ");
			query.append("WHERE id=?");

			st = conn.prepareStatement(query.toString());
			st.setInt(1, id);
			int rowsAffected = st.executeUpdate();

			if (rowsAffected == 0) {
				throw new DbException("Não foi encotrado o ID");
			}
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();

		try {
			query.append("SELECT department.* ");
			query.append("FROM department ");
			query.append("WHERE department.id = ?");

			st = conn.prepareStatement(query.toString());
			st.setInt(1, id);
			rs = st.executeQuery();
			if (rs.next()) {
				Department dep = instantiateDepartment(rs);
				return dep;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		StringBuilder query = new StringBuilder();

		try {
			query.append("SELECT * from department ORDER BY Name");

			st = conn.prepareStatement(query.toString());

			rs = st.executeQuery();

			List<Department> departmentList = new ArrayList<>();

			while (rs.next()) {
				Department department = instantiateDepartment(rs);
				departmentList.add(department);
			}
			return departmentList;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}
	}

	public Department instantiateDepartment(ResultSet rs) throws SQLException {

		Department dep = new Department();
		dep.setId(rs.getInt("id"));
		dep.setName(rs.getString("Name"));
		return dep;
	}

}
