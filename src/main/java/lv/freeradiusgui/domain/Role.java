package lv.freeradiusgui.domain;

import javax.persistence.*;

/**
 * Created by Dan on 03.05.2016.
 */
@Entity
@Table(name = "roles")
public class Role {

    public static final String ROLE_USER = "USER";
    public static final String ROLE_MODERATOR = "MODERATOR";
    public static final String ROLE_ADMIN = "ADMIN";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "role", nullable = false)
    private String role;

    public Role() {}

    public Role(String role) {
        this.role = role;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
