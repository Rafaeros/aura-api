package br.rafaeros.aura.modules.user.model;

import br.rafaeros.aura.core.model.BaseEntity;
import br.rafaeros.aura.modules.company.model.Company;
import br.rafaeros.aura.modules.device.model.UserDevice;
import br.rafaeros.aura.modules.user.model.enums.Role;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false, unique = true)
    private String username; // Este é o "apelido" do usuário

    @Column(nullable = false, unique = true)
    private String email; // Este é o LOGIN do usuário

    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserDevice> userDevices = new ArrayList<>();
    
    private boolean isFirstAccess;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public String getActualUsername() {
        return this.username;
    }

    @Override
    public boolean isEnabled() {
        if (!this.isActive()) return false;
        if (this.company != null && !this.company.isActive()) return false;
        return true;
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }

    @PrePersist
    public void prePersist() {
        if (!this.isActive()) {
            this.setActive(true);
        }
        this.isFirstAccess = true;
    }

    public void addDeviceLink(UserDevice link) {
        this.userDevices.add(link);
    }
}