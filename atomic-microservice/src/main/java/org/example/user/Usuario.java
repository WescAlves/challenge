package org.example.user;


import lombok.Data;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.LocalDate;

@Entity
@Table
@Data
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    @NotBlank
    @Pattern(regexp = "^[\\p{L} ]+$", message = "Não deve conter números")
    private String nome;
    @Email
    @Pattern(regexp = "^.+@.+\\..+$", message = "Email incorreto"
    )
    @NotBlank
    private String email;
    @NotBlank
    private String senha;
    @NotNull
    private LocalDate dataNascimento;
    @NotBlank
    @Pattern(regexp = "\\d{10,11}", message = "Telefone Incorreto")
    private String telefone;
}
