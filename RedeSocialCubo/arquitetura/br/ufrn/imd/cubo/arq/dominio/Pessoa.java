package br.ufrn.imd.cubo.arq.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/** 
 * Entidade que armazena os dados de uma pessoa no sistema.
 * @author Renan
 */
@SuppressWarnings("serial")
@Entity
@Table(name="pessoa", schema="arq")
public class Pessoa implements PersistDB {
	
	@Id
	@SequenceGenerator(name="gen_id_pessoa", sequenceName = "seq_id_pessoa", allocationSize = 1 )  
    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "gen_id_pessoa" )  
	@Column(name="id_pessoa", nullable = false)
	private int id;
	
	@Column(nullable = false, unique = true, length=14)
	private String cpf;
	
	@Column(nullable = false)
	private String nome;
	
	@Column(nullable = false)
	private String sobrenome;
	
	@Column(nullable = false)
	private String cidade;
	
	@Column(nullable = false, length=2)
	private String uf;
	
	@Column(nullable = false, name="data_nascimento")
	@Temporal(TemporalType.DATE)
	private Date dataNascimento;
	
	@Column(nullable = false, length = 1)
	private char sexo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getDescricaoSexo(){
		if (ValidatorUtil.isEmpty(sexo))
			return "-";
		else if (sexo == 'M'){
			return "Masculino";
		} else {
			return "Feminino";
		}
	}
	
	public boolean isSexoMasculino(){
		if (ValidatorUtil.isNotEmpty(sexo) && sexo == 'M'){
			return true;
		} 
		return false;
	}
	
	public boolean isSexoFeminino(){
		if (ValidatorUtil.isNotEmpty(sexo) && sexo == 'F'){
			return true;
		} 
		return false;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNome() {
		return nome;
	}
	
	public String getNomeSobrenome() {
		return nome + " " + sobrenome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUf() {
		return uf;
	}

	public void setUf(String uf) {
		this.uf = uf;
	}

	public Date getDataNascimento() {
		return dataNascimento;
	}

	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}

	public char getSexo() {
		return sexo;
	}

	public void setSexo(char sexo) {
		this.sexo = sexo;
	}

	public String getSobrenome() {
		return sobrenome;
	}

	public void setSobrenome(String sobrenome) {
		this.sobrenome = sobrenome;
	}
	
}
