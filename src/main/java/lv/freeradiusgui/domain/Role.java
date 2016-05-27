package lv.freeradiusgui.domain;

import javax.persistence.*;

/**
 * Created by Dan on 03.05.2016.
 */
@Entity
@Table(name = "roles")
public class Role {

    public static final String ROLE_USER = "ROLE_USER";
    public static final String ROLE_ADMIN = "ROLE_ADMIN";


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Integer id;

    @Column(name = "roleName", nullable = false)
    private String name;

    public Role() {}

    public Role(String role) {
        this.name = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !(o instanceof Role)) return false;

        Role role = (Role) o;
        return name != null ? name.equals(role.name) : role.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return name;
    }
}
