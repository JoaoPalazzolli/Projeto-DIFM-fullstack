package br.com.projetodifm.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidRefreshTokenUsername extends RuntimeException{

    private static final long serialVersionUID = 1L;

    private static final String DEFAULT_MSG = "Invalid email!";
    
    public InvalidRefreshTokenUsername(){
        super(DEFAULT_MSG);
    }

    public InvalidRefreshTokenUsername(String msg){
        super(msg);
    }
}
