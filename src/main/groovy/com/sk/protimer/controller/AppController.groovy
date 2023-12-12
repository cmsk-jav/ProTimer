/**
 '########::'########:::'#######:::::::::::'########:'####:'##::::'##:'########:'########::
 ##.... ##: ##.... ##:'##.... ##::::::::::... ##..::. ##:: ###::'###: ##.....:: ##.... ##:
 ##:::: ##: ##:::: ##: ##:::: ##::::::::::::: ##::::: ##:: ####'####: ##::::::: ##:::: ##:
 ########:: ########:: ##:::: ##:'#######:::: ##::::: ##:: ## ### ##: ######::: ########::
 ##.....::: ##.. ##::: ##:::: ##:........:::: ##::::: ##:: ##. #: ##: ##...:::: ##.. ##:::
 ##:::::::: ##::. ##:: ##:::: ##::::::::::::: ##::::: ##:: ##:.:: ##: ##::::::: ##::. ##::
 ##:::::::: ##:::. ##:. #######:::::::::::::: ##::::'####: ##:::: ##: ########: ##:::. ##:
 ..:::::::::..:::::..:::.......:::::::::::::::..:::::....::..:::::..::........::..:::::..::

 @AUTHOR Suresh
 @EMAIL cmsuresh@zohomail.in

 */

package com.sk.protimer.controller

import com.sk.protimer.Entry
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.media.Media
import javafx.scene.media.MediaPlayer
import javafx.stage.Stage
import org.jnativehook.GlobalScreen

import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier
import java.util.prefs.Preferences
import java.util.stream.Collectors
import java.util.stream.IntStream

class AppController {
    //Config folder and file Name which will get created to store time entries for each project
    final def folderPattern = ".pro-timer"
    final def yamlTemplateName = "ProjectStats.yml"
    final def fileTemplateName = "ProEntries.txt"
    //Name of the registry Node. Used to store recent history
    final def registryNode = "pro-timer/entry"
    Preferences rootPreferenceNode
    //Stores Project path which is currently running
    File projectPath
    //Controller to read from and write into Yaml files
    YamlController yamlController
    //Controller to read from and write into Plain files
    FileController fileController
    //Status to check if logger running or not...
    AtomicBoolean loggerStarted = new AtomicBoolean()
    //Checking System Tray support...
    static AtomicBoolean isSystemTraySupport = new AtomicBoolean(false)
    //Recent history Buffer
    ArrayList<String> recentHistoryList = new ArrayList<>()
    //Calculating Overall Elapsed time
    Duration overAllTimeElapsed
    //TimerCount
    long seconds = 0
    long lastElapsedSeconds
    //Frequency of the recent-project history. Only the top N histories will get entered into registry
    final int maxRecentHistory = 5
    //Filtering the project which is already initialized by ProTimer
    FilenameFilter projectFolderFilter = (File dir,String filename) -> {
        return filename == folderPattern
    }

    FilenameFilter projectFileFilter = (File dir, String filename)->{
        return filename == yamlTemplateName
    }
    //Executing required task after every 1sec of delay
    Timer timer
    Supplier<Timer> timerSupplier = ()-> new Timer("Pro-Timer")
    Supplier<TimerTask> timerTaskSupplier = ()->new TimerTask() {
        void run() {
            if (loggerStarted.get()){
                seconds++
                long h = (long)(seconds/3600)
                long m = (long) ((seconds%3600)/60)
                long s =  (seconds%3600)%60
                Platform.runLater(()->
                        Entry.fxmlController.localTimer_lbl.setText("${YamlController.formatDigit(h)}:${YamlController.formatDigit(m)}:${YamlController.formatDigit(s)}")
                )
            }
            /*else{
                println("Logger Stopped..")
                timer.cancel()
            }*/

        }
    }
    //Executing required task after every 1sec of delay
    Timer dateRefresh
    Supplier<Timer> dateRefreshSupplier = ()-> new Timer("Date-Refresh")
    Supplier<TimerTask> dateRefreshTaskSupplier = ()->new TimerTask() {
        void run() {
            println("Running")
            //Update Date in the UI
            Platform.runLater(()->Entry.fxmlController.updateDate())
            //Once update complete, We need to schedule for next refresh
            if (dateRefresh!=null) dateRefresh.cancel()
            scheduleRefresh()
        }
    }
    //Add some space while displaying project relative path and also ">" will be converted as Arrow-mark unicode by SF-PRO font
    final String spacing = ">  "
    //APP version
    final String APP_VERSION = "V 1.1"
    //For shortcut triggered notifier
    Media notificationMediaPause = new Media(getClass().getResource(Entry.resourcePath+"/media/pause.wav").toURI().toString());
    Media notificationMediaResume = new Media(getClass().getResource(Entry.resourcePath+"/media/resume.wav").toURI().toString());
    Media notificationMediaNoProject = new Media(getClass().getResource(Entry.resourcePath+"/media/empty.wav").toURI().toString());
    /**
     * creating app-controller instance
     */
    AppController(){
        fetchRegistry()
    }
    def scheduleRefresh(){
        dateRefresh = dateRefreshSupplier.get()
        //DateCalc
        LocalDateTime current = LocalDateTime.now()
        LocalDateTime nextDay = current.plusDays(1)
                                .minusHours(current.getHour())
                                .minusMinutes(current.getMinute())
                                .minusSeconds(current.getSecond())
                                .minusNanos(current.getSecond())
        //End time is exclusive and we didn't take care of nano-seconds. So we can add one seconds to get currect result/
        //of-course this gives < 1 sec delay. But that's not a big deal
        long delay = Duration.between(current,nextDay.plusSeconds(1)).getSeconds()
        println("CurrentDate: $current")
        println("next Date: $nextDay")
        println("Delay: $delay S")
        //----
//        dateRefresh.scheduleAtFixedRate(dateRefreshTaskSupplier.get(),delay,2000L)
        dateRefresh.schedule(dateRefreshTaskSupplier.get(),delay*1000)
    }
    /**
     *
     *Load recent history from the registry
     */
    def fetchRegistry(){
        rootPreferenceNode =Preferences.userRoot().node(registryNode)
        if (Preferences.userRoot().nodeExists(registryNode)){
            // println "Node already exists..."
            String[]  keys = rootPreferenceNode.keys()
            if (keys.size()==0){
                // println "No preferences available.."
                recentHistoryList = new ArrayList<>()
                return
            }
            recentHistoryList = Arrays.stream(keys).limit(maxRecentHistory)
                    .map(key->rootPreferenceNode.get(key.toString(),"INVALID"))
                    .collect(Collectors.toList())
        }else{
            // println "Node not exists..Creating New Node..."
            rootPreferenceNode.flush()
        }
    }
    /**
     * Making the application draggable. As this application uses Custom Window, It won't inherit windows draggable feature..
     * We externally need to add this ability
     *
     * @param node  - On which node we're giving this ability
     * @param stage - Window that u need to move with respective to your scene
     */

    void enableApplicationDrag(final Node node, final Stage stage){
        double Mx,My, Sx,Sy
        node.setOnMousePressed(e -> {
            Mx = e.getX();
            My = e.getY();
        })
        node.setOnMouseDragged(e -> {
            Sx = e.getScreenX() - Mx;
            Sy = e.getScreenY() - My;
            stage.setX(Sx)
            stage.setY(Sy)
        })
    }

    /**
     * Load Project from the source and validate the project whether pro-timer initiated or not.
     * @param filePath - Absolute path of the file that needs to validate
     */
    def  loadProject(final String filePath) {
        //Before loading another project first check whether timer running or not...
        File folder = new File(filePath)
        if (folder.exists() && folder.directory){
            File[] folderList = folder.listFiles(projectFolderFilter)
            if (folderList!=null && folderList.size() > 0 ){
                File project = folderList[0]
                File[] projectConfig = project.listFiles(projectFileFilter)
                if (projectConfig!=null && projectConfig.size() > 0){
                    projectPath = folder
                    String tmp = folder.getAbsolutePath()
                    Entry.fxmlController.projectTitle_lbl.setText(spacing + tmp.substring(tmp.lastIndexOf(File.separator)+1))
                    Entry.fxmlController.projectLocation_lbl.setText(tmp)
                    changeRecentListOrder(folder)
                    createController()
                    resetElapsedTimer()
                    resetRunningTimer()
                    // println "Project path was located.. path: ${projectPath.getAbsolutePath()}"
                    //Change application state
                    Entry.fxmlController.setDisableState(false, 1.0)
                    return
                }
                else{
                    //No need to take care of the disable state. New ProjectConfig take care of that.
                    Entry.fxmlController.createConfigAlert(folder, -1, Entry.primaryStage.getScene().getWindow())
                    return
                }

            }else{
                // println "Pro-timer wasn't initiated for this project"
                Entry.fxmlController.createWarning(1, Entry.primaryStage.getScene().getWindow())
                return
            }

        }else{
            //project location is available in the registry history but not physically
            Entry.fxmlController.createWarning(-1, Entry.primaryStage.getScene().getWindow())
            removeFromRecentList(folder)
        }

        Entry.fxmlController.setDisableState(true, 0.5)
    }


    /**
     *
     *Committing recent history list into registry using "recentHistoryList" buffer
     */

    void commitRegistry() {
//        recentHistoryList.clear()
        rootPreferenceNode.clear() //Clear old records
        IntStream.range( 0, Math.min( maxRecentHistory, recentHistoryList.size() ) )
                .forEach(e->rootPreferenceNode.put(e.intValue().toString(),recentHistoryList.get(e)))
    }

    /**
     *
     * @param project - Project file which's going to the recentHistoryList buffer
     * @return
     */

    void changeRecentListOrder(File project) {
        removeFromRecentList(project)
        recentHistoryList.add(0,project.getAbsolutePath())
        if (recentHistoryList.size() > maxRecentHistory) recentHistoryList.removeLast()
    }

    /**
     *
     * Removing entries from the buffer, IF the project won't exist or
     * changing the order of the project from old to recently (INDEX N -> INDEX 1)
     * @param project - project file which needs to be removed from the file.
     * @return
     */
    def removeFromRecentList(File project){
        recentHistoryList.removeIf(e-> e == project.getAbsolutePath().trim())
    }

    /**
     * Support method for creating yamlController after the project was selected by the user.
     */
    void createController() {
        yamlController = YamlController.build(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator,yamlTemplateName)
        fileController = FileController.build(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator,fileTemplateName)
        //overAllTimeElapsedRetrieval()
    }

    /**
     * Extracting Overall-time elapsed by the project by analysing the yaml config file.
     */
    void overAllTimeElapsedRetrieval(){
        if (loggerStarted.get() || projectPath==null) return
        overAllTimeElapsed = yamlController.loadOverallTimeElapsed()
        Entry.fxmlController.updateElapsedTime(yamlController.extractTimeFromDuration(overAllTimeElapsed), yamlController)
    }

    /*void updateOverAllTimeElapsed(Duration duration){
        Map<String, Long> data = yamlController.extractTimeFromDuration(duration)
        overAllTimeElapsed = overAllTimeElapsed.plusSeconds(yamlController.convertMapToSeconds(data))
        Entry.fxmlController.updateElapsedTime(yamlController.extractTimeFromDuration(overAllTimeElapsed))
    }*/

    /**
     * It's a toggle one. This'll get called for both starting and stopping the timer
     * This is the Core-Function which'll call the yamlController to add the elapsed time
     */
    void onLogging() {
        if (projectPath==null) return

        if (!loggerStarted.get()){
            loggerStarted.set(true)
            Entry.fxmlController.loggerAction(true)
            if (projectPath==null) return
            LocalDateTime now = LocalDateTime.now()
            timer = timerSupplier.get()
            timer.scheduleAtFixedRate(timerTaskSupplier.get(),0L,1000L)
            yamlController.addStartTimer(now)
            fileController.addStartTimer(now)
        }else{
            loggerStarted.set(false)
            if (timer!=null) timer.cancel()
            Entry.fxmlController.loggerAction(false)
//            LocalDateTime now = LocalDateTime.now()
            LocalDateTime now = yamlController.logStartTime.plusSeconds(seconds-lastElapsedSeconds)
            yamlController.addStopTimer(now)
            fileController.addStopTimer(now)
            lastElapsedSeconds = seconds
            /*yamlController = new YamlController(
                    projectPath.getAbsolutePath()+File.separator+folderPattern,templateName,now)*/
        }

    }

    /**
     *
     * @param folder - Creating configuration for newly created project or the project which config wasn't exists.
     */
    void creatingNewProjectConfig(File folder) {
        String tmp = folder.getAbsolutePath()
//        Entry.fxmlController.projectLocation_lbl.setText(tmp.substring(tmp.lastIndexOf(File.separator)+1))
        projectPath = folder
        Files.createDirectories(Paths.get(projectPath.getAbsolutePath()+File.separator+folderPattern))
        changeRecentListOrder(folder)
        createController()
        resetElapsedTimer()
        resetRunningTimer()
        Entry.fxmlController.setDisableState(false,1.0)
        Entry.fxmlController.projectTitle_lbl.setText(spacing + tmp.substring(tmp.lastIndexOf(File.separator)+1))
        Entry.fxmlController.projectLocation_lbl.setText(tmp)
    }
    /**
     * Reset overall elapsed timer in the UI..
     */
    void resetElapsedTimer(){
        Entry.fxmlController.totalTime_lbl.setText("00:00:00 (HH:MM:SS)")
        Entry.fxmlController.totalDays_lbl.setText("0 DAY(S)")
    }
    /**
     * Reset the timer which is running for every 1sec.
     * It'll get called iff new project was selected
     */
    void resetRunningTimer(){
        seconds = lastElapsedSeconds = 0
        Entry.fxmlController.localTimer_lbl.setText("${YamlController.formatDigit(0)}:${YamlController.formatDigit(0)}:${YamlController.formatDigit(0)}")
    }
    /**
     * Terminating Pro-Timer application
     */
    void closeApplication() {

        if (loggerStarted.get()){
            //logger was called internally to stop the timer, Then only time sheets will get update and we can smoothly close the app without any loss of data..
            onLogging()
        }
        //pushing history buffer into registry
        commitRegistry()
        unregisterGlobalKeyListener()
        if (timer!=null) timer.cancel()
        if (dateRefresh!=null) dateRefresh.cancel()
        Platform.exit()
    }
    /**
     *
     * Unregistering Global Key Listener
     */
    def unregisterGlobalKeyListener(){
        GlobalScreen.unregisterNativeHook()
    }
    /**
     * @WARNING
     * Clear all the records from the config file.
     * After this you won't be able to see the time sheets. It'll start getting recorded freshly
     */
    void resetProject() {
        if (projectPath==null || loggerStarted.get()) return
        //File file = new File(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator+templateName)
        if (projectPath.exists())
            Entry.fxmlController.createResetAlert(1,Entry.rootPane.getScene().getWindow())
    }
    void resetProjectData(){
        File ymlFile = new File(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator+yamlTemplateName)
        File plainFile = new File(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator+fileTemplateName)
        FileChannel.open(Paths.get(ymlFile.getAbsolutePath()), StandardOpenOption.WRITE).truncate(0).close()
        FileChannel.open(Paths.get(plainFile.getAbsolutePath()), StandardOpenOption.WRITE).truncate(0).close()
    }

    def getPage(final String page) throws IOException {
//        URL url = this.class.getResource("${Entry.resourcePath}/layouts/" + page + ".fxml");
        URL url = getClass().getResource("${Entry.resourcePath}/layouts/${page}.fxml");
        if (url == null) {
            throw new FileNotFoundException("FXML File " + page + " Not Found");
        }
        return url;
    }

    void pauseOrResumeTask() {
        if (projectPath==null){
            //Project hasn't selected
            println("ProjectPath not selected")
            notifyViaMedia(notificationMediaNoProject)
            return
        }
        if (loggerStarted.get()){
            onLogging()
            notifyViaMedia(notificationMediaPause)
            println("ProTimer got Paused")
        }else {
            onLogging()
            notifyViaMedia(notificationMediaResume)
            println("ProTimer got Resumed")
        }
    }
    void notifyViaMedia(Media media){
        new MediaPlayer(media).play()
    }
}
