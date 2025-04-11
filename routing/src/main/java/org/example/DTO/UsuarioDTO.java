package org.example.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
@Data
@NoArgsConstructor
public class UsuarioDTO {
    private String nome;
    private String email;
    private String senha;
    private String telefone;
    private LocalDate dataNascimento;
}
