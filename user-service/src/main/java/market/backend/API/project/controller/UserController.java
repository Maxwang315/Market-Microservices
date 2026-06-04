package market.backend.API.project.controller;

import market.backend.API.project.common.Result;
import market.backend.API.project.entity.User;
import market.backend.API.project.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public Result<List<User>> getAllUsers() {
        return Result.success(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable("id") int id) {
        return Result.success(userService.getUserById(id));
    }

    @PostMapping
    public Result<String> addUser(@RequestBody User user) {
        userService.addUser(user);
        return Result.success("User added");
    }

    @PutMapping
    public Result<String> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success("User updated");
    }

    @DeleteMapping("/{id}")
    public Result<String> deleteUser(@PathVariable("id") int id) {
        userService.deleteUser(id);
        return Result.success("User deleted");
    }
}
