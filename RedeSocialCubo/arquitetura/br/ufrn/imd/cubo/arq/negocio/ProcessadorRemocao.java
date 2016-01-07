package br.ufrn.imd.cubo.arq.negocio;

import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.PersistDB;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;

/** 
 * Processador capaz de realizar remoção de quaisquer entidades do sistema da base de dados.
 * @author Renan
 */
public class ProcessadorRemocao extends ProcessadorComando {
	
	/** 
	 * Objeto que se quer remover do banco. 
	 */
	protected PersistDB obj;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		IGenericDAO dao = new GenericDAOImpl();
		obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
		dao.delete(obj);
	}

	@Override
	protected void validar() throws NegocioException {
		if (obj == null){
			throw new NegocioException("Erro ao recuperar o objeto a ser removido!");
		}
	}

	@Override
	protected Object getResult() {
		return obj;
	}

	public void setObj(PersistDB obj) {
		this.obj = obj;
	}

}
