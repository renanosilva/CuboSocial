package br.ufrn.imd.cubo.arq.dao;

import java.util.List;

import javax.persistence.EntityManager;

import br.ufrn.imd.cubo.arq.dominio.PersistDB;

public interface IGenericDAO {

	public abstract EntityManager getEm();
	
	public abstract void clear();
	
	public abstract void detach(PersistDB p);
	
	public abstract void refresh(PersistDB p);

	public abstract void create(PersistDB c);

	public abstract void update(PersistDB c);
	
	public abstract void createOrUpdate(PersistDB c);

	public abstract void delete(PersistDB c);
	
	public abstract void flush();
	
	public abstract void update(String sql);
	
	public abstract <T extends PersistDB> T findByPrimaryKey(int id, Class<T> classe);

	public abstract <T extends PersistDB> List<T> findAll(Class<T> classe);
	
	public abstract <T extends PersistDB> List<T> findAllAtivos(Class<T> classe);
	
	public abstract <T extends PersistDB> List<T> findAllAtivos(Class<T> classe, String orderBy);

	public abstract <T extends PersistDB> List<T> findAllLike(String coluna, String valor, String orderby, Class<T> classe);
	
	public abstract <T extends PersistDB> List<T> findByExactField(String coluna, Object valor, Class<T> classe);
	
	public abstract <T extends PersistDB> List<T> findByExactField(String coluna, Object valor, String orderBy, Class<T> classe);
	
	public <T extends PersistDB> List<T> findByExactFields(String[] colunas, Object[] valores, Class<T> classe);
	
	public <T extends PersistDB> T findByExactFields(String[] colunas, Object[] valores, boolean limit, Class<T> classe);

	public <T extends PersistDB> void updateField(Class<T> classe, int id, String coluna, Object valor);

}