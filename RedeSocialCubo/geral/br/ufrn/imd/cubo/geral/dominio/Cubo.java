package br.ufrn.imd.cubo.geral.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.ufrn.imd.cubo.arq.dominio.PersistDB;

/** 
 * Entidade que representa um cubo de LED que está disponível
 * para uso pelo sistema.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@Entity
@Table(name="cubo", schema="geral")
public class Cubo implements PersistDB {
	
	/** Identificador da entidade. */
	@Id
	@SequenceGenerator(name="gen_id_cubo", sequenceName = "seq_id_cubo", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_cubo" )  
	@Column(name="id_cubo", nullable = false)
	private int id;
	
	/** Nome do cubo. */
	@Column(nullable = false)
	private String nome;
	
	/** URL de streaming do cubo. */
	@Column(nullable = false)
	private String url;
	
	/** Indica se o cubo está ativo (funcionando). */
	@Column(nullable=false)
	private Boolean ativo;

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
		Cubo other = (Cubo) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public Boolean is2x2x2(){
		if (nome == null)
			return null;
		
		return nome.contains("2x2x2");
	}
	
	public Boolean is3x3x3(){
		if (nome == null)
			return null;
		
		return nome.contains("3x3x3");
	}
	
	public Boolean is4x4x4(){
		if (nome == null)
			return null;
		
		return nome.contains("4x4x4");
	}
	
	public Boolean is5x5x5(){
		if (nome == null)
			return null;
		
		return nome.contains("5x5x5");
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
}
