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
import javafx.event.ActionEvent
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.fxml.Initializable
import javafx.geometry.Insets
import javafx.geometry.Pos
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.OverrunStyle
import javafx.scene.control.TextField
import javafx.scene.image.Image
import javafx.scene.image.ImageView
import javafx.scene.input.KeyCode
import javafx.scene.input.MouseEvent
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.stage.*

import java.awt.*
import java.awt.event.ActionListener
import java.awt.event.MouseAdapter
import java.nio.file.Paths
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.concurrent.atomic.AtomicBoolean

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
    public ImageView minimizeTray_imgView
    @FXML
    public ImageView minimize_imgView
    @FXML
    public ImageView quit_imgView

    AtomicBoolean isRecentListOpened = new AtomicBoolean(false)
    VBox recentProjectPane

    static Stage stage
    java.awt.TrayIcon applicationTray
    final java.awt.SystemTray tray = java.awt.SystemTray.getSystemTray();



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
                (no)->{cancelCloseWindow(no as ActionEvent)} as EventHandler)
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
    void createCloseAlert(int change, Window e,EventHandler yesAction, EventHandler noAction){
        def pane = FXMLLoader.load(Entry.appController.getPage("Close")) as AnchorPane
       /* def pane = FXMLLoader.load(getClass().getResource(
                "${Entry.resourcePath}/layouts/Close.fxml"
        )) as AnchorPane*/
        /*def pane = FXMLLoader.load(
                getClass().getResource("$Entry.resourcePath/layouts/Close.fxml")) as AnchorPane*/
        stage = new Stage()
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
        stage.setX(Entry.primaryStage.getX() + 183)
        stage.setY(Entry.primaryStage.getY() + 65)
        Scene scene = new Scene(pane,260,75)
        scene.setOnKeyPressed(event->{
            if (event.getCode()== KeyCode.ESCAPE) cancelCloseWindow(null)
        })
        scene.setFill(Color.TRANSPARENT)
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.initOwner(e)
        stage.setScene(scene)
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
    def createWarning(int severity, Window e){
        def pane = FXMLLoader.load(Entry.appController.getPage("Close")) as AnchorPane
        /*def pane = FXMLLoader.load(
                getClass().getResource("$Entry.resourcePath/layouts/Close.fxml")) as AnchorPane*/
        stage = new Stage()
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
        stage.setX(Entry.primaryStage.getX() + 183)
        stage.setY(Entry.primaryStage.getY() + 65)
        Scene scene = new Scene(pane,260,75)
        scene.setOnKeyPressed(event->{
            if (event.getCode()== KeyCode.ESCAPE) cancelCloseWindow(null)
        })
        scene.setFill(Color.TRANSPARENT)
        stage.initStyle(StageStyle.TRANSPARENT)
        stage.initModality(Modality.WINDOW_MODAL)
        stage.initOwner(e)
        stage.setScene(scene)
        stage.show()
    }
    /**
     * Iconified the app and add it into System-tray
     */
    void minimizeToTray(MouseEvent mouseEvent){
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
     void minimize(MouseEvent mouseEvent) {
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

        java.awt.MenuItem exit = new java.awt.MenuItem("Exit")

        exit.addActionListener(e-> {
            tray.remove(applicationTray);Entry.appController.closeApplication()
        })
        popup.add(app)
        popup.addSeparator()
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
                e.consume()
            }
        }
        if (tray.getTrayIcons().contains(applicationTray)) return
        applicationTray.setImageAutoSize(true);
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
        if (Entry.fxmlController.applicationTray!=null){
            Entry.fxmlController.tray.remove(Entry.fxmlController.applicationTray)
        }
        actionEvent.consume()
       Entry.appController.closeApplication()
    }


    void cancelCloseWindow(ActionEvent actionEvent) {
        if (stage!=null) stage.close()
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
        minimizeTray_imgView.setStyle("-fx-opacity:.2")
        Label tooltip = new Label("Feature Not Supported...")
        tooltip.setStyle("-fx-text-fill: #545454;-fx-font-size: 15px;-fx-background-color: white; -fx-background-radius: 5px;")

        minimizeTray_imgView.setOnMouseEntered(e->{
            def node = e.getSource() as ImageView
            tooltip.setTranslateX(1005 + node.getX()) // Adding the layout starting position...
            tooltip.setTranslateY(node.getFitHeight()+node.getY())
            Entry.rootPane.getChildren().add(tooltip)

        })
        minimizeTray_imgView.setOnMouseExited(e->{
            Entry.rootPane.getChildren().remove(tooltip)
        })

    }

    def createEntry( final String locationEntry, EventHandler hoverStart, EventHandler hoverEnd, EventHandler onAction){
        Insets insets = new Insets(0,2,0,2)
        Label entry_lbl = new Label(locationEntry)
        //entry_lbl.getStyleClass().add("recentListEntry")
        entry_lbl.setPrefWidth(312)
        entry_lbl.setPrefHeight(20)
        entry_lbl.setPadding(insets)
        entry_lbl.setTextOverrun(OverrunStyle.LEADING_ELLIPSIS)
        //pane1.getChildren().add(entry_lbl)
        entry_lbl.setOnMouseEntered(hoverStart)
        entry_lbl.setOnMouseExited(hoverEnd)
        entry_lbl.setOnMouseClicked(onAction)
        return entry_lbl
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
     *Creating Recent project pane based on the "recentProjectPane" buffer
     */
    void showRecentProject(ActionEvent event) {
        AnchorPane layout_2 = Entry.rootPane.lookup("#contentpane") as AnchorPane
        if (checkRecentListOpened()){
            layout_2.getChildren().remove(recentProjectPane)
            return
        }
        recentProjectPane = createRecentProjectPane(event)
        boolean containsEntry = Entry.appController.recentHistoryList.size() > 0;

        if (containsEntry){
            String onDefaultStyle = "-fx-font-size: 12px;-fx-font-weight: bold;-fx-background-color: #191919;-fx-text-fill: white;-fx-background-radius: 9" as String
            String onHoverStyle = "-fx-font-size: 12px;-fx-background-color: white;-fx-text-fill: #191919;-fx-background-radius: 9" as String //-fx-border-width:0 0 3px 0;
//            String onHoverStyle = "-fx-font-size: 12px;-fx-font-weight: bold;-fx-background-color: #191919;-fx-text-fill: white;-fx-background-radius: 3" as String

            EventHandler hoverStart = {
                Label lbl = it.getSource() as Label
                lbl.setStyle(onHoverStyle)
                /*lbl.setScaleX(1.7)
                lbl.setScaleY(1.7)*/
            }
            EventHandler hoverEnd = (it)->{
                Label lbl = it.getSource() as Label
                lbl.setStyle(onDefaultStyle)
                /*lbl.setScaleX(1)
                lbl.setScaleY(1)*/

            }
            EventHandler onAction = (it)->{
                Entry.appController.loadProject((it.getSource() as Label).getText().trim())
                if(isRecentListOpened.get()){
                    layout_2.getChildren().remove(recentProjectPane)
                    isRecentListOpened.set(false)
                }
            }
            for(String entry : Entry.appController.recentHistoryList){
                HBox pane1 = new HBox()
                pane1.setPadding(new Insets(2, 2, 0, 2))
                Label entryNode = createEntry(entry,hoverStart,hoverEnd,onAction)
                entryNode.setStyle(onDefaultStyle)
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
        String onHoverStyle = "-fx-background-color: #ff4e32 ;-fx-font-size: 15px; -fx-text-fill: white;-fx-background-radius: 9" as String
        String defaultStyle = "-fx-background-color: #ffc51a;-fx-font-size: 15px; -fx-text-fill: white;-fx-background-radius: 9" as String

        EventHandler onDefault = (it)-> (it.getSource() as Label).setStyle(defaultStyle)
        EventHandler onHover = (it)-> (it.getSource() as Label).setStyle(onHoverStyle)
        EventHandler onAction = (it)->{
            File folder = loadFolder("Load Existing Project")
            if (isRecentListOpened.get()){
                layout_2.getChildren().remove(recentProjectPane)
                isRecentListOpened.set(false)
            }
            if (folder == null) return
            Entry.appController.loadProject(folder.getAbsolutePath())
        }
        Label browse = createEntry("Choose Project", onHover, onDefault, onAction)
        browse.setPrefWidth(312)
        browse.setPrefHeight(20)
        browse.setLayoutX(65)
        browse.setPrefWidth(100)
        AnchorPane.setTopAnchor(browse,1)
        AnchorPane.setBottomAnchor(browse,5)
//        browsePane.setPrefHeight(30)
        browse.setStyle(defaultStyle)
        browse.setAlignment(Pos.CENTER)
        browsePane.getChildren().add(browse)
        recentProjectPane.getChildren().add(browsePane)
        recentProjectPane.setOnMouseExited(e->{
            if(isRecentListOpened.get()) {
                layout_2.getChildren().remove(recentProjectPane)
                isRecentListOpened.set(false)
            }
            e.consume()
        })
        isRecentListOpened.set(true)
        layout_2.getChildren().add(recentProjectPane)
        layout_2.getChildren().each {println(it)}
        event.consume()
    }

    def createRecentProjectPane(ActionEvent event) {
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

    /**
     * Update over-all elapsed time
     */
    void updateElapsedTime(Map<String,Long> data) {
        totalDays_lbl.setText("${data.get("D")} DAY(S)")
        totalTime_lbl.setText("${YamlController.formatDigit(data.get("H"))}:${YamlController.formatDigit(data.get("M"))}:${YamlController.formatDigit(data.get("S"))} (HH:MM:SS)")

    }

}
