# CovidSafe

[![CircleCI](https://circleci.com/gh/covidsafe/App-Android.svg?style=svg)](https://circleci.com/gh/covidsafe/App-Android)

Video demo
=======

Video demo of current capabilities

https://www.youtube.com/watch?v=2fPpLJ3MQpc

Rotating Bluetooth IDs in realtime

https://www.youtube.com/watch?v=9EkTWEod3Bk

Logging GPS/BLE and sending data to a local server

https://www.youtube.com/watch?v=0iWl9uaQ5Ds

Working commit for demo (7f55e5b405233620b1db0d709ba23fcd23e35e6c)

- The Bluetooth IDs change about once every 10 seconds.
- The phones need to be in Bluetooth contact for 30 seconds (3 different IDs) for it to count as an exposure.

The following sequence of steps can be executed to produce an end-to-end demo of the functionality of the app:
1) Install the app on two phones.
2) Turn on location and Bluetooth. Just leave them within Bluetooth range for about 30 seconds.
3) On phone A, submit a infection report. On phone B, pull down to refresh. You should now observe an exposure notification.
4) On phone A, pull down to refresh, observe that there are no notifications.
5) On phone B, submit an infection report.
6) On phone A, pull down to refresh, and observe that there is now an exposure notification.

A few key parameters:

- NetworkConstant.java contains the base URL for the backend endpoint you would want to change this.
- Constants.java encodes
- CDCExposureTimeInMinutesDebug: indicates 30 seconds is the threshold for exposure
- UUIDGenerationIntervalInSecondsDebug: indicates how frequently Bluetooth IDs are generated
- BluetoothScanIntervalInSecondsDebug: indicates how frequently the device scans for Bluetooth IDs (should be the same as UUIDGenerationIntervalInSecondsDebug)
- DEBUG can be set to true or false. The DEBUG mode uses the aforementioned parameters, and can be used for showing a demo. When DEBUG mode is false, the above parameters are altered. For example exposure time is defined as 10 minutes and IDs are generation every 15 minutes.
