package com.example.aloe.files.permissions;

import java.util.List;

/**
 * Represents a generic interface for managing file permissions.
 * <p>
 * This interface provides a uniform way to handle file permissions using a list-based
 * abstraction, where each permission is represented as a {@code boolean} in a fixed order.
 * Implementations may map these booleans to system-specific permission models (e.g., POSIX, ACL).
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * Permissions permissions = new POSIXPermissions(new File("example.txt"));
 * List<Boolean> current = permissions.loadPermissions();
 * current.set(0, true); // Grant OWNER_READ
 * permissions.savePermissions(current);
 * }</pre>
 *
 * @see POSIXPermissions
 * @see ACLPermissions
 * @since 1.7.9
 */
public interface Permissions {

    /**
     * Returns a list of supported permission identifiers.
     * <p>
     * Each element represents a permission name and corresponds to a boolean
     * index in the permission values used by {@link #loadPermissions()} and {@link #savePermissions(List)}.
     *
     * @return a list of permission names in a fixed and consistent order
     */
    List<String> getPermissionsList();

    /**
     * Loads the current file permissions and returns them as a list of boolean values.
     * <p>
     * Each boolean indicates whether the corresponding permission from {@link #getPermissionsList()}
     * is currently granted.
     *
     * @return list of booleans representing the state of each permission
     */
    List<Boolean> loadPermissions();

    /**
     * Saves the specified permission states to the target file or resource.
     * <p>
     * The list must be in the same order as provided by {@link #getPermissionsList()}.
     *
     * @param permissionsList list of booleans indicating which permissions to grant
     */
    void savePermissions(List<Boolean> permissionsList);
}
