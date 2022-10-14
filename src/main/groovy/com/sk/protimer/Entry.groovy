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

package com.sk.protimer

import com.sk.protimer.controller.AppController
import com.sk.protimer.controller.FXMLController
import com.sk.protimer.listener.AppListener
import javafx.application.Application
import javafx.application.Platform
import javafx.fxml.FXMLLoader
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.paint.Color
import javafx.scene.shape.Rectangle
import javafx.scene.text.Font
import javafx.stage.Stage
import javafx.stage.StageStyle

class Entry extends Application{
    public static AnchorPane rootPane
    Scene scene
    static FXMLController fxmlController
    AppListener listener
    public static  AppController appController
    static def resourcePath = "/res"
    static Stage primaryStage
    final int WIDTH = 600, HEIGHT = 200
    static Font font
    AnchorPane titleBar;
    @Override
    void init() throws Exception {
        //super.init()
        appController = new AppController()
        /*FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                        "$resourcePath/layouts/MainPage.fxml"
                )
        )*/
        FXMLLoader loader = new FXMLLoader(
                appController.getPage("MainPage")
        )
        font = Font.loadFont(getClass().getResourceAsStream("$resourcePath/font/BLUEBOLD.TTF"), 60)
        rootPane = loader.load() as AnchorPane
        fxmlController = loader.getController() as FXMLController
        listener = new AppListener(fxmlController,appController)
        scene = new Scene(rootPane,WIDTH,HEIGHT)
        titleBar = searchNode("#titlepane") as AnchorPane
    }
    Node searchNode(final String id){
        return scene.lookup(id)
    }
    @Override
    void stop() throws Exception {
        super.stop()
    }

    @Override
    void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("$resourcePath/icons/timer.png"),48,48,true,true))
        primaryStage.setOnCloseRequest(e->{
            fxmlController.createAlertWindow(rootPane.getScene().getWindow())
            e.consume()
        })
        primaryStage.setTitle("Pro-Timer")
        primaryStage.setScene(scene)
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT)
        appController.enableApplicationDrag(titleBar,primaryStage)
        fxmlController.checkTraySupport()
        Rectangle rect = new Rectangle(WIDTH,HEIGHT);
        rect.setArcHeight(30.0)
        rect.setArcWidth(30.0)
        rootPane.setClip(rect);
        scene.setFill(Color.TRANSPARENT)
        /**
         * Below line is used to prevent thread from implicitly gets exit.
         * It'll be useful for system tray feature..
         * Because once app stopped showing and added into system tray. Javafx thread tries to close running thread implicitly.
         */
        Platform.setImplicitExit(false)
        primaryStage.show()
    }


}
