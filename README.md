<p align="center">
A light client for Facebook with a modern look (Material Design).
_This app is based on Toffeed by JakeLane (https://github.com/JakeLane/Toffeed). Thanks JakeLane_

 
<img src="http://i.imgur.com/VctN6iC.png"> </p>

[![MaterialFBook on fdroid.org](http://i.imgur.com/nyp9kHq.png "Download from fdroid.org")](https://f-droid.org/app/me.zeeroooo.materialfb)

## Changelog

**Version 1.1:**
- Pinch to zoom
- Navbar with Facebook theme colour
- Code cleaned
- Less ram consumption (compared with Toffeed)


**Version 1.2:**
- Option to hide "Download Messenger"
- Deleted two permissions (vibrate and run after boot)
- New user agent

**Version 1.3:**
- Fab menu with better look (its blue like Facebook theme now).
- Better look on CircleImage from profile.
- Updated dependencies
- RSS deleted (No notifications = no work = more space for nothing)
- Moved "jump to up" buttom from left bar to fab menu

**Version 1.4:**
- Added options to navigation menui (events, groups, close app, log out, and more).
- Theme engine (Dark Theme, Material Dark Theme, Material Theme and Facebook Mobile Theme). Thx: rignaneseleo from Slimsocial For Facebook for the theme engine.
- Added option to hide buttons of navigation menu (hide messages or hide groups, etc).

**Version 1.5:**
- Added notifications (messages and common activity). Ported from FaceSlim THX: @indywidualny
- Updated useragent.

**Version 1.6:**
- Reworked notifications. Now we have button to "All notifications" or "All messages".
- Reworked and reorganized settings.
- Added new interval for notifications refresh: "Instant: For a better chat". 

**Version 1.7:**
- Updated AdvancedWebView to 3.0.
- Fixed "Exit app" button in navigation menu.
- Added "Back" button in navigation menu.
- Fixed FC when close the app. (Thank you again, indywidualny).
- Minor badge update.

**Version 1.8:**
- Code cleaned (app consume less cpu/ram).
- App dont start at the boot (Yeah, less permissions now).
- Improved notification Messages icon (when notification is displayed). Looks better now.
- Added Messages icon in action bar  (idea of @GorranKurd).
- Corrected wrong string.
- Added app themes (Yellow, Lime, Red, Green, Black, Grey, Purple, Light Blue, Pink and default blue color of Facebook) (if you want more colours, tell me).
- Fixed bug in Api 17 (Android 4.2.x). Now start correctly... or in AVD with 4.2.2 yes hahah.

**Version 1.8.1:**
- Bye "Download Messenger" ads. (Its automatic no more "Hide download messenger" button).
- Maybe white screen in messages was fixed. I dont know because i havent this bug. Anyway, test the app and tell me.

**Version 1.9:**
- Added "Save data" mode.
- Translated: Spanish (complete), Kurdish (complete), Chinesse (incomplet), Portuguesse (incomplet), Italian (incomplet).
- Added option to check updates (Go to "more and credits" option in Settings and download the latest apk)
- Reworked material theme in web
- Translucent status bar in navigation menu (api >19 = KitKat+)
- Replaced CheckBox with Switch. Looks better now :)

**Version 1.9.1:**
- Added Orange color
- Added "Double vibration" option
- Improved Black theme
- Improved "Save Data" mode. Now dont update the photo cover and profile.
- Improved themes. Now we have the theme colour when you press an item.

**Version 1.9.2:**
- New icon.
- Fixed wrong translation in the spanish.
- Translated to French by Sebastien Durussel.
- Fixed "More and Credits" bug in Marshmallow.
- Fixed wrong translation in Kurdish.
- Extended time (from 3s to 10s) when you long press the FAB. Its good when the FAB hide the "Send" button in chats.

**Version 1.9.3:**
- Fixed French missed translations.
- Translated to Polish by alekksander.

**Version 2.0.0:**
- Added "Flashlight as led" option. We can use the camera led as notification led :)
- Translated to Russian by maxel85.
- Added forgot lines for double vibration (i really forget it).
- Hide toolbar in scroll down.
- Now we can select and copy text (still in beta, works only in comments).

**Version 2.1.0:**
- Translated to German (by fabsmusicjunkie) - Vietnamese (by ngoisaosang) - Romanian (by nishimura-san).
- Added black shadow to User name in navigation view for white photo covers.
- Updated Facebook SDK.
- Improved "Log Out" button.
- Maybe Nougat is fully supported now.
- Hide News Feed content.
- Improved unread messages notice.
- Improved toolbar icons for black theme.
- Improved exit of Chrome Custom Tabs.

**Version 2.2.0:**
- Updated Polish translation.
- Updated libraries.
- Improved animation when external link open Chrome Custom Tabs.
- Reworked "More and credits" page.

**Version 2.3.0:**
- Translated to Portuguese by abacate123.
- Implemented option to force all traffic to Orbot (Tor).
- Added new theme: Google Play Green.
- Improved status bar color.
- Added shadow behind the icon.

**Version 2.3.5:**
- Updated Polish.
- Updated Vietnamese.
- Translucent status bar on ALL the app.

**Version 2.4.0:**
- Translated to Hungrian.
- Added BottomNavigationView.
- Options to configure BottomnavigationView (only icons, only text, etc).
- UI reworked.
- Improved the update of the photo cover and profile.

**Version 2.4.5:**
- Deleted BottomNavigationView and configurations.
- Improved the update of the photo cover and profile x2.
- Cleaned code.
- Updated Google libreries.
- Corrected typo in English translation.

**Version 2.5.0:**
- Deleted Orbot.
- Fixed status bar issues on KitKat.
- Improved status bar transparency in Lollipop+.
- Added icons for all display sizes.
- Improved notifications system.
- FAB disabled in Messages because it hide the "send" button.
- FAB disabled when we see a image in full screen.
- Fixed white page after go back from external link. (Yes, bye 2 times back button and reload the page)
- Deleted Chrome Custom Tabs. (now we use the browser, its better because most of the browsers have AdBlocks, lower data modes, etc).
- Renamed "Save data" to "Lower data mode".
- New feature: "Clear cache on exit", same as Settings > Apps > MaterialFBook > Clear Cache, but automatic.
- Fixed double vibration issues.

**Version 2.5.1:**
- Fixed FC in "More and credits".

**Version 2.6.0:**
- Now the app hide the birthdays from "events" and feed. #25
- EXPERIMENTAL: Switched from HandlerRunnable to AlarmManager to schedule notification´s sync ==> The NotificationsService.java start only to check if a notification are avaible, the rest of the time it will be stopped. (Configure the time in Settings > Notifications > Time interval -- Highly recommended put it into 5 mins).
- "Search" fixed. #37
- Click on profile picture to open the profile page fixed.
- Translated to Serbian by PoP992.
- Fixed toolbar/keyboard issues on KitKat devices. #36

**Version 2.6.1:**
- Fixed FC when the device is not connected and AlarmManager try to refresh the notifications. #40

**Version 2.7.0:**
- Notification now display the profile pic (messages and notifications) = BIIIIIIG THANKS to indywidualny!!!
- Battery improvement: When the device is not connected to the internet, the app dont try to sync the notifications or messages (same result as disable the "Notifications" button in Settings > Notifications).

**Version 2.7.5:**
- Now we can choose between "Facebook Notifications", "Facebook Messages" or both.
- Re-added "Hide News Feed content" option. #46
- Some fixes with notifications.
- Changes on the background of SwipeViewLayout.
- Updated some translations.

**Version 2.8.0:**
- Little changes when share an item (longpressing).
- Fixed top bar in events (bar who have "post" and "cancel" button).
- Fixed redirect to News Feed when we click in notification.
- "Hide news feed content" has been changed to "Messages mode" (load in the messages Facebook´s site instead of a blank page).
- Corrected some words in Spanish and English translation #51.
- Added a PhotoViewer who can load images and gifs (from Giphy, Gifspace, Tumblr and url´s with ".gif") **See notes**
- Fixed "Exit" button.
- Added option to show or hide the FloatingActionButton.
- Now we can choose enable or disable the vibration (two times or one time) for notifications and messages separately.
- Now we can choose enable or disable the led light for notifications and messages separately.
- Improved image quality by sharing.

**Version 2.8.5:**
- Changed name from "Messages only" to "Start in messages" #52.
- Minor changes.
- Fixed FC when user try share an image in Marshmallow +.
- Notifications improved (In messages it will display the name of the user).
- Corrected a word in Spanish.

**Version 2.9.0:**
- Code cleaned.
- Changes in the WebView.
- Bugfixes.
- Fullscreen on videos.
- Fixed wrong sizes on PhotoViewer (better image quality).
- Able to download archives from groups without open external browser.

**Version 3.0.0:**
- Translated to Filipino by klayd krta.
- Translated to Arabic by akkk33.
- Updated Vietnamese.
- Fixed video uploads.
- Changed the url when click on notification (no more "Most recent").
- Fixed all the issues with "Start in messages" enabled.
- Added 13 new Web Themes.
- Added: "Hide recently commented" option.

**Version 3.1.0:**
- Handled Facebook profiles links #57.
- Fixed share in Nougat 7.1.1 #57.
- Notifications improvements.
- Bugfixes.
- Added: "Start URL" option #52.

**Version 3.2.0:**
- Fixes on MaterialBlack theme.
- Updated French translation.
- Notifications improvements: Now the chat group image is displayed in the notification too.
- Fixed FC when the user goes to FB Page > about > send mail.
- Improved Web Themes.
- Updated Hebrew translation.
- Added bookmarks #60.

**Version 3.3.0:**
- Translated to Dutch.
- Translated to Turkish by Baran Aydin.
- Updated some translations.
- Auto-rotate the screen when video is playing (no more only-landscape mode).
- Ability to download documents attached in chats inside MFB.
- Added: Option to share bookmarks.
- Added: QuickShare option for videos and images (to share images from gallery) _(ONLY FOR BETA TESTERS)_.
- Deleted unnecessary Facebook permissions #67.
- Handled public profiles links #69.
- Fixed missing post button #70.
- Added support for Phygee and Imgur images and gifs.
- Updated Serbian translation.
- Bugfixes.

**Version 3.4.0:**
- Start URL fixed. #77
- Disabled Swipe Refresh when you are writing a state, posting a photo or sharing your location. #56
- Translated to Danish by mhtorp.
- Translated to Indonesian by Aditya Prasetyo.
- Updated some translations.
- Changed text color from black to white in "post status" with MaterialAmoled Web Theme. #83
- Now the video player save the position when you minimize the app and continue it when the app is reopened. #82
- Enabled QuickShare for photos and videos.

**Version 3.4.1:**
- Bugfixes and minor improvements.

**Version 3.4.5:**
- Fixed notifications issues. #98
- Updated some translations.
- Switched to the old package id to fix F-Droid issues.

**Version 3.6.0:**
- Bugfixes.
- Added support for emojis in notifications. #84
- Added a blacklist to notifications.
- Added support for Oreo devices.
- Translations updates.
- Switched from AlarmManager to JobScheduler (less battery usage).

**Version 3.6.1:**
- Bugfixes.

**Version 3.6.5:**
- Fixed crash on JB.
- Reworked schedule system (own api based on JobScheduler API21+ and AlarmManager API19-).

**Version 3.6.6:**
- Fixed some bugs on the notification system.

**Version 3.6.7:**
- Minor changes.
- Updated translations: spanish and russian.

**Version 3.6.8:**
- Fixed notification timeout error.
- Changes on messsages.

**Version 3.7.0:**
- Updated languages (added Czech).
- Notification changes.
- Force close fixed.
- Some changes in the UI.
- Language selector.

**Version 3.7.5:**
- Improved album photos quality.
- Fixed mbasic issue.
- Fixed wrong color in external link title.

**Version 3.7.7:**
- Changed photo viewer background.
- Implemented ACRA to send crash reports via email.
- Fixed messages notification.

**Version 3.7.8:**
- Bugfixes.

**Version 3.8.0:**
- Bugfixes.
- Text scale selector.

**Version 3.8.1:**
- Amoled theme fixes.
- Updated Spanish translation.

**Version 3.8.5:**
- Bugfixes.
- Notifications improvements.
- Enabled resizable activities for Oreo+.
- Added support for API 29 (Pie).

**Version 3.8.6:**
- Bugfixes.

**Version 3.8.7:**
- Facebook albums enhancements #204 .
- Updated serbian translation

**Version 4.0.0:**
- Added color picker.
- Notifications reworked.
- Translated to Amharic by Nigusu.
- Translated to Sardinian by asereze.
- Translated to Breton by Irriep Nala Novram.
- Translated to Ukranian by sfilmak.

**Version 4.0.1:**
- Fixed some visual glitches.
- Copy & paste feature restored.

**Version 4.0.2:**
- Fixed facebook menu bar hiding when option is disabled.
- Fixed language selector.
- Disabled swipe to refresh feature when keyboard is shown (#217).

**Version 4.0.3:**
- Fixed some black theme glitches.
- Fixed crashes on Android 10+.
- Notification fixes.
- Temporarily disabled copy & paste feature as it\'s causing crashes.

**Notes:**
_- Maybe some gifs cant be loaded because i need to add the url to the code. Report it as issue with the Facebook´s page link or website._

**Translations:** https://www.transifex.com/yaron/materialfbook/translate/


## Credits:

- Folio for Facebook by creativetrendsapps.
- FaceSlim  by indywidualny.
- Toffeed  by JakeLane.
- SlimSocial for Facebook by rignaneseleo.
- Glide by bumptech.
- jsoup by Jonathan Hedley.
- acra by ACRA.
- All people who translated the app (more details in "More and Credits" and Transifex)


## Screens:
 <img src="http://i.imgur.com/thUumVk.png" width="30%"> <img src="http://i.imgur.com/Lv12hwg.png" width="30%"> <img src="http://i.imgur.com/OZYVtbA.png" width="30%"> <img src="http://i.imgur.com/wEngW4M.png" width="30%"> <img src="http://i.imgur.com/o2phVsQ.png" width="30%"> <img src="http://i.imgur.com/4SvUcBL.png" width="30%"> <img src="http://i.imgur.com/hp0aNj5.png" width="30%"> 
