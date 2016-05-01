package lv.freeradiusgui.domain;
import org.hibernate.annotations.Type;

import java.time.LocalDateTime;

import javax.persistence.*;
import java.util.ArrayList;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name = "login")
    private String login;

    @Column(name = "password")
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "surname")
    private String surname;

    @Column(name = "enail")
    private String email;

    @Column(name = "created")
    @Type(type = "lv.freeradiusgui.utils.CustomLocalDateTime")
    private LocalDateTime creationDate;

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

    public User(String login, String password, String name, String surname, String email, LocalDateTime creationDate) {
        this.login = login;
        this.password = password;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.creationDate = creationDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return login.equals(user.login);

    }

    @Override
    public int hashCode() {
        return login.hashCode();
    }

    public static class UserBuilder {

        private String login;
        private String password;
        private String name;
        private String surname;
        private String email;
        private LocalDateTime creationDate;

        private UserBuilder(){

        }

        public static UserBuilder createUser(){
            return new UserBuilder();
        }


        public User build(){
            return new User(login, password, name, surname, email, creationDate);
        }


        public UserBuilder withLogin(String login){
            this.login = login;
            return this;
        }
        public UserBuilder withPassword(String password){
            this.password = password;
            return this;
        }

        public UserBuilder withName(String name){
            this.name = name;
            return this;
        }
        public UserBuilder withSurname(String surname){
            this.surname = surname;
            return this;
        }

        public UserBuilder withEmail(String email){
            this.email = email;
            return this;
        }
        public UserBuilder withCreationDate(LocalDateTime creationDate){
            this.creationDate = creationDate;
            return this;
        }

    }
}
