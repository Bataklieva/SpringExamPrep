package exam.service.impl;

import com.google.gson.Gson;
import exam.model.dto.json.CustomerSeedDto;
import exam.model.entity.Customer;
import exam.repository.CustomerRepository;
import exam.repository.TownRepository;
import exam.service.CustomerService;
import exam.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private static final String CUSTOMER_PATH = "/Users/betinabataklieva/Downloads/LaptopShop/src/main/resources/files/json/customers.json";

    private final CustomerRepository customerRepository;
    private final TownRepository townRepository;

    private final ModelMapper mapper;
    private final ValidationUtil validationUtil;
    private final Gson gson;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, TownRepository townRepository,
                               ModelMapper mapper, ValidationUtil validationUtil, Gson gson) {

        this.customerRepository = customerRepository;
        this.townRepository = townRepository;
        this.mapper = mapper;
        this.validationUtil = validationUtil;
        this.gson = gson;
    }

    @Override
    public boolean areImported() {
        return customerRepository.count() > 0;
    }

    @Override
    public String readCustomersFileContent() throws IOException {
        return Files.readString(Path.of(CUSTOMER_PATH));
    }

    @Override
    public String importCustomers() throws IOException {

        StringBuilder builder = new StringBuilder();

        CustomerSeedDto[] customerSeedDtos = gson.fromJson(readCustomersFileContent(), CustomerSeedDto[].class);

        Arrays.stream(customerSeedDtos).filter(customerDto -> {

            boolean isValid = validationUtil.isValid(customerDto)
                    && emailExists(customerDto.getEmail());

            builder.append(isValid ? String.format("Successfully imported Customer %s %s - %s",
                    customerDto.getFirstName(), customerDto.getLastName(), customerDto.getEmail())
                    : "Invalid Customer")
                    .append(System.lineSeparator());

            return isValid;
        }).map(customers -> {
            Customer mappedCustomer = mapper.map(customers, Customer.class);

            mappedCustomer.setRegisteredOn(LocalDate.parse(customers.getRegisteredOn(),
                    DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            mappedCustomer.setTown(townRepository.findByName
                    (customers.getTown().getName()).orElse(null));

            return mappedCustomer;
        }).forEach(customerRepository::save);


        return builder.toString();
    }

    private boolean emailExists(String email) {

        Optional<Customer> byEmail = customerRepository.findByEmail(email);

        if (byEmail.isPresent()) {
            return false;
        }

        return true;
    }

}
