package pl.tieto.mat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "User")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@NotNull
	private Integer id;
	@Size(min = 2, max = 20)
	private String firstName;
	@Size(min = 2, max = 20)
	private String lastName;
	private String email;

	public User(Integer id, String firstName, String lastName, String email) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
		this.email = email;
	}

	public User() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return firstName + ", " + lastName + ", " + email;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
