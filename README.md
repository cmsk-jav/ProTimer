# PRO-TIMER
[![Release][release-badge]][release]
[![Issues][git-issue-badge]][git-issue-url]

**Technology used**

[![ALT][java8-badge]][java-download-url]
[![ALT][groovy4-badge]][groovy-download-url]
![ALT][javafx8-badge]
![ALT][yaml-badge]

Pro-Timer records the time for the project that you're currently working on and groups them based on the date.
So that you can find the amount of time spent on the project

![ProTimer][pro-timer-img]

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
- Java 8 comes with pre-build of JAVAFX library.
- As u all know Oracle removed JavaFX from JDK-11 onwards to pull out 
  non-core modules and make them stand up as independent module.
- If you guys want to run my application on Java 11 or greater, Then please 
  download and add the dependency separately.



[release-badge]: https://img.shields.io/github/v/release/suresh-jav/test
[release]: https://github.com/suresh-jav/test/releases/late
[git-issue-badge]: https://img.shields.io/github/issues/suresh-jav/test
[git-issue-url]: https://github.com/suresh-jav/test
[java8-badge]: https://img.shields.io/badge/Java-8.0-blue
[java-download-url]: https://adoptium.net/temurin/releases/?version=8
[groovy4-badge]: https://img.shields.io/badge/Groovy-4.0-blue
[groovy-download-url]:https://groovy.apache.org/download.html
[javafx8-badge]: https://img.shields.io/badge/JavaFX-8.0-blue
[yaml-badge]: https://img.shields.io/badge/YAML-%20-brightgreen
[pro-timer-img]: /src/res/image/Protimer-snap.png
