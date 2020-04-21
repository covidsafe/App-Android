# CovidSafe

[![CircleCI](https://circleci.com/gh/covidsafe/App-Android.svg?style=svg)](https://circleci.com/gh/covidsafe/App-Android)

CovidSafe is a tool built by doctors and researchers at the University of Washington with Microsoft volunteers to alert you about highly relevant public health announcements, exposure to COVID-19, and to assist contact tracing without compromising your personal privacy.

Here is a [video](https://www.youtube.com/watch?v=2fPpLJ3MQpc) demonstrating how users are notified of potential exposures and announcements<br/>

Our app also has a [symptom tracker and location log](https://www.youtube.com/watch?v=Pr1YNAiKmFg) which is useful aggregating information for public health officials and contact tracers<br/>

Health care workers and public health officials can send broadcast messages to bounded geographic regions using Google Sheets. [Video](https://www.youtube.com/watch?v=mweXe470Mrs)<br/>
For more details for how to do this, check out this [repo](https://github.com/covidsafe/hcp-tools)

To run an end-to-end demo of the app, please refer to [these instructions](https://github.com/covidsafe/App-Android/wiki/Running-the-app) in our wiki.

Our tool is built upon the following white paper:<br/>
**PACT: Privacy Sensitive Protocols and Mechanisms for Mobile Contact Tracing<br/>**
*Justin Chan, Dean Foster\*, Shyam Gollakota, Eric Horvitz\*, Joseph Jaeger\*, Sham Kakade\*, Tadayoshi Kohno, John Langford\*, Jonathan Larson, Sudheesh Singanamalla, Jacob Sunshine, Stefano Tessaro\**
\* Corresponding authors

Our tool sends out Bluetooth IDs that are randomized every 15 minutes [Video](https://www.youtube.com/watch?v=9EkTWEod3Bk)<br/>

[This video](https://www.youtube.com/watch?v=0iWl9uaQ5Ds) demonstrates an initial prototype logging GPS and Bluetooth and sending data to a local server<br/>
