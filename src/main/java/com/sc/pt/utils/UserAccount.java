package com.sc.pt.utils;

/**
 * Created with IntelliJ IDEA.
 * User: 1513407
 * Date: 11/11/15
 * Time: 10:06 AM
 * To change this template use File | Settings | File Templates.
 */
public enum UserAccount {

    Rep("rep"), Mx("mx"), Sa("sa"),Alloy("alloy");
    private final String username;

    private UserAccount(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return username;
    }
}
