package com.sc.pt.tpsplit;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 11/27/15
 * Time: 4:58 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class TPMurex2 implements Strategy {
    @Override
    public void algorithm() {
        //To change body of implemented methods use File | Settings | File Templates.
//        TPMurex2TradeCOM t,TPMurex2Portfolio\ p,TPMurex2Entity\ e\ \
//        where\ t.BCDFTPPortfolioID=p.BCDFTPPortfolioID\ and\ t.BCDFTPEntityID=e.BCDFTPEntityID
//        TPMurex2CRD.dm.querySql=select\ *\ from\ \
//        TPMurex2TradeCRD t,TPMurex2Portfolio\ p,TPMurex2Entity\ e\ \
//        where\ t.BCDFTPPortfolioID=p.BCDFTPPortfolioID\ and\ t.BCDFTPEntityID=e.BCDFTPEntityID
//        TPMurex2CUR.dm.querySql=select\ *\ from\ \
//        TPMurex2TradeCUR t,TPMurex2Portfolio\ p,TPMurex2Entity\ e\ \
//        where\ t.BCDFTPPortfolioID=p.BCDFTPPortfolioID\ and\ t.BCDFTPEntityID=e.BCDFTPEntityID
//        TPMurex2IRD.dm.querySql=select\ *\ from\ \
//        TPMurex2TradeIRD t,TPMurex2Portfolio\ p,TPMurex2Entity\ e\ \
//        where\ t.BCDFTPPortfolioID=p.BCDFTPPortfolioID\ and\ t.BCDFTPEntityID=e.BCDFTPEntityID
//        TPMurex2SCF.dm.querySql=select\ *\ from\ \
//        TPMurex2TradeSCF t,TPMurex2Portfolio\ p,TPMurex2Entity\ e\ \
//        where\ t.BCDFTPPortfolioID=p.BCDFTPPortfolioID\ and\ t.BCDFTPEntityID=e.BCDFTPEntityID

        String BCDFTPPortfolioID_COM;
        String BCDFTPPortfolioID;

        String BCDFTPEntityID_COM;
        String BCDFTPEntityID;

        //read the reference to the memory
        //PORTFOLIO 3861 values/ ENTITY 2 values
        //Totally 7722 values



        //read the row number to the memory
        // 772863 rows, totally 1.13GB
        //



        //generate the new file based on the row number


    }
}
