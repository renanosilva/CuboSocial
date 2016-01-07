package br.ufrn.imd.cubo.geral.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
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
import javax.persistence.UniqueConstraint;

import br.ufrn.imd.cubo.arq.dominio.PersistDBRecorded;

/**
 * Esta entidade armazena um registro de "curtida" de uma publicação por um 
 * determinado usuário.
 * 
 * @author Renan.
 */
@SuppressWarnings("serial")
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name = "curtida", schema = "geral", 
	uniqueConstraints = { @UniqueConstraint(columnNames = { "id_publicacao", "id_criado_por" }),
						@UniqueConstraint(columnNames = { "id_comentario", "id_criado_por" })})
public class Curtida extends PersistDBRecorded {

	/** Chave primária. */
	@Id
	@SequenceGenerator(name="gen_id_codigo_curtida", sequenceName = "seq_id_codigo_curtida", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_codigo_curtida" )  
	@Column(name="id_codigo_curtida", nullable = false)
	private int id;
	
	/** Publicação que foi curtida. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_publicacao")
	private Codigo publicacao;
	
	/** Comentário que foi curtido. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_comentario")
	private Comentario comentario;

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
		Curtida other = (Curtida) obj;
		if (id == other.id)
			return true;
		if (publicacao != null){
			if (criadoPor.getId() == other.getCriadoPor().getId() &&
					publicacao.getId() == other.publicacao.getId())
				return true;
		}
		if (comentario != null){
			if (criadoPor.getId() == other.getCriadoPor().getId() &&
					comentario.getId() == other.comentario.getId())
				return true;
		}
		return false;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Codigo getPublicacao() {
		return publicacao;
	}

	public void setPublicacao(Codigo publicacao) {
		this.publicacao = publicacao;
	}

	public Comentario getComentario() {
		return comentario;
	}

	public void setComentario(Comentario comentario) {
		this.comentario = comentario;
	}
}
