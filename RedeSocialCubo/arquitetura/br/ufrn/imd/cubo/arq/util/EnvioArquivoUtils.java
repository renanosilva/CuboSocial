package br.ufrn.imd.cubo.arq.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

/**
 * Classe utilitária para envio de arquivos ao BD.
 * 
 * @author Renan
 */
public class EnvioArquivoUtils {
	
	private String URL = "jdbc:postgresql://localhost:5432/rede_social_cubo_arquivos";
	private String usuario = "postgres";
	private String pass = "postgres";

	private String DBerro;
	private Connection conn;

//	private static EnvioArquivoUtils singleton = new EnvioArquivoUtils();
	
//	public static EnvioArquivoUtils getInstance() {
//		return singleton;
//	}
	
	public void conectar() {
		try {
			conn = DriverManager.getConnection(URL, usuario, pass);
		} catch (SQLException e) {
			DBerro = "Conexão : " + e.toString();
			System.out.println(DBerro);
		} catch (Exception e) {
			DBerro = "Conexão : " + e.toString();
			System.out.println(DBerro);
		}
	}

	public void desconectar() {
		try {
			if (!(conn.isClosed()))
				conn.close();

			DBerro = "";
		} catch (SQLException e) {
			DBerro = "Erro ao Fechar Conexão : " + e.toString();
			System.out.println(DBerro);
		} finally {
			
			try {
				if (!(conn.isClosed()))
					conn.close();
			} catch (SQLException e) {
				DBerro = "Erro ao Fechar Conexão : " + e.toString();
				System.out.println(DBerro);
				e.printStackTrace();
			}
			
		}
	}

	/**
	 * Recupera o próximo Id de arquivo disponivel. Este
	 * método deve ser chamado antes da inserção de um novo arquivo.
	 *
	 * @return
	 * @throws SQLException 
	 */
	public int getNextIdArquivo() throws SQLException {
//		conectar();

		try {
			String sql = "select nextval('arquivos.seq_arquivos')";

			PreparedStatement ps = conn.prepareStatement(sql);
			ResultSet set = ps.executeQuery();
			set.next();
			return set.getInt(1);
			
		} finally {
//			desconectar();
		}
	}
	
	public void inserirArquivo(int idArquivo, String nome, final byte[] conteudo) throws SQLException{
//		conectar();

		try {

			String sql = " insert into arquivos.arquivos (id_arquivo, arquivo, nome) "
					+ " values (?, ?, ?)";

			PreparedStatement ps = conn.prepareStatement(sql);

			ps.setInt(1, idArquivo);
			ps.setBytes(2, conteudo);
			ps.setString(3, nome);
			
			ps.executeUpdate();
		} finally {
//			desconectar();
		}
	}
	
	public void removerArquivo(int idArquivo) throws SQLException{
//		conectar();

		try {

			String sql = " delete from arquivos.arquivos where id_arquivo = ? ";

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, idArquivo);
			
			ps.executeUpdate();
		} finally {
//			desconectar();
		}
	}
	
	public void recuperaArquivo(HttpServletResponse response, final int idArquivo, boolean save) throws SQLException, IOException {
//		conectar();
		
		try {
			PreparedStatement ps = conn.prepareStatement("select arquivo, nome from arquivos.arquivos where id_arquivo = ? ");
			ps.setInt(1, idArquivo);
			
			ResultSet result = ps.executeQuery();
			result.next();
			
			byte[] bytes = result.getBytes("arquivo");
			String nome = result.getString("nome");
			
			if (save) {
				response.setHeader("Content-disposition", "attachment; filename=\"" + nome + "\"");
			} else {
				response.setHeader("Content-disposition", "inline; filename=\"" + nome + "\"");
			}
			
			response.getOutputStream().write(bytes);
		} finally {
//			desconectar();
		}
	}
	
}
