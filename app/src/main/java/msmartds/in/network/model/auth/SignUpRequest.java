package msmartds.in.network.model.auth;

public class SignUpRequest {
    private String firstname;
    private String lastname;
    private String email;
    private String mobileno;
    private String companey;

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getCompaney() {
        return companey;
    }

    public void setCompaney(String companey) {
        this.companey = companey;
    }
}
