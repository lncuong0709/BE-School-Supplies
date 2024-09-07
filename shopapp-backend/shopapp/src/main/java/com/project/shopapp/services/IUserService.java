package com.project.shopapp.services;

import com.project.shopapp.models.User;

import java.util.List;

public interface IUserService {
    List<User> findAll();
}
