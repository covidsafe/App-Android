# CovidSafe

[![CircleCI](https://circleci.com/gh/covidsafe/App-Android.svg?style=svg)](https://circleci.com/gh/covidsafe/App-Android)

CovidSafe is a tool built by doctors and researchers at the University of Washington with Microsoft volunteers to alert you about highly relevant public health announcements, exposure to COVID-19, and to assist contact tracing without compromising your personal privacy.

Here is a video demonstrating how users are notified of potential exposures and announcements:
https://www.youtube.com/watch?v=2fPpLJ3MQpc

Our app also has a symptom tracker and location log which is useful aggregating information for public health officials and contact tracers:

https://www.youtube.com/watch?v=Pr1YNAiKmFg

Health care workers and public health officials can send broadcast messages to bounded geographic regions using Google Sheets like so:
https://www.youtube.com/watch?v=mweXe470Mrs
Details for how to do this can be found in the wiki

Our tool is built upon the following white paper:

PACT: Privacy Sensitive Protocols and Mechanisms for Mobile Contact Tracing<br/>
Justin Chan, Dean Foster*, Shyam Gollakota, Eric Horvitz*, Joseph Jaeger*, Sham Kakade*, Tadayoshi Kohno, John Langford*, Jonathan Larson, Sudheesh Singanamalla, Jacob Sunshine, Stefano Tessaro*
* Corresponding authors

Our tool sends out Bluetooth IDs that are randomized every 15 minutes:

https://www.youtube.com/watch?v=9EkTWEod3Bk

This video demonstrates an initial prototype logging GPS and Bluetooth and sending data to a local server:

https://www.youtube.com/watch?v=0iWl9uaQ5Ds

