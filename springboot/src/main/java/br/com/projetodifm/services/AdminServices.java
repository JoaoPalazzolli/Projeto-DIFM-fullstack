package br.com.projetodifm.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import br.com.projetodifm.controller.AdminController;
import br.com.projetodifm.data.vo.v1.admin.PermissionForPersonVO;
import br.com.projetodifm.data.vo.v1.admin.UserVO;
import br.com.projetodifm.exceptions.ConflictException;
import br.com.projetodifm.exceptions.EmailNotFoundException;
import br.com.projetodifm.exceptions.PermissionException;
import br.com.projetodifm.exceptions.ResourceNotFoundException;
import br.com.projetodifm.mapper.DozerMapper;
import br.com.projetodifm.repositories.PermissionRepository;
import br.com.projetodifm.repositories.UserRepository;
import br.com.projetodifm.util.ErrorMessages;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Service
public class AdminServices {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;
    
    public ResponseEntity<?> addPermission(PermissionForPersonVO person){

        var user = userRepository.findByEmail(person.getEmail())
            .orElseThrow(() -> new EmailNotFoundException(person.getEmail()));

        var permission = permissionRepository.findById(person.getPermissionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        user.getPermissions().stream().forEach(x -> {
            if (x.getId().equals(person.getPermissionId()))
                throw new ConflictException(ErrorMessages.PERMISSION_CONFLICT); 
        });

        user.getPermissions().add(permission);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<?> removePermission(PermissionForPersonVO person){

        var user = userRepository.findByEmail(person.getEmail())
            .orElseThrow(() -> new EmailNotFoundException(person.getEmail()));

        var permission = permissionRepository.findById(person.getPermissionId())
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        if (permission.getId().equals(3L))
            throw new PermissionException(ErrorMessages.PERMISSION_CANNOT_BE_REMOVED);

        user.getPermissions().remove(permission);

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    public ResponseEntity<?> findUserById(Long userId){
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorMessages.ID_NOT_FOUND));

        var userVO = DozerMapper.parseObject(user, UserVO.class);

        userVO.add(linkTo(methodOn(AdminController.class).findUserById(userId)).withSelfRel());

        return ResponseEntity.status(HttpStatus.OK).body(userVO);
    }

    public ResponseEntity<?> findAllUsers() {

        var usersVO = DozerMapper.parseListObjects(userRepository.findAll(), UserVO.class);

        usersVO.stream().forEach(x -> x.add(linkTo(methodOn(AdminController.class).findUserById(x.getKey())).withSelfRel()));

        return ResponseEntity.status(HttpStatus.OK).body(usersVO);
    }
}
