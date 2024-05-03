package Avalieaqui.user;

import java.util.Date;

public class UserDto {
    private String id;
    private String name;
    private String email;
    private boolean adm;
    private String cpf;
    private String cidade;
    private String estado;
    private String genero;
    private Date nascimento;

    public UserDto() {
    }

    public UserDto(String id, String name, String email, boolean adm) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.adm = adm;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public boolean getAdm() {
        return adm;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCidade() {
        return cidade;
    }

    public String getEstado() {
        return estado;
    }

    public String getGenero() {
        return genero;
    }

    public Date getNascimento() {
        return nascimento;
    }

    // Setters

    public void setId(String id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public void setNascimento(Date nascimento) {
        this.nascimento = nascimento;
    }

}
