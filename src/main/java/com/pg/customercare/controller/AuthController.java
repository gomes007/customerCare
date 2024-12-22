package com.pg.customercare.controller;

import com.pg.customercare.model.Employee;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.Role;
import com.pg.customercare.model.User;
import com.pg.customercare.service.EmployeeService;
import com.pg.customercare.service.UserService;
import com.pg.customercare.util.JwtUtil;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AuthController handles authentication and registration requests.
 * 
 * This controller provides endpoints for user login and registration.
 * It uses various services to authenticate users, generate JWT tokens,
 * and register new users.
 * 
 * Endpoints:
 * - POST /api/auth/login: Authenticates a user and returns a JWT token.
 * - POST /api/auth/register: Registers a new user.
 * 
 * Dependencies:
 * - AuthenticationManager: Manages authentication.
 * - JwtUtil: Utility class for generating JWT tokens.
 * - UserService: Service for user-related operations.
 * - EmployeeService: Service for employee-related operations.
 * - PasswordEncoder: Encodes passwords.
 * 
 * Logging:
 * - Uses SLF4J Logger for logging events.
 * 
 * Error Handling:
 * - Returns appropriate HTTP status codes and error messages for various error scenarios.
 * 
 * Example usage:
 * - To authenticate a user, send a POST request to /api/auth/login with email and password.
 * - To register a new user, send a POST request to /api/auth/register with email, password, and employeeId.
 * 
 * Note:
 * - Ensure that email and password are provided in the request body for both endpoints.
 * - Ensure that employeeId is provided and valid for the registration endpoint.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

  /**
   * Logger instance for logging events in the AuthController class.
   * Utilizes the LoggerFactory to obtain a logger specific to this class.
   */
  private static final Logger logger = LoggerFactory.getLogger(
    AuthController.class
  );

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private JwtUtil jwtUtil;

  @Autowired
  private UserService userService;

  @Autowired
  private EmployeeService employeeService;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @PostMapping("/login")
  public ResponseEntity<Map<String, Object>> authenticate(
    @RequestBody Map<String, String> loginRequest
  ) {
    String email = loginRequest.getOrDefault("email", "");
    String password = loginRequest.getOrDefault("password", "");

    logger.debug("Attempting to authenticate user with email: {}", email);

    if (
      email.isEmpty() || password.isEmpty()
    ) {
      logger.error("Email and password are required");
      return ResponseEntity
        .badRequest()
        .body(Map.of("error", "Email and password are required"));
    }

    try {
      // Authenticate the user
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
      );
      User user = userService.findUserByEmail(email);
      if (user == null) {
        logger.error("User not found after authentication: {}", email);
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid email or password"));
      }

      SecurityContextHolder.getContext().setAuthentication(authentication);

      // Generate the JWT token
      String token = jwtUtil.generateToken(user.getEmail());

      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("userEmail", user.getEmail());
      response.put("employeeName", user.getEmployee().getName());
      response.put("employeeId", user.getEmployee().getId());

      logger.debug("User successfully authenticated: {}", email);
      return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
      logger.error("Invalid credentials for user: {}", email);
      return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("error", "Invalid email or password"));
    } catch (Exception e) {
      logger.error("Unexpected error while authenticating user: {}", email, e);
      return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(Map.of("error", "An unexpected error occurred"));
    }
  }

  @PostMapping("/register")
  public ResponseEntity<Map<String, Object>> registerUser(
    @RequestBody Map<String, String> userRequest
  ) {
    String email = userRequest.get("email");
    String password = userRequest.get("password");
    Long employeeId = Long.parseLong(userRequest.get("employeeId"));

    if (
      email == null || email.isEmpty() || password == null || password.isEmpty()
    ) {
      return ResponseEntity
        .badRequest()
        .body(Map.of("error", "Email and password are required"));
    }

    User existingUser = userService.findUserByEmail(email);
    if (existingUser != null) {
      return ResponseEntity
        .badRequest()
        .body(Map.of("error", "Email already registered"));
    }

    Employee employee = employeeService.getEmployeeById(employeeId);
    if (employee == null) {
      return ResponseEntity
        .badRequest()
        .body(Map.of("error", "Employee not found"));
    }

    // Obtendo o Role do PositionSalary do Employee
    PositionSalary positionSalary = employee.getPositionSalary();
    Role role = positionSalary.getRole();

    
    User user = new User();
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    user.setEmployee(employee);
    user.setRole(role);

    userService.saveUser(user, passwordEncoder);

    Map<String, Object> response = new HashMap<>();
    response.put("message", "User registered successfully");
    response.put("userEmail", user.getEmail());
    response.put("employeeName", employee.getName());

    return ResponseEntity.ok(response);
  }
}
