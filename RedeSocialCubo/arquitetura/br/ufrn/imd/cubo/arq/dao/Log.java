package br.ufrn.imd.cubo.arq.dao;

import java.util.Date;

public class Log {
	
	private int idEntidade;
	
	private String operacao;
	
	private String entidade;
	
	private Date data;
	
	private String dados;
	
	private Integer idUsuario;
	
	public String getOperacao() {
		return operacao;
	}

	public void setOperacao(String operacao) {
		this.operacao = operacao;
	}

	public String getEntidade() {
		return entidade;
	}

	public void setEntidade(String entidade) {
		this.entidade = entidade;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getIdEntidade() {
		return idEntidade;
	}

	public void setIdEntidade(int idEntidade) {
		this.idEntidade = idEntidade;
	}

	public String getDados() {
		return dados;
	}

	public void setDados(String dados) {
		this.dados = dados;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}
}
