package lv.freeradiusgui.domain;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "account_id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "email")
    private String email;

    @Column(name = "created")
    @Type(type = "lv.freeradiusgui.utils.CustomLocalDateTime")
    private LocalDateTime creationDate;

    @Column(name = "enabled", nullable = false)
    @Type(type = "org.hibernate.type.NumericBooleanType")
    private boolean enabled;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "accounts_roles",
            joinColumns = {@JoinColumn(name = "account_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")})
    private Set<Role> role = new HashSet<Role>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public Set<Role> getRole() {
        return role;
    }

    public void setRole(Set<Role> role) {
        this.role = role;
    }

    public Account() {
    }

    public Account(String login, String password, String name, String surname, String email, LocalDateTime creationDate, boolean enabled) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.creationDate = creationDate;
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Account account = (Account) o;

        return login != null ? login.equals(account.login) : account.login == null;

    }

    @Override
    public int hashCode() {
        return login != null ? login.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + id +
                ", login='" + login + '\'' +
                ", password='" + password + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", creationDate=" + creationDate +
                ", enabled=" + enabled +
                ", role=" + role +
                '}';
    }

    public static class AccountBuilder {

        private String login;
        private String password;
        private String name;
        private String surname;
        private String email;
        private LocalDateTime creationDate;
        private boolean enabled;

        private AccountBuilder(){

        }

        public static AccountBuilder createUser(){
            return new AccountBuilder();
        }


        public Account build(){
            return new Account(login, password, name, surname, email, creationDate, enabled);
        }


        public AccountBuilder withLogin(String login){
            this.login = login;
            return this;
        }
        public AccountBuilder withPassword(String password){
            this.password = password;
            return this;
        }

        public AccountBuilder withName(String name){
            this.name = name;
            return this;
        }
        public AccountBuilder withSurname(String surname){
            this.surname = surname;
            return this;
        }

        public AccountBuilder withEmail(String email){
            this.email = email;
            return this;
        }
        public AccountBuilder withCreationDate(LocalDateTime creationDate){
            this.creationDate = creationDate;
            return this;
        }
        public AccountBuilder withEnabled(boolean enabled){
            this.enabled = enabled;
            return this;
        }

    }
}
