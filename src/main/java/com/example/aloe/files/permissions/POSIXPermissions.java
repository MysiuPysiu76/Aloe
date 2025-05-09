package com.example.aloe.files.permissions;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A class for managing POSIX file permissions using a boolean list representation.
 * <p>
 * This class allows loading and saving POSIX file permissions to and from a given {@link File}.
 * It supports recursive permission application on directory trees.
 * The permissions are mapped to the standard {@link PosixFilePermission} values.
 * </p>
 *
 * Example permissions order:
 * <pre>
 * OWNER_READ, OWNER_WRITE, OWNER_EXECUTE,
 * GROUP_READ, GROUP_WRITE, GROUP_EXECUTE,
 * OTHERS_READ, OTHERS_WRITE, OTHERS_EXECUTE
 * </pre>
 *
 * @see Permissions
 * @see ACLPermissions
 * @since 1.7.9
 */
public class POSIXPermissions implements Permissions {

    final private File file;
    private boolean recursively = false;

    /**
     * Constructs a POSIXPermissions instance for the specified file or directory.
     *
     * @param file the target file or directory
     */
    public POSIXPermissions(File file) {
        this.file = file;
    }

    /**
     * Checks whether permission changes should be applied recursively to all subdirectories and files.
     *
     * @return {@code true} if recursive mode is enabled, {@code false} otherwise
     */
    public boolean isRecursively() {
        return this.recursively;
    }

    /**
     * Enables or disables recursive permission changes.
     *
     * @param recursively {@code true} to enable recursive permission updates, {@code false} to disable
     */
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

    /**
     * Converts a list of booleans representing permissions into a set of {@link PosixFilePermission}.
     *
     * @param permissionsList a list of booleans, where each index corresponds to a specific permission
     * @return a set of enabled POSIX file permissions
     */
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

    /**
     * Returns the list of POSIX permission names used for mapping.
     *
     * @return list of permission names in standard POSIX order
     */
    @Override
    public List<String> getPermissionsList() {
        return List.of("OWNER_READ", "OWNER_WRITE", "OWNER_EXECUTE", "GROUP_READ", "GROUP_WRITE", "GROUP_EXECUTE", "OTHERS_READ", "OTHERS_WRITE", "OTHERS_EXECUTE");
    }

    /**
     * Loads the current permissions of the file and returns them as a list of booleans.
     *
     * @return a list of booleans indicating which permissions are set
     */
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

    /**
     * Saves the specified permissions to the file. If recursive mode is enabled and the file is a directory,
     * it applies the permissions recursively to all files and subdirectories.
     *
     * @param permissionsList a list of booleans representing desired permissions
     */
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
