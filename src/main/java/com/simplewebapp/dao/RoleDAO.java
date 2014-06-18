package com.simplewebapp.dao;

import com.simplewebapp.model.Role;

public interface RoleDAO {

    public Role getRole(String name);

    public void addRole(Role role);

    public void removeRole(int id);
}