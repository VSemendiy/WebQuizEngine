package engine;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.HashSet;

@Entity
public class User {
/*
    @Autowired
    @Transient
    UserRepository userRepository;
*/

    @Id
    @NotBlank @Email @Column(unique = true)
    @Pattern(regexp="^([a-zA-Z0-9\\-\\.\\_]+)(\\@)([a-zA-Z0-9\\-\\.]+)(\\.)([a-zA-Z]{2,4})$")
    private String email;
    @NotNull @NotBlank
    private String password;

    public User() {
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        if(password.length()<5) throw new ConstraintViolationException(new HashSet<ConstraintViolation<?>>());
        this.password = Base64.encodeBase64String((email +":"+ password).getBytes());
    }
}
