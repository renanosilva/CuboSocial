package br.ufrn.imd.cubo.arq.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.primefaces.model.UploadedFile;

import br.ufrn.imd.cubo.arq.util.Formatador;

/** 
 * Entidade que armazena os dados de um usuário do sistema.
 * @author Renan
 */
@SuppressWarnings("serial")
@Entity
@Table(name="usuario", schema="arq")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Usuario extends PersistDBRecorded {
	
	@Id
	@SequenceGenerator(name="gen_id_usuario", sequenceName = "seq_id_usuario", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_usuario" )  
	@Column(name="id_usuario", nullable = false)
	private int id;
	
	@Column(nullable = false)
	private String senha;
	
	@Column(nullable = false)
	private String email;
	
	/** Pessoa associada ao usuário do sistema. */
	@ManyToOne(fetch=FetchType.LAZY, cascade=CascadeType.PERSIST)
	@JoinColumn(name = "id_pessoa", referencedColumnName = "id_pessoa", nullable=false)
	private Pessoa pessoa;
	
	/** Tipo do usuário. */
	@Enumerated(EnumType.STRING)
	@Column(nullable=false)
	private TipoUsuario tipoUsuario;
	
	/** Data do último acesso do usuário no sistema. */
	@Column(name="ultimo_acesso")
	@Temporal(TemporalType.TIMESTAMP)
	private Date ultimoAcesso;
	
	/** Utilizado para remoção lógica. */
	@Column(nullable = false)
	private boolean ativo = true;
	
	/** 
	 * ID da foto que está associada ao usuário.
	 */
	@Column(name="id_foto")
	private Integer idFoto;
	
	/** 
	 * Atributo não persisitido que armazena uma foto que o usuário deseja
	 * para seu perfil.
	 * */
	@Transient
	private UploadedFile arquivo;
	
	/** Atributo transient que armazena a senha real do usuário. Utilizado na recuperação de senha. */
	@Transient
	private String senhaReal;
	
	/** Atributo transient que armazena uma nova senha para o usuário. */
	@Transient
	private String novaSenha;
	
	/** Confirmação da nova senha. */
	@Transient
	private String novaSenhaConfirmacao;
	
	@Override
	public String toString() {
		return pessoa != null && pessoa.getNome() != null ? pessoa.getNome() : email;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
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
		Usuario other = (Usuario) obj;
		if (id != other.id)
			return false;
		return true;
	}



	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescricaoTipoUsuario(){
		return tipoUsuario.toString();
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Date getUltimoAcesso() {
		return ultimoAcesso;
	}
	
	public String getUltimoAcessoFormatado() {
		if (ultimoAcesso == null)
			return "-";
		
		return Formatador.getInstance().formatarDataHora(ultimoAcesso);
	}

	public void setUltimoAcesso(Date ultimoAcesso) {
		this.ultimoAcesso = ultimoAcesso;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public TipoUsuario getTipoUsuario() {
		return tipoUsuario;
	}

	public void setTipoUsuario(TipoUsuario tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}
	
	public boolean isAdministrador(){
		return tipoUsuario != null && tipoUsuario == TipoUsuario.ADMINISTRADOR;
	}
	
	public boolean isUsuarioComum(){
		return tipoUsuario != null && tipoUsuario == TipoUsuario.COMUM;
	}

	public String getSenhaReal() {
		return senhaReal;
	}

	public void setSenhaReal(String senhaReal) {
		this.senhaReal = senhaReal;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	public String getNovaSenhaConfirmacao() {
		return novaSenhaConfirmacao;
	}

	public void setNovaSenhaConfirmacao(String novaSenhaConfirmacao) {
		this.novaSenhaConfirmacao = novaSenhaConfirmacao;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public UploadedFile getArquivo() {
		return arquivo;
	}

	public void setArquivo(UploadedFile arquivo) {
		this.arquivo = arquivo;
	}
	
}
