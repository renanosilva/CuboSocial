package br.ufrn.imd.cubo.geral.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.hibernate.SQLQuery;
import org.hibernate.Session;

import br.ufrn.imd.cubo.arq.dao.Database;
import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dominio.Pessoa;
import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.DAOException;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/**
 * DAO com métodos relativos à entidade {@link Usuario}.
 * @author Renan
 */
public class UsuarioDAO extends GenericDAOImpl {

	public Usuario findUsuarioByLoginSenha(String login, String senha){
		EntityManager em = getEm();
		
		String hql = "SELECT usuario ";
		hql += " FROM Usuario usuario WHERE usuario.email = :login and usuario.senha = :senha ";
		
		Query q = em.createQuery(hql);
		q.setParameter("login", login);
		q.setParameter("senha", senha);
		
		try {
			Usuario usuario = (Usuario) q.getSingleResult();
			return usuario;
		} catch (NoResultException e){
			return null;
		}
	}
	
	public void updateUltimoAcesso(int idUsuario) throws DAOException{
		try {  
			Session session = Database.getInstance().getSession();
			
			String sql = "update arq.usuario set ultimo_acesso = ? ";
			sql += "where id_usuario = ? ";
			
			SQLQuery query = session.createSQLQuery(sql);
			query.setTimestamp(0, new Date());
			query.setInteger(1, idUsuario);
			
			query.executeUpdate();
		
		} catch (Exception e) {
			throw new DAOException();
		}
	}
	
	/**
	 * Método que permite listar usuários através da busca por
	 * diversos atributos.
	 */
	public List<Usuario> findUsuarioGeral(String login, String nome, String cpf, 
			boolean incluirUsuariosInativos){
		EntityManager em = getEm();
		
		String hql = "SELECT u.id, u.pessoa.nome, u.pessoa.cpf, u.email, u.ativo ";
		hql += " FROM Usuario u WHERE 1=1 ";
		
		if (ValidatorUtil.isNotEmpty(login)){
			hql += " AND u.email = :login ";
		}
		if (ValidatorUtil.isNotEmpty(nome)){
			hql += " AND upper(u.pessoa.nome) like :nome ";
		}
		if (ValidatorUtil.isNotEmpty(cpf)){
			hql += " AND u.pessoa.cpf = :cpf ";
		}
		if (!incluirUsuariosInativos){
			hql += " AND u.ativo = :ativo ";
		}
		
		Query q = em.createQuery(hql);
		
		if (ValidatorUtil.isNotEmpty(login)){
			q.setParameter("login", login);
		}
		if (ValidatorUtil.isNotEmpty(nome)){
			q.setParameter("nome", "%" + nome.toUpperCase() + "%");
		}
		if (ValidatorUtil.isNotEmpty(cpf)){
			q.setParameter("cpf", cpf);
		}
		if (!incluirUsuariosInativos){
			q.setParameter("ativo", true);
		}
		
		try {
			@SuppressWarnings("unchecked")
			List<Object[]> usuarios = q.getResultList();
			List<Usuario> result = new ArrayList<Usuario>();
			
			if (usuarios != null){
				for (Object[] obj : usuarios){
					int i = 0;
					
					Usuario usuario = new Usuario();
					usuario.setPessoa(new Pessoa());
					
					usuario.setId((Integer) obj[i++]);
					usuario.getPessoa().setNome((String) obj[i++]);
					usuario.getPessoa().setCpf((String) obj[i++]);
					usuario.setEmail((String) obj[i++]);
					usuario.setAtivo((Boolean) obj[i++]);
					
					result.add(usuario);
				}
			}
			
			return result;
		} catch (NoResultException e){
			return null;
		}
	}
	
}
