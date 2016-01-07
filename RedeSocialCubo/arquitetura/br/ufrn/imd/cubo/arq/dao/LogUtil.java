package br.ufrn.imd.cubo.arq.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

public class LogUtil {

	private static String URL = "jdbc:postgresql://localhost:5432/rede_social_cubo_logs";
	private static String usuario = "postgres";
	private static String pass = "postgres";

	private String DBerro;
	private Connection conn;

	private static LogUtil singleton = new LogUtil();

	public static LogUtil getInstance() {
		return singleton;
	}

	private void conectar() throws SQLException {
		try {
			this.conn = DriverManager.getConnection(URL, usuario, pass);
		} catch (SQLException e) {
			this.DBerro = "Conexão : " + e.toString();
			System.out.println(this.DBerro);
		} catch (Exception e) {
			this.DBerro = "Conexão : " + e.toString();
			System.out.println(this.DBerro);
		}
	}

	public void desconectar() throws SQLException {
		try {
			if (!(this.conn.isClosed()))
				this.conn.close();

			this.DBerro = "";
		} catch (SQLException e) {
			DBerro = "Erro ao Fechar Conexão : " + e.toString();
			System.out.println(this.DBerro);
		} finally {
			if (!(this.conn.isClosed()))
				this.conn.close();
		}
	}

	public void inserirLog(Log log) throws SQLException {
		conectar();

		try {

			String sql = " insert into log (id_entidade, operacao, entidade, data, dados, id_usuario "
					+ ") " + " values (?, ?, ?, ?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setInt(1, log.getIdEntidade());
			ps.setString(2, log.getOperacao());
			ps.setString(3, log.getEntidade());
			ps.setDate(4, new java.sql.Date(log.getData().getTime()));
			ps.setString(5, log.getDados());
			if (log.getIdUsuario() != null)
				ps.setInt(6, log.getIdUsuario());
			else
				ps.setNull(6, Types.INTEGER);

			ps.executeUpdate();
		} finally {
			desconectar();
		}

	}

}
