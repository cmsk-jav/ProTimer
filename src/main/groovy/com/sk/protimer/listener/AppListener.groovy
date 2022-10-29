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

package com.sk.protimer.listener

import com.sk.protimer.Entry
import com.sk.protimer.controller.AppController
import com.sk.protimer.controller.FXMLController
import javafx.animation.Interpolator
import javafx.animation.KeyFrame
import javafx.animation.KeyValue
import javafx.animation.ScaleTransition
import javafx.animation.SequentialTransition
import javafx.animation.Timeline
import javafx.animation.TranslateTransition
import javafx.fxml.FXML
import javafx.scene.Node
import javafx.scene.control.Button
import javafx.scene.control.Control
import javafx.scene.image.Image
import javafx.scene.input.Clipboard
import javafx.scene.input.ClipboardContent
import javafx.scene.input.MouseButton
import javafx.scene.layout.AnchorPane
import javafx.util.Duration

class AppListener {

    private FXMLController fxmlController
    private AppController appController

    AppListener(FXMLController fxmlController, AppController appController){
        this.fxmlController = fxmlController
        this.appController = appController
        initiateElements()
    }

    private def initiateElements(){
        setNavigationListener()
        setProjectLocationListener()
        setExecutionListener()
        setAnalyticsListener()
        setClipboardPaste()
    }

    private void setClipboardPaste(){
        fxmlController.projectLocation_lbl.setOnMouseClicked(event->fxmlController.copyPathToClipboard(event))
    }

    private def setAnalyticsListener(){
        fxmlController.calculateTime_btn.setOnAction(e->appController.overAllTimeElapsedRetrieval())
        fxmlController.resetProject_btn.setOnAction(e->appController.resetProject())
    }

    private def setNavigationListener(){
        fxmlController.quit_btn.setOnAction(e->fxmlController.createAlertWindow(((Node)e.getSource()).getScene().getWindow()))
        fxmlController.minimizeTray_btn.setOnAction( e-> fxmlController.minimizeToTray(e))
        fxmlController.minimize_btn.setOnAction(e->fxmlController.minimize(e))
    }


    private def setExecutionListener(){
        fxmlController.play_imgView.setOnMouseClicked(e->{
            println("Play Clicked")
            appController.onLogging()
        })
    }

    private def setProjectLocationListener() {
        /**
         * Drag N Drop support for getting Project location
         */
        /*fxmlController.projectLocation_Txtfield.setOnDragOver(e->{
            if(e.getDragboard().hasString() || (e.getDragboard().hasFiles() && e.getDragboard().getFiles().size()==1) ){
                def folder = e.getDragboard().hasString() ? new File(e.getDragboard().getString().trim())
                        : e.getDragboard().getFiles()[0]
                if (folder.exists() && folder.directory) e.acceptTransferModes(TransferMode.COPY);
            }
            e.consume();
        })
        fxmlController.projectLocation_Txtfield.setOnDragDropped(e->{
            if(e.getDragboard().hasString() || (e.getDragboard().hasFiles() && e.getDragboard().getFiles().size()==1)  ){
                def folder = e.getDragboard().hasString() ? new File(e.getDragboard().getString().trim())
                        : e.getDragboard().getFiles()[0]
                appController.projectPath = folder
                fxmlController.projectLocation_Txtfield.setText(folder.path)
            }
            e.setDropCompleted(true);e.consume();
        })*/

        fxmlController.changeProject_btn.setOnAction(e-> fxmlController.showRecentProject(e) )
        fxmlController.newProject_btn.setOnAction(e-> fxmlController.loadNewProject() )

    }
}
