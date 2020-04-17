package edu.uw.covidsafe.permissions;


import android.Manifest;

import androidx.annotation.NonNull;
import androidx.annotation.StringRes;

import com.example.covidsafe.R;

public enum CovidSafePermission {

    BluetoothPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, R.string.blueetooth_require_permission),


    ;

    @StringRes
    private final int permissionRationaleResId;

    private final String[] permissions;

    CovidSafePermission(@NonNull String permission, @StringRes int permissionRationaleResId) {
        this.permissions = new String[] {permission};
        this.permissionRationaleResId = permissionRationaleResId;
    }

    CovidSafePermission(@NonNull String[] permissions, @StringRes int permissionRationaleResId) {
        this.permissions = permissions;
        this.permissionRationaleResId = permissionRationaleResId;
    }

    public static CovidSafePermission getPermissionFromOrdinal(int ordinal) {
        CovidSafePermission[] values = CovidSafePermission.values();
        if (ordinal < 0 || ordinal >= values.length) {
            return null;
        }
        return values[ordinal];
    }

    /**
     * Return an String resource reference for permission ratinonale.
     *
     * @return {@code Integer}
     */
    @StringRes
    public int getPermissionRationaleResId() {
        return permissionRationaleResId;
    }

    /**
     * Array of {@code Manifest.permission } name to request.
     *
     * @return array {@code String} of permission
     */
    public String[] getPermissions() {
        return permissions;
    }

}
