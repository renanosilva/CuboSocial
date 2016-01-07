package br.ufrn.imd.cubo.geral.dominio;

import java.util.ArrayList;
import java.util.List;

/** 
 * Classe que representa uma Unidade Federativa (Estado) do Brasil.
 * Não é persistida.
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
		ufs.add(new UF("AP", "Amapá"));
		ufs.add(new UF("AM", "Amazonas"));
		ufs.add(new UF("BA", "Bahia"));
		ufs.add(new UF("CE", "Ceará"));
		ufs.add(new UF("DF", "Distrito Federal"));
		ufs.add(new UF("ES", "Espírito Santo"));
		ufs.add(new UF("GO", "Goiás"));
		ufs.add(new UF("MA", "Maranhão"));
		ufs.add(new UF("MT", "Mato Grosso"));
		ufs.add(new UF("MS", "Mato Grosso do Sul"));
		ufs.add(new UF("MG", "Minas Gerais"));
		ufs.add(new UF("PA", "Pará"));
		ufs.add(new UF("PB", "Paraíba"));
		ufs.add(new UF("PR", "Paraná"));
		ufs.add(new UF("PE", "Pernambuco"));
		ufs.add(new UF("PI", "Piauí"));
		ufs.add(new UF("RJ", "Rio de Janeiro"));
		ufs.add(new UF("RN", "Rio Grande do Norte"));
		ufs.add(new UF("RS", "Rio Grande do Sul"));
		ufs.add(new UF("RO", "Rondônia"));
		ufs.add(new UF("RR", "Roraima"));
		ufs.add(new UF("SC", "Santa Catarina"));
		ufs.add(new UF("SP", "São Paulo"));
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
