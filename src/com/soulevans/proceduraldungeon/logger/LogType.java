package com.soulevans.proceduraldungeon.logger;

public enum LogType {
    ERROR,
    WARNING,
    NOTICE,

    EVENT;

    @Override
    public String toString() {
            return "[" + this.name() + "]";
        }
}
