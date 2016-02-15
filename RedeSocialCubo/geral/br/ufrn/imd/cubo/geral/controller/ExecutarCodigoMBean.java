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
 * MBean que controla a execu��o de c�digos de usu�rios.
 * 
 * @author Renan
 */
@SuppressWarnings("serial")
@ManagedBean
@ViewScoped
public class ExecutarCodigoMBean extends AbstractControllerCadastro<Codigo> {
	
	/** Indica se deve exibir o status da fila de execu��o de c�digos. */
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
	 * Executa um c�digo sem recarregar a p�gina. 
	 * � necess�rio passar o ID do c�digo.
	 * */
	public void executar(ActionEvent evt){
		int idCodigo = getParameterInt("idCodigo", -1);
		executarCodigo(idCodigo);
	}
	
	public void executarCodigo(int idCodigo){
		if (idCodigo == 0){
			addMsgError("� necess�rio salvar o c�digo antes de execut�-lo.");
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
				
				//addMsgInfo("Seu c�digo entrou na fila de execu��o!");
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
			int qtdAFrente = 0; //quantidade de c�digos � frente na fila 
			boolean executado = !fila.contains(obj);
			
			String msg = "";
			
			if (executado){
				msg = "C�digo executado!";
			} else {
				Iterator<Codigo> it = fila.iterator();
				
				while (it.hasNext()){
					Codigo c = it.next();
					
					if (!c.equals(obj)){
						qtdAFrente++;
					}
				}
				
				if (qtdAFrente == 0){
					msg = "C�digo executado!";
				} else {
					msg = "Existe(m) " + (qtdAFrente) + " pedido(s) de execu��o na sua frente.<br/>";
					
					Integer tempo = (qtdAFrente) * (ExecucaoCodigoThread.TEMPO_MIN_EXECUCAO_CODIGO/1000);
					
					msg += "Ser� necess�rio aguardar at� " + tempo + " segundos para que este c�digo seja executado.<br/><br/>";
					
					msg += "Por favor, aguarde. Essas informa��es ser�o atualizadas automaticamente.";
				}
			}
			
			addMsgInfo(msg);
		}
	}
	
	public String getTextoConsole(){
//		String console = ConsoleArduino.getInstance().getTextoConsole();
//		int idxInicio = console.lastIndexOf("EXECUTANDO CODIGO: " + obj.getId());
//		int idxFim = console.lastIndexOf("TERMINADA EXECU��O DE: " + obj.getId());
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
