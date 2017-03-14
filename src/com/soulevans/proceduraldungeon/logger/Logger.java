package com.soulevans.proceduraldungeon.logger;

public class Logger {

    public static void log(LogType level, String msg){
        System.out.println( level.toString() + " " + msg );
    }

    public static void log(String msg){
        System.out.println( msg );
    }
}
