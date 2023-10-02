# PRO-TIMER
[![Release][release-badge]][release]
[![Issues][git-issue-badge]][git-issue-url]
[![Twitter][twitter-badge]][twitter-url]
[![Buy-me-A-Coffee][coffee-cdn]][coffee-url]

**Technology Used**

[![Java 8.0][java8-badge]][java-download-url]
[![Groovy 4.0][groovy4-badge]][groovy-download-url]
![JavaFX 8.0][javafx8-badge]
![YAML][yaml-badge]

Pro-Timer records the time for the project that you're currently working on and groups them based on the date. So that you can find the amount of time spent on the project.

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
 - Shows the timesheet in a graph-based manner

### CHANGE_PROJECT
 - Choose the existing project
 - Recent histories are stored inside the registry
   `Computer\HKEY_CURRENT_USER\Software\JavaSoft\Prefs\pro-timer\entry`

### Addon
 - You can also directly open the **Project-Stats.yml** file where you can find the timesheet.

## Installation

To run Pro-Timer, you'll need the following prerequisites:
- Java 11+
- JavaFX 15+
- Groovy 4.0+

Follow these steps to install and run the application:

1. Download and install Java from [here](https://adoptium.net/temurin/releases/?version=8).
2. Download and install JavaFX from [here](<JavaFX download URL>).
3. Download and install Groovy from [here](https://groovy.apache.org/download.html).
4. Clone this repository: `git clone https://github.com/cmsk-jav/ProTimer.git`.
5. Change directory to the project folder: `cd ProTimer`.
6. Run the application: `./run.sh` (Linux/Mac) or `./run.bat` (Windows).

   Ensure that you replace `<JavaFX download URL>` with the actual URL for downloading JavaFX.

## Usage

Pro-Timer is designed to be intuitive and user-friendly. Here's a brief guide on how to use its main features:

1. **Starting a Timer**:
   - Select a project from the list.
   - Click the "Start Timer" button to begin tracking time for the selected project.

2. **Stopping a Timer**:
   - To stop the timer, click the "Stop Timer" button.
   - The elapsed time will be recorded for the project.

3. **Resetting a Project**:
   - If you need to reset the time data for a project, go to the "Project Settings" menu.
   - Click on the "Reset Project" option. Please note that this action is irreversible.

4. **Viewing Project Statistics**:
   - You can check the total time spent on a project by selecting it from the list and clicking the "Project Stats" button.
   - This will display a breakdown of time spent per day and the overall project total.

5. **Switching Projects**:
   - To switch between different projects, use the "Change Project" option.
   - Recent project history is conveniently stored for quick access.

These are the basic steps to get started with Pro-Timer. Feel free to explore the application and use the "Addon" section to access the detailed timesheet data in the **Project-Stats.yml** file.


## Developer Notes

- The application requires Java 11+, JavaFX 15+, and Groovy 4.0+ to run.
- (Any other developer-specific notes go here)

## Credits

[![Install4J][install4j-cdn]][install4j-home]

> [Install4J](https://www.ej-technologies.com/products/install4j/overview.html): I would like to thank **Install4J** for giving me an **Open-Source license** for their build tool, which I can easily create an installer for multiple platforms.

> [Time icons](https://www.flaticon.com/free-icons/time): Time icons created by Freepik - Flaticon

## Troubleshooting

If you encounter any issues while using Pro-Timer, you can refer to the following troubleshooting tips to resolve common problems:

**1. Timer Not Starting:**
   - Ensure that you have selected a project before starting the timer.
   - Check if you have the required dependencies (Java, JavaFX, Groovy) installed.
   - Restart the application if the issue persists.

**2. Unable to Stop Timer:**
   - Click the "Stop Timer" button again if it doesn't respond initially.
   - Check for any background processes or system resource constraints that might be causing delays.

**3. Project Data Loss:**
   - Be cautious when using the "Reset Project" option, as it permanently deletes project time data.
   - Always back up important data before resetting a project.

**4. Graph Generation Issues:**
   - If you encounter problems with the graph generation feature, note that it's marked as "UNDER DEVELOPMENT." Check for updates in future releases.

If you encounter an issue that is not covered here or if the problem persists, you can [report it on our GitHub Issues page](https://github.com/cmsk-jav/ProTimer/issues). We'll do our best to assist you in resolving the problem.


## Support

If you like our project, please consider buying us a coffee.

Thank you for your support!

[![Buy Me A Coffee](https://cdn.buymeacoffee.com/buttons/v2/default-yellow.png)](https://www.buymeacoffee.com/cmsuresh/protimer)

## Contribution Guidelines

We welcome contributions to Pro-Timer from the open-source community. If you'd like to contribute, please follow these guidelines:

**Reporting Bugs:**
   - If you encounter a bug or issue with the application, please [open an issue](https://github.com/cmsk-jav/ProTimer/issues) on our GitHub repository.
   - Provide detailed information about the problem, including steps to reproduce it and your environment (e.g., operating system, Java version).

**Suggesting Features:**
   - Have an idea for a new feature or improvement? You can [create a feature request](https://github.com/cmsk-jav/ProTimer/issues) on GitHub.
   - Describe the feature in detail, including its purpose and potential benefits.

**Submitting Pull Requests:**
   - If you'd like to contribute code, please fork the repository and create a pull request (PR) with your changes.
   - Ensure that your code follows our coding standards and is well-documented.
   - We'll review your PR and work with you to merge it into the project.

By contributing to Pro-Timer, you're helping us make the application better for everyone. We appreciate your support and contributions to the project.


## Conclusion

Thank you for your support, and kudos to all the open-source developers who make projects like these possible!

[release-badge]: https://img.shields.io/github/v/release/cmsk-jav/ProTimer
[release]: https://github.com/cmsk-jav/ProTimer/releases/latest
[git-issue-badge]: https://img.shields.io/github/issues/cmsk-jav/ProTimer
[git-issue-url]: https://github.com/cmsk-jav/ProTimer/issues
[java8-badge]: https://img.shields.io/badge/Java-8.0-blue
[java-download-url]: https://adoptium.net/temurin/releases/?version=8
[groovy4-badge]: https://img.shields.io/badge/Groovy-4.0-blue
[groovy-download-url]: https://groovy.apache.org/download.html
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

