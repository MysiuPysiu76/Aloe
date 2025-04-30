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

/**
 * A class for managing file access control lists (ACLs) on systems that support the {@link AclFileAttributeView}.
 * <p>
 * This implementation allows reading and writing detailed permissions for a specific user using ACL entries.
 * The permissions are mapped to {@link AclEntryPermission} values and handled as a list of booleans.
 * </p>
 *
 * <p>
 * Usage:
 * <ul>
 *   <li>Set the target file via constructor</li>
 *   <li>Set the target user via {@link #setUserName(String)}</li>
 *   <li>Use {@link #loadPermissions()} and {@link #savePermissions(List)} for permission management</li>
 * </ul>
 * </p>
 *
 * The permissions list includes:
 * <pre>
 * READ_DATA, WRITE_DATA, APPEND_DATA, READ_NAMED_ATTRS, WRITE_NAMED_ATTRS,
 * EXECUTE, DELETE_CHILD, READ_ATTRIBUTES, WRITE_ATTRIBUTES,
 * DELETE, READ_ACL, WRITE_ACL, WRITE_OWNER, SYNCHRONIZE
 * </pre>
 *
 * @see Permissions
 * @see POSIXPermissions
 * @see AclFileAttributeView
 * @see AclEntryPermission
 *
 * @since 1.8.0
 */
public class ACLPermissions implements Permissions {

    final private File file;
    private String userName;

    /**
     * Constructs an {@code ACLPermissions} instance for the given file or directory.
     *
     * @param file the target file or directory
     */
    public ACLPermissions(File file) {
        this.file = file;
    }

    /**
     * Gets the name of the user whose ACL permissions are managed.
     *
     * @return the username
     */
    public String getUserName() {
        return this.userName;
    }

    /**
     * Sets the name of the user whose ACL permissions should be managed.
     *
     * @param userName the target username
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * Returns a list of supported ACL permission names.
     *
     * @return a list of permission names in {@link AclEntryPermission} order
     */
    @Override
    public List<String> getPermissionsList() {
        return List.of("READ_DATA", "WRITE_DATA", "APPEND_DATA", "READ_NAMED_ATTRS", "WRITE_NAMED_ATTRS", "EXECUTE", "DELETE_CHILD", "READ_ATTRIBUTES", "WRITE_ATTRIBUTES", "DELETE", "READ_ACL", "WRITE_ACL", "WRITE_OWNER", "SYNCHRONIZE");
    }

    /**
     * Loads the ACL permissions for the configured user on the associated file.
     * The permissions are returned as a list of booleans corresponding to {@link #getPermissionsList()}.
     *
     * @return a list of booleans indicating granted permissions
     * @throws NullPointerException if the username is not set
     */
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

    /**
     * Saves the specified permissions to the ACL for the configured user on the associated file.
     * Any existing ACL entries for that user are removed and replaced.
     *
     * @param permissionsList a list of booleans indicating which permissions should be granted
     * @throws NullPointerException if the username is not set
     */
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
