package pl.tieto.mat;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.tieto.mat.service.UserServiceImpl;

@Controller
public class UserController {
	public Iterable<User> users;
	private String regexEmailValid = "^(.+)@(.+)$";
	@Autowired
	private UserRepository userRepositories;
	@Autowired
	private UserServiceImpl userService;
	@Autowired
	private DataSource dataSource;

	@GetMapping("/adduser")
	public String greetingForm(Model model) {
		model.addAttribute("user", new User());
		return "user";
	}

	@GetMapping("/")
	public @ResponseBody String homePage(Model model) {
		return "Home Page";
	}

	@GetMapping("/login")
	public String loginFrom(Model model) {
		model.addAttribute("user", new User());
		return "login";
	}

	@PostMapping("/adduser")
	public String checkPersonInfo(@ModelAttribute @Valid User user, BindingResult bindingResult) {
		if (!isEmail(user.getEmail()))
			bindingResult.rejectValue("email", "error.user", "Wrong format, try use: xxx@xx.xx");
		if (bindingResult.hasErrors()) {
			return "user";
		} else {
			addToDB(user);
			return "result";
		}
	}

	private boolean isEmail(String email) {
		Pattern pattern = Pattern.compile(regexEmailValid);
		Matcher matcher = pattern.matcher(email);
		return matcher.matches();
	}

	private void addToDB(User user) {
		userService.save(user);
	}

	@GetMapping("/showusers")
	public String showUser(Model model) {
		if (!isAdminRole())
			return "accessDenied";

		users = userRepositories.findAll();

		model.addAttribute("users", users);
		return "resultall";
	}

	@GetMapping("/showWaitingUsers")
	public String showWaintingUsers(Model model) {
		if (!isAdminRole())
			return "accessDenied";
		users = userRepositories.findAll();
		List<User> userToApprove = new ArrayList<>();
		for (User user : users) {
			if (!user.isApproved())
				userToApprove.add(user);
		}

		model.addAttribute("users", userToApprove);
		return "showWaitingUsers";
	}

	@GetMapping("/showWaitingUsers/approve/{id}")
	public String usersApprove(Model model, @PathVariable(name = "id") int id) {
		approveUser(id, dataSource);
		return "showWaitingUsers";
	}

	public static void approveUser(int id, DataSource dataSource) {
		String SQL = "update user set approved = 1 where id = ?";
		JdbcTemplate jdbcTemplateObject = new JdbcTemplate(dataSource);
		jdbcTemplateObject.update(SQL, id);
	}

	@GetMapping("/showWaitingUsers/edit/{id}")
	public String usersedit(Model model, @PathVariable(name = "id") int id) {
		model.addAttribute("user", userRepositories.findById(id));
		return "userEditForm";

	}

	@PostMapping("/showWaitingUsers/edit/{id}")
	public String usersEditAccept(@ModelAttribute @Valid User user, BindingResult bindingResult,
			@PathVariable(name = "id") int id) {
		user.setId(id);
		if (!isEmail(user.getEmail()))
			bindingResult.rejectValue("email", "error.user", "Wrong format, try use: xxx@xx.xx");
		if (bindingResult.hasErrors() && !bindingResult.getFieldError().getField().equals("id")) { // second condition
																									// is temporary
			return "userEditForm";
		} else {
			updateUser(user, dataSource);
			return "result";
		}
	}

	public static void updateUser(User user, DataSource dataSource) {
		String SQL = "update user set email = ?, first_name = ?, last_name = ?, password = ? where id = ?";
		JdbcTemplate jdbcTemplateObject = new JdbcTemplate(dataSource);
		jdbcTemplateObject.update(SQL, user.getEmail(), user.getFirstName(), user.getLastName(), user.getPassword(),
				user.getId());
	}

	private boolean isAdminRole() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		String name = auth.getName(); //
		User userActual = userRepositories.findByFirstName(name);
		ArrayList<String> roles = new ArrayList<>();
		for (Role role : userActual.getRoles()) {
			roles.add(role.getName());
		}

		return roles.contains(("admin"));
	}
}
