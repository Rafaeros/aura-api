package br.rafaeros.aura.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "admin")
public class AdminConfig {
    
    private String username;
    private String email;
    private String password;
    private CompanyInfo company;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public CompanyInfo getCompany() { return company; }
    public void setCompany(CompanyInfo company) { this.company = company; }

    public static class CompanyInfo {
        private String name;
        private String cnpj;
        private String cep;
        private String address;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getCnpj() { return cnpj; }
        public void setCnpj(String cnpj) { this.cnpj = cnpj; }
        public String getCep() { return cep; }
        public void setCep(String cep) { this.cep = cep; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
    }
}