package com.sc.pt.utils;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/17/15
 * Time: 3:05 PM
 * To change this template use File | Settings | File Templates.
 */
public enum ScriptType {
    Run("COLLAT_RUN"), Reset("COLLAT_RESET"), TPLoading("TP_LOADING");
    private final String type;

    private ScriptType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
