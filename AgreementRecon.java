package agreerecon;

import jxl.*;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import vo.CptyMarginTerms;
import vo.MarginTerms;
import vo.OursMarginTerms;

import javax.swing.*;
import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: 1518008
 * Date: 8/4/16
 * Time: 3:01 PM
 * This code is just for internal use only.
 * If you need more info please contact the security admin.
 */
public class AgreementRecon {

    private static AgreementRecon agreementRecon = null;

    private AgreementRecon(){

    }

    public static AgreementRecon getInstance(){

        if(agreementRecon == null) agreementRecon = new AgreementRecon();

        return agreementRecon;
    }

    //Read MRP Agreement Recon file and create the target newBreak.xls
    public String seperateNewBreak(String mrp_agree){

        String newBreak = null;

        return newBreak;

    }

    public void updateNewBreak(String newBreak) throws IOException, BiffException, WriteException {

//        String newBreak = "C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak.xls";
        String csa = "C:\\Users\\1518008\\Desktop\\AgreeRecon\\Agreement\\CDMS_LEGALAGRMNT_CSA_20160730.xls";
        String marginTerms = "C:\\Users\\1518008\\Desktop\\AgreeRecon\\Agreement\\CDMS_LEGALAGRMNT_MARGIN_TERMS_20160730.xls";
        String master = "C:\\Users\\1518008\\Desktop\\AgreeRecon\\Agreement\\CDMS_LEGALAGRMNT_MASTER_20160730.xls";

        System.out.println("1 Getting csa IM info...");
        HashMap<String, String> agreementIdImHM = CompareIcdms.enrichWithCsa(csa);
        //Return Currency
        System.out.println("2 Getting agreementIdCurrencyHM info...");
        HashMap<String, String> agreementIdCurrencyHM = CompareIcdms.enrichWithMarginTerms(marginTerms, CompareIcdms.enrichWithMaster(master));
        System.out.println("3 Getting agreementIdOursMarginTermsHM info...");
        HashMap<String, OursMarginTerms> agreementIdOursMarginTermsHM = CompareIcdms.getAgreementIdOursMarginTermsHM();
        System.out.println("4 Getting agreementIdCptyMarginTermsHM info...");
        HashMap<String, CptyMarginTerms> agreementIdCptyMarginTermsHM = CompareIcdms.getAgreementIdCptyMarginTermsHM();

        HashMap<String, String> agreementIdRatingBasedHM = CompareIcdms.getAgreementIdRatingBasedHM();

//        InputStream is = new FileInputStream("C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak.xls");
//        jxl.Workbook rwb = Workbook.getWorkbook(is);

//        OutputStream os = new FileOutputStream("C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak_new.xls");
        Workbook rwb = Workbook.getWorkbook(new File("C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak.xls"));
        WritableWorkbook wwb = Workbook.createWorkbook(new File("C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak.xls"), rwb);
        WritableSheet sheet = wwb.getSheet(0);
        int rows = sheet.getRows();
        int columns = sheet.getColumns();

        String agreementId = null;
        //Write header

        Label im_header = new Label(columns, 0, "IM_ICDMS");
        Label currency_header = new Label(columns+1, 0, "Currency_ICDMS");
        Label oursMarginTermsMta_header = new Label(columns+2, 0, "OurMta_ICDMS");
        Label oursMarginTermsTh_header = new Label(columns+3, 0, "OursThreshold_ICDMS");
        Label cptyMarginTermsMta_header = new Label(columns+4, 0, "CptyMta_ICDMS");
        Label cptyMarginTermsTh_header = new Label(columns+5, 0, "CptyThreshold_ICDMS");
        Label ratingBasedFlag_header = new Label(columns+6, 0, "RatingBasedFlag");

        sheet.addCell(im_header);
        sheet.addCell(currency_header);
        sheet.addCell(oursMarginTermsMta_header);
        sheet.addCell(oursMarginTermsTh_header);
        sheet.addCell(cptyMarginTermsMta_header);
        sheet.addCell(cptyMarginTermsTh_header);
        sheet.addCell(ratingBasedFlag_header);


        WritableCellFormat wcfF = new WritableCellFormat(
                NumberFormats.TEXT);

        //Write how many rows into the new file
        for(int i=1; i<rows; i++) {
            WritableCellFormat numberFromart = new WritableCellFormat(NumberFormats.TEXT);
            agreementId = sheet.getCell(2, i).getContents().trim();
//            System.out.println(agreementId);

            Label im = new Label(columns, i, agreementIdImHM.get(agreementId));
            Label currency = new Label(columns+1, i, agreementIdCurrencyHM.get(agreementId));
            Label oursMarginTermsMta = new Label(columns+2, i, agreementIdOursMarginTermsHM.get(agreementId).getOursMta(), numberFromart);
            Label oursMarginTermsTh = new Label(columns+3, i, agreementIdOursMarginTermsHM.get(agreementId).getOursThreshold(), numberFromart);
            Label cptyMarginTermsMta = new Label(columns+4, i, agreementIdCptyMarginTermsHM.get(agreementId).getCptyMta(), numberFromart);
            Label cptyMarginTermsTh = new Label(columns+5, i, agreementIdCptyMarginTermsHM.get(agreementId).getCptyThreshold(), numberFromart);
            Label ratingBasedFlag = new Label(columns+6, i, agreementIdRatingBasedHM.get(agreementId));

            sheet.addCell(im);
            sheet.addCell(currency);
            sheet.addCell(oursMarginTermsMta);
            sheet.addCell(oursMarginTermsTh);
            sheet.addCell(cptyMarginTermsMta);
            sheet.addCell(cptyMarginTermsTh);
            sheet.addCell(ratingBasedFlag);
        }

        wwb.write();
        wwb.close();
        rwb.close();
    }

    public static void main(String []args) throws IOException, BiffException, WriteException {
//        String newBreak = "C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak.xls";

        AgreementRecon agreementRecon = AgreementRecon.getInstance();

//        newBreak = agreementRecon.seperateNewBreak(newBreak);

        agreementRecon.updateNewBreak("C:\\Users\\1518008\\Desktop\\AgreeRecon\\newBreak.xls");

    }
}
