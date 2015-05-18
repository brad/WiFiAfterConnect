WiFiAfterConnect
================

[![Build Status](https://travis-ci.org/brad/WiFiAfterConnect.svg?branch=lollipop)](https://travis-ci.org/brad/WiFiAfterConnect) [![Coverage Status](https://coveralls.io/repos/brad/WiFiAfterConnect/badge.svg?branch=lollipop)](https://coveralls.io/r/brad/WiFiAfterConnect?branch=lollipop)

Android app automating logins into Captive Portals often encountered on free and many non-free Wi-Fi hostpots.

Install from [Google Play](https://play.google.com/store/apps/details?id=com.wifiafterconnect).

If you are feeling adventurous and wish to try out new features, you can see a list
of recent debug builds [here](https://wifiafterconnect.s3.amazonaws.com/index.html)


Supported Portals
-----------------

Most standard captive portals with a simple form are supported, both free and
those requiring authentication. In addition to this, there is support for captive
portals that do non-standard things, those are:

* AT&amp;T Wi-Fi (free)
* The Club, in many US malls owned by [GGP](http://ggp.com/) (free)
* [WiFi in de trein](http://www.ns.nl/reizigers/reisinformatie/informatie/trein--en-stationsvoorzieningen/voorzieningen-in-de-trein.html#draadloos-internet-in-de-trein) (WiFi in Dutch NS trains) (free)
* Hilton Honors
* [Cisco Web Authentication](http://www.cisco.com/c/en/us/td/docs/wireless/controller/7-3/configuration/guide/b_cg73/b_wlc-cg_chapter_01011.html)
* [NetNearU](http://nnu.com/)
* [UniFi](http://community.ubnt.com/unifi)
* [Wandering WiFi](http://www.wanderingwifi.com/)
* Motorola WiNG5.x


Building
--------

Make sure you have OpenJDK 7 or Oracle JDK 7-8

1. Make sure you have the [Android SDK](https://developer.android.com/sdk/installing/index.html) installed
2. In the source directory, generate build scripts with

   ```
   android update project --path . --name WiFiAfterConnect
   ```

3. Build using `ant debug` (or `ant release`)


License
-------

This software is released under the [Apache 2.0 License](LICENSE).
