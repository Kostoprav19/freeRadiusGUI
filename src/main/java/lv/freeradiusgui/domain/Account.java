package lv.freeradiusgui.domain;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.format.annotation.DateTimeFormat;

@Table("accounts")
public class Account {

    @Id
    @Column("account_id")
    private Integer id;

    @Column("login")
    private String login;

    @Column("password")
    private String password;

    @Column("name")
    private String name;

    @Column("surname")
    private String surname;

    @Column("email")
    private String email;

    @Column("created")
    @DateTimeFormat(pattern = "dd.MM.yyyy HH:mm")
    private LocalDateTime creationDate;

    @Column("enabled")
    private boolean enabled;

    @MappedCollection(idColumn = "account_id")
    private Set<AccountRoleRef> roleRefs = new HashSet<>();

    @Transient private Set<Role> roles = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = (roles == null) ? new HashSet<>() : roles;
        rebuildRoleRefs();
    }

    public void addRole(Role role) {
        if (role == null) return;
        this.roles.add(role);
        if (role.getId() != null) {
            this.roleRefs.add(new AccountRoleRef(role.getId()));
        }
    }

    public Set<AccountRoleRef> getRoleRefs() {
        return roleRefs;
    }

    public void setRoleRefs(Set<AccountRoleRef> roleRefs) {
        this.roleRefs = (roleRefs == null) ? new HashSet<>() : roleRefs;
    }

    /** Rebuilds {@link #roleRefs} from {@link #roles} ids. Call after role ids are populated. */
    public void rebuildRoleRefs() {
        Set<AccountRoleRef> refs = new HashSet<>();
        for (Role r : this.roles) {
            if (r != null && r.getId() != null) {
                refs.add(new AccountRoleRef(r.getId()));
            }
        }
        this.roleRefs = refs;
    }

    public Account() {}

    public Account(
            String login,
            String password,
            String name,
            String surname,
            String email,
            LocalDateTime creationDate,
            boolean enabled) {
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
        if (o == null || !(o instanceof Account)) return false;

        Account account = (Account) o;

        return login != null ? login.equals(account.login) : account.login == null;
    }

    @Override
    public int hashCode() {
        return login != null ? login.hashCode() : 0;
    }

    @Override
    public String toString() {
        StringBuilder rolesStr = new StringBuilder();
        if (roles != null) {
            for (Role role : this.roles) {
                if (role != null)
                    rolesStr.append("[ id:" + role.getId() + " | name:" + role.getName() + "]");
            }
        }
        return "Account{"
                + "id="
                + id
                + ", login='"
                + login
                + '\''
                + ", password='"
                + password
                + '\''
                + ", name='"
                + name
                + '\''
                + ", surname='"
                + surname
                + '\''
                + ", email='"
                + email
                + '\''
                + ", creationDate="
                + creationDate
                + ", enabled="
                + enabled
                + ", roles="
                + rolesStr.toString()
                + '}';
    }

    public static class AccountBuilder {

        private String login;
        private String password;
        private String name;
        private String surname;
        private String email;
        private LocalDateTime creationDate;
        private boolean enabled;

        private AccountBuilder() {}

        public static AccountBuilder createUser() {
            return new AccountBuilder();
        }

        public Account build() {
            return new Account(login, password, name, surname, email, creationDate, enabled);
        }

        public AccountBuilder withLogin(String login) {
            this.login = login;
            return this;
        }

        public AccountBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public AccountBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public AccountBuilder withSurname(String surname) {
            this.surname = surname;
            return this;
        }

        public AccountBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public AccountBuilder withCreationDate(LocalDateTime creationDate) {
            this.creationDate = creationDate;
            return this;
        }

        public AccountBuilder withEnabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }
    }
}
