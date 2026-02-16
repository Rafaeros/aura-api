package br.rafaeros.aura.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "admin")
public class AdminConfig {

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private CompanyInfo company;

    @Getter
    @Setter
    public static class CompanyInfo {
        private String name;
        private String cnpj;
        private String cep;
        private String address;
    }
}