# HelloWorldMAD26

## Members
- Sergio Pastor Mesas (bt0385) ([GitHub](https://github.com/bt0385))
- Gabriel Gutiérrez Fuentes (bt0055) ([GitHub](https://github.com/gabgutf))

## Links
GitHub:
- Repository: https://github.com/bt0385/HelloWorldMAD26
- Releases: https://github.com/bt0385/HelloWorldMAD26/releases

Sharepoint:
- Landing page: https://upm365.sharepoint.com/sites/MAD26GS/SitePages/Home.aspx
- Tracking: https://upm365.sharepoint.com/sites/MAD26GS/SitePages/Tracking.aspx

## Description
This repository contains a basic Android app, which contains three Activities
and a basic navigation system to switch between them. The code is based on
Android Studio's "Empty Views Activity" template.

The objective of this repository is to act as a foundation for further
application development.

## Changelog

### Week 1 ([release](https://github.com/bt0385/HelloWorldMAD26/releases/tag/w1.2026))
- Created Sharepoint page.
- Created GitHub repository.
- Created Hello World app.

### Week 2 ([release](https://github.com/bt0385/HelloWorldMAD26/releases/tag/w2.2026))
- Created `SecondActivity` and `ThirdActivity`.
- Added basic navigation system between activities.

### Week 3 ([release](https://github.com/bt0385/HelloWorldMAD26/releases/tag/w3.2026))
- Added capability to show latitude and longitude of current location in `MainActivity`.
- Added logging in `MainActivity`, `SecondActivity` and `ThirdActivity`.
- Created `OpenStretMapsActivity`, which shows in a map, with a marker, your current location.
- Added markers in UPM buildings (see in OpenStreetMaps).

### Week 4 ([release](https://github.com/bt0385/HelloWorldMAD26/releases/tag/w4.2026))
- Created new activity (`LandingActivity`) for users launching the app for the first time.
- Modified `MainActivity` with a text welcoming the user and the `SecondActivity` button now letting you go to `ProfileActivity`.
- Modified `SecondActivity`, now called `ProfileActivity` so that the user can see and change his/her profile parameters.
- Implemented a "Shared Preferences" persistence system to save the data inputed by the user in the `LandingActivity` and `ProfileActivity`.
- Added a drawer menu in the `MainActivity` and `ProfileActivity` for better navigation.
- IMPORTANT: Now the activities finish when the user leaves them. To go back, use the side menu or the buttons.

### Week 5 ([release](https://github.com/bt0385/HelloWorldMAD26/releases/tag/w5.2026))
Implemented the following changes:
- Implemented mission system.
    - Generates missions around the user.
    - Checks the location of the user.
    - Clears the mission and notifies the user if they are close enough.
- Refactoring and code cleanup.
  - Added `AppActivityState`, which stores common state shared by most activities.
  - Implemented `PermissionManager` to simplify permission requests.
  - Added `NavigationActivityHelper` to manage the navigation drawer.
  - Implemented `UserLocationManager` and `LocationActivityHelper` to simplify location management.

Fixed the following issues:
- [#11](https://github.com/bt0385/HelloWorldMAD26/issues/11) `OpenStreetMapActivity` does not show current location unless launched from `MainActivity`.
- [#12](https://github.com/bt0385/HelloWorldMAD26/issues/12) Cannot navigate out of `OpenStreetMapActivity` without closing app.
- [#13](https://github.com/bt0385/HelloWorldMAD26/issues/13) Create utility class for navigation drawer.
- [#14](https://github.com/bt0385/HelloWorldMAD26/issues/14) Remove negative age check from `LandingActivity`.
- [#15](https://github.com/bt0385/HelloWorldMAD26/issues/15) Mission system.
- [#16](https://github.com/bt0385/HelloWorldMAD26/issues/16) Show current mission on map.
- [#17](https://github.com/bt0385/HelloWorldMAD26/issues/17) Show mission location on `MainActivity`.
- [#18](https://github.com/bt0385/HelloWorldMAD26/issues/18) Create state class.

Related pull requests:
- [#19](https://github.com/bt0385/HelloWorldMAD26/pull/19) Week 5 Mission Generator
- [#20](https://github.com/bt0385/HelloWorldMAD26/pull/20) Refactor location, navigation and permission systems into helper classes
- [#21](https://github.com/bt0385/HelloWorldMAD26/pull/21) Merge week 5 changes to master

## Screenshots of the App

| ![App view](readme/LandingActivity.jpeg "Landing activity") | ![App view](readme/MainActivity.jpeg "Main activity") | ![App view](readme/OpenStreetMapsActivity.png "OpenStreetMaps activity") | ![App view](readme/ProfileActivity.jpeg "Profile activity") | ![App view](readme/ThirdActivity.png "Third activity") |
|:----------------------------------------------------:|:----------------------------------------------------:|:------------------------------------------------------------------------:|:--------------------------------------------------------:|:-------------------------------------------------------:
|                   *Landing Activity*                    |                   *Main Activity*                    |                          *OpenStreetMaps Activity*                       |                    *Second Activity*                     |                  *Third Activity*                      |
