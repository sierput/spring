package pl.tieto.mat.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		if(user == null)
			throw new org.springframework.security.core.userdetails.UsernameNotFoundException("User not exsist");
	       Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
	        
	            grantedAuthorities.add(new SimpleGrantedAuthority("user"));
	        

	        return new org.springframework.security.core.userdetails.User(user.getFirstName(), user.getPassword(), grantedAuthorities);
	    }

     
	
}