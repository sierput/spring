package pl.tieto.mat.test;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pl.tieto.mat.Role;
import pl.tieto.mat.RoleRepository;
import pl.tieto.mat.User;
import pl.tieto.mat.UserController;
import pl.tieto.mat.UserRepository;
import pl.tieto.mat.service.UserServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration

@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class UserDbTest {
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private DataSource dataSource;
	private User user;
	private String password = "test";
	private String firstName = "TestUser";
	private Set<Role> roles = new HashSet<>();

	@Before
	public void setup() {
		User userFound = userRepository.findByFirstName(firstName);
		if (userFound == null) {
			user = new User(firstName, "lastName", "email@email.com", false, password, password);
			roles.add(roleRepository.findByName("admin"));
			user.setRoles(roles);
			userService.save(user);
		} else {
			user = userFound;
		}
	}

	@Test()
	public void testAddUserToDb() throws Exception {
		User userFound = userRepository.findByFirstName(firstName);
		assertThat(user.getFirstName()).isEqualTo(userFound.getFirstName());
	}

	@Test
	public void testEditUser() throws Exception {
		user.setLastName("LastNameAfterUpdate");
		UserController.updateUser(user, dataSource);
		User userFound = userRepository.findByFirstName(firstName);
		assertThat(user.getLastName()).isEqualTo(userFound.getLastName());
	}

	@Test
	public void testApproveUser() throws Exception {
		UserController.approveUser(user.getId(), dataSource);
		User userFound = userRepository.findByFirstName(firstName);
		assertThat(user.isApproved()).isNotEqualTo(userFound.isApproved());
	}
}