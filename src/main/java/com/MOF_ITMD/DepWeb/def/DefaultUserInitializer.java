package com.MOF_ITMD.DepWeb.def;

import com.MOF_ITMD.DepWeb.models.users.ERole;
import com.MOF_ITMD.DepWeb.models.users.Role;
import com.MOF_ITMD.DepWeb.models.users.User;
import com.MOF_ITMD.DepWeb.repository.RoleRepository;
import com.MOF_ITMD.DepWeb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class DefaultUserInitializer implements CommandLineRunner {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        createRoles();
        createDefaultUser();
    }

    private void createRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role(ERole.ROLE_SUPER_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_ADMIN));
            roleRepository.save(new Role(ERole.ROLE_MODERATOR));
            roleRepository.save(new Role(ERole.ROLE_USER));
            System.out.println("System Roles created!.");
        }

    }

    private void createDefaultUser() {
        if (userRepository.count() == 0) {
            User superAdmin = new User();
            superAdmin.setUsername("super");
            superAdmin.setEmail("super@gmail.com");
            superAdmin.setPassword(passwordEncoder.encode("super123"));

            Optional<Role> superAdminRole = roleRepository.findByName(ERole.ROLE_SUPER_ADMIN);
            if (superAdminRole.isPresent()) {
                Role superUserRole = superAdminRole.get();
                Set<Role> roles = new HashSet<>();
                roles.add(superUserRole);
                superAdmin.setRoles(roles);

                userRepository.save(superAdmin);

                System.out.println("Super Admin created!.");
            } else {
                System.out.println("Role 'SUPER_ADMIN' not found. Super Admin not created.");
            }

            User defaultUser = new User();
            defaultUser.setUsername("admin");
            defaultUser.setEmail("admin@gmail.com");
            defaultUser.setPassword(passwordEncoder.encode("admin123"));

            Optional<Role> defaultUserRole = roleRepository.findByName(ERole.ROLE_ADMIN);
            if (defaultUserRole.isPresent()) {
                Role firstUserRole = defaultUserRole.get();
                Set<Role> roles = new HashSet<>();
                roles.add(firstUserRole);
                defaultUser.setRoles(roles);

                userRepository.save(defaultUser);

                System.out.println("Default user created.");
            } else {
                System.out.println("Role 'ADMIN' not found. Default user not created.");
            }
        }
    }

}
