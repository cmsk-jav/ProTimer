package com.sk.protimer.controller

import com.sk.protimer.Entry

import java.time.LocalDateTime

interface Controller {
    void addStartTimer(LocalDateTime startTime)
    void addStopTimer(LocalDateTime startTime)
    default def setupFile(File file){
        if (!file.exists()){
            file.createNewFile()
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))
            addHeader(bufferedWriter)
            bufferedWriter.flush()
            bufferedWriter.close()
        }
        return this
    }
    default def addHeader(BufferedWriter bufferedWriter){
        bufferedWriter.writeLine("# PRO-TIMER ")
        println("Addheader permission from Controller: ${Entry.appController.projectPath.getAbsolutePath()}" )
        bufferedWriter.writeLine("# PROJECT LOCATION: ${Entry.appController.projectPath.getAbsolutePath()}")
        bufferedWriter.newLine()
        //don't close the Writer. It's going to write after this call.
    }
}