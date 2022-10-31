# PRO-TIMER
[![Release][release-badge]][release]
[![Issues][git-issue-badge]][git-issue-url]
[![Twitter][twitter-badge]][twitter-url]
[![Buy-me-A-Coffee][coffee-cdn]][coffee-url]

**Technology used**

[![ALT][java8-badge]][java-download-url]
[![ALT][groovy4-badge]][groovy-download-url]
![ALT][javafx8-badge]
![ALT][yaml-badge]

Pro-Timer records the time for the project that you're currently working on and groups them based on the date.
So that you can find the amount of time spent on the project

![ProTimer][pro-timer-img]

| ![ProTimer][pro-timer-img-win] | ![ProTimer][pro-timer-img-lin] | ![ProTimer][pro-timer-img-mac] |
|--------------------------------|--------------------------------|--------------------------------|

## Functions 
___
###  LOGGER
  - Once you select a project, Our application will check whether the pro-timer was already initiated or not.
    - If it wasn't, Then it initiates the pro-timer for the selected project
    
      **Note:** Initialization is nothing but configuring the pro-timer (**.pro-timer/Project-Stats.yml**)
  - Starting the timer will record current time and store it into the config file.
    Once stopped then the time differences will be calculated and stored as elapsed time.
    > It's possible to execute the timer multiple times and pro-timer will take care of that...
  
### RESET_PROJECT

 - Resetting the project will clear the timesheets from the config file ( **.pro-timer/Project-Stats.yml** )
   > **This is an irrecoverable task. Be cautious about that !!!**

### CALCULATE_TOTAL_TIME
 - Process the timesheet and extract the overall time that was spent on the selected project
 - The day and time are independent of each other. 
   > Consider that you have spent _20hrs_ in _3days_.  
   > The timer will still say that you have spent 
   > **3 DAY(S) 20:00:00 (HH:MM:SS)**
 
### GENERATE_GRAPH ***`UNDER DEVELOPMENT`***
 - Shows the timesheet in a graph based manner

### CHANGE_PROJECT
 - Choose the existing project
 - Recent histories are stored inside the registry
   `Computer\HKEY_CURRENT_USER\Software\JavaSoft\Prefs\pro-timer\entry`

### Addon
 - You can also directly open the **Project-Stats.yml** file where you can find the timesheet.

# Developer Notes
- Needs Java-11+, JavaFX15+ and Groovy 4.0+ requires to run this application

# Credits

[![Install4J][install4j-cdn]][install4j-home]

[//]: # (<a href="https://www.buymeacoffee.com/cmsuresh/protimer" target="_blank"><img src="https://www.ej-technologies.com/images/product_banners/install4j_medium.png" alt="Install4J" ></a>)

> https://www.ej-technologies.com/products/install4j/overview.html
>>I would like to thank **Install4J** for giving me an **Open-Source license**
for their build tool which I can easily create an installer for multiple platforms.

> <a href="https://www.flaticon.com/free-icons/time" title="time icons">Time icons created by Freepik - Flaticon</a>

---
> **If you like our project, please consider buying us a coffee.**
> 
> **Thank you for your support!**
> 
> <a href="https://www.buymeacoffee.com/cmsuresh/protimer" target="_blank"><img src="https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png" alt="Buy Me A Coffee" style="height: 60px !important;width: 217px !important;" ></a>
> 
## And kudos to all the Open-Source developers :-)


[release-badge]: https://img.shields.io/github/v/release/cmsk-jav/ProTimer
[release]: https://github.com/cmsk-jav/ProTimer/releases/latest
[git-issue-badge]: https://img.shields.io/github/issues/cmsk-jav/ProTimer
[git-issue-url]: https://github.com/cmsk-jav/ProTimer/issues
[java8-badge]: https://img.shields.io/badge/Java-8.0-blue
[java-download-url]: https://adoptium.net/temurin/releases/?version=8
[groovy4-badge]: https://img.shields.io/badge/Groovy-4.0-blue
[groovy-download-url]:https://groovy.apache.org/download.html
[javafx8-badge]: https://img.shields.io/badge/JavaFX-8.0-blue
[yaml-badge]: https://img.shields.io/badge/YAML-%20-brightgreen
[pro-timer-img]: src/main/resources/res/image/Protimer-snap.png
[twitter-badge]: https://img.shields.io/twitter/url?label=Follow%20%40cmskj&style=social&url=https%3A%2F%2Ftwitter.com%2Fcmskj
[twitter-url]: https://twitter.com/cmskj
[coffee-cdn]: https://img.shields.io/badge/Buy--Me--A--Coffee-%E2%98%BA%EF%B8%8F-orange
[coffee-url]: https://www.buymeacoffee.com/cmsuresh/protimer
[install4j-cdn]: https://www.ej-technologies.com/images/product_banners/install4j_medium.png
[install4j-home]: https://www.ej-technologies.com
[buy-me-coffee-cdn]: https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png
[pro-timer-img-win]: src/main/resources/res/image/protimer_windows.png
[pro-timer-img-lin]: src/main/resources/res/image/protimer_linux.png
[pro-timer-img-mac]: src/main/resources/res/image/protimer_mac.png

