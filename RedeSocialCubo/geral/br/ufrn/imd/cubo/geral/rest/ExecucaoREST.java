package br.ufrn.imd.cubo.geral.rest;

import java.util.Base64;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Classe responsável por se comunicar com o servidor de execução de códigos.
 * 
 * @author Renan
 *
 */
public class ExecucaoREST {

    public static String executarCodigo(String codigo, int idCubo) throws RestClientException {

//    	try {
//    		byte[] bytes = codigo.getBytes("ISO-8859-1"); 
//    		String encoded = Arrays.toString(bytes);
    		
    		String encoded = Base64.getEncoder().encodeToString(codigo.getBytes());
    		
        	String url = EnderecoServidor.URL + "cubo/executar?codigo=" + 
        			encoded + "&idCubo=" + idCubo;
            
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

            String dado = restTemplate.getForObject(url, String.class);

            return dado;
			
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			return null;
//		}

    }

}
