package br.com.projetodifm.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import br.com.projetodifm.data.vo.v1.security.LoginRequestVO;
import br.com.projetodifm.data.vo.v1.security.RegisterRequestVO;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.EmailNotFoundException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;
import br.com.projetodifm.model.Permission;
import br.com.projetodifm.model.User;
import br.com.projetodifm.repositories.PermissionRepository;
import br.com.projetodifm.repositories.UserRepository;
import br.com.projetodifm.util.ErrorMessages;

@Service
public class AuthServices {

    @Autowired
    private UserRepository repository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtServices service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @SuppressWarnings("rawtypes")
    public ResponseEntity register(RegisterRequestVO request) {

        List<Permission> permissions = new ArrayList<>();

        permissions.add(permissionRepository.findById(3L)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND)));

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .phoneNumber(request.getPhoneNumber())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .gender(request.getGender())
                .permissions(permissions)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .enabled(true)
                .build();

        if (repository.existsByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber()))
            throw new ConflictException(ErrorMessages.EMAIL_OR_PHONENUMBER_CONFLICT);

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
            throw new BadCredentialsException(ErrorMessages.BAD_CREDENTIALS);
        }
    }

    @SuppressWarnings("rawtypes")
    public ResponseEntity refreshToken(String email, String refreshToken) {

        var user = repository.findByEmail(email)
                .orElseThrow(() -> new EmailNotFoundException(email));

        return ResponseEntity.ok(service.refreshToken(refreshToken, user));
    }

}
