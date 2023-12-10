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
import javafx.application.Platform
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
import org.jnativehook.GlobalScreen
import org.jnativehook.NativeHookException
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener

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
    /*
      Set Global Key Listener
     */
    static class GlobalKeyListener implements NativeKeyListener{
        private short hotKeyFlag = 0x00;
        private static final short MASK_ALT = 1 << 0;
        private static final short MASK_X = 1 << 1;

        @Override
        void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {
        }

        @Override
        void nativeKeyPressed(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_ESCAPE) {
                try {
                    GlobalScreen.unregisterNativeHook();
                } catch (NativeHookException ex) {
                    throw new RuntimeException(ex);
                }
            }
            else if (e.getKeyCode() == NativeKeyEvent.VC_ALT) {
                hotKeyFlag &= MASK_ALT;
            }
            else if (e.getKeyCode() == NativeKeyEvent.VC_X) {
                hotKeyFlag &= MASK_X;
            }
        }

        @Override
        void nativeKeyReleased(NativeKeyEvent e) {
            if (e.getKeyCode() == NativeKeyEvent.VC_ALT) {
                hotKeyFlag ^= MASK_ALT;
            }
            else if (e.getKeyCode() == NativeKeyEvent.VC_X) {
                hotKeyFlag ^= MASK_X;
            }
            // Check the mask and do work.
            if (hotKeyFlag == (short)(MASK_ALT ^ MASK_X)) {
                Platform.runLater(()->{
                    Entry.primaryStage.setAlwaysOnTop(true)
                    Entry.primaryStage.setAlwaysOnTop(false)
                    Entry.primaryStage.requestFocus()
                    if (Entry.primaryStage.iconified){
                        //For Iconified - Minimize
                        Entry.primaryStage.setIconified(false);
                        return
                    }
                    if (!Entry.primaryStage.isShowing()){
                        //For Minimized to Tray
                        Entry.primaryStage.toFront()
                        Entry.primaryStage.show();
                    }
                });
                println("Triggered ALT + X");
            }
        }
    }
}
