package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.mindrot.jbcrypt.BCrypt;

@Entity
public class User extends play.db.ebean.Model {

    @Id
    private String email;
    private String name;
    private String password;
    
    public static Finder<String,User> find = new Finder<>(String.class, User.class);
    
    public User(String email, String name, String password) {
        this.setEmail(email);
        this.setName(name);
        this.setPassword(createPassword(password));
       // this.setPassword(password);   
    }
    
    public String createPassword(String clearString) {
        if (clearString == null) {
            //throw new AppException("empty.password");
        }
        return BCrypt.hashpw(clearString, BCrypt.gensalt());
    }
    
    public static boolean checkPassword(String candidate, String encryptedPassword) {
        if (candidate == null) {
            return false;
        }
        if (encryptedPassword == null) {
            return false;
        }
        return BCrypt.checkpw(candidate, encryptedPassword);
    }

    public String getEmail() {
    	return email;
    }
    
    public void setEmail(String email) {
    	this.email=email;
    }
    
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}    
}