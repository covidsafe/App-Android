package edu.uw.covidsafe.gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import edu.uw.covidsafe.utils.Constants;
import edu.uw.covidsafe.utils.Utils;

public class GpsUtils {

    public static LocationListener[] locListeners = new LocationListener[2];

    public static class LocationListener implements android.location.LocationListener {

        String provider;
        Context cxt;

        public LocationListener(String provider, Context cxt) {
            this.provider = provider;
            this.cxt = cxt;
        }

        @Override
        public void onLocationChanged(Location location) {
            Log.e("gps","onlocationchanged");
            Log.e("gps", location.getLatitude()+","+location.getLongitude());
            Utils.gpsLogToDatabase(cxt, location);
        }

        @Override
        public void onProviderDisabled(String provider) { }

        @Override
        public void onProviderEnabled(String provider) { }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) { }
    }

    public static void haltGps() {
        if (Constants.mLocationManager != null) {
            try {
                Constants.mLocationManager.removeUpdates(locListeners[0]);
                Constants.mLocationManager.removeUpdates(locListeners[1]);
            } catch (Exception ex) {
                Log.e("logme", "fail to remove location listners, ignore", ex);
            }
        }
    }

    private static void initializeLocationManager(Context cxt) {
        Log.e("logme", "initializeLocationManager");
        if (Constants.mLocationManager == null) {
            Log.e("logme", "initializeLocationManager2");
            Constants.mLocationManager = (LocationManager) cxt.getSystemService(Context.LOCATION_SERVICE);
        }
    }

    public static Location getLastLocation(Context cxt) {
        Log.e("gps","get last known location");
        LocationManager locationManager = (LocationManager) cxt.getSystemService(Context.LOCATION_SERVICE);
        try {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                Log.e("gps","got last known location "+loc.getLatitude()+","+loc.getLongitude());
                return loc;
            }
            loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                Log.e("gps","got last known location "+loc.getLatitude()+","+loc.getLongitude());
                return loc;
            }
            loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                Log.e("gps","got last known location "+loc.getLatitude()+","+loc.getLongitude());
                return loc;
            }
            return null;
        }
        catch (SecurityException e) {
            Log.e("err",e.getMessage());
            return null;
        }
        catch (Exception e) {
            Log.e("err",e.getMessage());
            return null;
        }
    }

    public static double getCoarseGpsCoord(double d, int precision) {
        double shift = (1 << 16); //16 is some number that 1 << 32 > 180 and bigger than maximum precision value that we are using
        return (getCoarseGpsCoordHelper(d + shift, precision) - shift);
    }

    public static double getCoarseGpsCoordHelper(double d, int precision) {
//        Log.e("ERR ",d+","+precision);
        long bits = Double.doubleToLongBits(d);
//        Log.e("ERR ",d+","+precision);

        long negative = bits & (1L << 63);
        int exponent = (int) ((bits >> 52) & 0x7ffL);
        long mantissa = bits & 0xfffffffffffffL;

        int mantissaLog = 52;
        if (exponent == 0) {
            mantissaLog = (int) Utils.log(mantissa, 2);
        } else {
            mantissa = mantissa | (1L << 52);
        }

        int precisionShift = mantissaLog + exponent - 1075;

        int maskLength = Math.min(precision + precisionShift, 52);

        mantissa = mantissa >> (52 - maskLength);
        mantissa = mantissa << (52 - maskLength);

        if (mantissa == 0) {
            exponent = 0;
        }
        long result = negative |
                ((long) (exponent & 0x7ffL) << 52) |
                (mantissa & 0xfffffffffffffL);

        return Double.longBitsToDouble(result);
    }

    public static void startGps(Context cxt) {
        initializeLocationManager(cxt);
        getLastLocation(cxt);
        try {
            Log.e("gps", "start gps");

            if (Constants.DEBUG) {
                Log.e("gps", "start gps "+Constants.GPS_TIME_INTERVAL_IN_MILLISECONDS_DEBUG);
                Log.e("gps", "start gps "+Constants.GPS_LOCATION_INTERVAL_IN_METERS_DEBUG);
                locListeners[0] = new LocationListener(LocationManager.NETWORK_PROVIDER, cxt);
                Constants.mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, Constants.GPS_TIME_INTERVAL_IN_MILLISECONDS_DEBUG, Constants.GPS_LOCATION_INTERVAL_IN_METERS_DEBUG,
                        locListeners[0]);

                locListeners[1] = new LocationListener(LocationManager.GPS_PROVIDER, cxt);
                Constants.mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, Constants.GPS_TIME_INTERVAL_IN_MILLISECONDS_DEBUG, Constants.GPS_LOCATION_INTERVAL_IN_METERS_DEBUG,
                        locListeners[1]);
            }
            else {
                locListeners[0] = new LocationListener(LocationManager.NETWORK_PROVIDER, cxt);
                Constants.mLocationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER, Constants.GPS_TIME_INTERVAL_IN_MILLISECONDS, Constants.GPS_LOCATION_INTERVAL_IN_METERS,
                        locListeners[0]);

                locListeners[1] = new LocationListener(LocationManager.GPS_PROVIDER, cxt);
                Constants.mLocationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER, Constants.GPS_TIME_INTERVAL_IN_MILLISECONDS, Constants.GPS_LOCATION_INTERVAL_IN_METERS,
                        locListeners[1]);
            }

        } catch (java.lang.SecurityException ex) {
            Log.e("logme", "fail to request location update, ignore", ex);
        } catch (IllegalArgumentException ex) {
            Log.e("logme", "gps provider does not exist " + ex.getMessage());
        } catch (Exception e) {
            Log.e("logme", e.getMessage());
        }
    }
}
