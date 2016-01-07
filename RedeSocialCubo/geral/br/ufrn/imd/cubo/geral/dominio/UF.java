package br.ufrn.imd.cubo.geral.dominio;

import java.util.ArrayList;
import java.util.List;

/** 
 * Classe que representa uma Unidade Federativa (Estado) do Brasil.
 * N�o � persistida.
 * 
 * @author Renan
 */
public class UF {
	
	private String sigla;
	
	private String nome;
	
	public UF(String sigla, String nome) {
		super();
		this.sigla = sigla;
		this.nome = nome;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((sigla == null) ? 0 : sigla.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UF other = (UF) obj;
		if (sigla == null) {
			if (other.sigla != null)
				return false;
		} else if (!sigla.equals(other.sigla))
			return false;
		return true;
	}

	public static final List<UF> getAllUFsBrasileiras(){
		List<UF> ufs = new ArrayList<UF>();
		ufs.add(new UF("AC", "Acre"));
		ufs.add(new UF("AL", "Alagoas"));
		ufs.add(new UF("AP", "Amap�"));
		ufs.add(new UF("AM", "Amazonas"));
		ufs.add(new UF("BA", "Bahia"));
		ufs.add(new UF("CE", "Cear�"));
		ufs.add(new UF("DF", "Distrito Federal"));
		ufs.add(new UF("ES", "Esp�rito Santo"));
		ufs.add(new UF("GO", "Goi�s"));
		ufs.add(new UF("MA", "Maranh�o"));
		ufs.add(new UF("MT", "Mato Grosso"));
		ufs.add(new UF("MS", "Mato Grosso do Sul"));
		ufs.add(new UF("MG", "Minas Gerais"));
		ufs.add(new UF("PA", "Par�"));
		ufs.add(new UF("PB", "Para�ba"));
		ufs.add(new UF("PR", "Paran�"));
		ufs.add(new UF("PE", "Pernambuco"));
		ufs.add(new UF("PI", "Piau�"));
		ufs.add(new UF("RJ", "Rio de Janeiro"));
		ufs.add(new UF("RN", "Rio Grande do Norte"));
		ufs.add(new UF("RS", "Rio Grande do Sul"));
		ufs.add(new UF("RO", "Rond�nia"));
		ufs.add(new UF("RR", "Roraima"));
		ufs.add(new UF("SC", "Santa Catarina"));
		ufs.add(new UF("SP", "S�o Paulo"));
		ufs.add(new UF("SE", "Sergipe"));
		ufs.add(new UF("TO", "Tocantins"));
		
		return ufs;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
