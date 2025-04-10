package org.example.user;

import org.example.exceptions.UserNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Usuario create (Usuario user) {
        return userRepository.save(user);
    }

    public boolean delete (Long id){
        try{
            userRepository.deleteById(id);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    public Usuario findById(Long id){
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public Usuario updateUserById (Usuario newUser, Long id){
        Usuario user = findById(id);
        user.setNome(newUser.getNome());
        user.setEmail(newUser.getEmail());
        user.setTelefone(newUser.getTelefone());
        user.setDataNascimento(newUser.getDataNascimento());
        user.setSenha(newUser.getSenha());
        return userRepository.save(user);
    }
}
