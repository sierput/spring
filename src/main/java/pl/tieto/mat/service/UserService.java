package pl.tieto.mat.service;

import pl.tieto.mat.User;

public interface UserService {
	void save(User user);
	User findByUsername(String username);
}