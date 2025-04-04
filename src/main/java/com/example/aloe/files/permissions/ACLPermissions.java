package com.example.aloe.files.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.attribute.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ACLPermissions implements Permissions {

    final private File file;
    private String userName;

    public ACLPermissions(File file) {
        this.file = file;
    }

    public String getUserName() {
        return this.userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public List<String> getPermissionsList() {
        return List.of("READ_DATA", "WRITE_DATA", "APPEND_DATA", "READ_NAMED_ATTRS", "WRITE_NAMED_ATTRS", "EXECUTE", "DELETE_CHILD", "READ_ATTRIBUTES", "WRITE_ATTRIBUTES", "DELETE", "READ_ACL", "WRITE_ACL", "WRITE_OWNER", "SYNCHRONIZE");
    }

    @Override
    public List<Boolean> loadPermissions() {
        if (this.userName == null) throw new NullPointerException("User name must be set");
        List<Boolean> permissions = new ArrayList<>();

        try {
            AclFileAttributeView view = Files.getFileAttributeView(this.file.toPath(), AclFileAttributeView.class);
            List<AclEntry> aclEntries = view.getAcl();

            for (int i = 0; i < AclEntryPermission.values().length; i++) {
                permissions.add(false);
            }

            for (AclEntry entry : aclEntries) {
                if (entry.principal().getName().equals(this.userName)) {
                    int i = 0;
                    for (AclEntryPermission perm : AclEntryPermission.values()) {
                        permissions.set(i, permissions.get(i) || entry.permissions().contains(perm));
                        i++;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return permissions;
    }

    @Override
    public void savePermissions(List<Boolean> permissionsList) {
        if (this.userName == null) throw new NullPointerException("User name must be set");

        List<String> permissionsNames = getPermissionsList();
        Set<AclEntryPermission> permissions = new HashSet<>();

        try {
            UserPrincipal user = FileSystems.getDefault().getUserPrincipalLookupService().lookupPrincipalByName(this.userName);
            AclFileAttributeView view = Files.getFileAttributeView(this.file.toPath(), AclFileAttributeView.class);
            List<AclEntry> aclList = view.getAcl();

            aclList.removeIf(entry -> entry.principal().equals(user));

            for (byte i = 0; i < permissionsNames.size(); i++) {
                if (permissionsList.get(i)) {
                    permissions.add(AclEntryPermission.valueOf(permissionsNames.get(i)));
                }
            }

            AclEntry entry = AclEntry.newBuilder()
                    .setType(AclEntryType.ALLOW)
                    .setPrincipal(user)
                    .setPermissions(permissions)
                    .build();

            aclList.add(entry);
            view.setAcl(aclList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
