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

package com.sk.listener

import com.sk.controller.AppController
import com.sk.controller.FXMLController
import javafx.scene.Node

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
    }
    private def setAnalyticsListener(){
        fxmlController.calculateTime_btn.setOnAction(e->appController.overAllTimeElapsedRetrieval())
        fxmlController.resetProject_btn.setOnAction(e->appController.resetProject())
    }
    private def setNavigationListener(){
        fxmlController.quit_imgView.setOnMouseClicked(e->fxmlController.createAlertWindow(((Node)e.getSource()).getScene().getWindow()))
        fxmlController.minimizeTray_imgView.setOnMouseClicked( e-> fxmlController.minimizeToTray(e))
        fxmlController.minimize_imgView.setOnMouseClicked(e->fxmlController.minimize(e))
    }


    private def setExecutionListener(){
        fxmlController.play_imgView.setOnMouseClicked(e->appController.onLogging())
        //fxmlController.pause_imgView.setOnMouseClicked(e->appController.stopLogging())
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

        fxmlController.changeProject_btn.setOnAction(e->{
            fxmlController.showRecentProject(e)
        })
        fxmlController.newProject_btn.setOnAction(e->{
            fxmlController.checkRecentListOpened()
            def folder = fxmlController.loadFolder("Load Project Folder")
            if (folder!=null) appController.creatingNewProjectConfig(folder)
        })

    }

}
