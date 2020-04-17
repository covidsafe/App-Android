package edu.uw.covidsafe.permissions;

public interface PermissionsHandler {
    void requestPermission();
    void onPermissionDeclinedFromDialog(CovidSafePermission covidSafePermission);
}