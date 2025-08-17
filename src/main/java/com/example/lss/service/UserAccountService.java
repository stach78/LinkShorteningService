package com.example.lss.service;

import com.example.lss.dto.UserRegisterRequest;
import com.example.lss.entity.UserAccount;
import com.example.lss.repo.UrlMappingRepository;
import com.example.lss.repo.UserAccountRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserAccountService {

    private final UserAccountRepository users;
    private final UrlMappingRepository links;
    private final PasswordEncoder encoder;

    public UserAccountService(UserAccountRepository users,
                              UrlMappingRepository links,
                              PasswordEncoder encoder) {
        this.users = users;
        this.links = links;
        this.encoder = encoder;
    }

    @Transactional
    public UserAccount registerUser(UserRegisterRequest req) {
        if (users.existsByUsername(req.getUsername()))
            throw new IllegalArgumentException("username already taken");

        UserAccount u = new UserAccount();
        u.setUsername(req.getUsername());
        u.setPasswordHash(encoder.encode(req.getPassword()));
        try {
            return users.save(u);
        } catch (DataIntegrityViolationException e) {
            // for race condition with unique constraint
            throw new IllegalArgumentException("username already taken");
        }
    }

    @Transactional
    public void deleteUserAndLinks(String username) {
        links.deleteByOwner_Username(username);
        users.deleteByUsername(username);
    }
}
