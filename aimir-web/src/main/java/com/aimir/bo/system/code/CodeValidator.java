package com.aimir.bo.system.code;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import com.aimir.model.system.Code;

public class CodeValidator implements Validator {
	
	public boolean supports(Class<?> clazz) {
		return Code.class.isAssignableFrom(clazz);
	}
	
	public void validate(Object object, Errors errors) {

        // validationUtils를 이용하여 입력값이 비었는지 체크
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "name",
            "required", new Object[] {"name" }, "Enter your Name");
        
/*       
        Code code = (Code) object;

        //프로퍼티 파일을 사용하여 유효성 체크에 따른 메시지 출력
        if (code.getPassword().length() < 6)
            errors.rejectValue("password", "error.password.tooshort");

        if (!code.getPassword().equals(code.getConfirmPassword()))
            errors.rejectValue("confirmPassword", "error.confirm");
*/            
	}

	
}
