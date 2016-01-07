package br.ufrn.imd.cubo.geral.negocio;

import java.util.Arrays;
import java.util.List;

import br.ufrn.imd.cubo.arq.dominio.Usuario;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.arq.util.EnvioArquivoUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;

/** 
 * Classe responsável por realizar cadastro/alteração de usuários.
 * @author Renan
 */
public class ProcessadorCadastraUsuario extends ProcessadorCadastro {
	
	private final String[] FORMATOS_PERMITIDOS = {"png", "jpg", "jpeg"};
	
	/** Tamanho máximo para upload, em bytes. */
	private final Integer TAM_MAXIMO_ARQUIVO = 2097152;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Usuario obj = (Usuario) this.obj;
		
		try {
			
			//Se o usuário anexou alguma foto
			if (obj.getArquivo() != null && ValidatorUtil.isNotEmpty(obj.getArquivo().getFileName()) && obj.getArquivo().getSize() != -1){
				
				EnvioArquivoUtils arq = new EnvioArquivoUtils();
				
				try {
					arq.conectar();
					
					if (obj.getId() > 0 && obj.getIdFoto() != null && obj.getIdFoto() > 0){
						arq.removerArquivo(obj.getIdFoto());
					}
					
					obj.setIdFoto(arq.getNextIdArquivo());
					arq.inserirArquivo(obj.getIdFoto(), obj.getArquivo().getFileName(), obj.getArquivo().getContents());
				} finally {
					arq.desconectar();					
				}
			}
			
			super.iniciarExecucao();
			
		} catch (Exception e) {
			throw new ArqException(e);
		} 
	}
	
	@Override
	protected void validar() throws NegocioException {
		super.validar();
		
		Usuario obj = (Usuario) this.obj;
		
		if (obj.getArquivo() != null && ValidatorUtil.isNotEmpty(obj.getArquivo().getFileName())
				&& obj.getArquivo().getSize() != -1){
			//Verificando extensão
			
			String[] nomes = obj.getArquivo().getFileName().split("\\.");
			String extensao = nomes[nomes.length - 1].toLowerCase();
			
			List<String> formatos = Arrays.asList(FORMATOS_PERMITIDOS);
			
			if (!formatos.contains(extensao)){
				String msg = "O formato de arquivo que você anexou não é suportado. Por favor, ";
				msg += "envie em um dos seguintes formatos: png ou jpg.";
				
				throw new NegocioException(msg);
			}
			
			//Verificando tamanho arquivo
			if (obj.getArquivo().getSize() > TAM_MAXIMO_ARQUIVO) {
				throw new NegocioException("O tamanho máximo para upload de arquivo é de 2 MB.");
			}
		}
	}
	
}
