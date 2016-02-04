package br.ufrn.imd.cubo.arq.dao;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Date;

import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;

import br.ufrn.imd.cubo.arq.dominio.PersistDB;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.util.UsuarioUtil;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/**
 * Listener criado para registro de logs referentes a alterações em registros do
 * banco de dados do sistema.
 * 
 * @author Renan
 */
public class LogDatabase extends EmptyInterceptor {

	private static final long serialVersionUID = 1L;

	// private Log log;

	@Override
	public boolean onSave(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		System.out.println("Salvando...");
		Log log = null;
		if (entity instanceof PersistDB) {
			Usuario usuarioLogado = UsuarioUtil.getUsuarioLogado();

			log = new Log();
			log.setData(new Date());
			log.setIdEntidade(((PersistDB) entity).getId());
			log.setEntidade(entity.getClass().getName());
			log.setOperacao("inserção");

			if (ValidatorUtil.isNotEmpty(usuarioLogado))
				log.setIdUsuario(usuarioLogado.getId());

			StringBuilder dados = new StringBuilder();
			for (int i = 0; i < state.length && i < propertyNames.length; i++) {
				dados.append(propertyNames[i].toString() + "=" + state[i] + ";");
			}
			log.setDados(dados.toString());
			
			try {
				LogUtil.getInstance().inserirLog(log);
			} catch (SQLException e) {
				// TODO tratar isso.
				e.printStackTrace();
			}

		}

		return super.onSave(entity, id, state, propertyNames, types);
	}

	@Override
	public void onDelete(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		System.out.println("Apagando...");
		Log log = null;

		if (!(entity instanceof Log)) {
			Usuario usuarioLogado = UsuarioUtil.getUsuarioLogado();

			log = new Log();
			log.setData(new Date());
			log.setIdEntidade(((PersistDB) entity).getId());
			log.setEntidade(entity.getClass().getName());
			log.setOperacao("remoção");

			if (ValidatorUtil.isNotEmpty(usuarioLogado))
				log.setIdUsuario(usuarioLogado.getId());

			StringBuilder dados = new StringBuilder();
			for (int i = 0; i < state.length && i < propertyNames.length; i++) {
				dados.append(propertyNames[i].toString() + "=" + state[i] + ";");
			}
			log.setDados(dados.toString());
			
			try {
				
				LogUtil.getInstance().inserirLog(log);
			} catch (SQLException e) {
				// TODO tratar isso.
				e.printStackTrace();
			}

		}

		super.onSave(entity, id, state, propertyNames, types);
	}

	@Override
	public boolean onLoad(Object entity, Serializable id, Object[] state,
			String[] propertyNames, Type[] types) {
		System.out.println("Carregando...");
		Log log = null;

		if (!(entity instanceof Log)) {
			Usuario usuarioLogado = UsuarioUtil.getUsuarioLogado();

			log = new Log();
			log.setData(new Date());
			log.setIdEntidade(((PersistDB) entity).getId());
			log.setEntidade(entity.getClass().getName());
			log.setOperacao("Consulta");

			if (ValidatorUtil.isNotEmpty(usuarioLogado))
				log.setIdUsuario(usuarioLogado.getId());

			StringBuilder dados = new StringBuilder();
			for (int i = 0; i < state.length && i < propertyNames.length; i++) {
				dados.append(propertyNames[i].toString() + "=" + state[i] + ";");
			}
			log.setDados(dados.toString());

			try {
				LogUtil.getInstance().inserirLog(log);
			} catch (SQLException e) {
				// TODO tratar isso.
				e.printStackTrace();
			}

		}
		return super.onLoad(entity, id, state, propertyNames, types);
	}

//	@Override
//	public void postFlush(Iterator entities) {
//		try {
//			LogUtil.getInstance().inserirLog(log);
//		} catch (SQLException e) {
//			// TODO tratar isso.
//			e.printStackTrace();
//		}
//
//		super.postFlush(entities);
//	}
	
}
