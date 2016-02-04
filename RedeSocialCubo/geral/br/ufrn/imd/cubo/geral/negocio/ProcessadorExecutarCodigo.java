package br.ufrn.imd.cubo.geral.negocio;

import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.thread.ExecucaoCodigoThread;
import br.ufrn.imd.cubo.geral.util.ThreadExecucaoUtils;

/** 
 * Executa o código de uma publicação de um usuário.
 * 
 * @author Renan
 */
public class ProcessadorExecutarCodigo extends ProcessadorCadastro {
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Codigo obj = (Codigo) this.obj;
		
		try {
//			CodigoDAO dao = new CodigoDAO();
//			
//			dao.refresh(obj.getCubo());
			
			ExecucaoCodigoThread e = ThreadExecucaoUtils.getThreadExecucao(obj.getCubo()); 
			boolean adicionou = e.adicionarAFila(obj);
			
			if (!adicionou){
				throw new NegocioException("Este código já foi adicionado à fila! Por favor, aguarde até sua execução.");
			}
			
			if (!e.isAlive()){
				e.start();
			}
			
		} catch (NegocioException e){
			throw new NegocioException(e.getMessage());
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
	@Override
	protected void validar() throws NegocioException {
		super.validar();
		
		Codigo obj = (Codigo) this.obj;
		
		CodigoDAO dao = new CodigoDAO();
		dao.refresh(obj.getCubo());
		
		if (!obj.getCubo().getAtivo()){
			throw new NegocioException("Não foi possível executar este código, pois o cubo para o qual "
					+ "ele foi projetado está desativado no momento.");
		}
	}
	
}
