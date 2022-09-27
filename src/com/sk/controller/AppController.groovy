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

package com.sk.controller

import com.sk.Entry
import javafx.application.Platform
import javafx.scene.Node
import javafx.stage.Stage

import java.nio.channels.FileChannel
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier
import java.util.prefs.Preferences
import java.util.stream.Collectors
import java.util.stream.IntStream

class AppController {
    //Config folder and file Name which will get created to store time entries for each project
    final def folderPattern = ".pro-timer"
    final def templateName = "ProjectStats.yml"
    //Name of the registry Node. Used to store recent history
    final def registryNode = "pro-timer/entry"
    Preferences rootPreferenceNode
    //Stores Project path which is currently running
    File projectPath
    //Controller to read from and write into Yaml files
    YamlController yamlController
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
        return filename == templateName
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
    /**
     * creating app-controller instance
     */
    AppController(){
        fetchRegistry()
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
    void loadProject(final String filePath) {
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
                    Entry.fxmlController.projectLocation_Txtfield.setText(tmp.substring(tmp.lastIndexOf(File.separator)+1))
                    changeRecentListOrder(folder)
                    createController()
                    resetElapsedTimer()
                    resetRunningTimer()
                    // println "Project path was located.. path: ${projectPath.getAbsolutePath()}"
                }
                else{
                    //println "configuration file $templateName wasn't available. But if u confirmed the alert then pro-timer will create the config file"
                    Entry.fxmlController.createConfigAlert(folder, -1, Entry.primaryStage.getScene().getWindow())
                }

            }else{
                // println "Pro-timer wasn't initiated for this project"
                Entry.fxmlController.createWarning(1, Entry.primaryStage.getScene().getWindow())
            }

        }else{
            //project location is available in the registry history but not physically
            Entry.fxmlController.createWarning(-1, Entry.primaryStage.getScene().getWindow())
            removeFromRecentList(folder)
        }
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
        yamlController = YamlController.build(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator,templateName)
        //overAllTimeElapsedRetrieval()
    }

    /**
     * Extracting Overall-time elapsed by the project by analysing the yaml config file.
     */
    void overAllTimeElapsedRetrieval(){
        overAllTimeElapsed = yamlController.loadOverallTimeElapsed()
        Entry.fxmlController.updateElapsedTime(yamlController.extractTimeFromDuration(overAllTimeElapsed))
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
        }else{
            loggerStarted.set(false)
            if (timer!=null) timer.cancel()
            Entry.fxmlController.loggerAction(false)
//            LocalDateTime now = LocalDateTime.now()
            LocalDateTime now = yamlController.logStartTime.plusSeconds(seconds-lastElapsedSeconds)
            yamlController.addStopTimer(now)
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
        Entry.fxmlController.projectLocation_Txtfield.setText(tmp.substring(tmp.lastIndexOf(File.separator)+1))
        projectPath = folder
        Files.createDirectories(Paths.get(projectPath.getAbsolutePath()+File.separator+folderPattern))
        changeRecentListOrder(folder)
        createController()
        resetElapsedTimer()
        resetRunningTimer()
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
            //logger was called internally to stop the timer, Then only time sheets will get update and we can smoothly close the app without any loss of date..
            onLogging()
        }
        //pushing history buffer into registry
        commitRegistry()
        if (timer!=null) timer.cancel()
        Platform.exit()
    }
    /**
     * @WARNING
     * Clear all the records from the config file.
     * After this you won't be able to see the time sheets. It'll start getting recorded freshly
     */
    void resetProject() {
        if (projectPath==null || loggerStarted.get()) return
        File file = new File(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator+templateName)
        if (projectPath.exists())
            Entry.fxmlController.createResetAlert(1,Entry.rootPane.getScene().getWindow())
    }
    void resetProjectData(){
        File file = new File(projectPath.getAbsolutePath()+File.separator+folderPattern+File.separator+templateName)
        FileChannel.open(Paths.get(file.getAbsolutePath()), StandardOpenOption.WRITE).truncate(0).close()
    }

}
