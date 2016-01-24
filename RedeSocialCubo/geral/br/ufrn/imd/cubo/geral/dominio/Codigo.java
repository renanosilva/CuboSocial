package br.ufrn.imd.cubo.geral.dominio;

import java.util.List;

import javax.persistence.CascadeType;
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
import javax.persistence.Transient;

import org.primefaces.model.UploadedFile;

import br.ufrn.imd.cubo.arq.dominio.PersistDBRecorded;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.CuboSocialUtils;
import br.ufrn.imd.cubo.arq.util.Formatador;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/** 
 * Entidade que representa um c�digo criado por um usu�rio.
 * @author Renan
 */
@SuppressWarnings("serial")
@Entity
@Table(name="codigo", schema="geral")
@Inheritance(strategy=InheritanceType.TABLE_PER_CLASS)
public class Codigo extends PersistDBRecorded {
	
	/** Identificador da entidade. */
	@Id
	@SequenceGenerator(name="gen_id_codigo", sequenceName = "seq_id_codigo", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_codigo" )  
	@Column(name="id_codigo", nullable = false)
	private int id;
	
	/** T�tulo do post do usu�rio com o c�digo. */
	@Column(nullable = false)
	private String titulo;
	
	/** Descri��o do c�digo. */
	private String descricao;
	
	/** O c�digo propriamente dito. */
	@Column(nullable = false)
	private String codigo;
	
	/** Indica se o c�digo j� est� finalizado ou se � s� rascunho. */
	@Column(nullable = false)
	private boolean finalizado;
	
	/** O usu�rio pode anexar um arquivo de imagem ao c�digo */
	@Column(name="id_foto")
	private Integer idFoto;
	
	/** O usu�rio pode anexar um arquivo de v�deo ao c�digo*/
	@Column(name="id_video")
	private Integer idVideo;
	
	/** N�mero total de curtidas da publica��o. Serve para evitar consultas extras ao banco. */
	@Column(name="qtd_curtidas", nullable = false)
	private int qtdCurtidas;
	
	/** N�mero total de coment�rios da publica��o. Serve para evitar consultas extras ao banco. */
	@Column(name="qtd_comentarios", nullable = false)
	private int qtdComentarios;
	
	/** Refere-se ao cubo para o qual o c�digo foi projetado. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_cubo", nullable=false)
	private Cubo cubo;
	
	/** Curtidas que a publica��o recebeu. */
	@OneToMany(mappedBy = "publicacao", fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	private List<Curtida> curtidas;
	
	/** Coment�rios associados � publica��o. */
	@OneToMany(mappedBy = "publicacao", fetch = FetchType.LAZY, cascade=CascadeType.REMOVE)
	private List<Comentario> comentarios;
	
	/** Avalia��o dos usu�rios quanto ao c�digo. Varia de 0 a 5. */
	@Column(name="nota", nullable = false, scale=2)
	private float nota;
	
	/** Armazena a nota atribu�da pelo usu�rio logado ao c�digo em quest�o. Vari�vel auxiliar. */
	@Transient
	private Integer notaUsuarioLogado;
	
	/** 
	 * Atributo n�o persisitido que armazena uma foto que o usu�rio deseja
	 * para seu post.
	 * */
	@Transient
	private UploadedFile arquivoFoto;
	
	/** 
	 * Atributo n�o persisitido que armazena um v�deo que o usu�rio deseja
	 * para seu post.
	 * */
	@Transient
	private UploadedFile arquivoVideo;
	
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
		Codigo other = (Codigo) obj;
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
	
	/** Retorna a URL da foto do criador do c�digo */
	public String getUrlFotoCriador(){
		return "/verArquivo?"
				+ "idFoto=" + criadoPor.getIdFoto()
				+"&key=" + CriptografiaUtils.criptografarMD5(String.valueOf(criadoPor.getIdFoto())) 
				+ "&salvar=false"; 
	}
	
	/** Retorna a URL da imagem anexada ao post */
	public String getUrlImagemPost(){
		if (idFoto == null)
			return "";
		
		return "/verArquivo?"
				+ "idFoto=" + idFoto
				+"&key=" + CriptografiaUtils.criptografarMD5(String.valueOf(idFoto)) 
				+ "&salvar=false"; 
	}
	
	/** Retorna a URL do v�deo anexado ao post */
	public String getUrlVideoPost(){
		if (idVideo == null)
			return "";
		
		return CuboSocialUtils.getContextBarras() + "verArquivo?"
				+ "idArquivo=" + idVideo
				+"&key=" + CriptografiaUtils.criptografarMD5(String.valueOf(idVideo)) 
				+ "&salvar=false"; 
	}
	
	/** Retorna uma String com os nomes de quem curtiu a publica��o, separados por v�rgula. */
	public String getDescricaoQuemCurtiu() {
		StringBuffer descricao = new StringBuffer();
		
		if (ValidatorUtil.isEmpty(curtidas)) {
			descricao.append("Ningu�m curtiu esta publica��o ainda.");
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
			descricao.append("esta publica��o.");
		}
		
		return descricao.toString();
	}
	
	/** Verifica se o usu�rio logado curtiu a publica��o. */
	public boolean isUsuarioLogadoCurtiu() {
		Usuario usuarioLogado = UsuarioUtil.getUsuarioLogado();
		
		if (ValidatorUtil.isEmpty(usuarioLogado) || ValidatorUtil.isEmpty(curtidas))
			return false;
		
		Curtida curtida = new Curtida();
		curtida.setPublicacao(this);
		curtida.setCriadoPor(usuarioLogado);
		return curtidas.contains(curtida);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public boolean isFinalizado() {
		return finalizado;
	}

	public void setFinalizado(boolean finalizado) {
		this.finalizado = finalizado;
	}

	public Integer getIdFoto() {
		return idFoto;
	}

	public void setIdFoto(Integer idFoto) {
		this.idFoto = idFoto;
	}

	public Integer getIdVideo() {
		return idVideo;
	}

	public void setIdVideo(Integer idVideo) {
		this.idVideo = idVideo;
	}

	public int getQtdCurtidas() {
		return qtdCurtidas;
	}

	public void setQtdCurtidas(int qtdCurtidas) {
		this.qtdCurtidas = qtdCurtidas;
	}

	public float getNota() {
		return nota;
	}
	
	public Integer getNotaArredondada(){
		return Math.round(nota);
	}

	public void setNota(float nota) {
		this.nota = nota;
	}

	public UploadedFile getArquivoFoto() {
		return arquivoFoto;
	}

	public void setArquivoFoto(UploadedFile arquivoFoto) {
		this.arquivoFoto = arquivoFoto;
	}

	public UploadedFile getArquivoVideo() {
		return arquivoVideo;
	}

	public void setArquivoVideo(UploadedFile arquivoVideo) {
		this.arquivoVideo = arquivoVideo;
	}

	public List<Curtida> getCurtidas() {
		return curtidas;
	}

	public void setCurtidas(List<Curtida> curtidas) {
		this.curtidas = curtidas;
	}

	public List<Comentario> getComentarios() {
		return comentarios;
	}

	public void setComentarios(List<Comentario> comentarios) {
		this.comentarios = comentarios;
	}

	public int getQtdComentarios() {
		return qtdComentarios;
	}

	public void setQtdComentarios(int qtdComentarios) {
		this.qtdComentarios = qtdComentarios;
	}

	public Integer getNotaUsuarioLogado() {
		return notaUsuarioLogado;
	}

	public void setNotaUsuarioLogado(Integer notaUsuarioLogado) {
		this.notaUsuarioLogado = notaUsuarioLogado;
	}

	public Cubo getCubo() {
		return cubo;
	}

	public void setCubo(Cubo cubo) {
		this.cubo = cubo;
	}
	
}
