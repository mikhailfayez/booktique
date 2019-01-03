package it.giorgiaauroraadorni.booktique;

import it.giorgiaauroraadorni.booktique.model.Address;
import it.giorgiaauroraadorni.booktique.model.Employee;
import it.giorgiaauroraadorni.booktique.repository.AddressRepository;
import it.giorgiaauroraadorni.booktique.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class EmployeeRepositoryTest {

    // Set automatically the attribute to the EmployeeRepository instance
    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private AddressRepository addressRepository;

    private List<Employee> dummyEmployees;
    private List<Address> dummyAddresses;

    /**
     * Create a list of addresses entities that will be use in the test
     */
    private void createDummyAddress() {
        dummyAddresses = IntStream
                .range(0, 3)
                .mapToObj(i -> new Address())
                .collect(Collectors.toList());

        // create a addresses with only the mandatory parameter
        dummyAddresses.get(0).setStreetAddress("Via Vinicio 59");
        dummyAddresses.get(0).setCity("Montecassiano");
        dummyAddresses.get(0).setProvince("MC");
        dummyAddresses.get(0).setPostalCode("04017");
        dummyAddresses.get(0).setCountry("Italia");

        // create an addresses with all the possible attributes
        dummyAddresses.get(1).setStreetAddress("Via Tancredi 96");
        dummyAddresses.get(1).setCity("Fonteblanda");
        dummyAddresses.get(1).setProvince("GR");
        dummyAddresses.get(1).setRegion("Toscana");
        dummyAddresses.get(1).setPostalCode("32349");
        dummyAddresses.get(1).setCountry("Italia");
        dummyAddresses.get(1).setBuilding("Appartamento 62 De Santis del friuli");

        // create an addresses with all the possible attributes
        dummyAddresses.get(2).setStreetAddress("Via Leone 1");
        dummyAddresses.get(2).setCity("Milano");
        dummyAddresses.get(2).setProvince("MI");
        dummyAddresses.get(2).setPostalCode("41845");
        dummyAddresses.get(2).setCountry("Italia");
        dummyAddresses.get(2).setBuilding("Piano 8");

        // save the addresses in the repository
        dummyAddresses = addressRepository.saveAll(dummyAddresses);
    }

    /**
     * Create a list of employees entities that will be use in the test
     */
    private void createDummyEmployee() {
        dummyEmployees = IntStream
                .range(0, 3)
                .mapToObj(i -> new Employee())
                .collect(Collectors.toList());

        // create a employees with only the mandatory parameter (inherited from person)
        dummyEmployees.get(0).setFiscalCode("GRGBVR75C13G224W");
        dummyEmployees.get(0).setName("Beverley");
        dummyEmployees.get(0).setSurname("Gregory");
        dummyEmployees.get(0).setUsername("BeverleyGregory75");
        dummyEmployees.get(0).setSupervisor(dummyEmployees.get(1));
        dummyEmployees.get(0).setPassword("yJmKKSjRJX4HZXrvxjBs");

        // create a employees with all the person attributes
        dummyEmployees.get(1).setFiscalCode("STNPTR70A11C933C");
        dummyEmployees.get(1).setName("Peter");
        dummyEmployees.get(1).setSurname("Stone");
        dummyEmployees.get(1).setUsername("PeterStone70");
        dummyEmployees.get(1).setPassword("XxzNh9jMkfWaHhzG2YVG");
        dummyEmployees.get(1).setSupervisor(dummyEmployees.get(1));
        dummyEmployees.get(1).setDateOfBirth(LocalDate.of(1970, 11, 3));
        dummyEmployees.get(1).setEmail("peter.stone40@example.com");
        dummyEmployees.get(1).setMobilePhone("+393733733730");

        // create a employees with many attributes
        dummyEmployees.get(2).setFiscalCode("STWJSP77T12A271K");
        dummyEmployees.get(2).setName("Josephine");
        dummyEmployees.get(2).setSurname("Stewart");
        dummyEmployees.get(2).setAddress(dummyAddresses.get(0));
        dummyEmployees.get(2).setDateOfBirth(LocalDate.of(1977, 1, 10));
        dummyEmployees.get(2).setEmail("josephine.stewart85@mail.com");
        dummyEmployees.get(2).setMobilePhone("3263261001");
        dummyEmployees.get(2).setUsername("JosephineStewart");
        dummyEmployees.get(2).setPassword("QQP6XH67PRNV42UZYPEM");
        dummyEmployees.get(2).setSupervisor(dummyEmployees.get(1));
        dummyEmployees.get(2).setHireDate(LocalDate.of(1997, 2, 1));

        // save the employees in the repository
        dummyEmployees = employeeRepository.saveAll(dummyEmployees);
    }

    @BeforeEach
    void createDummyEntities() {
        createDummyAddress();
        createDummyEmployee();
    }

    @Test
    void repositoryLoads() {}

    @Test
    void repositoryFindAll() {
        var savedEmployees = employeeRepository.findAll();
        var savedAddresses = addressRepository.findAll();

        // check if all the employees are correctly added to the repository
        assertTrue(savedEmployees.containsAll(dummyEmployees), "findAll should fetch all dummy employees");
        assertTrue(savedAddresses.containsAll(dummyAddresses), "findAll should fetch all dummy addresses");
    }

    /**
     * Insert many entries in the repository and check if these are readable and the attributes are correct
     */
    @Test
    public void testCreateEmployee() {
        List<Employee> savedEmployees = new ArrayList<>();

        for (int i = 0; i < dummyEmployees.size(); i++) {
            // check if the employees id are correctly automatic generated
            assertNotNull(employeeRepository.getOne(dummyEmployees.get(i).getId()));
            savedEmployees.add(employeeRepository.getOne(dummyEmployees.get(i).getId()));

            // check if the employees contain the createdAt and updatedAt annotation that are automatically populate
            assertNotNull(savedEmployees.get(i).getCreatedAt());
            assertNotNull(savedEmployees.get(i).getUpdatedAt());

            // check that all the attributes have been created correctly and contain the expected value
            assertEquals(savedEmployees.get(i).getFiscalCode(), dummyEmployees.get(i).getFiscalCode());
            assertEquals(savedEmployees.get(i).getName(), dummyEmployees.get(i).getName());
            assertEquals(savedEmployees.get(i).getSurname(), dummyEmployees.get(i).getSurname());
            assertEquals(savedEmployees.get(i).getDateOfBirth(), dummyEmployees.get(i).getDateOfBirth());
            assertEquals(savedEmployees.get(i).getEmail(), dummyEmployees.get(i).getEmail());
            assertEquals(savedEmployees.get(i).getMobilePhone(), dummyEmployees.get(i).getMobilePhone());
            assertEquals(savedEmployees.get(i).getUsername(), dummyEmployees.get(i).getUsername());
            assertEquals(savedEmployees.get(i).getPassword(), dummyEmployees.get(i).getPassword());
            assertEquals(savedEmployees.get(i).getAddress(), dummyEmployees.get(i).getAddress());
            assertEquals(savedEmployees.get(i).getSupervisor(), dummyEmployees.get(i).getSupervisor());
            assertEquals(savedEmployees.get(i).getHireDate(), dummyEmployees.get(i).getHireDate());
            assertEquals(savedEmployees.get(i).getId(), dummyEmployees.get(i).getId());
        }
    }

    @Test
    public void testEmployeeAddress() {
        // check if the addresses are set correctly
        assertNull(employeeRepository.findById(dummyEmployees.get(0).getId()).get().getAddress());
        assertNull(employeeRepository.findById(dummyEmployees.get(1).getId()).get().getAddress());
        assertNotNull(employeeRepository.findById(dummyEmployees.get(2).getId()).get().getAddress());

    }

    @Test
    public void testEmployeeSupervisor() {
        // check if the supervisors are set correctly
        for (int i = 0; i < dummyEmployees.size(); i++) {
            assertNotNull(employeeRepository.findById(dummyEmployees.get(0).getId()).get().getSupervisor());
        }
    }

    /**
     * Update one entry partially, edit different attributes and check if the fields are changed correctly
     */
    @Test
    public void testUpdateEmployee() {
        // get a employees from the repository
        Employee savedEmployee = employeeRepository.findById(dummyEmployees.get(0).getId()).get();

        // change some attributes
        savedEmployee.setName("Terry");
        savedEmployee.setFiscalCode("MTCTRR83C13G224W");
        savedEmployee.setUsername("TerryMitchell83");
        savedEmployee.setPassword("W422g31C38rRtCtM");
        savedEmployee.setAddress(dummyAddresses.get(1));
        savedEmployee.setSupervisor(dummyEmployees.get(2));

        // update the employee object
        employeeRepository.save(savedEmployee);
        Employee updatedEmployee = employeeRepository.findById(savedEmployee.getId()).get();

        // check that all the attributes have been updated correctly and contain the expected value
        assertNotNull(updatedEmployee);
        assertEquals(savedEmployee, updatedEmployee);
        assertEquals("Terry", updatedEmployee.getName());
        assertEquals("MTCTRR83C13G224W", updatedEmployee.getFiscalCode());
        assertEquals("TerryMitchell83", updatedEmployee.getUsername());
        assertEquals("W422g31C38rRtCtM", updatedEmployee.getPassword());
        assertEquals(addressRepository.getOne(dummyAddresses.get(1).getId()), updatedEmployee.getAddress());
        assertEquals(employeeRepository.getOne(dummyEmployees.get(2).getId()), updatedEmployee.getSupervisor());
    }

    /**
     * Creates an employee with the same username of another and throws an exception when attempting to insert data
     * by violating an integrity constraint, in particular, the unique constraints.
     */
    @Test
    public void testUniqueEmployeeUsernameIdentifier() {
        Employee duplicatedEmployee = new Employee();

        // set manually a new id in order to insert a new record and not for update an existing record
        duplicatedEmployee.setId(9999l);
        duplicatedEmployee.setFiscalCode("MTCKLN83C18G224W");
        duplicatedEmployee.setName("Kaitlin Josephine");
        duplicatedEmployee.setSurname("Mitchell Stewart");
        duplicatedEmployee.setAddress(dummyAddresses.get(0));
        duplicatedEmployee.setPassword("W422g31C38nLkCtM");

        // save the employee in the repository
        assertThrows(DataIntegrityViolationException.class, () -> {
            duplicatedEmployee.setUsername("JosephineStewart");
            employeeRepository.saveAndFlush(duplicatedEmployee);
        });
    }

    /**
     * Throws an exception when attempting to create an employee without mandatory attributes
     */
    @Test
    public void testIllegalCreateEmployee() {
        Employee invalidEmployee = new Employee();

        assertThrows(DataIntegrityViolationException.class, () -> {
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /**
     * Throws an exception when attempting to create or update an employee with illegal size for the attributes
     */
    @Test
    public void testIllegalSizeAttributes() {
        Employee invalidEmployee = new Employee();

        invalidEmployee.setFiscalCode("CRLCHR83C13G224W");
        invalidEmployee.setName("Christie");
        invalidEmployee.setSurname("Carlson");
        invalidEmployee.setSupervisor(employeeRepository.getOne(dummyEmployees.get(0).getId()));
        invalidEmployee.setUsername("ChristieCarlson83");
        invalidEmployee.setPassword("W422g31C38rHcLrC");

        employeeRepository.save(invalidEmployee);

        assertThrows(DataIntegrityViolationException.class, () -> {
            invalidEmployee.setUsername("ChristieCarlsonClark15gennaio1983");
            employeeRepository.saveAndFlush(invalidEmployee);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidEmployee.setUsername("Chri");
            employeeRepository.saveAndFlush(invalidEmployee);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidEmployee.setPassword("-X2LPM4r`2.SJn)nGxW3Dt}4$C+z??\"d7np=fHWDTB`y2ye:w2>\\5Kf,}\\Ks?*NBq7FG./Qp" +
                    "(>uxFtfs~U(A!tLHSGk>a5bhue^2wq#~3K9mc2[P(J:]c&hez(Jm&F?j2");
            employeeRepository.saveAndFlush(invalidEmployee);
        });

        assertThrows(ConstraintViolationException.class, () -> {
            invalidEmployee.setPassword("Chris83");
            employeeRepository.saveAndFlush(invalidEmployee);
        });
    }

    /**
     * Delete an entry and check if the employee was removed correctly
     */
    @Test
    public void testDeleteEmployee() {
        // get a employee from the repository
        Employee savedEmployee = employeeRepository.findById(dummyEmployees.get(0).getId()).get();

        // delete the employee object
        employeeRepository.delete(savedEmployee);

        // check that the employee has been deleted correctly
        assertEquals(employeeRepository.findById(dummyEmployees.get(0).getId()), Optional.empty());

        // delete all the entries verifying that the operation has been carried out correctly
        employeeRepository.deleteAll();
        employeeRepository.flush();
        assertTrue(employeeRepository.findAll().isEmpty());
    }

    /**
     * Update the supervisor of an entry and check if the fields of the parents are changed correctly
     */
    @Test
    public void testUpdateEmployeeSupervisor() {
        // get a supervisor from the repository
        Employee employee = employeeRepository.findById(dummyEmployees.get(0).getId()).get();
        Employee supervisor = employee.getSupervisor();

        // change the supervisor and update the employee object
        employee.setSupervisor(employeeRepository.findById(dummyEmployees.get(0).getId()).get());
        employeeRepository.save(employee);

        Employee updatedEmployee = employeeRepository.findById(employee.getId()).get();

        // check that the employees exist and that the supervisor attribute has been updated correctly and contain
        // the expected value
        assertNotNull(employee);
        assertNotNull(updatedEmployee);
        assertNotNull(updatedEmployee.getSupervisor());
        assertEquals(employee, updatedEmployee.getSupervisor());
        assertNotEquals(supervisor, updatedEmployee.getSupervisor());
    }
}
