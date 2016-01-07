/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/07/2006
 */
package br.ufrn.imd.cubo.arq.helper;

import java.io.UnsupportedEncodingException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Classe que contém métodos para envio de emails.
 * 
 * @author Renan
 */
public class MailHelper {

//	private static final String HOSTNAME = "smtp.gmail.com";
//	private static final String USERNAME = "sgp.ifrn";
	private static final String PASSWORD = "cubo.social.imd";
	private static final String EMAIL_ORIGEM = "cubosocial.imd@gmail.com";

//	private static Email conectaEmail() throws EmailException {
//		Email email = new SimpleEmail();
//		email.setHostName(HOSTNAME);
//		email.setSmtpPort(587);
//		email.setAuthenticator(new DefaultAuthenticator(USERNAME, PASSWORD));
//		email.setTLS(true);
//		email.setFrom(EMAIL_ORIGEM);
//		return email;
//	}
//
//	public static void enviaEmail(String destino, String titulo, String mensagem) throws EmailException {
//		Email email = new SimpleEmail();
//		email = conectaEmail();
//		email.setSubject(titulo);
//		email.setMsg(mensagem);
//		email.addTo(destino);
//		email.send();
//	}
	
	public static void enviarEmail(String destino, String titulo,
			String mensagem) throws AddressException, MessagingException {
		
		// Parâmetros de conexão com servidor Gmail
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class",
				"javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");

		Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(EMAIL_ORIGEM,
								PASSWORD);
					}
				});

		// Ativa Debug para sessão 
		session.setDebug(true);

		final Message message = new MimeMessage(session);
		
		try {
			message.setFrom(new InternetAddress(EMAIL_ORIGEM, "Cubo Social"));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
			return;
		}

		Address[] toUser = InternetAddress // Destinatário(s)
				.parse(destino);

		message.setRecipients(Message.RecipientType.TO, toUser);
		message.setSubject(titulo);// Assunto
		message.setContent(mensagem, "text/html");
		
		Transport.send(message);
		System.out.println("Email enviado...");

	}

}