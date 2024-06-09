package com.pg.customercare.service;

import com.pg.customercare.exception.impl.BadRequestException;
import com.pg.customercare.exception.impl.InternalServerException;
import com.pg.customercare.exception.impl.NotFoundException;
import com.pg.customercare.exception.impl.ValidationException;
import com.pg.customercare.model.Dependent;
import com.pg.customercare.model.Employee;
import com.pg.customercare.model.Person;
import com.pg.customercare.model.PositionSalary;
import com.pg.customercare.repository.EmployeeRepository;
import com.pg.customercare.repository.PositionSalaryRepository;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class EmployeeService {

  private EmployeeRepository employeeRepository;
  private PositionSalaryRepository positionSalaryRepository;

  public EmployeeService(
    EmployeeRepository employeeRepository,
    PositionSalaryRepository positionSalaryRepository
  ) {
    this.employeeRepository = employeeRepository;
    this.positionSalaryRepository = positionSalaryRepository;
  }

  private final String UPLOAD_FOLDER = "C:\\Uploads\\";

  @Transactional
  public Employee saveEmployee(
    Employee employee,
    MultipartFile file,
    Map<String, MultipartFile> files
  ) {
    try {
      validateEmployee(employee);
      savePhoto(employee, file);

      PositionSalary positionSalary = getPositionSalary(
        employee.getPositionSalary()
      );
      if (positionSalary == null) {
        throw new NotFoundException(
          "PositionSalary not found for the given ID"
        );
      }
      employee.setPositionSalary(positionSalary);

      setDependentsAndValidate(employee);

      processDependentFiles(employee, files);

      return employeeRepository.save(employee);
    } catch (ValidationException e) {
      throw e;
    } catch (NotFoundException e) {
      throw e;
    } catch (IOException e) {
      throw new InternalServerException(
        "Failed to save employee due to file handling error",
        e
      );
    } catch (Exception e) {
      throw new BadRequestException(
        "Failed to save employee due to an unexpected error",
        e
      );
    }
  }

  @Transactional
  public void deleteEmployee(Long id) {
    Employee employee = employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new NotFoundException("Employee not found with id " + id)
      );

    if (employee.getPhotoAddress() != null) {
      Path photoPath = Paths.get(employee.getPhotoAddress());
      try {
        Files.deleteIfExists(photoPath);
      } catch (IOException e) {
        throw new InternalServerException(
          "Failed to delete employee photo at address: " +
          employee.getPhotoAddress(),
          e
        );
      }
    }

    employee
      .getDependents()
      .forEach(dependent -> {
        if (dependent.getPhotoAddress() != null) {
          Path dependentPhotoPath = Paths.get(dependent.getPhotoAddress());
          try {
            Files.deleteIfExists(dependentPhotoPath);
          } catch (IOException e) {
            throw new InternalServerException(
              "Failed to delete dependent photo at address: " +
              dependent.getPhotoAddress(),
              e
            );
          }
        }
      });

    try {
      employeeRepository.deleteById(id);
    } catch (Exception e) {
      throw new InternalServerException(
        "Failed to delete employee with id " + id,
        e
      );
    }
  }

  public List<Employee> getAllEmployees() {
    return employeeRepository.findAll();
  }

  public Employee getEmployeeById(Long id) {
    return employeeRepository
      .findById(id)
      .orElseThrow(() ->
        new NotFoundException("Employee not found with id " + id)
      );
  }

  public Employee updateEmployee(Employee employee) {
    if (!employeeRepository.existsById(employee.getId())) {
      throw new NotFoundException(
        "Employee not found with id " + employee.getId()
      );
    }

    validateEmployee(employee);

    PositionSalary positionSalary = getPositionSalary(
      employee.getPositionSalary()
    );
    employee.setPositionSalary(positionSalary);

    setDependentsAndValidate(employee);

    return employeeRepository.save(employee);
  }

  public List<Employee> getEmployeesByPosition(String position) {
    List<Employee> employees = employeeRepository.findByPosition(position);
    if (employees.isEmpty()) {
      Map<String, Object> errorDetails = new HashMap<>();
      errorDetails.put("position", position);
      throw new ValidationException(
        "No employees found with position: " + position,
        errorDetails
      );
    }
    return employees;
  }

  // auxiliary methods
  private void validateEmployee(Employee employee) {
    if (employee.getPositionSalary() == null) {
      throw new ValidationException(
        "Position salary is required",
        new HashMap<>()
      );
    }

    LocalDate birthDate = employee.getBirthDate();
    LocalDate hireDate = employee.getHireDate();

    if (birthDate == null || !birthDate.isBefore(LocalDate.now())) {
      throw new ValidationException(
        "Birth date must be in the past",
        new HashMap<>()
      );
    }

    if (hireDate.isBefore(birthDate)) {
      throw new ValidationException(
        "Hire date must be after birth date",
        new HashMap<>()
      );
    }
  }

  private PositionSalary getPositionSalary(PositionSalary positionSalary) {
    if (positionSalary.getId() == null) {
      return positionSalaryRepository.save(positionSalary);
    } else {
      return positionSalaryRepository
        .findById(positionSalary.getId())
        .orElseThrow(() -> new NotFoundException("PositionSalary not found"));
    }
  }

  private void validateDependent(Dependent dependent) {
    if (dependent.getRelationship() == null) {
      throw new ValidationException(
        "Relationship is required for dependent",
        new HashMap<>()
      );
    }
  }

  private void setDependentsAndValidate(Employee employee) {
    if (employee.getDependents() != null) {
      employee
        .getDependents()
        .forEach(dependent -> {
          dependent.setEmployee(employee);
          validateDependent(dependent);
        });
    }
  }

  public void savePhoto(Person person, MultipartFile file) throws IOException {
    if (file != null && !file.isEmpty()) {
      String originalFileName = file.getOriginalFilename();
      String photoName = System.currentTimeMillis() + "_" + originalFileName;
      String photoAddress = UPLOAD_FOLDER + photoName;

      // Create the directory if it does not exist
      Path directoryPath = Paths.get(UPLOAD_FOLDER);
      if (!Files.exists(directoryPath)) {
        Files.createDirectories(directoryPath);
      }

      // Save the file to the directory
      Path filePath = Paths.get(photoAddress);
      Files.write(filePath, file.getBytes());

      // Set the photo to the person
      person.setPhotoName(photoName);
      person.setPhotoAddress(photoAddress);
    }
  }

  private void processDependentFiles(
    Employee employee,
    Map<String, MultipartFile> files
  ) throws IOException {
    for (Dependent dependent : employee.getDependents()) {
      String key =
        "dependents[" + employee.getDependents().indexOf(dependent) + "].file";
      MultipartFile file = files.get(key);
      if (file != null && !file.isEmpty()) {
        savePhoto(dependent, file);
      }
    }
  }
}
