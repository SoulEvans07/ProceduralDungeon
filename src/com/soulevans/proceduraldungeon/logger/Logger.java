package com.soulevans.proceduraldungeon.logger;

import java.io.*;

public class Logger {
    private static final String logFolder = "data";

    public static final boolean DEBUG = false;

    public static void log(LogType level, String msg){
        System.out.println( level.toString() + " " + msg );
    }

    public static void log(String msg){
        System.out.println( msg );
    }

    public static void logDebug(LogType level, String msg){
        if(DEBUG)
            Logger.log(level, msg);
    }

    public static void logDebug(String msg){
        if(DEBUG)
            Logger.log(msg);
    }

    public static boolean logExist(String fileName){
        File logFile = new File(logFolder + "\\" + fileName);

        return logFile.exists();
    }

    public static void logFile(String fileName, String line){
        File saveFolder = new File(logFolder);
        if(!saveFolder.exists() && !saveFolder.isDirectory())
            saveFolder.mkdir();

        File logFile = new File(logFolder + "\\" + fileName);

        try ( BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(logFile, true), "ISO-8859-1")) ) {
            writer.write(line);
            writer.newLine();

            Logger.log("["+ fileName.toUpperCase() +"] " + line);
        } catch (IOException e) {
            e.printStackTrace();
            Logger.log("Log failed! " + logFile.getAbsolutePath());
        }
    }
}
