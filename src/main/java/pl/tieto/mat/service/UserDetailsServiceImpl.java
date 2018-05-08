package pl.tieto.mat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pl.tieto.mat.Role;
import pl.tieto.mat.UserRepository;

import java.util.HashSet;
import java.util.Set;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		pl.tieto.mat.User user = userRepository.findByFirstName(username);
		if (user == null)
			throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not exsist");
		if (!user.isApproved())
			throw new org.springframework.security.core.userdetails.UsernameNotFoundException(
					"User is not approve by admin");
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for (Role role : user.getRoles()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return new org.springframework.security.core.userdetails.User(user.getFirstName(), user.getPassword(),
				grantedAuthorities);
	}
}