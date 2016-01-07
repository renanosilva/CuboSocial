package br.ufrn.imd.cubo.geral.dominio;

import java.util.List;

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
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.ufrn.imd.cubo.arq.dominio.PersistDBRecorded;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.Formatador;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/** 
 * Entidade que representa um comentário criado por um usuário.
 * Um comentário está associado a uma publicação.
 * @author Renan
 */
@SuppressWarnings("serial")
@Entity
@Table(name="comentario", schema="geral")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Comentario extends PersistDBRecorded {
	
	/** Identificador da entidade. */
	@Id
	@SequenceGenerator(name="gen_id_comentario", sequenceName = "seq_id_comentario", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_comentario" )  
	@Column(name="id_comentario", nullable = false)
	private int id;
	
	/** Texto do comentário. */
	@Column(nullable=false)
	private String comentario;
	
	/** Publicação associada ao comentário */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_publicacao", nullable=false)
	private Codigo publicacao;
	
	/** Número total de curtidas do comentário. Serve para evitar consultas extras ao banco. */
	@Column(name="qtd_curtidas", nullable = false)
	private int qtdCurtidas;
	
	/** Curtidas que o comentário recebeu. */
	@OneToMany(mappedBy = "comentario", fetch = FetchType.LAZY)
	private List<Curtida> curtidas;
	
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
		Comentario other = (Comentario) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String getDataCriacaoFormatada(){
		return Formatador.getInstance().formatarDataHora(criadoEm);
	}
	
	public String getDataEdicaoFormatada(){
		return Formatador.getInstance().formatarDataHora(atualizadoEm);
	}
	
	/** Retorna a URL da foto do criador do código */
	public String getUrlFotoCriador(){
		return "/verArquivo?"
				+ "idFoto=" + criadoPor.getIdFoto()
				+"&key=" + CriptografiaUtils.criptografarMD5(String.valueOf(criadoPor.getIdFoto())) 
				+ "&salvar=false"; 
	}
	
	/** Retorna uma String com os nomes de quem curtiu o comentário, separados por vírgula. */
	public String getDescricaoQuemCurtiu() {
		StringBuffer descricao = new StringBuffer();
		
		if (ValidatorUtil.isEmpty(curtidas)) {
			descricao.append("Ninguém curtiu este comentário ainda.");
		} else {
			
			for (int i = 0; i < curtidas.size(); i++) {
				Curtida c = curtidas.get(i);
				boolean hasNext = i < curtidas.size() - 1; 
				boolean isPenultimo = i == curtidas.size() - 2;
				
				descricao.append(c.getCriadoPor().getPessoa().getNome());
				
				if (isPenultimo)
					descricao.append(" e ");
				if (hasNext && !isPenultimo)
					descricao.append(", ");
			}
			
			descricao.append(curtidas.size() > 1 ? " curtiram " : " curtiu ");
			descricao.append("este comentário.");
		}
		
		return descricao.toString();
	}
	
	/** Verifica se o usuário logado curtiu o comentário. */
	public boolean isUsuarioLogadoCurtiu() {
		Usuario usuarioLogado = UsuarioUtil.getUsuarioLogado();
		
		if (ValidatorUtil.isEmpty(usuarioLogado) || ValidatorUtil.isEmpty(curtidas))
			return false;
		
		Curtida curtida = new Curtida();
		curtida.setComentario(this);
		curtida.setCriadoPor(usuarioLogado);
		return curtidas.contains(curtida);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getComentario() {
		return comentario;
	}

	public void setComentario(String comentario) {
		this.comentario = comentario;
	}

	public int getQtdCurtidas() {
		return qtdCurtidas;
	}

	public void setQtdCurtidas(int qtdCurtidas) {
		this.qtdCurtidas = qtdCurtidas;
	}

	public List<Curtida> getCurtidas() {
		return curtidas;
	}

	public void setCurtidas(List<Curtida> curtidas) {
		this.curtidas = curtidas;
	}

	public Codigo getPublicacao() {
		return publicacao;
	}

	public void setPublicacao(Codigo publicacao) {
		this.publicacao = publicacao;
	}

}
