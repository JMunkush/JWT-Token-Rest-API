package com.myspring.app.services.impl;

import com.myspring.app.entities.Role;
import com.myspring.app.repositories.RoleRepository;
import com.myspring.app.services.RoleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;


    @Override
    public List<Role> getRoles() {
        log.info("roles has successfully loaded");
        return roleRepository.findAll();
    }

    @Override
    public Role getRoleById(Long id) {
        Role roleFromDb = roleRepository.findById(id).get();
        if( roleFromDb != null ){
            log.info("role with id: {} has successfully loaded", id);
            return roleFromDb;
        }

        else {
            log.error("role with id: {} not found", id);
            return null;
        }
    }


}
