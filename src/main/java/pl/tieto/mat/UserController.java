package pl.tieto.mat;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
	private final String INSERT_SQL = "INSERT INTO User(id,firstName, lastName, email) values(:id, :firstName, :lastName,:email)";
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	@Autowired
	JdbcTemplate jdbcTemplate;
	public List<User> users;
	private String regexEmailValid = "^(.+)@(.+)$";

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
		KeyHolder holder = new GeneratedKeyHolder();
		SqlParameterSource parameters = new MapSqlParameterSource()
				.addValue("id", user.getId())
				.addValue("firstName",user.getFirstName())
				.addValue("lastName", user.getLastName())
				.addValue("email", user.getEmail());
		namedParameterJdbcTemplate.update(INSERT_SQL, parameters, holder);
	}

	@GetMapping("/showusers")
	public String showUser(Model model) {

		users = jdbcTemplate.query("SELECT id, firstName, lastName, email FROM User",
				new ResultSetExtractor<List<User>>() {

					@Override
					public List<User> extractData(ResultSet rs) throws SQLException, DataAccessException {
						List<User> list = new ArrayList<>();
						while (rs.next()) {
							User user = new User(rs.getInt("id"), rs.getString("firstName"), rs.getString("lastName"),
									rs.getString("email"));
							list.add(user);
						}
						users = list;
						return list;
					}
				});
		model.addAttribute("users", users);
		return "resultall";
	}
}
