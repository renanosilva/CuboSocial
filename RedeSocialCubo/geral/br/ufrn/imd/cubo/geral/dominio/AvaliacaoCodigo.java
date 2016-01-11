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
 * Armazena um registro de avaliação (atribuição de nota) de um usuário 
 * para uma determinada publicação de código.
 * 
 * @author Renan.
 */
@SuppressWarnings("serial")
@Entity
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
@Table(name = "avaliacao_codigo", schema = "geral", 
	uniqueConstraints = { @UniqueConstraint(columnNames = { "id_publicacao", "id_criado_por" })})
public class AvaliacaoCodigo extends PersistDBRecorded {

	/** Chave primária. */
	@Id
	@SequenceGenerator(name="gen_id_avaliacao_codigo", sequenceName = "seq_id_avaliacao_codigo", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_avaliacao_codigo" )  
	@Column(name="id_avaliacao_codigo", nullable = false)
	private int id;
	
	/** Publicação que foi avaliada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_publicacao", nullable=false)
	private Codigo publicacao;
	
	/** Nota atribuída pelo usuário à publicação. */
	@Column(nullable=false)
	private Integer nota;
	
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
		AvaliacaoCodigo other = (AvaliacaoCodigo) obj;
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

	public Codigo getPublicacao() {
		return publicacao;
	}

	public void setPublicacao(Codigo publicacao) {
		this.publicacao = publicacao;
	}

	public Integer getNota() {
		return nota;
	}

	public void setNota(Integer nota) {
		this.nota = nota;
	}
}
