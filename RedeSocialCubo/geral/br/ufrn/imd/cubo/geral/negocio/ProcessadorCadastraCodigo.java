package br.ufrn.imd.cubo.geral.negocio;

import java.util.Arrays;
import java.util.List;

import br.ufrn.imd.cubo.arq.dao.GenericDAOImpl;
import br.ufrn.imd.cubo.arq.dao.IGenericDAO;
import br.ufrn.imd.cubo.arq.exception.ArqException;
import br.ufrn.imd.cubo.arq.exception.NegocioException;
import br.ufrn.imd.cubo.arq.negocio.ProcessadorCadastro;
import br.ufrn.imd.cubo.arq.util.EnvioArquivoUtils;
import br.ufrn.imd.cubo.arq.util.ValidatorUtil;
import br.ufrn.imd.cubo.geral.dominio.Codigo;
import br.ufrn.imd.cubo.geral.dominio.Cubo;

/** 
 * Classe responsável por realizar cadastro/alteração de usuários.
 * @author Renan
 */
public class ProcessadorCadastraCodigo extends ProcessadorCadastro {
	
	/** Formatos permitidos de imagem. */
	private final String[] FORMATOS_IMAGEM = {"png", "jpg", "jpeg"};
	
	/** Formatos permitidos de vídeo. */
	private final String[] FORMATOS_VIDEO = {"mp4", "avi"};
	
	/** Tamanho máximo para upload, em bytes. */
	private final Integer TAM_MAXIMO_ARQUIVO = 15728640;
	
	@Override
	protected void iniciarExecucao() throws NegocioException, ArqException {
		Codigo obj = (Codigo) this.obj;
		
		EnvioArquivoUtils arq = null;
		
		try {
			arq = new EnvioArquivoUtils();
			arq.conectar();
			
			//Se o usuário anexou alguma foto
			if (obj.getArquivoFoto() != null && ValidatorUtil.isNotEmpty(obj.getArquivoFoto().getFileName()) 
					&& obj.getArquivoFoto().getSize() != -1){
				
				if (obj.getId() > 0 && obj.getIdFoto() != null && obj.getIdFoto() > 0){
					arq.removerArquivo(obj.getIdFoto());
				}
				
				obj.setIdFoto(arq.getNextIdArquivo());
				arq.inserirArquivo(obj.getIdFoto(), obj.getArquivoFoto().getFileName(), 
						obj.getArquivoFoto().getContents());
			}
			
			//Se o usuário anexou algum vídeo
			if (obj.getArquivoVideo() != null && ValidatorUtil.isNotEmpty(obj.getArquivoVideo().getFileName()) 
					&& obj.getArquivoVideo().getSize() != -1){
				
				if (obj.getId() > 0 && obj.getIdVideo() != null && obj.getIdVideo() > 0){
					arq.removerArquivo(obj.getIdVideo());
				}
				
				obj.setIdVideo(arq.getNextIdArquivo());
				arq.inserirArquivo(obj.getIdVideo(), obj.getArquivoVideo().getFileName(), 
						obj.getArquivoVideo().getContents());
			}
			
			//Evitando erros de lazy e atualizando cubo
			IGenericDAO dao = new GenericDAOImpl();
			
			int idCubo = obj.getCubo().getId();
			dao.detach(obj.getCubo());
			obj.setCubo(dao.findByPrimaryKey(idCubo, Cubo.class));
			
			super.iniciarExecucao();
			
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			arq.desconectar();
		}
	}
	
	@Override
	protected void validar() throws NegocioException {
		super.validar();
		
		Codigo obj = (Codigo) this.obj;
		
		//Validando imagem
		
		if (ValidatorUtil.isNotEmpty(obj.getArquivoFoto().getFileName()) 
				&& obj.getArquivoFoto().getSize() != -1){
			//Verificando extensão
			
			String[] nomes = obj.getArquivoFoto().getFileName().split("\\.");
			String extensao = nomes[nomes.length - 1].toLowerCase();
			
			List<String> formatos = Arrays.asList(FORMATOS_IMAGEM);
			
			if (!formatos.contains(extensao)){
				String msg = "O formato de imagem que você anexou não é suportado. Por favor, ";
				msg += "envie em um dos seguintes formatos: png ou jpg.";
				
				throw new NegocioException(msg);
			}
		}
		
		//Validando vídeo
		
		if (ValidatorUtil.isNotEmpty(obj.getArquivoVideo().getFileName()) 
				&& obj.getArquivoVideo().getSize() != -1){
			//Verificando extensão
			
			String[] nomes = obj.getArquivoVideo().getFileName().split("\\.");
			String extensao = nomes[nomes.length - 1].toLowerCase();
			
			List<String> formatos = Arrays.asList(FORMATOS_VIDEO);
			
			if (!formatos.contains(extensao)){
				String msg = "O formato de vídeo que você anexou não é suportado. Por favor, ";
				msg += "envie em um dos seguintes formatos: mp4 ou avi.";
				
				throw new NegocioException(msg);
			}
		}
		
		//Verificando tamanho arquivo
		if (obj.getArquivoFoto().getSize() > TAM_MAXIMO_ARQUIVO || 
				obj.getArquivoVideo().getSize() > TAM_MAXIMO_ARQUIVO) {
			throw new NegocioException("O tamanho máximo para upload de arquivo é de 15 MB.");
		}
	}
	
}
