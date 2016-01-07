package br.ufrn.imd.cubo.arq.negocio;

import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.PersistDB;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;

/** 
 * Processador capaz de realizar inativação/ativação lógica de quaisquer entidades do sistema
 * que possuam um atributo chamado "ativo".
 * @author Renan
 */
public class ProcessadorInativar extends ProcessadorComando {
	
	public static final int INATIVAR = 1;
	public static final int REATIVAR = 2;
	
	/** Indica a operação a ser realizada. Deve ser INATIVAR ou ser REATIVAR. */
	private int operacao;
	
	/** 
	 * Objeto que se quer inativar no banco. 
	 */
	private PersistDB obj;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		IGenericDAO dao = new GenericDAOImpl();
		
		if (operacao == INATIVAR)
			dao.updateField(obj.getClass(), obj.getId(), "ativo", false);
		else if (operacao == REATIVAR)
			dao.updateField(obj.getClass(), obj.getId(), "ativo", true);
		else
			throw new ArqException("Operação inválida!");
	}

	@Override
	protected void validar() throws NegocioException {
		if (obj == null){
			throw new NegocioException("Erro ao recuperar o objeto a ser inativado!");
		}
	}

	@Override
	protected Object getResult() {
		return obj;
	}

	public void setObj(PersistDB obj) {
		this.obj = obj;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}
	
}
