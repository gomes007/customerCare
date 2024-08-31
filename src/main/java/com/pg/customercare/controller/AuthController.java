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

@RestController
@RequestMapping("/api/auth")
public class AuthController {

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
    String email = loginRequest.get("email");
    String password = loginRequest.get("password");

    logger.debug("Tentando autenticar usuário com email: {}", email);

    if (
      email == null || email.isEmpty() || password == null || password.isEmpty()
    ) {
      logger.error("Email e senha são obrigatórios");
      return ResponseEntity
        .badRequest()
        .body(Map.of("error", "Email and password are required"));
    }

    try {
      // Autentica o usuário
      Authentication authentication = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(email, password)
      );
      SecurityContextHolder.getContext().setAuthentication(authentication);

      User user = userService.findUserByEmail(email);
      if (user == null) {
        logger.error("Usuário não encontrado após autenticação: {}", email);
        return ResponseEntity
          .status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid email or password"));
      }

      // Gera o token JWT
      String token = jwtUtil.generateToken(email);

      Map<String, Object> response = new HashMap<>();
      response.put("token", token);
      response.put("userEmail", user.getEmail());
      response.put("employeeName", user.getEmployee().getName());
      response.put("employeeId", user.getEmployee().getId());

      logger.debug("Usuário autenticado com sucesso: {}", email);
      return ResponseEntity.ok(response);
    } catch (BadCredentialsException e) {
      logger.error("Credenciais inválidas para o usuário: {}", email);
      return ResponseEntity
        .status(HttpStatus.UNAUTHORIZED)
        .body(Map.of("error", "Invalid email or password"));
    } catch (Exception e) {
      logger.error("Erro inesperado ao autenticar usuário: {}", email, e);
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
    user.setPassword(password);
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
