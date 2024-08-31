package com.pg.customercare.config;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.pg.customercare.model.Address;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.Permission;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.model.Role;
import com.pg.customercare.model.User;
import com.pg.customercare.model.ENUM.Gender;
import com.pg.customercare.repository.EmployeeRepository;
import com.pg.customercare.repository.PermissionRepository;
import com.pg.customercare.repository.PositionSalaryRepository;
import com.pg.customercare.repository.RoleRepository;
import com.pg.customercare.repository.UserRepository;

@Component
public class DatabaseInitializer implements CommandLineRunner {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PositionSalaryRepository positionSalaryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        // Verifica se existem roles ou usuários já criados para evitar duplicação
        if (roleRepository.count() == 0 && userRepository.count() == 0) {
            // Criar permissões
            Permission readPermission = new Permission(null, "READ_PRIVILEGES", new HashSet<>());
            Permission writePermission = new Permission(null, "WRITE_PRIVILEGES", new HashSet<>());
            permissionRepository.save(readPermission);
            permissionRepository.save(writePermission);

            // Criar role e associar permissões
            Role adminRole = new Role(null, "ADMIN", new HashSet<>(), new HashSet<>());
            adminRole.getPermissions().add(readPermission);
            adminRole.getPermissions().add(writePermission);
            roleRepository.save(adminRole);

            // Criar PositionSalary
            PositionSalary positionSalary = new PositionSalary(null, "Manager", 75000.00, 5000.00, adminRole);
            positionSalaryRepository.save(positionSalary);

            // Criar Endereços
            List<Address> addresses = new ArrayList<>();
            addresses.add(new Address(null, "Main St", "123", "Downtown", "12345-678", "Apt 101", "City", "State"));

            // Criar Employee
            Employee adminEmployee = new Employee();
            adminEmployee.setPositionSalary(positionSalary);
            adminEmployee.setHireDate(LocalDate.now());
            adminEmployee.setCompanyEmail("admin@company.com");
            adminEmployee.setHasDependents(false);
            adminEmployee.setName("Admin User");
            adminEmployee.setPrivateEmail("admin.private@domain.com");
            adminEmployee.setCpf("123.456.789-00");
            adminEmployee.setPhone("123-456-7890");
            adminEmployee.setBirthDate(LocalDate.of(1990, 1, 1));
            adminEmployee.setAddresses(addresses);
            adminEmployee.setGender(Gender.MALE); 
            adminEmployee.setOtherInformation("Informações adicionais");
            adminEmployee.setPhotoName("admin_photo.jpg");
            adminEmployee.setPhotoAddress("/photos/admin_photo.jpg");
            employeeRepository.save(adminEmployee);

            // Criar User
            User adminUser = new User();
            adminUser.setEmail("admin@company.com");
            adminUser.setPassword(passwordEncoder.encode("adminpassword"));
            adminUser.setEmployee(adminEmployee);
            adminUser.setRole(adminRole);
            userRepository.save(adminUser);

            System.out.println("Default admin user and employee created");
        }
    }
}
