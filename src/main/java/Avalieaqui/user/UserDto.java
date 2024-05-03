package Avalieaqui.user;

import java.util.Date;

public class UserDto {
    private String id;
    private String name;
    private String email;
    private boolean adm;
    private String cpf;
    private String city;
    private String state;
    private String gender;
    private Date birth;

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

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getGender() {
        return gender;
    }

    public Date getBirth() {
        return birth;
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

    public void setCity(String city) {
        this.city = city;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }

}
