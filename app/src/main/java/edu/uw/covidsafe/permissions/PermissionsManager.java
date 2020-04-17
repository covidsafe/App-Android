package edu.uw.covidsafe.permissions;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.annotation.UiThread;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Lifecycle;

import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Set;


public class PermissionsManager implements PermissionsHandler {

    private boolean mIsPermissionRequested;
    private PermissionsProvider mPermissionsProvider;
    private final Set<CovidSafePermission> mPermissionsThatHaveShownRationaleDialog = new HashSet<>();


    public PermissionsManager() {
    }

    public void checkAndRequestPermission(CovidSafePermission covidSafePermission,
                                          @NonNull FragmentActivity activity,
                                          @NonNull PermissionsCallback permissionsCallback) {

        if (validatePermissionRequested(activity, covidSafePermission, permissionsCallback)) {
            return;
        }

        if (checkPermission(covidSafePermission, activity)) {
            permissionsCallback.onPermissionGranted(covidSafePermission);
            return;
        }

        mPermissionsProvider = new PermissionsProvider(activity, covidSafePermission, permissionsCallback);
        requestPermission();
    }

    /**
     * In case of Device Orientation, Activity/PermissionsCallback gets recreated. re-assigning back, for calling
     * callbacks of newly created Activity/PermissionsCallback.
     */
    public boolean validatePermissionRequested(FragmentActivity activity,
                                               CovidSafePermission covidSafePermission,
                                               PermissionsCallback permissionsCallback) {
        if (mIsPermissionRequested) {
            mPermissionsProvider.permissionsCallback = permissionsCallback;
            mPermissionsProvider.activityRef = new WeakReference<>(activity);
            mPermissionsProvider.covidSafePermission = covidSafePermission;
            return true;
        }
        return false;
    }

    public static boolean checkPermission(CovidSafePermission covidSafePermission,
                                          @NonNull Context context) {

        for (String permission : covidSafePermission.getPermissions()) {
            if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void checkGrantedPermissions(int[] grantResults, CovidSafePermission covidSafePermission) {
        PermissionsCallback permissionCallback = mPermissionsProvider.getPermissionCallback();
        if (permissionCallback == null) {
            return;
        }

        if (verifyPermissions(grantResults)) {
            mIsPermissionRequested = false;
            permissionCallback.onPermissionGranted(covidSafePermission);
        } else if (mPermissionsProvider.shouldShowPermissionRationale()) {
            //We need to show some rationale to the user as in why we need this particular permission

        } else {
            mIsPermissionRequested = false;
            final PermissionsCallback callback = mPermissionsProvider.getPermissionCallback();

            if (callback != null) {
                if (mPermissionsThatHaveShownRationaleDialog.contains(covidSafePermission)) {
                    mPermissionsThatHaveShownRationaleDialog.remove(covidSafePermission);
                    callback.onPermissionDeniedFromRationaleDialog(covidSafePermission);
                } else {
                    callback.onPermissionPermanentlyDenied(covidSafePermission);
                }
            }
        }
    }

    private boolean verifyPermissions(int[] grantResults) {
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    /**
     * Wrapper of requesting/checking permission.
     */
    private static final class PermissionsProvider {
        private WeakReference<FragmentActivity> activityRef;
        private CovidSafePermission covidSafePermission;
        private PermissionsCallback permissionsCallback;

        PermissionsProvider(FragmentActivity activity, CovidSafePermission covidSafePermission,
                            PermissionsCallback permissionsCallback) {
            activityRef = new WeakReference<>(activity);
            this.covidSafePermission = covidSafePermission;
            this.permissionsCallback = permissionsCallback;
        }

        boolean isActivityValid() {
            FragmentActivity activity = activityRef.get();
            if (activity != null) {
                Lifecycle.State state = activity.getLifecycle().getCurrentState();
                return state.isAtLeast(Lifecycle.State.INITIALIZED);
            }
            return false;
        }

        boolean shouldShowPermissionRationale() {
            if (!isActivityValid()) {
                return false;
            }

            for (String permission : covidSafePermission.getPermissions()) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        activityRef.get(), permission)) {
                    return true;
                }
            }
            return false;
        }

        boolean requestPermission() {
            if (!isActivityValid()) {
                return false;
            }

            ActivityCompat.requestPermissions(activityRef.get(), covidSafePermission.getPermissions(),
                    covidSafePermission.ordinal());
            return true;
        }

        public FragmentActivity getActivity() {
            if (!isActivityValid()) {
                return null;
            }
            return activityRef.get();
        }

        PermissionsCallback getPermissionCallback() {
            return permissionsCallback;
        }
    }

    public interface PermissionsCallback {
        /**
         * A permission has been granted.
         *
         * @param covidSafePermission the permission
         */
        @UiThread
        void onPermissionGranted(CovidSafePermission covidSafePermission);

        /**
         * The user has denied a permission, but from the rationale dialog, so it may be possible to ask again.
         *
         * @param covidSafePermission the permission
         */
        @UiThread
        void onPermissionDeniedFromRationaleDialog(CovidSafePermission covidSafePermission);

        /**
         * The user has permanently denied a permission, and it can only be re-allowed in Settings. This case should be
         * handled gracefully by either asking the user to open Settings or falling back in functionality.
         *
         * @param covidSafePermission the permission
         */
        @UiThread
        void onPermissionPermanentlyDenied(CovidSafePermission covidSafePermission);
    }

    @Override
    public void requestPermission() {
        if (mPermissionsProvider.requestPermission()) {
            // If the request for permission succeeded, record that we are awaiting the response
            mIsPermissionRequested = true;
        }
    }

    @Override
    public void onPermissionDeclinedFromDialog(CovidSafePermission covidSafePermission) {
        mIsPermissionRequested = false;
        PermissionsCallback permissionCallback = mPermissionsProvider.getPermissionCallback();
        if (permissionCallback != null) {
            permissionCallback.onPermissionDeniedFromRationaleDialog(covidSafePermission);
        }
    }
}
