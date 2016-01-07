package br.ufrn.imd.cubo.arq.dominio;

import java.io.Serializable;

public interface PersistDB extends Serializable {

	public int getId();
	
	public void setId(int id);
}

