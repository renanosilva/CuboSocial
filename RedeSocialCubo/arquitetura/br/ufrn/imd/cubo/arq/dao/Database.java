package br.ufrn.imd.cubo.arq.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;

/**
 * Singleton que controla a comunicação com o banco de dados, 
 * inclusive o gerenciamento do EntityManager.
 * 
 * @author Renan
 */
public class Database {

	private static Database singleton = new Database();
	private static EntityManager em;

	private Database() {
		criarEM();
	}
	
	private void criarEM(){
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("ConexaoDB");
		em = emf.createEntityManager();
	}

	public static Database getInstance() {
		return singleton;
	}

	public EntityManager getEntityManager() {
		if (!em.isOpen()){
			criarEM();
		}
		
		return em;
	}
	
	public Session getSession(){
		return (Session) em.getDelegate();
	}

}

