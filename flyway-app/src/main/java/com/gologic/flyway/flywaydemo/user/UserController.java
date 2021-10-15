package com.gologic.flyway.flywaydemo.user;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class UserController {

  private final UserRepository repository;

  UserController(UserRepository repository) { this.repository = repository; }

  @GetMapping("/users")
  List<User> all() {     return repository.findAll();  }

  @PostMapping("/users")
  User newUser(@RequestBody User newUser) {     return repository.save(newUser);  }

  @GetMapping("/users/{id}")
  User one(@PathVariable Long id) {     return repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));  }

  @PutMapping("/users/{id}")
  User replaceUser(@RequestBody User newUser, @PathVariable Long id) {
    return repository.findById(id)
                     .map(User -> {
                       User.setName(newUser.getName());
                       return repository.save(User);
                     })
                     .orElseGet(() -> {
                       newUser.setId(id);
                       return repository.save(newUser);
                     });
  }

  @DeleteMapping("/users/{id}")
  void deleteUser(@PathVariable Long id) {    repository.deleteById(id);  }
  
}
