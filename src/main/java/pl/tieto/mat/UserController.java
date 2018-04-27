package pl.tieto.mat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
	public Iterable<User> users;
	private String regexEmailValid = "^(.+)@(.+)$";
	@Autowired
	private UserRepository userRepositories;

	@GetMapping("/adduser")
	public String greetingForm(Model model) {
		model.addAttribute("user", new User());
		return "user";
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
		userRepositories.save(user);
	}

	@GetMapping("/showusers")
	public String showUser(Model model) {
		users = userRepositories.findAll();

		model.addAttribute("users", users);
		return "resultall";
	}
}
