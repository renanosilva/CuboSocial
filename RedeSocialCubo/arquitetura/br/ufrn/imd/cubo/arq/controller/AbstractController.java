package br.ufrn.imd.cubo.arq.controller;

import java.io.IOException;
import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.primefaces.model.UploadedFile;

import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.util.EnvioArquivoUtils;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/**
 * Classe que implementa métodos comuns a todos os controladores do sistema.
 * Portanto, deve ser estendido por eles.
 *  
 * @author Renan
 */
@SuppressWarnings("serial")
public class AbstractController implements Serializable {
	
	protected void addMsgInfo(String msg){
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, null);
		FacesContext.getCurrentInstance().addMessage(null, fm);
	}
	
	protected void addMsgWarning(String msg){
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_WARN, msg, null);
		FacesContext.getCurrentInstance().addMessage(null, fm);
	}
	
	protected void addMsgError(String msg){
		FacesMessage fm = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null);
		FacesContext.getCurrentInstance().addMessage(null, fm);
	}
	
	/**
	 * Tratamento padrão de exceções em geral do sistema.
	 */
	protected void tratamentoErroPadrao(Exception e){
		e.printStackTrace();
		addMsgError("Ocorreu um erro ao realizar a operação. Por favor, entre em contato com a "
				+ "administração do sistema, ou tente novamente mais tarde.");
	}
	
	/**
	 * Tratamento padrão das exceções do tipo {@link NegocioException}.
	 * Deve ser chamado sempre que ocorrer esse tipo de exceção.
	 */
	protected void tratamentoNegocioException(NegocioException e){
		if (ValidatorUtil.isEmpty(e.getMsgs())){
			addMsgError(e.getMessage());
		} else {
			for (String msg : e.getMsgs()){
				addMsgError(msg);
			}
		}
	}
	
	/**
	 * Possibilita o acesso ao HttpServletRequest.
	 */
	public HttpServletRequest getCurrentRequest() {
		return (HttpServletRequest) getExternalContext().getRequest();
	}

	/**
	 * Possibilita o acesso ao HttpServletResponse.
	 */
	public HttpServletResponse getCurrentResponse() {
		return (HttpServletResponse) getExternalContext().getResponse();
	}

	/**
	 * Possibilita o acesso ao HttpSession.
	 */
	public HttpSession getCurrentSession() {
		return getCurrentRequest().getSession(true);
	}
	
	/**
	 * Retorna um managed-bean existente no container do JavaServer Faces.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getMBean(String mbeanName) {
		FacesContext fc = FacesContext.getCurrentInstance();
		return (T) fc.getELContext().getELResolver().getValue(fc.getELContext(), null, mbeanName);
	}
	
	/**
	 * Acessa o external context do JavaServer Faces
	 **/
	private ExternalContext getExternalContext() {
		return FacesContext.getCurrentInstance().getExternalContext();
	}
	
	/** Retorna o usuário logado no sistema. */
	public Usuario getUsuarioLogado() {
		return UsuarioUtil.getUsuarioLogado();
	}
	
	public String getParameter(String param) {
		return getCurrentRequest().getParameter(param);
	}
	
	public Integer getParameterInt(String param) {
		return Integer.parseInt(getParameter(param));
	}
	
	public Integer getParameterInt(String param, int padrao) {
		String valor = getParameter(param);
		return valor != null ? Integer.parseInt(valor) : padrao;
	}
	
	/**
	 * Método utilizado para fazer download de arquivos.
	 */
	public String baixarArquivo(Integer idArquivo) throws ArqException {
		EnvioArquivoUtils arq = new EnvioArquivoUtils();
		
		try {    	
        	arq.conectar();
			arq.recuperaArquivo(getCurrentResponse(), idArquivo, true);
        } catch (Exception e) {
            e.printStackTrace();
            addMsgError("Erro ao recuperar o arquivo!");
        } finally {
        	arq.desconectar();
        }
		
        FacesContext.getCurrentInstance().responseComplete();
        
		return null;
	}
	
	/**
	 * Método utilizado para fazer download de arquivos.
	 * @throws IOException 
	 */
	public String baixarArquivo(UploadedFile arquivo) throws ArqException, IOException {
        
		byte[] bytes = arquivo.getContents();
		String nome = arquivo.getFileName();
		
		getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\"" + nome + "\"");
		getCurrentResponse().getOutputStream().write(bytes);
		
		FacesContext.getCurrentInstance().responseComplete();
        
		return null;
	}
}
