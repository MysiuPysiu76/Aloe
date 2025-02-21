package com.example.aloe.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class POSIXPermissions implements Permissions {

    final private File file;
    private boolean recursively = false;

    public POSIXPermissions(File file) {
        this.file = file;
    }

    public boolean isRecursively() {
        return this.recursively;
    }

    public void setRecursively(boolean recursively) {
        this.recursively = recursively;
    }

    private Set<PosixFilePermission> convertToPermissions(List<Boolean> permissionsList) {
        List<String> permissions = getPermissionsList();
        Set<PosixFilePermission> permissionsSet = new HashSet<>();

        for (int i = 0; i < permissionsList.size(); i++) {
            if (permissionsList.get(i)) {
                permissionsSet.add(PosixFilePermission.valueOf(permissions.get(i)));
            }
        }
        return permissionsSet;
    }

    private void updateRecursively(File dir, List<Boolean> permissionsList) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File child : files) {
                try {
                    Set<PosixFilePermission> permissionsSet = convertToPermissions(permissionsList);
                    Files.setPosixFilePermissions(child.toPath(), permissionsSet);

                    if (child.isDirectory()) {
                        updateRecursively(child, permissionsList);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Error updating permissions for: " + child.getAbsolutePath(), e);
                }
            }
        }
    }

    @Override
    public List<String> getPermissionsList() {
        return List.of("OWNER_READ", "OWNER_WRITE", "OWNER_EXECUTE", "GROUP_READ", "GROUP_WRITE", "GROUP_EXECUTE", "OTHERS_READ", "OTHERS_WRITE", "OTHERS_EXECUTE");
    }

    @Override
    public List<Boolean> loadPermissions() {
        try {
            Set<PosixFilePermission> perms = Files.getPosixFilePermissions(this.file.toPath());
            List<String> permissions = getPermissionsList();
            return permissions.stream().map(p -> perms.contains(PosixFilePermission.valueOf(p))).collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void savePermissions(List<Boolean> permissionsList) {
        try {
            List<String> permissions = getPermissionsList();
            Set<PosixFilePermission> permissionsSet = new HashSet<>();

            for(int i = 0; i < permissionsList.size(); i++) {
                if(permissionsList.get(i)) permissionsSet.add(PosixFilePermission.valueOf(permissions.get(i)));
            }

            Files.setPosixFilePermissions(this.file.toPath(), permissionsSet);

            if (this.recursively && this.file.isDirectory()) {
                updateRecursively(this.file, permissionsList);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}