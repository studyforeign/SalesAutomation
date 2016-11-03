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

public class RMIServer
{
    // 由于这里测试我们是最终用户，所以直接将异常抛给虚拟机
    public static void main(String[] args) throws Exception
    {
        System.setSecurityManager(new RMISecurityManager());

        Converter c = new ConverterImpl();

        Naming.bind("convert", c);

        System.out.println("rmi server start ...");
    }
}
