package br.ufrn.imd.cubo.arq.exception;

import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.NavigationHandler;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import br.ufrn.imd.cubo.arq.helper.ExceptionHelper;
import br.ufrn.imd.cubo.arq.helper.MailHelper;

public class CustomExceptionHandler extends ExceptionHandlerWrapper {

	private static final Logger log = Logger.getLogger(CustomExceptionHandler.class.getCanonicalName());
	private ExceptionHandler wrapped;

	CustomExceptionHandler(ExceptionHandler exception) {
		this.wrapped = exception;
	}

	@Override
	public ExceptionHandler getWrapped() {
		return wrapped;
	}

	@Override
	public void handle() throws FacesException {

		final Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator();
		while (i.hasNext()) {
			ExceptionQueuedEvent event = i.next();
			ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

			// get the exception from context
			Throwable t = context.getException();
			t.printStackTrace();

			final FacesContext fc = FacesContext.getCurrentInstance();
			final Map<String, Object> requestMap = fc.getExternalContext().getRequestMap();
			final NavigationHandler nav = fc.getApplication().getNavigationHandler();

			// here you do what ever you want with exception
			try {
				String msg = ExceptionHelper.getStackTrace(t);
				
				try {
					MailHelper.enviarEmail(MailHelper.EMAIL_SISTEMA, "Erro", msg);
				} catch (AddressException e) {
					e.printStackTrace();
				} catch (MessagingException e) {
					e.printStackTrace();
				}
				
				// //log error ?
				// log.log(Level.SEVERE, "Critical Exception!", t);
				//
				// //redirect error page
				// requestMap.put("exceptionMessage", t.getMessage());
				// nav.handleNavigation(fc, null, "/error");
				// fc.renderResponse();
				//
				// // remove the comment below if you want to report the error
				// in a jsf error message
				// //JsfUtil.addErrorMessage(t.getMessage());

			} finally {
				// remove it from queue
				i.remove();
			}
		}
		// parent hanle
		getWrapped().handle();
	}
}
