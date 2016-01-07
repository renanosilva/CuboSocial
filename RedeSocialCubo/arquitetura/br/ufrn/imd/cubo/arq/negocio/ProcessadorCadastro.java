package br.ufrn.imd.cubo.arq.negocio;

import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.dominio.PersistDB;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;

/** 
 * Processador capaz de realizar cadastro/edição de quaisquer entidades do sistema.
 * @author Renan
 */
public class ProcessadorCadastro extends ProcessadorComando {
	
	/** 
	 * Objeto que se quer cadastrar/editar no banco. 
	 */
	protected PersistDB obj;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		IGenericDAO dao = new GenericDAOImpl();
		
		if (obj.getId() == 0){
			dao.create(obj);
		} else {
			dao.update(obj);
		}
	}

	@Override
	protected void validar() throws NegocioException {
		if (obj == null){
			throw new NegocioException("Erro ao recuperar o objeto a ser cadastrado!");
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
