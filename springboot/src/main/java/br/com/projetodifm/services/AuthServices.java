package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.projetodifm.data.vo.v1.security.LoginRequestVO;
import br.com.projetodifm.data.vo.v1.security.RegisterRequestVO;
import br.com.projetodifm.exceptions.ConflictException;
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

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .roles(UserRoles.USER)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        if (repository.existsByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber()))
            throw new ConflictException("this Email or PhoneNumber already exists");

        repository.save(user);

        return ResponseEntity.ok(service.createToken(user));
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity login(LoginRequestVO request) {
        try {

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

        var user = repository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("email [ " + email + " ] not found!"));

        return ResponseEntity.ok(service.refreshToken(refreshToken, user));
    }

}
