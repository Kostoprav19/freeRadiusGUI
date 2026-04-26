package lv.freeradiusgui.domain;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Join-row for the {@code accounts_roles} many-to-many. Holds only the role id; account id is
 * supplied by the {@link
 * org.springframework.data.relational.core.mapping.MappedCollection @MappedCollection} on {@link
 * Account}.
 */
@Table("accounts_roles")
public class AccountRoleRef {

    @Column("role_id")
    private Integer roleId;

    public AccountRoleRef() {}

    public AccountRoleRef(Integer roleId) {
        this.roleId = roleId;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AccountRoleRef)) return false;
        AccountRoleRef that = (AccountRoleRef) o;
        return roleId != null ? roleId.equals(that.roleId) : that.roleId == null;
    }

    @Override
    public int hashCode() {
        return roleId != null ? roleId.hashCode() : 0;
    }
}
