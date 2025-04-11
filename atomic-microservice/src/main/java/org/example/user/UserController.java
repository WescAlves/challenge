package org.example.user;

import org.example.exceptions.UserNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/usuarios")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<Usuario> create(@RequestBody @Valid Usuario user){
        return ResponseEntity.ok(userService.create(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> findById(@PathVariable Long id){
        return ResponseEntity.ok(userService.findById(id));
    }

    @GetMapping("/saudacao/{id}")
    public ResponseEntity<Map<String, String>> getSaudacaoById(@PathVariable Long id){
        String mensagem = "Olá, " + userService.findById(id).getNome() + ", tudo bem com você? Espero que esteja bem.";
        return ResponseEntity.ok(Map.of("mensagem",mensagem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> update(@PathVariable Long id, @RequestBody @Valid Usuario user){
        return ResponseEntity.ok(userService.updateUserById(user, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteById(@PathVariable Long id){
        if(userService.delete(id)){
            return ResponseEntity.ok(Map.of("message", "Usuário deletado com sucesso!"));
        }
        else{
            throw new UserNotFoundException();
        }
    }

}
