package br.ufrn.imd.cubo.arq.controller;

import java.util.Collection;
import java.util.List;

import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.PersistDB;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorRemocao;

/**
 * Classe que implementa métodos comuns de CRUD. Deve ser estendido pelos MBeans que
 * quiserem ter acesso a essas funcionalidades.
 * 
 * @author Renan
 * @param <T>
 */
@SuppressWarnings("serial")
public class AbstractControllerCadastro<T extends PersistDB> extends AbstractController {
	
	/** Constante que armazena o índice padrão da aba de consulta. */
//	private final Integer ABA_CONSULTA = 0;
	
	/** Constante que armazena o índice padrão da aba de visualização. */
//	private final Integer ABA_VISUALIZACAO = 1;
	
	/** Constante que armazena o índice padrão da aba de cadastro. */
//	private final Integer ABA_CADASTRO = 2;

	/** DAO genérico. */
	protected IGenericDAO dao;
	
	/** Utilizado para criação/edição de entidades. */
	protected T obj;
	
	/** Lista de objetos utilizado em buscas. */
	protected Collection<T> resultadosBusca;
	
//	/** Refere-se às abas utilizadas para cadastro/consulta, etc. */
//	protected TabView tabCrud = new TabView();
	
	/** Índice da aba que se encontra ativa atualmente. */
//	protected int abaAtiva = 0;
	
	/** Em casos de uso do tipo CRUD, o sistema geralmente apresenta três abas. Uma delas
	 * é a de consulta. Esta variável armazena essa aba. Armazenar essas variáveis é necessário
	 * para tratar corretamente a mudança de abas. */
//	private Tab abaConsulta;
	
	/** Em casos de uso do tipo CRUD, o sistema geralmente apresenta três abas. Uma delas
	 * é a de cadastro. Esta variável armazena essa aba. Armazenar essas variáveis é necessário
	 * para tratar corretamente a mudança de abas. */
//	private Tab abaCadastro;
	
	/** Em casos de uso do tipo CRUD, o sistema geralmente apresenta três abas. Uma delas
	 * é a de visualização. Esta variável armazena essa aba. Armazenar essas variáveis é necessário
	 * para tratar corretamente a mudança de abas. */
//	private Tab abaVisualizacao;
	
	/**
	 * Retorna a listagem de todos os registros do banco
	 * referente a uma determinada entidade.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<T> getListaCompleta(){
		return (List<T>) dao.findAll(obj.getClass());
	}
	
	/** 
	 * Verifica as permissões que o usuário deve ter para realizar cadastro, edição e remoção
	 * de dados. Por padrão, deve ser administrador. Caso se deseje um comportamento diferente,
	 * o método deve ser sobrescrito.  
	 * */
	protected void checkRole() throws NegocioException {
		if (!getUsuarioLogado().isAdministrador())
			throw new NegocioException("Este usuário não tem permissão para realizar a operação selecionada.");
	}

	/**
	 * Método que salva o objeto
	 * @return
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	@SuppressWarnings("unchecked")
	public String salvar() throws InstantiationException, IllegalAccessException{
		try {
			checkRole();
			
			ProcessadorCadastro p = new ProcessadorCadastro();
			p.setObj(obj);
			p.execute();
			obj = (T) obj.getClass().newInstance();
			
			addMsgInfo("Operação realizada com sucesso!");
			
			return posCadastro();
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		}
	}
	
	/**
	 * Método chamado após a conclusão do cadastro de uma entidade.
	 * O comportamento padrão é carregar todos os registros da entidade
	 * em questão. Deve ser sobrescrito pelos MBeans que não quiserem tal
	 * comportamento. 
	 */
	protected String posCadastro(){
//		getListaCompleta();
//		abaAtiva = getIndiceAbaConsulta();
		return null;
	}
	
	public String cancelar() throws InstantiationException, IllegalAccessException {
		PersistDB obj = (PersistDB) this.obj;
		obj = obj.getClass().newInstance();
//		return Paginas.PORTAL_SERVIDOR_INICIO;
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String iniciarEdicao(PersistDB obj){
		this.obj = (T) dao.findByPrimaryKey(obj.getId(), obj.getClass());
//		abaAtiva = getIndiceAbaCadastro();
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public String visualizar(PersistDB obj){
		this.obj = (T) dao.findByPrimaryKey(obj.getId(), obj.getClass());
//		abaAtiva = getIndiceAbaVisualizacao();
		return null;
	}
	
	/** Remove um objeto da base de dados. */
	@SuppressWarnings("unchecked")
	public String remover(PersistDB obj) throws InstantiationException, IllegalAccessException {
		try {
			checkRole();
			
			ProcessadorRemocao p = new ProcessadorRemocao();
			p.setObj(obj);
			p.execute();
			
			obj = (T) obj.getClass().newInstance();
			
			addMsgInfo("Operação realizada com sucesso!");
			
			return posCadastro();
			
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		} catch (NegocioException e) {
			tratamentoNegocioException(e);
			return null;
		} 
	}
	
	/**
	 * Gerencia a mudança de abas do sistema, para que, ao recarregar a página,
	 * o sistema continue na mesma aba de antes.
	 * @param evt
	 */
//	public void tratarMudancaAba(TabChangeEvent evt){
//		if (evt.getTab().equals(abaCadastro))
//			abaAtiva = getIndiceAbaCadastro();
//		else if (evt.getTab().equals(abaConsulta))
//			abaAtiva = getIndiceAbaConsulta();
//		else if (evt.getTab().equals(abaVisualizacao))
//			abaAtiva = getIndiceAbaVisualizacao();
//	}
	
	/**
	 * Retorna a palavra "cadastrar" ou "editar", dependendo do tipo
	 * de operação sendo realizada.
	 */
	public String getNomeOperacaoSalvar(){
		PersistDB obj = (PersistDB) this.obj;
		return obj.getId() == 0 ? "Cadastrar" : "Editar";
	}
	
	/** 
	 * Retorna o índice padrão da aba de consulta. Caso algum MBean precise de um valor
	 * diferente, deve sobrescrever o método.
	 */
//	public Integer getIndiceAbaConsulta(){
//		return ABA_CONSULTA;
//	}
	
	/** 
	 * Retorna o índice padrão da aba de cadastro. Caso algum MBean precise de um valor
	 * diferente, deve sobrescrever o método.
	 */
//	public Integer getIndiceAbaCadastro(){
//		return ABA_CADASTRO;
//	}
	
	/** 
	 * Retorna o índice padrão da aba de visualização. Caso algum MBean precise de um valor
	 * diferente, deve sobrescrever o método.
	 */
//	public Integer getIndiceAbaVisualizacao(){
//		return ABA_VISUALIZACAO;
//	}

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}

	public Collection<T> getResultadosBusca() {
		return resultadosBusca;
	}

	public void setResultadosBusca(Collection<T> resultadosBusca) {
		this.resultadosBusca = resultadosBusca;
	}

//	public int getAbaAtiva() {
//		return abaAtiva;
//	}
//
//	public void setAbaAtiva(int abaAtiva) {
//		this.abaAtiva = abaAtiva;
//	}
//
//	public Tab getAbaConsulta() {
//		return abaConsulta;
//	}
//
//	public void setAbaConsulta(Tab abaConsulta) {
//		this.abaConsulta = abaConsulta;
//	}
//
//	public Tab getAbaCadastro() {
//		return abaCadastro;
//	}
//
//	public void setAbaCadastro(Tab abaCadastro) {
//		this.abaCadastro = abaCadastro;
//	}
//
//	public Tab getAbaVisualizacao() {
//		return abaVisualizacao;
//	}
//
//	public void setAbaVisualizacao(Tab abaVisualizacao) {
//		this.abaVisualizacao = abaVisualizacao;
//	}
	
}
