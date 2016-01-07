package br.ufrn.imd.cubo.arq.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.imd.cubo.arq.util.UsuarioUtil;

/**
 * Trata-se de um {@link PersistDB} que inclui mecanismos 
 * de grava��o de data de cria��o, data de atualiza��o, e
 * usu�rio de atualiza��o.<br/>
 * Deve ser estendido pelas entidades que quiserem utilizar
 * esses atributos.
 *  
 * @author Renan
 */
@SuppressWarnings("serial")
@MappedSuperclass
public abstract class PersistDBRecorded implements PersistDB {

	/** Data de cria��o do registro. */
	@Column(name="criado_em", nullable = false, updatable=false)
	@Temporal(TemporalType.TIMESTAMP)
	protected Date criadoEm;
	
	/** Usu�rio de cria��o do registro. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_criado_por", referencedColumnName = "id_usuario", nullable=false, updatable=false)
	protected Usuario criadoPor;
	
	/** Data da �ltima atualiza��o do registro. */
	@Column(name="atualizado_em")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date atualizadoEm;
	
	/** Usu�rio que atualizou o registro pela �ltima vez. */
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="id_atualizado_por", referencedColumnName = "id_usuario")
	protected Usuario atualizadoPor;
	
	@PrePersist
	protected void onCreate(){
		criadoEm = new Date();
		criadoPor = UsuarioUtil.getUsuarioLogado();
	}
	
	@PreUpdate
	protected void onUpdate(){
		atualizadoEm = new Date();
		atualizadoPor = UsuarioUtil.getUsuarioLogado();
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public Usuario getCriadoPor() {
		return criadoPor;
	}

	public Date getAtualizadoEm() {
		return atualizadoEm;
	}

	public Usuario getAtualizadoPor() {
		return atualizadoPor;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public void setAtualizadoEm(Date atualizadoEm) {
		this.atualizadoEm = atualizadoEm;
	}

	public void setAtualizadoPor(Usuario atualizadoPor) {
		this.atualizadoPor = atualizadoPor;
	}
	
}
