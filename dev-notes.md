## To Run the Application from local
- First run _Copy_ task which is in build.gradle. That'll copy all the dependencies and put inside libs folder
- Then, Run gradle jar which compile and build ur application as jar which is dropped in build/libs
- Then copy the dependency folder(<project>/libs) into build/libs
- Now open terminal and cd to <project>/build/libs and run `java -jar <app-name>.jar`
- Example Folder Structure

 |-build<br>
 |-libs<br>
 |-|___pro-timer.jar<br>
 |--|___libs<br>
 |--------|___dep1.jar<br>
 |--------|___dep2.jar<br>
 
