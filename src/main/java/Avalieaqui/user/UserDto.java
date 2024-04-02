package Avalieaqui.user;

public class UserDto {
    private String id;
    private String name;
    private String email;
    private boolean adm;

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

}
