package br.ufrn.imd.cubo.arq.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.ufrn.imd.cubo.arq.util.CriptografiaUtils;
import br.ufrn.imd.cubo.arq.util.EnvioArquivoUtils;

/**
 * Servlet para download de arquivos da base de arquivos.
 * 
 * @author Renan de Oliveira
 */
@SuppressWarnings("serial")
public class VerArquivoServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		String arquivo = req.getParameter("idArquivo");
		
		if (arquivo == null) {
			arquivo =  req.getParameter("idFoto"); 
		}
		
		String auxSalvar = req.getParameter("salvar");
		Boolean salvar = Boolean.valueOf(auxSalvar != null ? req.getParameter("salvar") : "true");
		
		if (arquivo != null && !arquivo.equals("")) {

			int idArquivo = new Integer(arquivo);

			String key = req.getParameter("key");
			String generatedKey = CriptografiaUtils.criptografarMD5(String.valueOf(idArquivo));

			if (key != null && key.equals(generatedKey)) {
				EnvioArquivoUtils arq = new EnvioArquivoUtils();
				
				try {
					arq.conectar();
					arq.recuperaArquivo(res, idArquivo, salvar);						
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					arq.desconectar();
				}
				
			} else {
				res.getWriter().print("Acesso Negado!");
				res.getWriter().flush();
			}
		}
	}

	
}
