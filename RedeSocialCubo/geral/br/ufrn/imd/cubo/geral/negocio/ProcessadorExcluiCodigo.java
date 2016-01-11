package br.ufrn.imd.cubo.geral.negocio;

import java.util.List;

import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorRemocao;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dao.CodigoDAO;
import br.ufrn.imd.cubo.geral.dominio.AvaliacaoCodigo;
import br.ufrn.imd.cubo.geral.dominio.Codigo;

/** 
 * Classe respons�vel por remover uma publica��o de c�digo.
 * 
 * @author Renan
 */
public class ProcessadorExcluiCodigo extends ProcessadorRemocao {
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Codigo obj = (Codigo) this.obj;
		CodigoDAO dao = null;
		
		try {
			dao = new CodigoDAO();
			
			//Primeiro � necess�rio remover as avalia��es da publica��o, para que n�o ocorra erro
			
			List<AvaliacaoCodigo> avaliacoes =  
					dao.findByExactField("publicacao.id", obj.getId(), AvaliacaoCodigo.class);
			
			if (ValidatorUtil.isNotEmpty(avaliacoes)){
				for (AvaliacaoCodigo ava : avaliacoes){
					dao.delete(ava);
				}
			}
			
			super.iniciarExecucao();
			
		} catch (Exception e) {
			throw new ArqException(e);
		}
	}
	
}
