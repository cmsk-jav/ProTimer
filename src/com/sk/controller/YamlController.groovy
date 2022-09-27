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

import groovy.yaml.YamlSlurper
import org.yaml.snakeyaml.DumperOptions
import org.yaml.snakeyaml.Yaml

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class YamlController {
    File yamlFile
    YamlSlurper parser = new YamlSlurper()
    DumperOptions options
    Yaml yamlWriter
    BufferedWriter writer;
    LocalDateTime logStartTime
    def year,mon
    final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss")
    final static String datePattern = "MMM d, yyyy"
    final static Map<String,Integer> indexing = ["Jan":1, "Feb":2, 'Mar':3, "Apr":4, "May":5, "Jun":6, "Jul":7,
                                    "Aug":8, "Sep":9, "Oct":10, "Nov":11, "Dec":12]
    String projectPath, templateName

    private YamlController(){
        options = new DumperOptions()
        options.setPrettyFlow(true)
        options.setIndent(3)
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK)
        yamlWriter = new Yaml(options)
    }
    /**
     *  Make Yaml Controller by Creating yaml file if it doesn't exists
     * @return YamlController Instance
     */
    def setYamlFile(final String projectPath,  final String templateName){
        this.projectPath = projectPath
        this.templateName = templateName
        yamlFile = new File(projectPath+templateName)
        if (!yamlFile.exists()) yamlFile.createNewFile()
        return this
    }
    /**
     * This class uses Builder-Pattern to create an instance
     * @return
     */
    static YamlController build(String projectPath,  String templateName){
        println("Project path:$projectPath")
        println("Template Name:$templateName")
        return  new YamlController()
                .setYamlFile(projectPath, templateName)
    }
    /**
     * Stopping logger
     * @param stopTime - the time when the logger stops
     */
    void addStopTimer( LocalDateTime stopTime){
        year = "Y_"+stopTime.getYear()
        mon = logStartTime.format("MMM")  //months[stopTime.getMonthValue()]
        def config = parser.parse(yamlFile) as LinkedHashMap
        if (!yamlFile.exists()){
            println "Configuration file is missing from the project folder $projectPath..."
            return
        }
        if (config==null) return
        def monEntry = config[year][mon] as Map
        for (int i=0;i<monEntry.size();i++){
            def x = monEntry[i].Started.trim() as String
            x = x.substring(0,x.lastIndexOf(" "))
            def entry = LocalDate.parse(x, datePattern) as LocalDate
            if(entry == LocalDate.parse(logStartTime.format(datePattern), datePattern)){
                monEntry[i].Ended = stopTime.format(formatter)
                Duration overall = Duration.between(logStartTime,stopTime)
                def prevElapsed = monEntry[i].Elapsed.trim() as String
                if (prevElapsed.size()!=0){
//                    println "contains-previous-elapsed-entry"
                    long prevElapsedSeconds = parse(prevElapsed)
                    overall = overall.plusSeconds(prevElapsedSeconds)
                }
//                println "Not-Contains previous-elapsed-entry"
                Map<String,Long> extract = extractTimeFromDuration(overall)
                String elapsedTime = formatTime(extract.get("D"),extract.get("H"),extract.get("M"),extract.get("S"))
//                println "elapsedTime==>$elapsedTime"
                monEntry[i].Elapsed = elapsedTime //xxxD / xxxH yyyM zzzS
                break
            }
        }

        writer  = new BufferedWriter(new FileWriter(yamlFile))
        //config = sortData(config)
        yamlWriter.dump(config, writer)
        writer.flush()
        writer.close()
        println "Yaml Data Updated successfully...."
    }
    /**
     * Starting logger
     * @param startTime - the time when the logger starts
     */
    void addStartTimer(LocalDateTime startTime){

        this.logStartTime = startTime
        year = "Y_"+logStartTime.getYear()
        mon =  logStartTime.format("MMM")// months[logStartTime.getMonthValue()]
        def config
        if (!yamlFile.exists()){
            println "Creating New Yaml file..."
            yamlFile.createNewFile()
            config = new LinkedHashMap()
            config[year]= dumpInitialData(true)
        }else {
            //Need to search the file
            config = parser.parse(yamlFile) as LinkedHashMap
            if (config!=null){
                def yearBasedEntry = config[year]
                if (yearBasedEntry!=null && yearBasedEntry.size() > 0){
                    def monthBasedEntry = yearBasedEntry[mon] as ArrayList
                    if (monthBasedEntry!=null && monthBasedEntry.size()>0){
                        //Inserting entry to a pre-exist month
                        if (monthBasedEntry.size()>0){
                            def isNoEntryAvailable=  monthBasedEntry.stream().noneMatch(e->{
                                String date = e.Started.trim();
                                return date.substring(0,date.lastIndexOf(" ")).equals(logStartTime.format(datePattern))
                            })
                            if (isNoEntryAvailable){
                                //println "No entry available for this date"
                                monthBasedEntry.add(dumpInitialData(false))
                            }else println("Entry Already available for the date...")
                        }
                        else
                            monthBasedEntry.add(dumpInitialData(false))

                    }else{
                        //Creating new month
                        config[year][mon]=[dumpInitialData(false)]
                    }
                }else{
                    //Creating a new Year entry as well as month entry..
                    config[year] = dumpInitialData(true)
                }
            }else
                println("Problem to parse Yaml document...")
        }
        writer  = new BufferedWriter(new FileWriter(yamlFile))
        config = sortData(config)
        yamlWriter.dump(config, writer)
        writer.flush()
        writer.close()
        println "Yaml Data dumped successfully...."

    }

    /**
     * This functionality is used to sort the yaml data.
     * For example, If my timesheet had 2022 entries and then i want some logs which needs to be there  for 2021 means
     *             it should enter before 2022 visually and the same with month and day also. Everything comes in an order.
     *             So this function sort the data based on the date-time order
     *
     * @param config - Timesheets
     * @return
     */
    def sortData(def config) {
        def yearBasedEntry = config
        yearBasedEntry = yearBasedEntry.sort {
            e1, e2 ->
                {
                    return e1.getKey().toString() <=> e2.getKey().toString()
                }
        }
        // println "After Year-Based-Sorting ==>\n" + yearBasedEntry
        yearBasedEntry.keySet().stream().each {
            def monthBasedEntry = yearBasedEntry.get(it) as Map
            monthBasedEntry = monthBasedEntry.sort {
                e1, e2 ->
                    {
                        return indexing.get(e1.getKey().toString()) <=> indexing.get(e2.getKey().toString())
                    }
            }
            monthBasedEntry.keySet().stream().each {
                mon ->{
                        def list = monthBasedEntry[mon] as List<Map>
                        list = list.sort {
                            e1, e2 ->
                                {
                                    def e1_time = e1.get("Started").toString().trim()
                                    def e2_time = e2.get("Started").toString().trim()
                                    LocalDate d1 = LocalDate.parse(e1_time.substring(0, e1_time.lastIndexOf(" ")), datePattern)
                                    LocalDate d2 = LocalDate.parse(e2_time.substring(0, e2_time.lastIndexOf(" ")), datePattern)
                                    return d1 <=> d2
                                }
                        }
                        monthBasedEntry[mon] = list
                    }
            }
            yearBasedEntry[it] = monthBasedEntry
        }
        return yearBasedEntry
    }

    def dumpInitialData(boolean isyearBased){

        def entries =[
                "Started": logStartTime.format(formatter),
                 "Ended":'',
                 "Elapsed":''
                ]
        if(isyearBased){
            def monthBasedEntry = new LinkedHashMap()
            monthBasedEntry.put(mon,[entries])
            return monthBasedEntry
        }
        return entries
    }

    /*
        time format stored in this order ==> xxxD / xxxH yyyM zzzS
     */
    static long parse(String time) {
        time = time.replaceAll(" ","")
        long hrs = time.substring(time.indexOf("/")+1,time.indexOf("H")) as long
        long mins = time.substring(time.indexOf("H")+1,time.indexOf("M")) as long
        long secs = time.substring(time.indexOf("M")+1,time.size()-1) as long
        return hrs*3600 + mins*60 + secs
    }

    static Map<String,Long> extractTimeFromDuration(Duration duration){
        Map<String, Long> converted = new LinkedHashMap<>()
        converted.put("D",(long) Math.ceil(duration.get(ChronoUnit.SECONDS)/86400))
        converted.put("H",(long) (duration.get(ChronoUnit.SECONDS) / 3600))
        converted.put("M",(long) ((duration.get(ChronoUnit.SECONDS)%3600)/60))
        converted.put("S",(long) ((duration.get(ChronoUnit.SECONDS)%3600)%60))
        return converted
    }

    static String formatTime(long days, long hours, long minutes, long seconds) {
        return "${days}D / ${hours}H ${minutes}M ${seconds}S"
    }
    /**
     * Process the timesheet and add the overall elapsed time..
     *
     * @return Duration - Contains sum up of full timesheet
     */
    Duration loadOverallTimeElapsed() {
        Duration duration = Duration.of(0,ChronoUnit.SECONDS)
        if (!yamlFile.exists()){
            return duration
        }

        def config = parser.parse(yamlFile) as LinkedHashMap
        if (config!=null){
            //println "Config=>$config"
            config.keySet().stream().each {
                String yearItr->{
                    def monthListForYear = config[yearItr] as LinkedHashMap
                    /*println "year=>$yearItr"*/
                    // println "monthListForYear ==>$monthListForYear"
                    monthListForYear.keySet().stream().each {
                        String monthItr->{
                            // println "MonthItr==> $monthItr"
                            def daysListForMonth = config[yearItr][monthItr] as List<Map<String,String>>
                            daysListForMonth.stream().each {
                                duration = duration.plusSeconds(parse(it.get("Elapsed")))
                            }
                        }
                    }
                }
            }
        }
        return duration
    }

    static long convertMapToSeconds(Map<String, Long> data) {
        return data.get("H")*3600 + data.get("M")*60 + data.get("S")
    }
    static String formatDigit(long digit){
        return digit%10L==digit?"0"+digit:""+digit
    }
}
