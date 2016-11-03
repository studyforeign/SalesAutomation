package com.sc.pt.rmi;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 12/24/15
 * Time: 10:48 AM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
import java.rmi.*;
import java.rmi.server.*;

public class ConverterImpl extends UnicastRemoteObject implements Converter
{
    public ConverterImpl() throws RemoteException
    {
        super();
    }

    public double rmb2Dollar(double rmb)
    {
        return rmb * 0.125;
    }
}
