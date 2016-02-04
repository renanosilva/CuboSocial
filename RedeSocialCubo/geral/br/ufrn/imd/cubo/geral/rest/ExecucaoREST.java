package br.ufrn.imd.cubo.geral.rest;

import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

public class ExecucaoREST {

    public static String executarCodigo(String codigo, int idCubo) throws RestClientException {

    	String url = "http://localhost:8080/ExecucaoCubo/rest/cubo/executar?codigo=" + codigo + "&idCubo=" + idCubo;
        
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJacksonHttpMessageConverter());

        String dado = restTemplate.getForObject(url, String.class);

        return dado;

    }

}
