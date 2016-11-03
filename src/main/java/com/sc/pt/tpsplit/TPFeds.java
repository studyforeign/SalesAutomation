package com.sc.pt.tpsplit;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 11/27/15
 * Time: 4:58 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class TPFeds implements Strategy {
    @Override
    public void algorithm() {
        //To change body of implemented methods use File | Settings | File Templates.
        //TPFeds strategy
        //select t.*, p.BranchName from TPFedsTradeCN t,TPFedsPortfolioCN p
        // where t.BCDFTPPortfolioID=p.BCDFTPPortfolioID

    }
}
