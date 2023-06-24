package br.com.projetodifm.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.projetodifm.data.vo.v1.admin.PermissionForUserVO;
import br.com.projetodifm.data.vo.v1.admin.UserVO;
import br.com.projetodifm.services.AdminServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin")
@Tag(name = "Admin Endpoint")
public class AdminController {

    @Autowired
    private AdminServices services;
    
    @Operation(summary = "Add a Permission")
    @PostMapping(value = "/add-permission")
    public ResponseEntity<?> addPermission(@RequestBody @Valid PermissionForUserVO userPermission){
        return services.addPermission(userPermission);
    }

    @Operation(summary = "Remove a Permission")
    @PostMapping(value = "/remove-permission")
    public ResponseEntity<?> removePermission(@RequestBody @Valid PermissionForUserVO userPermission){
        return services.removePermission(userPermission);
    }

    @Operation(summary = "Finds all Users")
    @GetMapping(value = "/users")
    public ResponseEntity<List<UserVO>> findAllUsers(){
        return services.findAllUsers();
    }

    @Operation(summary = "Finds a User")
    @GetMapping(value = "/users/{user_id}")
    public ResponseEntity<UserVO> findUserById(@PathVariable(value = "user_id") Long userId){
        return services.findUserById(userId);
    }

    @Operation(summary = "Finds all Users by Permission")
    @GetMapping(value = "/users/permission/{permission_id}")
    public ResponseEntity<List<UserVO>> findAllUsersByPermissionId(@PathVariable(value = "permission_id") Long permissionId){
        return services.findAllUsersByPermissionId(permissionId);
    }
}
