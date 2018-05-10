package pl.tieto.mat.test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import pl.tieto.mat.Role;
import pl.tieto.mat.RoleRepository;
import pl.tieto.mat.User;
import pl.tieto.mat.service.UserServiceImpl;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@AutoConfigureMockMvc

public class SecurityTest {
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private MockMvc mvc;;
	private String password = "test";
	private Set<Role> roles = new HashSet<>();

	@Test
	public void loginAvailableForAll() throws Exception {
		mvc
			.perform(get("/login"))
			.andExpect(status().isOk());
	}

	@Test
	public void homePageAvailableForAll() throws Exception {
		mvc
			.perform(get("/"))
			.andExpect(status().isOk())
			.andExpect(content().string(containsString("Home Page")));
	}

	@Test
	public void invalidLoginDenied() throws Exception {
		String loginErrorUrl = "/login?error";
		mvc
			.perform(formLogin().user("UserNotExsist").password("invalid"))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(loginErrorUrl))
			.andExpect(unauthenticated());
		mvc
			.perform(get(loginErrorUrl))
			.andExpect(content().string(containsString("Invalid user name and password")));
	}

	@Test
	public void redirectedToLoginFormForNotLoggedToHelloWorld() throws Exception {
		String loginUrl = "/login";
		String pageAfterSingIn = "/helloworld";
		mvc
			.perform(get(pageAfterSingIn))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("http://localhost" + loginUrl))
			.andExpect(unauthenticated());
	}

	@Test
	public void redirectedToLoginFormForNotLoggedToShowUser() throws Exception {
		String loginUrl = "/login";
		String showUser = "/showusers";
		mvc
			.perform(get(showUser))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("http://localhost" + loginUrl))
			.andExpect(unauthenticated());
	}

	@Test
	public void redirectedToLoginFormForNotLoggedToShowWaintingUser() throws Exception {
		String loginUrl = "/login";
		String showUser = "/showWaitingUsers";
		mvc
			.perform(get(showUser))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl("http://localhost" + loginUrl))
			.andExpect(unauthenticated());
	}

	@Test
	public void loginForApprovedUser() throws Exception {
		String userName = "UserApproved";
		createUser(userName, true, "user");
		mvc
			.perform(formLogin().user(userName).password(password))
			.andExpect(status().isFound())
			.andExpect(authenticated());
	}

	@Test
	public void invalidloginForNotApprovedUser() throws Exception {
		String userName = "UserNotApproved";
		createUser(userName, false,"user");
		mvc
			.perform(formLogin().user(userName).password(password))
			.andExpect(status().isFound())
			.andExpect(unauthenticated());
	}

	private void createUser(String userName, boolean approved, String roleName) {
		User user = new User(userName, "lastName", "email@email.com", approved, password, password);
		roles.add(roleRepository.findByName(roleName));
		user.setRoles(roles);
		userService.save(user);
	}
}
