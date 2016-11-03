package com.sc.pt.rmi;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 12/24/15
 * Time: 10:49 AM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
import java.rmi.*;
import java.net.MalformedURLException;

public class RMIClient
{
    // 同样为了方便，直接异常抛出
    public static void main(String[] args) throws Exception
    {
        System.setSecurityManager(new RMISecurityManager());
        try
        {
            // 这里因为是在本地所以省略了地址跟协议，若在网络中的远程方法调用，需要这样写
            // Converter c = (Converter)Naming.lookup("rmi://192.168.0.13/convert");
            Converter c = (Converter)Naming.lookup("convert");
            double rmb = c.rmb2Dollar(1000);
            System.out.println("converter result : " + rmb);
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (RemoteException e)
        {
            e.printStackTrace();
        }
        catch (NotBoundException e)
        {
            e.printStackTrace();
        }
    }
}
