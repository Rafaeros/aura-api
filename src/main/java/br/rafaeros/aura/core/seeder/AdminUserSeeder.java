package br.rafaeros.aura.core.seeder;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import br.rafaeros.aura.core.config.AdminConfig;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.company.repository.CompanyRepository;
import br.rafaeros.aura.modules.user.model.User;
import br.rafaeros.aura.modules.user.model.enums.Role;
import br.rafaeros.aura.modules.user.repository.UserRepository;

@Component
public class AdminUserSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminConfig adminConfig;

    public AdminUserSeeder(
            UserRepository userRepository,
            CompanyRepository companyRepository,
            PasswordEncoder passwordEncoder,
            AdminConfig adminConfig) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminConfig = adminConfig;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (userRepository.findByEmail(adminConfig.getEmail()).isPresent()) {
            System.out.println("Seeder: Admin user already exists. Skipping.");
            return;
        }
        System.out.println("Seeder: Creating default Admin and Company...");

        Company company = companyRepository.findByCnpj(adminConfig.getCompany().getCnpj())
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(adminConfig.getCompany().getName());
                    newCompany.setCnpj(adminConfig.getCompany().getCnpj());
                    newCompany.setCep(adminConfig.getCompany().getCep());
                    try {
                        newCompany.setAddressNumber(Integer.parseInt(adminConfig.getCompany().getAddress()));
                    } catch (NumberFormatException e) {
                        newCompany.setAddressNumber(0);
                    }
                    return companyRepository.save(newCompany);
                });
        User admin = new User();
        admin.setUsername(adminConfig.getUsername());
        admin.setEmail(adminConfig.getEmail());
        admin.setPassword(passwordEncoder.encode(adminConfig.getPassword()));
        admin.setRole(Role.ADMIN);
        admin.setCompany(company);

        userRepository.save(admin);

        System.out.println("Seeder: Admin user created successfully: " + adminConfig.getEmail());
    }
}