package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class DB {

	private static Connection conn = null;// conexão
	private static Statement st = null;// consulta
	private static ResultSet rs = null;// recebe o resultado

	// método responsável por conectar com o banco de dados . Conectar no banco de
	// dados em jdbc é instanciar um objeto do tipo "Connection"
	public static Connection getConnection() {
		if (conn == null) {
			try {
				Properties props = loadProperties();
				String url = props.getProperty("dburl");
				conn = DriverManager.getConnection(url, props);
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
		return conn;
	}

	// fecha a conexão
	public static void closeConnection() {
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	// método estático auxiliar -- carrega as propriedades do banco de dados
	// retornando esses dados no objeto props
	private static Properties loadProperties() {

		try (FileInputStream fs = new FileInputStream("db.properties")) {
			Properties props = new Properties();
			props.load(fs); // load- faz a leitura do arquivo properties apontato pelo Input Stream fs e
							// guardar os dados dentro do objeto props
			return props;
		} catch (IOException e) {
			throw new DbException(e.getMessage());//// excessão personalizada
		}
	}

	public static void inserirDadosComRetornoChave() {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		PreparedStatement st = null;
		try {
			conn = DB.getConnection();

			st = conn.prepareStatement("insert into department (Name) values ('D1'),('D2')",
					Statement.RETURN_GENERATED_KEYS);
			int rowsAffected = st.executeUpdate();
			int queryTimout = st.getQueryTimeout();

			if (rowsAffected > 0) {
				rs = st.getGeneratedKeys(); // Função retorna objeto do tipo resultSet, pode ter mais valores;
				while (rs.next()) {
					int id = rs.getInt(1);// // O valor passado no parametro é 1, porque o rs vai conter apenas um valor
											// para ser extraído
					System.out.println("Done! Id= " + id);
				}
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			closeStatement(st);
			closeConnection();
		}
	}

	public static void atualizarDados() {

		PreparedStatement st = null;

		try {
			conn = DB.getConnection(); // conecta com o banco de dados;
			st = conn.prepareStatement(
					"UPDATE seller " + "SET BaseSalary = BaseSalary + ? " + "WHERE " + "(DepartmentId = ?");

			st.setDouble(1, 200.0);
			st.setInt(2, 2);

			int rowsAffected = st.executeUpdate();// Qtd de linhas que foram afetadas.

			System.out.println("Done! Rows affected : " + rowsAffected);
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			closeStatement(st);
			closeConnection();
		}
	}

	public static void recuperarDados() {

		try {
			conn = DB.getConnection();

			st = conn.createStatement();

			rs = st.executeQuery("select * from department");

			while (rs.next()) {
				System.out.println(rs.getInt("id") + ", " + rs.getString("Name"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			closeStatement(st);
			closeResultSet(rs);
			closeConnection();
		}
	}

	public static void deletarDados() {

		PreparedStatement st = null;

		try {
			conn = DB.getConnection();
			st = conn.prepareStatement("DELETE FROM department " + "WHERE " + "Id = ? ");

			st.setInt(1, 2);

			int rowsAffected = st.executeUpdate();

			System.out.println("Done! Rows Affected : " + rowsAffected);
		} catch (SQLException e) {
			throw new DbIntegrityException(e.getMessage());
		} finally {
			closeStatement(st);
			closeConnection();
		}
	}

	public static void transacoes() {

		try {
			conn = getConnection();

			conn.setAutoCommit(false);// Define para não commitar as alteraçoes automaticamente

			st = conn.createStatement();

			int rows1 = st.executeUpdate("UPDATE seller SET BaseSalary = 2090 WHERE DepartmentId = 1 ");

			int rows2 = st.executeUpdate("UPDATE seller SET BaseSalary = 3090 WHERE DepartmentId = 2 ");

			conn.commit();

			System.out.println("rows1 " + rows1);
			System.out.println("rows2 " + rows2);
		} catch (SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Transaction rolled back! Caused by : " + e.getMessage());
			} catch (SQLException e1) {
				throw new DbException("Error trying to rollback! Caused by : " + e1.getMessage());
			}
		} finally {
			closeConnection();
			closeStatement(st);
		}
	}

	public static void closeStatement(Statement st) {
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}

	public static void closeResultSet(ResultSet rs) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				throw new DbException(e.getMessage());
			}
		}
	}
}
