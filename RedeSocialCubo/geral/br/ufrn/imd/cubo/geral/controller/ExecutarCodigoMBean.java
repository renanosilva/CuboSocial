package br.ufrn.imd.cubo.geral.controller;

import java.util.Date;
import java.util.Iterator;
import java.util.Queue;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import br.ufrn.imd.cubo.arq.controller.AbstractControllerCadastro;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.ConsoleArduino;
import br.ufrn.imd.cubo.geral.dominio.ExecucaoCodigo;
import br.ufrn.imd.cubo.geral.negocio.ProcessadorExecutarCodigo;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCodigoThread;
import br.ufrn.imd.cubo.geral.util.ThreadExecucaoUtils;

/** 
 * MBean que controla a execução de códigos de usuários.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class ExecutarCodigoMBean extends AbstractControllerCadastro<Codigo> {
	
	/** Indica se deve exibir o status da fila de execução de códigos. */
	private boolean exibirStatusExecucao;
	
	private String textoConsole;
	
	@PostConstruct
	private void init() {
		obj = new Codigo();
		dao = new GenericDAOImpl();
		exibirStatusExecucao = false;
		textoConsole = "";
	}
	
	/** 
	 * Executa um código sem recarregar a página. 
	 * É necessário passar o ID do código.
	 * */
	public void executar(ActionEvent evt){
		int idCodigo = getParameterInt("idCodigo", -1);
		executarCodigo(idCodigo);
	}
	
	public void executarCodigo(int idCodigo){
		if (idCodigo == 0){
			addMsgError("É necessário salvar o código antes de executá-lo.");
			return;
		}
		
		if (idCodigo != -1){
			obj = dao.findByPrimaryKey(idCodigo, Codigo.class);
			
			ExecucaoCodigo exec = new ExecucaoCodigo();
			exec.setCodigo(obj);
			exec.setDataHora(new Date());
			exec.setUsuarioExecucao(getUsuarioLogado());
			
			obj.setExecucao(exec);
			
			try {
				ProcessadorExecutarCodigo p = new ProcessadorExecutarCodigo();
				p.setObj(obj);
				p.execute();
				
				exibirStatusExecucao = true;
				
				//addMsgInfo("Seu código entrou na fila de execução!");
				atualizarTextoFilaEspera();
				
			} catch (ArqException e) {
				tratamentoErroPadrao(e);
			} catch (NegocioException e) {
				tratamentoNegocioException(e);
			}
			
		}
	}
	
	public void atualizarTextoFilaEspera(){
		if (exibirStatusExecucao){
			ExecucaoCodigoThread e = ThreadExecucaoUtils.getThreadExecucao(obj.getCubo());
			Queue<Codigo> fila = e.getFila();
			int qtdAFrente = 0; //quantidade de códigos à frente na fila 
			boolean executado = !fila.contains(obj);
			
			String msg = "";
			
			if (executado){
				msg = "Código executado!";
			} else {
				Iterator<Codigo> it = fila.iterator();
				
				while (it.hasNext()){
					Codigo c = it.next();
					
					if (!c.equals(obj)){
						qtdAFrente++;
					}
				}
				
				if (qtdAFrente == 0){
					msg = "Código executado!";
				} else {
					msg = "Existe(m) " + (qtdAFrente) + " pedido(s) de execução na sua frente.<br/>";
					
					Integer tempo = (qtdAFrente) * (ExecucaoCodigoThread.TEMPO_MIN_EXECUCAO_CODIGO/1000);
					
					msg += "Será necessário aguardar até " + tempo + " segundos para que este código seja executado.<br/><br/>";
					
					msg += "Por favor, aguarde. Essas informações serão atualizadas automaticamente.";
				}
			}
			
			addMsgInfo(msg);
		}
	}
	
	public String getTextoConsole(){
//		String console = ConsoleArduino.getInstance().getTextoConsole();
//		int idxInicio = console.lastIndexOf("EXECUTANDO CODIGO: " + obj.getId());
//		int idxFim = console.lastIndexOf("TERMINADA EXECUÇÃO DE: " + obj.getId());
//		
//		if (idxInicio == -1){
//			return "";
//		} else {
//			String texto = console.substring(idxInicio, idxFim);
//			return texto;
//		}
		
		ExecucaoCodigo exec = ConsoleArduino.getInstance().getExecucao(obj.getExecucao());
		
		if (exec != null){
			textoConsole = exec.getResultado();
			ConsoleArduino.getInstance().removerExecucao(exec);
		}
		
		return textoConsole;
	}
	
}
