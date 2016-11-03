package com.sc.pt.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 12/24/15
 * Time: 10:48 AM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public interface Converter extends Remote
{
    public double rmb2Dollar(double rmb) throws RemoteException;
}
