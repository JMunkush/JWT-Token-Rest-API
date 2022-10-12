package com.myspring.app.services;

import com.myspring.app.entities.User;

import java.util.List;

public interface UserService {

    List<User> getAll();

    boolean save(User user);

    boolean deleteById(Long id);

    User getOneById(Long id);

    boolean update(Long id, User user);
}
