package br.com.projetodifm.data.vo.v1.security;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequestVO implements Serializable{
    
    private static final long serialVersionUID = 1L;

    private String email;
    private String password;

}
