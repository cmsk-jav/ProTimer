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
import javafx.animation.FadeTransition
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.application.Platform
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.control.Hyperlink
import javafx.scene.control.Label
import javafx.scene.control.OverrunStyle
import javafx.scene.control.Tooltip
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Text
import javafx.stage.*
import javafx.util.Duration

import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Consumer

class FXMLController implements Initializable {
    /**
     * referring all the components from the FXML file
     */
    @FXML
    public Label projectLocation_lbl
    @FXML
    public Button changeProject_btn
    @FXML
    public Button newProject_btn
    @FXML
    public Label localTimer_lbl
    @FXML
    public Label currentDate_lbl
    @FXML
    public ImageView play_imgView
    @FXML
    public Button resetProject_btn
    @FXML
    public Button calculateTime_btn
    @FXML
    public Button genGraph_btn
    @FXML
    public Label totalDays_lbl
    @FXML
    public Label totalTime_lbl
    @FXML
    public Button minimizeTray_btn
    @FXML
    public Button minimize_btn
    @FXML
    public Button quit_btn
    @FXML
    public ImageView logo_imgView
    @FXML
    public AnchorPane timerPane
    @FXML
    public AnchorPane runningTimerPane
    @FXML
    public Label projectTitle_lbl

    AtomicBoolean isRecentListOpened = new AtomicBoolean(false)
    VBox recentProjectPane
    Stage aboutStage
    //Avoid animation collision if new event triggered in-between of the animation
    boolean clipboardTransitionLock
    static Stage stage
    java.awt.TrayIcon applicationTray
    final java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();

    /**
     *My Profile
     */
      final String twitterURL = "https://twitter.com/cmskj"
      final String githubURL = "https://github.com/cmsk-jav"
      final String emailAddress = "cmsuresh@zohomail.in"
      final String websiteURL = "https://cmsk-jav.github.io/suresh/"


    /**
     * Choose the project folder from explorer
     * @param title - Window title
     * @return null, If cancelled the folderChooser. Else return the selected folder
     */
    File loadFolder(final def title) {
        DirectoryChooser directoryChooser = new DirectoryChooser()
        /*FileChooser.ExtensionFilter extension = new FileChooser.ExtensionFilter(extensionName, extensionType);
        fileChooser.getExtensionFilters().add(extension);*/
        directoryChooser.setTitle(title)
        return directoryChooser.showDialog(Entry.primaryStage)
    }

    FXMLController(){
        println "Creating FXMlController Instance"
    }
    /**
     *
     * @param location
     * The location used to resolve relative paths for the root object, or
     * <tt>null</tt> if the location is not known.
     *
     * @param resources
     * The resources used to localize the root object, or <tt>null</tt> if
     * the root object was not localized.
     */
    void initialize(URL location, ResourceBundle resources) {
        if (currentDate_lbl!=null)
         currentDate_lbl.setText(LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, YYYY")))

        /*Tooltip tooltip = new Tooltip("Close")
        tooltip.setStyle("-fx-background-color:#FFFFE1; -fx-text-fill:black")
        Tooltip.install(quit_btn, tooltip)*/
        setDisableState(true, 0.5)
    }
    /**
     *
     */
    def setDisableState(boolean state, double opacity){
        if (Entry.appController.projectPath!=null && state) return
        timerPane.setDisable(state)
        runningTimerPane.setDisable(state)
        //Even though i disabled the whole pane the image opacity not getting reduced. So manually need to reduce
        //but yes the functionality remains same and the button won't triggered in disable state.
        play_imgView.setOpacity(opacity)
    }
    /**
     * Alert for closing Pro-Timer application
     * @param window - Modal Window to create the alert
     * @return
     */
    void createAlertWindow(Window window){
        createCloseAlert(0,window, (yes)->{closeWindow(yes as ActionEvent)} as EventHandler ,
                (no)->{cancelCloseWindow(no as ActionEvent)} as EventHandler)
    }
    /**
     * Alert for creating new project configuration, If it wasn't exist
     */
    void createConfigAlert(File existingProjectFolder, int change, Window modal){
        createCloseAlert(change,modal,(yes)->{ Entry.appController.creatingNewProjectConfig(existingProjectFolder)
            cancelCloseWindow(null) },
                (no)->{setDisableState(true, 0.5); cancelCloseWindow(no as ActionEvent)} as EventHandler)
    }
    /**
     * @WARNING
     * Alert for resetting the whole project timesheet
     */
    void createResetAlert(int change, Window modal){
        createCloseAlert(change,modal, (yes)->{
            Entry.appController.resetProjectData()
            Entry.appController.resetElapsedTimer()
            Entry.appController.resetRunningTimer()
            cancelCloseWindow(null)
        }, (no)->{cancelCloseWindow(null)})
    }
    /**
     * Creating Alert Panel
     *
     * @param change {
     *     < 0 Project config Missing
     *     > 0 Reset project records.
     *     = 0 Closing Window
     * }
     */
    void createCloseAlert(int change, Window modal,EventHandler yesAction, EventHandler noAction){
//        def pane = FXMLLoader.load(Entry.appController.getPage("Close")) as AnchorPane
        def pane = createPopup() as AnchorPane
        Button yes_btn = pane.lookup("#yes_btn") as Button
        Button no_btn = pane.lookup("#no_btn") as Button
        Label closeWarning_lbl = pane.lookup("#closeWarning_lbl") as Label

        if (change < 0){
            closeWarning_lbl.setText("PROJECT CONFIG MISSING. WANT TO CREATE ?")
//            closeWarning_lbl.setStyle("-fx-font-size: 15px")
        }else if (change > 0){
            closeWarning_lbl.setText("Reset Project TimeSheets. Are U Sure ?")
        }

        yes_btn.setOnAction(yesAction)
        no_btn.setOnAction(noAction)
        Scene scene = new Scene(pane,260,75)
        scene.setOnKeyPressed(event->{
            if (event.getCode()== KeyCode.ESCAPE) cancelCloseWindow(null)
        })
        scene.setFill(Color.TRANSPARENT)
        stage = createPopupStage(scene, modal)
        stage.show()
    }

    /**
     * Change the icon while pause and play the timer..
     */

    def loggerAction(boolean started){
        if (started){
            changeProject_btn.setDisable(true)
            newProject_btn.setDisable(true)
            play_imgView.setImage(new Image(getClass().getResourceAsStream("$Entry.resourcePath/icons/Pause.png")))
        }else{
            changeProject_btn.setDisable(false)
            newProject_btn.setDisable(false)
            play_imgView.setImage(new Image(getClass().getResourceAsStream("$Entry.resourcePath/icons/Play.png")))
        }

    }
    /**
     * Create alert if project config was missing
     */
    def createWarning(int severity, Window modal){
        def pane = createPopup() as AnchorPane
        /*def pane = FXMLLoader.load(
                getClass().getResource("$Entry.resourcePath/layouts/Close.fxml")) as AnchorPane*/
        Button btn1 = pane.lookup("#yes_btn") as Button
        Button btn2 = pane.lookup("#no_btn") as Button
        Label closeWarning_lbl = pane.lookup("#closeWarning_lbl") as Label
        btn2.setText("OK")
        btn2.setOnAction(event->cancelCloseWindow(event))
        btn2.setPrefWidth(50)
        btn2.setLayoutX(pane.getPrefWidth()/2-btn2.getPrefWidth()/2)
        pane.getChildren().remove(btn1)
        if (severity>0) closeWarning_lbl.setText("PRO-TIMER NOT INITIATED FOR THIS PROJECT...")
        else closeWarning_lbl.setText("Project is missing...")
//        closeWarning_lbl.setStyle("-fx-font-size: 18px")
        Scene scene = new Scene(pane,260,75)
        scene.setOnKeyPressed(event->{
            if (event.getCode()== KeyCode.ESCAPE) cancelCloseWindow(null)
        })
        scene.setFill(Color.TRANSPARENT)
        stage = createPopupStage(scene, modal)
        stage.show()
    }
    /**
     * Iconified the app and add it into System-tray
     */
    void minimizeToTray(ActionEvent mouseEvent){
        if (!AppController.isSystemTraySupport.get()) {
            mouseEvent.consume()
            return
        }
        Entry.primaryStage.hide()
        createTray()

    }

    /**
     * Minimize Pro-Timer application
     */
     void minimize(ActionEvent mouseEvent) {
        Entry.primaryStage.setIconified(true)
        mouseEvent.consume()
    }

    /**
     * Creating System Tray if the operation system supports...
     */
    private def createTray(){
        if (applicationTray!=null && tray.getTrayIcons().contains(applicationTray)) return
        //File imageFile = new File( Paths.getResource("$Entry.resourcePath/icons/").getText()+"timer.png")
//        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(URI.getResource("$Entry.resourcePath/icons/timer.png"))
        java.awt.Image image = java.awt.Toolkit.getDefaultToolkit().getImage(getClass().getResource("$Entry.resourcePath/icons/timer.png"))
        java.awt.PopupMenu popup = new java.awt.PopupMenu("Pro-Timer")
        popup.setFont(new Font("Blue Highway", Font.BOLD, 11));
        applicationTray = new java.awt.TrayIcon(image, "PRO-TIMER", popup)
        java.awt.MenuItem app = new java.awt.MenuItem("PRO-TIMER")
        app.setFont(new Font("Blue Highway", Font.BOLD, 12))

        java.awt.MenuItem about = new java.awt.MenuItem("About")
        java.awt.MenuItem exit = new java.awt.MenuItem("Exit")
        popup.setFont(Font.getResource("${Entry.resourcePath}/font/BLUEBOLD.TTF"))

        exit.addActionListener(e-> {
//            Entry.appController.closeApplication()
            Platform.runLater(()->{
                if (stage!=null) return
                if (!Entry.primaryStage.isShowing()) {
                    Entry.primaryStage.toFront()
                    Entry.primaryStage.show()
                }
                createAlertWindow(Entry.primaryStage.getScene().getWindow())
            })

        })
        about.addActionListener(e-> Platform.runLater(()->showAbout()))
        app.addActionListener(e-> Platform.runLater(()->Entry.primaryStage.show()))
        popup.add(app)
        popup.addSeparator()
        popup.add(about)
        popup.add(exit)

        ActionListener actionListener = new ActionListener() {
            void actionPerformed(java.awt.event.ActionEvent e) {
                /*applicationTray.displayMessage("Action Event",
                        "An Action Event Has Been Performed!",
                        java.awt.TrayIcon.MessageType.INFO);*/
            }
        }
        MouseAdapter mouseAdapter =  new MouseAdapter(){
            @Override
            void mousePressed(java.awt.event.MouseEvent e) {
                if (e.getButton() == 1 && !Entry.primaryStage.isShowing())
                    Platform.runLater(()->Entry.primaryStage.show())
                /*
                //Trying to create custom tray pane using javafx. It's good but the problem is that i need to handle the tray pane close. It won't close once we clicked
                // on another tab. I don't how to create a global listener or track mouse action to close the tray if it's not clicked inside of my pane
                else if (e.getButton() == 3){
                    println("Hello right click")
                    Platform.runLater(()->{
                        Stage rightstage = new Stage();
                        rightstage.setAlwaysOnTop(true)
                        rightstage.initStyle(StageStyle.UNDECORATED)
                        VBox vBox = new VBox(new Button("Hello there..."))
                        Scene scene = new Scene( vBox, 300,200)
                        rightstage.setScene(scene)
                        rightstage.setX(e.getX())
                        rightstage.setY(e.getY()-200)
                        rightstage.show()

                    })
                }*/
                e.consume()
            }
        }
//        tray.addShutdownHook {println("tray shutdown")}
        if (tray.getTrayIcons().contains(applicationTray)) return
        applicationTray.setImageAutoSize(true)
        applicationTray.addActionListener(actionListener)
        applicationTray.addMouseListener(mouseAdapter)

        try {
            tray.add(applicationTray)
            //applicationTray.displayMessage("PRO-TIMER","Working at the background",java.awt.TrayIcon.MessageType.INFO)
        } catch (java.awt.AWTException e) {
            println e.getMessage()
        }

    }

    /**
     *Remove app from the system-tray then initiate further closing steps
     */
    void closeWindow(ActionEvent actionEvent) {
        if ( tray!=null && applicationTray!=null ){
            tray.remove(applicationTray)
        }
        actionEvent.consume()
       Entry.appController.closeApplication()
    }


    void cancelCloseWindow(ActionEvent actionEvent) {
        if (stage!=null) stage.close()
        //set it to uninitialised to avoid stage collision. At a time only one warning stage should be shown.
        stage = null
    }

    /**
     * Checking if SystemTray is supported by this OS
     * If not, then will disable the system tray navigation icon
     */
    void checkTraySupport() {
        if (java.awt.SystemTray.isSupported()){
            AppController.isSystemTraySupport.set(true)
            return
        }
        minimizeTray_btn.setStyle("-fx-opacity:.2")
        Label tooltip = new Label("Feature Not Supported...")
        tooltip.setStyle("-fx-text-fill: #545454;-fx-font-size: 15px;-fx-background-color: white; -fx-background-radius: 5px;")

        minimizeTray_btn.setOnAction(e->{
            def node = e.getSource() as ImageView
            tooltip.setTranslateX(1005 + node.getX()) // Adding the layout starting position...
            tooltip.setTranslateY(node.getFitHeight()+node.getY())
            Entry.rootPane.getChildren().add(tooltip)

        })
        minimizeTray_btn.setOnAction(e->{
            Entry.rootPane.getChildren().remove(tooltip)
        })

    }


    boolean checkRecentListOpened(){
        AnchorPane layout_2 = Entry.rootPane.lookup("#runningTimerPane") as AnchorPane
        if (isRecentListOpened.get()) {
            if (recentProjectPane!=null){
                layout_2.getChildren().remove(recentProjectPane)
                isRecentListOpened.set(false)
                return true
            }
        }
        return false
    }
    /**
     * Loading new project which wasn't initialised by Pro-Timer
     */
    def loadNewProject(){
        checkRecentListOpened()
        /*def folder = loadFolder("Load Project Folder")
        if (folder!=null) Entry.appController.creatingNewProjectConfig(folder)*/
        Consumer<File> consumer = (folderName) -> {
            def folder = loadFolder(folderName)
            if (folder!=null) Entry.appController.creatingNewProjectConfig(folder)
        }
        //-----<<<<Animation>>>------------
        translateTransition(150, Interpolator.EASE_BOTH, newProject_btn, consumer, Optional<String>.of("Load Project Folder"), true)
        //---------------------------------
    }
    /**
     *Creating Recent project pane based on the "recentProjectPane" buffer
     */
    void showRecentProject(ActionEvent event) {
        AnchorPane layout_2 = Entry.rootPane.lookup("#contentpane") as AnchorPane
        if (checkRecentListOpened()){
            layout_2.getChildren().remove(recentProjectPane)
            return
        }

        //-----<<<<Animation>>>------------
        Consumer<? extends Parent> consumer = (layout)-> showRecentProject(layout)
        translateTransition(150, Interpolator.EASE_BOTH, changeProject_btn, consumer, Optional<? extends Parent>.of(layout_2), false)
        //---------------------------------
        event.consume()
    }

    def translateTransition( final int DELAY, Interpolator interpolator, Control control, Consumer consumer, Optional target, boolean onFinish){
        Timeline changeProjectTransStart = new Timeline()
        Timeline changeProjectTransEnd = new Timeline()
        int incValue = control.getPrefHeight()*0.2
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(DELAY),
                new KeyValue(control.layoutYProperty(),control.getLayoutY()+incValue,interpolator))

        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(DELAY),
                new KeyValue(control.layoutYProperty(),control.getLayoutY(),interpolator))
        changeProjectTransStart.getKeyFrames().add(keyFrame1)
        changeProjectTransEnd.getKeyFrames().add(keyFrame2)
        SequentialTransition sequentialTransition = new SequentialTransition(changeProjectTransStart, changeProjectTransEnd)
        if (!onFinish)
            changeProjectTransStart.setOnFinished(e->target.ifPresent(consumer))
        else
            sequentialTransition.setOnFinished(e->target.ifPresent(consumer))
        sequentialTransition.setCycleCount(0)
        sequentialTransition.play()
    }

    def showRecentProject(AnchorPane layout){
        recentProjectPane = createRecentProjectPane()
        boolean containsEntry = Entry.appController.recentHistoryList.size() > 0;
        if (containsEntry){

            EventHandler onAction = {
                Entry.appController.loadProject((it.getSource() as Label).getText().trim())

                //This won't needed cuz mouse exit will triggered once the entry got selected and that'll remove the entry pane
                if(isRecentListOpened.get()){
                    try {
                        layout.getChildren().remove(recentProjectPane)
                    }catch(Exception exception){
                        // No worries about this exception cuz it's just a race-condition.
                       // Between mouse exit and manual entry selection
                    }
                    isRecentListOpened.set(false)
                }
            }
            for(String entry : Entry.appController.recentHistoryList){
                HBox pane1 = new HBox()
                pane1.setPadding(new Insets(2, 2, 0, 2))
                Label entryNode = createEntry(entry, onAction)
                entryNode.getStyleClass().add("recentListEntry")
//                entryNode.setStyle(onDefaultStyle)
                pane1.getChildren().add(entryNode)
                recentProjectPane.getChildren().add(pane1)
            }
        }
        else{
            // No recent history available in the registry

            /* HBox pane1 = new HBox()
             pane1.setPadding(new Insets(0, 2, 0, 2))
             Label entryNode = createEntry("No Entries..",null,null,null)
             pane1.getChildren().add(entryNode)
             recentProjectPane.getChildren().add(pane1)*/
        }

        AnchorPane browsePane = new AnchorPane()
        browsePane.setPadding(new Insets(2, 2, 0, 2))

        EventHandler onAction = (it)->{
            File folder = loadFolder("Load Existing Project")
            if (isRecentListOpened.get()){
                layout.getChildren().remove(recentProjectPane)
                isRecentListOpened.set(false)
            }
            if (folder == null) return
            Entry.appController.loadProject(folder.getAbsolutePath())
        }
        Label browse = createEntry("Choose Project", onAction)
        browse.getStyleClass().add("browseHistory")
        browse.setPrefWidth(312)
        browse.setPrefHeight(20)
        browse.setLayoutX(65)
        browse.setPrefWidth(100)
        AnchorPane.setTopAnchor(browse,1)
        AnchorPane.setBottomAnchor(browse,5)
//        browsePane.setPrefHeight(30)
        browse.setAlignment(Pos.CENTER)
        browsePane.getChildren().add(browse)
        recentProjectPane.getChildren().add(browsePane)
        recentProjectPane.setOnMouseExited(e->{
            if(isRecentListOpened.get()) {
                layout.getChildren().remove(recentProjectPane)
                isRecentListOpened.set(false)
            }
            e.consume()
        })
        isRecentListOpened.set(true)
        recentProjectPane.setOpacity(0.0)
        layout.getChildren().add(recentProjectPane)
        FadeTransition fadeTransition = new FadeTransition(Duration.millis(300), recentProjectPane)
        fadeTransition.setFromValue(0.0)
        fadeTransition.setToValue(1.0)
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH)
        fadeTransition.play()
//        layout.getChildren().each {println(it)}
    }

    def createRecentProjectPane() {
//        Control control = event.getSource() as Control
        VBox recentProjectPane = new VBox(1);
        recentProjectPane.setId("recentProjectPane")
        recentProjectPane.setPrefWidth(230)
        recentProjectPane.setMaxHeight(140)
        recentProjectPane.setLayoutX(360)
        recentProjectPane.setLayoutY(40)
        recentProjectPane.getStyleClass().add("recentProjectPane")
        return recentProjectPane
    }

    def createEntry( final String locationEntry, EventHandler onAction){
        Insets insets = new Insets(0,2,0,2)
        Label entry_lbl = new Label(locationEntry)
        entry_lbl.setPrefWidth(312)
        entry_lbl.setPrefHeight(20)
        entry_lbl.setPadding(insets)
        entry_lbl.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS)
        entry_lbl.setOnMouseClicked(onAction)
        return entry_lbl
    }


    /**
     * Update over-all elapsed time
     */
    void updateElapsedTime(Map<String,Long> data , YamlController yamlController) {
       /*
         // Calculating the days based on the aggregation of time -- OCT 16, 2022
         totalDays_lbl.setText("${data.get("D")} DAY(S)")
        */

        //Calculating the days based on the no.of days spend and irrespective of how much time they spend
        totalDays_lbl.setText("${yamlController.getDayWiseCount()} DAY(S)")

        totalTime_lbl.setText("${YamlController.formatDigit(data.get("H"))}:${YamlController.formatDigit(data.get("M"))}:${YamlController.formatDigit(data.get("S"))} (HH:MM:SS)")

    }

    def createPopup(){
        AnchorPane mainPanel = new AnchorPane()
        mainPanel.setId("closePane")
        mainPanel.setPrefWidth(260)
        mainPanel.setPrefHeight(75)
        mainPanel.getStylesheets().add(Entry.rootPane.getStylesheets().get(0))

        //Context
        Label label  = new Label()
        label.setId("closeWarning_lbl")
        label.setAlignment(Pos.CENTER)
        label.setPrefWidth(260)
        label.setPrefHeight(40)
        label.getStyleClass().add("box")
        label.setText("ARE YOU SURE?")

        //Action Buttons

        Button yes = new Button();
        yes.setId("yes_btn")
        yes.setLayoutX(65)
        yes.setLayoutY(43)
        yes.setPrefWidth(65)
        yes.setPrefHeight(25)
        yes.getStyleClass().add("box")
        yes.setText("YES")
        yes.setOnAction(FXMLController::closeWindow)

        Button no = new Button();
        no.setId("no_btn")
        no.setLayoutX(138)
        no.setLayoutY(43)
        no.setPrefWidth(65)
        no.setPrefHeight(25)
        no.getStyleClass().add("box")
        no.setText("NO")
        no.setOnAction(FXMLController::cancelCloseWindow)

        mainPanel.getChildren().addAll(label,yes,no)
        return mainPanel
    }

    def createPopupStage(Scene scene, Window modal){
        Stage stage = new Stage()
        stage.setX(Entry.primaryStage.getX() + 183)
        stage.setY(Entry.primaryStage.getY() + 65)
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.initOwner(modal)
        stage.setScene(scene)
        return stage
    }

    /**
     * Copy absolute path of the selected project
     */
    def copyPathToClipboard(javafx.scene.input.MouseEvent event){
        if (Entry.appController.projectPath == null || event.getButton()!=MouseButton.PRIMARY || event.getClickCount()<2 || clipboardTransitionLock) return
        clipboardTransitionLock = true
        Clipboard clipboard = Clipboard.getSystemClipboard()
        final ClipboardContent content = new ClipboardContent()
        content.putString(Entry.appController.projectPath.getAbsolutePath())
        clipboard.setContent(content)
        //Animation--------------

        /*FadeTransition fadeTransition = new FadeTransition(Duration.millis(2000), text)
        fadeTransition.setFromValue(1.0)
        fadeTransition.setToValue(0.0)
        fadeTransition.setInterpolator(Interpolator.EASE_BOTH)
        fadeTransition.play()*/
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(projectLocation_lbl.getPrefWidth(),projectLocation_lbl.getPrefHeight())
        /*rect.setArcHeight(30.0)
        rect.setArcWidth(30.0)*/

        projectLocation_lbl.setClip(rect)
    //        translateTransition1(1000, Interpolator.EASE_BOTH, text, projectLocation_lbl)
        //---------------
        clipboardTransition()
        event.consume()

    }
/**
 * Creation Translate animation of Clipboard copying.
 * Note: We're not animating root element instead we tries to get the children node and animate that node.
 *       It's an unmodifiable list and so we can't change anything except translate.
 */
    def clipboardTransition(){
        String tmpPath = projectLocation_lbl.getText()
        Text text = projectLocation_lbl.getChildrenUnmodifiable().get(0) as Text
        //-----------------
        //======================
        Timeline start_low = new Timeline()
        Timeline start_high = new Timeline()

        final int START_DELAY = 500;
        final int END_DELAY = 200;
        final int PEAK_WAITING_TIME = 300;
        int incValue = projectLocation_lbl.getPrefHeight()*0.6
        KeyFrame keyFrame1 = new KeyFrame(Duration.millis(START_DELAY),
                new KeyValue(text.translateYProperty(),text.getY()+incValue,Interpolator.TANGENT(Duration.millis(START_DELAY*0.4),0.6,Duration.millis(START_DELAY),1.0)))//Interpolator.EASE_IN


        KeyFrame keyFrame2 = new KeyFrame(Duration.millis(END_DELAY),
                new KeyValue(text.translateYProperty(),text.getY(),Interpolator.TANGENT(Duration.millis(END_DELAY*0.4),0.6,Duration.millis(END_DELAY),0.0))) //Interpolator.EASE_OUT
        start_low.getKeyFrames().add(keyFrame1)
        start_high.getKeyFrames().add(keyFrame2)

        start_low.setOnFinished(event->projectLocation_lbl.setText("Copied to Clipboard !!"))
        //====================
           /* Timeline end_low = new Timeline()
            Timeline end_high = new Timeline()
            KeyFrame keyFrame3 = new KeyFrame(Duration.millis(DELAY),
                    new KeyValue(text.translateYProperty(),text.getY()+incValue,Interpolator.TANGENT(Duration.millis(DELAY*0.4),0.6,Duration.millis(DELAY),1.0))) //Interpolator.EASE_IN

            KeyFrame keyFrame4 = new KeyFrame(Duration.millis(300),
                    new KeyValue(text.translateYProperty(),text.getY(),Interpolator.TANGENT(Duration.millis(350*0.4),0.6,Duration.millis(350),0.0))) //Interpolator.EASE_OUT
            end_low.getKeyFrames().add(keyFrame3)
            end_high.getKeyFrames().add( keyFrame4)

            end_low.setOnFinished(event->projectLocation_lbl.setText(tmpPath))*/
        //        SequentialTransition sequentialTransition = new SequentialTransition(start_low,start_high,end_low,end_high)
        //======================
        SequentialTransition sequentialTransition = new SequentialTransition(start_low,start_high)
        SequentialTransition sequentialTransition1 = new SequentialTransition(start_low,start_high)
        sequentialTransition.setOnFinished(event->{
            start_low.setOnFinished(e -> projectLocation_lbl.setText(tmpPath))
            Thread.sleep(PEAK_WAITING_TIME)
            sequentialTransition1.play()

        })
        //release clipboardTransitionLock
        sequentialTransition1.setOnFinished(e->clipboardTransitionLock = false)

        sequentialTransition.play()
    }

    def showAbout(){
        if (aboutStage!=null){
            if (!aboutStage.isShowing()) aboutStage.show()
            return
        }
        AnchorPane aboutPane = FXMLLoader.load(Entry.appController.getPage("About")) as AnchorPane
        def github = aboutPane.lookup("#github") as Hyperlink
        def twitter = aboutPane.lookup("#twitter") as Hyperlink
        def website = aboutPane.lookup("#website") as Hyperlink
        def email = aboutPane.lookup("#email") as Hyperlink
        def close = aboutPane.lookup("#quit") as Button
        javafx.scene.shape.Rectangle rect = new javafx.scene.shape.Rectangle(650,270);
        rect.setArcHeight(30.0)
        rect.setArcWidth(30.0)
        aboutPane.setClip(rect)
        github.setOnAction(e->Entry.entryInstance.browse(githubURL))
        twitter.setOnAction(e->Entry.entryInstance.browse(twitterURL))
        website.setOnAction(e->Entry.entryInstance.browse(websiteURL))
        email.setOnAction(e->{
            //It's possible to get exception if default mail app wasn't available.
            try {
                Desktop desktop = Desktop.getDesktop()
                desktop.mail(URI.create("mailto:${emailAddress}"))
                //If inbuilt mail box wasn't available then UI thread went to DEADLOCK state. So, Will wait for 1SEC
                //And if no mailbox was triggered then we can suspend the task.
                //Issue came on  issue on KALI-LINUX - VM Image Machine
                Platform.runLater(x-> { Thread.sleep(1000);desktop.enableSuddenTermination()})
            }catch(Exception ex){
                ex.printStackTrace()
            }
            e.consume()
        })
        def version_lbl = aboutPane.lookup("#appVersion") as Label
        version_lbl.setText(Entry.appController.APP_VERSION)
        aboutStage =  new Stage()
        Entry.appController.enableApplicationDrag(aboutPane,aboutStage)
        //Closing a stage is equivalent to hide, So we can use it later iff we have the instance
        close.setOnAction(e-> aboutStage.close())
        Scene scene1 = new Scene(aboutPane, aboutPane.getPrefWidth(), aboutPane.getPrefHeight())
        scene1.setFill(Color.TRANSPARENT)
        aboutStage.setScene(scene1)
        aboutStage.initStyle(StageStyle.TRANSPARENT)
        aboutStage.getIcons().add(new Image(getClass().getResourceAsStream("$Entry.resourcePath/icons/timer.png"),48,48,true,true))
        aboutStage.show()
    }

}

