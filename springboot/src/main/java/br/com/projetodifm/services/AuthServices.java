package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.projetodifm.data.vo.v1.security.LoginRequestVO;
import br.com.projetodifm.data.vo.v1.security.RegisterRequestVO;
import br.com.projetodifm.model.User;
import br.com.projetodifm.model.UserRoles;
import br.com.projetodifm.repositories.UserRepository;

@Service
public class AuthServices {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtServices service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @SuppressWarnings("rawtypes")
    public ResponseEntity register(RegisterRequestVO request) {

        if (checkIfParamsIsNotNull(request)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .cellPhone(request.getCellPhone())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .roles(UserRoles.USER)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        repository.save(user);

        return ResponseEntity.ok(service.createToken(user));
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity login(LoginRequestVO request) {
        try {
            if (checkIfParamsIsNotNull(request)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
            }

            var email = request.getEmail();
            var password = request.getPassword();

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

            var user = repository.findByEmail(email).orElseThrow();

            return ResponseEntity.ok(service.createToken(user));
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid username/password supplied!");
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity refreshToken(String email, String refreshToken) {

        if (checkIfParamsIsNotNull(email, refreshToken)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid client request!");
        }

        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("email [ " + email + " ] not found!"));

        return ResponseEntity.ok(service.refreshToken(refreshToken, user));
    }

    private Boolean checkIfParamsIsNotNull(LoginRequestVO request) {
        return request.getEmail() == null || request.getPassword() == null || request == null
                || request.getPassword().isBlank() || request.getEmail().isBlank();
    }

    private Boolean checkIfParamsIsNotNull(RegisterRequestVO request) {
        return request.getEmail() == null || request.getPassword() == null || request == null
                || request.getPassword().isBlank() || request.getEmail().isBlank()
                || request.getFirstName() == null || request.getFirstName().isBlank() ||
                request.getLastName() == null || request.getLastName().isBlank() ||
                request.getCellPhone() == null || request.getCellPhone().isBlank() ||
                request.getGender() == null || request.getGender().isBlank();
    }

    private Boolean checkIfParamsIsNotNull(String email, String refreshToken) {
        return email == null || email.isBlank() || refreshToken == null || refreshToken.isBlank();
    }
}
