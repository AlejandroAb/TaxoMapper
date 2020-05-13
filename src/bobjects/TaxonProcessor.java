/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bobjects;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Alejandro
 */
public class TaxonProcessor extends Taxon {

    private String taxones;
    private String taxFile;
    public TaxonProcessor(int tax_id) {
        super(tax_id);
    }

    public String getTaxones() {
        return taxones;
    }

    public void setTaxones(String taxones) {
        this.taxones = taxones;
    }

    public String getTaxFile() {
        return taxFile;
    }

    public void setTaxFile(String taxFile) {
        this.taxFile = taxFile;
    }

    /**
     * Method to parse taxid list from command line, one value or comma
     * separated
     *
     * @param taxList
     * @return
     */
    public String parseTaxones(String taxList) {
        String log = "";
        String taxs[] = taxList.split(",");
        boolean isfirst = true;
        for (String t : taxs) {
            try {
                int tmp = Integer.parseInt(t.trim());
                if (isfirst) {
                    isfirst = false;
                    taxones = "" + tmp;
                } else {
                    taxones += "," + tmp;
                }
            } catch (NumberFormatException nfe) {
                return "Error on taxonomy list. Numerical value expected. Offending value: " + t;
            }
        }
        return log;
    }

    /**
     * This method is created for load taxons
     *
     * @param inputFile file with tax-id list one per line
     * @return error message in case of failure
     */
    public String loadTaxonesFromFile(String inputFile) {
        String log = "";
        try {
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String linea;
            boolean isfirst = true;
            while ((linea = reader.readLine()) != null) {
                try {
                    int tmp = Integer.parseInt(linea.trim());
                    if (isfirst) {
                        isfirst = false;
                        taxones = "" + tmp;
                    } else {
                        taxones += "," + tmp;
                    }
                } catch (NumberFormatException nfe) {
                    return "Error on taxonomy list. Numerical value expected. Offending value: " + linea;
                }

            }
        } catch (FileNotFoundException ex) {
            return "Taxonomy file not found: " + taxFile;
        } catch (IOException ex) {
            return "Error reading file: " + taxFile;
        }
        return log;
    }

}
