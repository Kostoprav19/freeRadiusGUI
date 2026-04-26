package lv.freeradiusgui.services;

import java.util.ArrayList;
import java.util.List;
import lv.freeradiusgui.domain.Role;
import lv.freeradiusgui.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired private RoleRepository roleRepository;

    @Override
    public List<Role> getAll() {
        List<Role> result = new ArrayList<>();
        roleRepository.findAll().forEach(result::add);
        return result;
    }

    @Override
    public Role getByName(String name) {
        if (name == null || name.isEmpty()) return null;
        return roleRepository.findByName(name);
    }

    @Override
    public Role getById(Integer id) {
        if (id == null || id < 0) return null;
        return roleRepository.findById(id).orElse(null);
    }
}
