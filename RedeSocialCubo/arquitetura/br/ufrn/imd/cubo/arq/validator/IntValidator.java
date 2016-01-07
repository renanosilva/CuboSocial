package br.ufrn.imd.cubo.arq.validator;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
* Valida se um inteiro é maior que zero.
*
* @author Renan de Oliveira
*/
@FacesValidator("intValidator")
public class IntValidator implements Validator {
    
	@Override
     public void validate(FacesContext arg0, UIComponent arg1, Object valorTela) throws ValidatorException {
          Number valor = (Number) valorTela;
          
          if (valor.intValue() <= 0){
        	  FacesMessage message = new FacesMessage("Campo " + arg1.getId() + " Obrigatório.");
              message.setSeverity(FacesMessage.SEVERITY_ERROR);
        	  throw new ValidatorException(message);
          }
          
     }
 
}