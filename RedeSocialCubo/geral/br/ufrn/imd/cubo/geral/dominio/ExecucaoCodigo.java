package br.ufrn.imd.cubo.geral.dominio;

import java.util.Date;

import br.ufrn.imd.cubo.arq.dominio.Usuario;

/**
 * Representa uma execução de código no sistema.
 *  
 * @author Renan
 */
public class ExecucaoCodigo {

	/** Código executado **/
	private Codigo codigo;
	
	/** Quem executou o código */
	private Usuario usuarioExecucao;
	
	/** Data/hora da execução */
	private Date dataHora;
	
	/** Resultado (console) da execução. */
	private String resultado;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((codigo == null) ? 0 : codigo.hashCode());
		result = prime * result + ((dataHora == null) ? 0 : dataHora.hashCode());
		result = prime * result + ((usuarioExecucao == null) ? 0 : usuarioExecucao.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExecucaoCodigo other = (ExecucaoCodigo) obj;
		if (codigo == null) {
			if (other.codigo != null)
				return false;
		} else if (!codigo.equals(other.codigo))
			return false;
		if (dataHora == null) {
			if (other.dataHora != null)
				return false;
		} else if (!dataHora.equals(other.dataHora))
			return false;
		if (usuarioExecucao == null) {
			if (other.usuarioExecucao != null)
				return false;
		} else if (!usuarioExecucao.equals(other.usuarioExecucao))
			return false;
		return true;
	}

	public Codigo getCodigo() {
		return codigo;
	}

	public void setCodigo(Codigo codigo) {
		this.codigo = codigo;
	}

	public Usuario getUsuarioExecucao() {
		return usuarioExecucao;
	}

	public void setUsuarioExecucao(Usuario usuarioExecucao) {
		this.usuarioExecucao = usuarioExecucao;
	}

	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public String getResultado() {
		return resultado;
	}

	public void setResultado(String resultado) {
		this.resultado = resultado;
	}
	
}
