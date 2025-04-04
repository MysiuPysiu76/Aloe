package com.example.aloe.files.permissions;

import java.util.List;

public interface Permissions {

    List<String> getPermissionsList();

    List<Boolean> loadPermissions();

    void savePermissions(List<Boolean> permissionsList);
}