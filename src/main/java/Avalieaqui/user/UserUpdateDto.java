package Avalieaqui.user;

import java.util.Date;

public class UserUpdateDto {
    private String id;
    private String name;
    private String email;
    private boolean adm;
    private String cpf;
    private String city;
    private String state;
    private String gender;
    private String birth;
    private String phone;
    private String password;
    private String oldPassword;

    public UserUpdateDto() {
    }

    public UserUpdateDto(String id, String name, String email, boolean adm) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.adm = adm;
    }

    public UserUpdateDto(String id, String name, String email, boolean adm, String cpf, String city, String state,
            String gender, String birth, String phone) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.adm = adm;
        this.cpf = cpf;
        this.city = city;
        this.state = state;
        this.gender = gender;
        this.birth = birth;
        this.phone = phone;
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

    public String getBirth() {
        return birth;
    }

    public String getPhone() {
        return phone;
    }

    public String getPassword() {
        return password;
    }

    public String getOldPassword() {
        return oldPassword;
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

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void Password(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

}
