package br.ufrn.imd.cubo.arq.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import br.ufrn.imd.cubo.arq.constantes.RedeSocialConstants;

/**
 * Filtro que verifica se um usuário está logado para poder ter acesso a determinadas
 * funcionalidades do sistema.
 * @author Renan
 */
public class LoginFilter implements Filter {
	
	private final String[] urlsPermitidas = {};

	@Override
	public void destroy() {
		
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res,
			FilterChain chain) throws IOException, ServletException {
		
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		
		HttpSession session = request.getSession();
        String reqUrl = request.getRequestURI();
        
   		boolean permitido = false;

   		if (reqUrl.contains("/publico/") || reqUrl.equals(RedeSocialConstants.CONTEXT_NAME_COM_BARRAS) 
   				|| reqUrl.contains(".faces."))
   			permitido = true;
   		
   		if (!permitido){
	   		for (String url : urlsPermitidas){
	   			if (reqUrl.contains(url)){
	   				permitido = true;
	   				break;
	   			}
	   		}
   		}

        if (session.getAttribute("usuarioLogado") != null || permitido) {
            chain.doFilter(request, response);
        } else {
        	response.sendRedirect(RedeSocialConstants.CONTEXT_NAME_COM_BARRAS + "sessao_expirada.html");
            return;
        }
		
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		
	}
	
}
