package com.pheanith.dev.restaurant.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.pheanith.dev.restaurant.entity.User;

@DataJpaTest
public class UserRepositoryTest {
	
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void testFindByUsername() {
		//given
		User u1 = new User();
		u1.setUsername("n1@gmail.com");
		u1.setId(1L);
		userRepository.save(u1);
		//when
		Optional<User> user = userRepository.findByUsername("n1@gmail.com");
		
		//then
		assertEquals(1, user.get().getId());
		assertEquals("n1@gmail.com", user.get().getUsername());
	}
	
}
