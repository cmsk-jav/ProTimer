package com.sk.protimer.controller

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class FileController implements Controller {
    String projectPath, templateName
    File file
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss")
    private FileController(String projectPath, String templateName){
        this.projectPath = projectPath
        this.templateName = templateName
    }
    /**
     *  Make File Controller by Creating plain file if it doesn't exists
     * @return FileController Instance
     */
    def setupFile(){
        file = new File(projectPath+templateName)
        return setupFile(file) as FileController
    }

    /**
     * This class uses Builder-Pattern to create an instance
     * @return
     */
    static FileController build(String projectPath,  String templateName){
        return new FileController(projectPath, templateName)
                .setupFile()
    }


    @Override
    void addStartTimer(LocalDateTime startTime) {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))
        writer.write(startTime.format(formatter))
        writer.flush()
        writer.close()
    }

    @Override
    void addStopTimer(LocalDateTime startTime) {
        BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))
        writer.writeLine(" >>> ${startTime.format(formatter)}")
        writer.flush()
        writer.close()
    }

}
