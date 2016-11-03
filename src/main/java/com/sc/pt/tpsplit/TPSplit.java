package com.sc.pt.tpsplit;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 11/27/15
 * Time: 4:59 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class TPSplit {

    public TPSplit(){

    }

    public static void split(String Strategy){

        if("TPFeds".equals(Strategy)) {
            Strategy tpFeds = new TPFeds();
        } else if ("TPOpics".equals(Strategy)) {
            Strategy tpOpics = new TPOpics();
        } else if ("TPSophis".equals(Strategy)) {
            Strategy tpSophis = new TPSophis();
        } else if("TPMurex2".equals(Strategy)) {
            Strategy tpMurex2 = new TPMurex2();
        } else if ("TPMurex3".equals(Strategy)) {
            Strategy tpMurex3 = new TPMurex3();
        } else
            System.out.println("Please input the correct file type");
    }

    public static void main(String[] args){
        TPSplit.split("DDD");
    }
}
