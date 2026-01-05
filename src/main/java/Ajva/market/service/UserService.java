package Ajva.market.service;

import Ajva.market.entity.User;
import Ajva.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.findByUsername(username).isPresent();
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserByUsername(String username) {
        userRepository.findByUsername(username);
        return userRepository.findByUsername(username).get();
    }

    public User getUserById(Long workerId) {
        userRepository.findById(workerId);
        return userRepository.findById(workerId).get();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
