//package com.example.apigateway.controller;
//
//import org.springframework.web.bind.annotation.*;
//import com.example.apigateway.security.JwtUtil;
//
//import java.util.Map;
//
//@RestController
//@RequestMapping("/auth")
//@CrossOrigin(origins = "http://localhost:4200")
//public class AuthController {
//
////    @PostMapping("/login")
////    public String login(@RequestBody Map<String, String> request) {
////
////        String username = request.get("username");
////        String password = request.get("password");
////
////        if ("admin".equals(username) && "admin123".equals(password)) {
////            return JwtUtil.generateToken(username);
////        }
////
////        throw new RuntimeException("Invalid credentials");
////    }
//	
//	@PostMapping("/login")
//	public String login(@RequestParam String username,
//	                    @RequestParam String password) {
//
//	    if ("admin".equals(username) && "admin123".equals(password)) {
//	        return JwtUtil.generateToken(username, "ADMIN");
//	    }
//
//	    if ("user".equals(username) && "user123".equals(password)) {
//	        return JwtUtil.generateToken(username, "USER");
//	    }
//
//	    throw new RuntimeException("Invalid credentials");
//	}
//}
//

package com.example.apigateway.controller;

import com.example.apigateway.model.User;
import com.example.apigateway.repository.UserRepository;
import com.example.apigateway.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public AuthController(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        return jwtUtil.generateToken(user.getUsername(), user.getRole());
    }
}