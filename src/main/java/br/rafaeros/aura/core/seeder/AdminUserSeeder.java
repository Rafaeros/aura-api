package br.rafaeros.aura.core.seeder;

import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final String adminUsername;
    private final String adminPassword;
    private final String adminCompanyName;
    private final String adminCompanyCnpj;
    private final String adminCompanyCep;
    private final String adminCompanyAddress;

    public AdminUserSeeder(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            @Value("${admin.username}") String adminUsername,
            @Value("${admin.password}") String adminPassword,
            @Value("${admin.company.name}") String adminCompanyName,
            @Value("${admin.company.cnpj}") String adminCompanyCnpj,
            @Value("${admin.company.cep}") String adminCompanyCep,
            @Value("${admin.company.address}") String adminCompanyAddress) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.adminCompanyName = adminCompanyName;
        this.adminCompanyCnpj = adminCompanyCnpj;
        this.adminCompanyCep = adminCompanyCep;
        this.adminCompanyAddress = adminCompanyAddress;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            Company company = new Company();
            company.setName(adminCompanyName);
            company.setCnpj(adminCompanyCnpj);
            company.setCep(adminCompanyCep);
            company.setAddressNumber(Integer.parseInt(adminCompanyAddress));
            companyRepository.save(company);

            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            admin.setCompany(company);

            userRepository.save(admin);
            System.out.println("Admin default criado: " + adminUsername);
        }
    }
}
