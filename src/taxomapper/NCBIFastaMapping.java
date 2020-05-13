/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxomapper;

import bobjects.Taxon;
import database.Transacciones;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.StringUtils;

/**
 * This class is created to map a accessions ids to tax ids according to NCBI
 * nucleotide database and NCBI's Taxonomi DB. The expecting mapping files for
 * this programs can be downloaded from:
 * ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/accession2taxid/README The NCBI
 * mapping files to taxids conteins the following format accession -tab-
 * accession.version -tab- taxid -tab- gi
 *
 * @author aabdala
 */
public class NCBIFastaMapping {

    private String mappingFile; //NCBI mapping file 2 accession ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/accession2taxid/ info https://www.ncbi.nlm.nih.gov/guide/genomes-maps/
    private String fastaFile; //fasta files with accession number rigth before >
    private Transacciones transacciones;
    private String output;
    private boolean debug = false;
    private boolean appendTaxid = false;
    private boolean fullLine = false;
    private boolean taxID0 = false; //funciona como retain not found cuando esta en true
    private String outSep = "\t";
    private String rankType = "ALL";
    private String rankValues = "";
    private boolean notFasta = false;
    private boolean flagBulkNotFound = false;
    private boolean withHashMap = false;
    private String sep = ";";
    private String filter = "";
    int notFound = 0;
    int errors = 0;
    int notAtDB = 0;
    int oks = 0;
    FileWriter mappwriter;
    FileWriter notFoundWriter;
    FileWriter errorWriter;
    FileWriter notAtDBWriter;
    //List<String> rangos = Arrays.asList("kingdom", "superkingdom", "subkingdom", "superphylum", "phylum", "subphylum", "superclass", "infraclass", "class", "subclass", "parvorder", "superorder", "infraorder", "order", "suborder", "superfamily", "family", "subfamily", "tribe", "subtribe", "genus", "subgenus", "species", "species group", "species subgroup", "subspecies", "forma", "varietas", "no rank");
    String splitChars = "\t";
    /**
     * we set fixed values for some parameters column to find the tax id and the
     * char to split each line. However this could be changed by the setters of
     * this attribs.
     */
    int taxIDCol = 3;// non zero index
    int accCol = 2;

    public boolean isDebug() {
        return debug;
    }

    public String getFilter() {
        return filter;
    }

    public boolean isFullLine() {
        return fullLine;
    }

    public void setFullLine(boolean fullLine) {
        this.fullLine = fullLine;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public int getAccCol() {
        return accCol;
    }

    public void setAccCol(int accCol) {
        this.accCol = accCol;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public String getSplitChars() {
        return splitChars;
    }

    public void setSplitChars(String splitChars) {
        this.splitChars = splitChars;
    }

    public int getTaxIDCol() {
        return taxIDCol;
    }

    public void setTaxIDCol(int taxIDCol) {
        this.taxIDCol = taxIDCol;
    }

    /**
     * Contructor for this class
     *
     * @param mappingFile the file with accessions and tax ids
     * @param fastaFile a fasta file with the accessions
     * @param transacciones The interface for the DB
     */
    public NCBIFastaMapping(String mappingFile, String fastaFile, Transacciones transacciones) {
        this.mappingFile = mappingFile;
        this.fastaFile = fastaFile;
        this.transacciones = transacciones;
    }

    public String getMappingFile() {
        return mappingFile;
    }

    public void setMappingFile(String mappingFile) {
        this.mappingFile = mappingFile;
    }

    public String getFastaFile() {
        return fastaFile;
    }

    public void setFastaFile(String fastaFile) {
        this.fastaFile = fastaFile;
    }

    public Transacciones getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(Transacciones transacciones) {
        this.transacciones = transacciones;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public boolean isAppendTaxid() {
        return appendTaxid;
    }

    public void setAppendTaxid(boolean appendTaxid) {
        this.appendTaxid = appendTaxid;
    }

    public String getOutSep() {
        return outSep;
    }

    public void setOutSep(String outSep) {
        this.outSep = outSep;
    }

    /**
     * This method takes both files: mapping and fasta file. It process the
     * fasta file line by line, extract the accession and runs grep one by one
     * against the mapping
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void mappTaxIDs() throws FileNotFoundException, IOException {
        BufferedReader fastaReader = new BufferedReader(new FileReader(fastaFile));
        Map<String, String> taxoMapp = new HashMap<String, String>();
        String taxoLevels = getDesiredLinage();
        File mf = new File(mappingFile);
        if (!mf.exists()) {
            throw new FileNotFoundException(mappingFile + " (No such file or directory)");
        }
        FileWriter mappwriter = new FileWriter(output);
        FileWriter notFoundWriter = null;
        FileWriter errorWriter = null;
        FileWriter notAtDBWriter = null;
        String linea;
        int headers = 0;
        int notFound = 0;
        int errors = 0;
        int notAtDB = 0;
        int oks = 0;
        long start = System.currentTimeMillis();
        long counter5k = System.currentTimeMillis();
        while ((linea = fastaReader.readLine()) != null) {
            if (notFasta || linea.charAt(0) == '>') {
                headers++;
                String accession;
                if (notFasta) {//not fasta file
                    accession = linea;
                } else {
                    accession = linea.indexOf(" ") != -1 ? linea.substring(1, linea.indexOf(" ")) : linea.substring(1);
                }
                String res = grepAccession(accession);
                if (res.length() > 0) {
                    try {
                        String taxid = res.split(splitChars)[taxIDCol - 1];
                        if (taxoMapp.containsKey(taxid)) {
                            if (appendTaxid) {
                                mappwriter.write(accession + outSep + taxoMapp.get(taxid) + outSep + taxid + "\n");
                            } else {
                                mappwriter.write(accession + outSep + taxoMapp.get(taxid) + "\n");
                            }
                            oks++;
                        } else {
                            String hierarchy = transacciones.getHirarchyByTaxid(taxid);
                            if (hierarchy.length() > 0) {
                                String taxonomy = transacciones.getLiteralTaxonomy(hierarchy + "," + taxid, taxoLevels, sep);
                                if (appendTaxid) {
                                    mappwriter.write(accession + outSep + taxonomy + outSep + taxid + "\n");
                                } else {
                                    mappwriter.write(accession + outSep + taxonomy + "\n");
                                }
                                oks++;
                                if (withHashMap) {
                                    taxoMapp.put(taxid, taxonomy);
                                }
                            } else {
                                if (notAtDBWriter == null) {
                                    notAtDBWriter = new FileWriter(output + ".not_at_db");
                                    notAtDBWriter.write("tax id\n");
                                }
                                notAtDBWriter.write(accession + outSep + taxid + "\n");
                                notAtDB++;
                            }
                        }
                    } catch (IndexOutOfBoundsException iobe) {
                        System.err.println("No column index: " + taxIDCol + " for grep result: " + res + "\nLine: " + linea + " Accession: " + accession);
                        if (errorWriter == null) {
                            errorWriter = new FileWriter(output + ".err");
                            errorWriter.write("Line\tAccession\n");
                        }
                        errors++;
                        errorWriter.write(linea + "/n" + accession + "\n");
                    }
                } else {
                    if (notFoundWriter == null) {
                        notFoundWriter = new FileWriter(output + ".not_found");
                        notFoundWriter.write("Accession\n");
                    }
                    notFound++;
                    notFoundWriter.write(accession + "\n");
                }
                if (headers % 5000 == 0) {
                    long current = System.currentTimeMillis();
                    //finish / 1000 + " s."
                    System.out.println("Accession numbers processed: " + headers + " Time: " + (current - counter5k) / 1000 + ".s ... Total time elapsed: " + (current - start) / 1000 + ".s");
                    counter5k = System.currentTimeMillis();
                    if (((double) oks / (double) headers) < 0.5) {
                        System.out.println("At this point your mapping yield is not so good: " + (double) oks / (double) headers
                                + "!!\nctrl+c if you want to stop this run and maybe check for a better mapping file!");
                    }
                }
            }
        }
        System.out.println("***************END***************\n"
                + "Total time: " + (System.currentTimeMillis() - start) / 1000 + " seconds\n"
                + "   -- files created --\n");
        fastaReader.close();
        mappwriter.close();
        System.out.println("\nTaxonomy mapped file: " + output);
        if (notFoundWriter != null) {
            notFoundWriter.close();
            System.out.println("\nAccessions not mapped: " + output + ".not_found");
        }
        if (errorWriter != null) {
            errorWriter.close();
            System.out.println("\nError parsing mapping: " + output + ".err");

        }
        if (notAtDBWriter != null) {
            notAtDBWriter.close();
            System.out.println("\nTax ids not found into DB: " + output + ".not_at_db\n");
        }
        System.out.println("\n   -- sumary --\n"
                + "Mapping yield: " + (double) oks / (double) headers
                + "\nAccessions processed:" + headers
                + "\nAccessions mapped:" + oks
                + "\nAccessions not found on mapping file:" + notFound
                + "\nTaxids not found on reference database:" + notAtDB
                + "\nParsing mapping errors:" + errors);
    }

    /**
     * This method only use one file, the ncbi mapping file. and for all the
     * accessions into this file, the program will create an output file with
     * accession <delim> linage. This method is a shortcut when working with
     * files very big. So the mapping is done by greps LIKE: grep -F -f
     * all_accession.txt nucl_gb.accession2taxid > mapFile.txt. With the new
     * proccessing method,
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void completeTaxa() throws FileNotFoundException, IOException {
        BufferedReader mappReader = new BufferedReader(new FileReader(mappingFile));
        Map<String, String> taxoMapp = new HashMap<String, String>();
        //ranktype ALL = no extra
        /*  try {
            transacciones.getConexion().setPreparedStatemenS1("SELECT hierarchy FROM ncbi_node WHERE tax_id = ?");
        } catch (SQLException ex) {
            Logger.getLogger(NCBIFastaMapping.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SQL exp");
        }*/
        String taxoLevels = getDesiredLinage();
        FileWriter mappwriter = new FileWriter(output);
        FileWriter notFoundWriter = null;
        FileWriter errorWriter = null;
        FileWriter notAtDBWriter = null;
        String linea;
        int headers = 0;
        int notFound = 0;
        int errors = 0;
        int notAtDB = 0;
        int oks = 0;
        long start = System.currentTimeMillis();
        long counter5k = System.currentTimeMillis();
        while ((linea = mappReader.readLine()) != null) {
            if (linea.startsWith("#")) {
                mappwriter.write(linea + "\n");
            } else {
                headers++;
                try {
                    String splitLine[] = linea.split(splitChars);
                    //String taxid = splitLine[taxIDCol - 1];
                    // String accession = splitLine[1];//usually on column2 make it a param!
                    if (taxoMapp.containsKey(splitLine[taxIDCol - 1])) {
                        if (appendTaxid) {
                            mappwriter.write(splitLine[accCol - 1] + outSep + taxoMapp.get(splitLine[taxIDCol - 1]) + outSep + splitLine[taxIDCol - 1] + "\n");
                        } else if (fullLine) {
                            mappwriter.write(linea + outSep + taxoMapp.get(splitLine[taxIDCol - 1]) + "\n");
                        } else {
                            mappwriter.write(splitLine[accCol - 1] + outSep + taxoMapp.get(splitLine[taxIDCol - 1]) + "\n");
                        }
                        oks++;
                    } else {
                        String taxid = splitLine[taxIDCol - 1];
                        String hierarchy = transacciones.getHirarchyByTaxid(taxid);
                        //look for merged nodes
                        if (hierarchy.length() == 0 && !taxid.equals("0")) {
                            taxid = getMergedTaxID(taxid);
                            if (taxid.length() > 0) {
                                hierarchy = transacciones.getHirarchyByTaxid(taxid);
                            }
                        }
                        //String hierarchy = transacciones.getHierarcheByTaxIDPrepared(Integer.parseInt(taxid.trim()));
                        if (hierarchy.length() > 0) {
                            String taxonomy = transacciones.getLiteralTaxonomy(hierarchy + "," + taxid, taxoLevels, sep);
                            if (appendTaxid) {
                                mappwriter.write(splitLine[accCol - 1] + outSep + taxonomy + outSep + splitLine[taxIDCol - 1] + "\n");
                            } else if (fullLine) {
                                mappwriter.write(linea + outSep + taxonomy + "\n");
                            } else {
                                mappwriter.write(splitLine[accCol - 1] + outSep + taxonomy + "\n");
                            }
                            oks++;
                            if (withHashMap) {
                                taxoMapp.put(splitLine[taxIDCol - 1], taxonomy);
                            }
                        } else if (taxid.equals("0")) {
                            if (appendTaxid) {
                                mappwriter.write(splitLine[accCol - 1] + outSep + "NotAssigned" + outSep + splitLine[taxIDCol - 1] + "\n");
                            } else if (fullLine) {
                                mappwriter.write(linea + outSep + "NotAssigned" + "\n");
                            } else {
                                mappwriter.write(splitLine[accCol - 1] + outSep + "NotAssigned" + "\n");
                            }
                            oks++;

                        } else {
                            if (notAtDBWriter == null) {
                                notAtDBWriter = new FileWriter(output + ".not_at_db");
                                notAtDBWriter.write("#Acc\ttax id\n");
                            }
                            notAtDBWriter.write(splitLine[accCol - 1] + "\t" + splitLine[taxIDCol - 1] + "\n");
                            notAtDB++;
                            if (taxID0) {
                                if (appendTaxid) {
                                    mappwriter.write(splitLine[accCol - 1] + outSep + "NotAssigned" + outSep + splitLine[taxIDCol - 1] + "\n");
                                } else if (fullLine) {
                                    mappwriter.write(linea + outSep + "NotAssigned" + "\n");
                                } else {
                                    mappwriter.write(splitLine[accCol - 1] + outSep + "NotAssigned" + "\n");
                                }                                
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException iobe) {
                    /* String splitLine[] = linea.split(splitChars);
                String taxid = splitLine[taxIDCol - 1];
                String accession = splitLine[1];*/
                    System.err.println("Split line with " + splitChars + "on index to find taxid at col: " + (taxIDCol - 1) + " or index to find acc at col: " + (accCol - 1) + " for line: " + linea);
                    if (errorWriter == null) {
                        errorWriter = new FileWriter(output + ".err");
                        errorWriter.write("Line\tAccession\n");
                    }
                    errors++;
                    errorWriter.write(linea + "\n");
                }

                if (headers % 50000 == 0) {
                    long current = System.currentTimeMillis();
                    //finish / 1000 + " s."
                    System.out.println("Accession numbers processed: " + headers + " Time: " + (current - counter5k) / 1000 + ".s ... Total time elapsed: " + (current - start) / 1000 + ".s");
                    counter5k = System.currentTimeMillis();
                    if (((double) oks / (double) headers) < 0.5) {
                        System.out.println("At this point your mapping yield is not so good: " + (double) oks / (double) headers
                                + "!!\nctrl+c if you want to stop this run and maybe check for a better mapping file!");
                    }
                }
            }

        }
        System.out.println("***************END***************\n"
                + "Total time: " + (System.currentTimeMillis() - start) / 1000 + " seconds\n"
                + "   -- files created --\n");
        mappReader.close();
        mappwriter.close();
        System.out.println("\nTaxonomy mapped file: " + output);
        if (notFoundWriter != null) {
            notFoundWriter.close();
            System.out.println("\nAccessions not mapped: " + output + ".not_found");
        }
        if (errorWriter != null) {
            errorWriter.close();
            System.out.println("\nError parsing mapping: " + output + ".err");

        }
        if (notAtDBWriter != null) {
            notAtDBWriter.close();
            System.out.println("\nTax ids not found into DB: " + output + ".not_at_db\n");
        }
        System.out.println("\n   -- sumary --\n"
                + "Mapping yield: " + (double) oks / (double) headers
                + "\nAccessions processed:" + headers
                + "\nAccessions mapped:" + oks
                + "\nAccessions not found on mapping file:" + notFound
                + "\nTaxids not found on reference database:" + notAtDB
                + "\nParsing mapping errors:" + errors);
    }

    public boolean isTaxID0() {
        return taxID0;
    }

    public void setTaxID0(boolean taxID0) {
        this.taxID0 = taxID0;
    }

    /**
     * This method try to read one file (the map file) which should contain at
     * least two columns: one with the accession numbers and one with the
     * taxids. Then with that info, this method will create taxonomy mapping
     * files between names and access ids as the ones used by qiime for taxonomy
     * assignation.
     *
     * @param forQiime if true is for qiime, and use qiime construction methods,
     * otherwise use classic rank levels
     * @throws FileNotFoundException If the map file doesn't exists
     * @throws IOException input output write issues
     */
    public void completeTaxaQiime(boolean forQiime) throws FileNotFoundException, IOException {
        BufferedReader mappReader = new BufferedReader(new FileReader(mappingFile));
        Map<String, String> taxoMapp = new HashMap<String, String>();
        //ranktype ALL = no extra
        /*  try {
            transacciones.getConexion().setPreparedStatemenS1("SELECT hierarchy FROM ncbi_node WHERE tax_id = ?");
        } catch (SQLException ex) {
            Logger.getLogger(NCBIFastaMapping.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("SQL exp");
        }*/
        String taxoLevels = getDesiredLinage();
        FileWriter mappwriter = new FileWriter(output);
        FileWriter notFoundWriter = null;
        FileWriter errorWriter = null;
        FileWriter notAtDBWriter = null;
        FileWriter notAtListWriter = null;
        String linea;
        int headers = 0;
        int notFound = 0;
        int errors = 0;
        int notAtDB = 0;
        int filtered = 0;
        int oks = 0;
        long start = System.currentTimeMillis();
        long counter5k = System.currentTimeMillis();
        StringUtils su = new StringUtils();
        while ((linea = mappReader.readLine()) != null) {
            headers++;
            try {
                String splitLine[] = linea.split(splitChars);
                //String taxid = splitLine[taxIDCol - 1];
                // String accession = splitLine[1];//usually on column2 make it a param!
                if (taxoMapp.containsKey(splitLine[taxIDCol - 1])) {
                    if (appendTaxid) {
                        mappwriter.write(splitLine[accCol - 1] + outSep + taxoMapp.get(splitLine[taxIDCol - 1]) + outSep + splitLine[taxIDCol - 1] + "\n");
                    } else if (fullLine) {
                        mappwriter.write(linea + outSep + taxoMapp.get(splitLine[taxIDCol - 1]) + "\n");
                    } else {
                        mappwriter.write(splitLine[accCol - 1] + outSep + taxoMapp.get(splitLine[taxIDCol - 1]) + "\n");
                    }
                    oks++;
                } else {
                    String taxid = splitLine[taxIDCol - 1];
                    ArrayList<ArrayList<String>> data = transacciones.getHirarchyAndNameByTaxid(taxid);
                    String hierarchy = "";
                    String taxoName = "";
                    String tidRank = "";
                    if (data != null && data.size() > 0) {
                        taxoName = data.get(0).get(0);
                        hierarchy = data.get(0).get(1);
                        tidRank = data.get(0).get(2);
                    }
                    //look for merged nodes
                    if (hierarchy.length() == 0 && !taxid.equals("0")) {
                        taxid = getMergedTaxID(taxid);
                        if (taxid.length() > 0) {
                            data = transacciones.getHirarchyAndNameByTaxid(taxid);
                            if (data != null && data.size() > 0) {
                                taxoName = data.get(0).get(0);
                                hierarchy = data.get(0).get(1);
                                tidRank = data.get(0).get(2);
                            }
                        }
                    }
                    int t = 0;
                    try {
                        t = Integer.parseInt(taxid);
                    } catch (NumberFormatException nfe) {

                    }
                    Taxon taxon;
                    if (forQiime) {
                        taxon = new Taxon(t, true);
                    } else {
                        taxon = new Taxon(t);
                    }
                    //String hierarchy = transacciones.getHierarcheByTaxIDPrepared(Integer.parseInt(taxid.trim()));
                    if (hierarchy.length() > 0 && (filter.length() == 0 || hierarchy.contains(filter))) {
                        ArrayList<ArrayList<String>> taxonomyAl = transacciones.getTaxonomybyTaxIDList(hierarchy + "," + taxid, taxoLevels);
                        String sk = "";
                        for (ArrayList<String> taxones : taxonomyAl) {
                            String rank = taxones.get(1);
                            /**
                             * If at the end the taxon doesn't have any kingdom
                             * it saves the superkingdom to be assigned to it.
                             * This could be seen on some eukaryota or virus.
                             */
                            if (rank.equals("superkingdom")) {
                                sk = taxones.get(2);
                            }
                            if (forQiime) {
                                taxon.assignQiimeRank(taxones.get(2), rank);
                            } else {
                                taxon.assignRank(taxones.get(2), rank);
                            }
                        }
                        //only assign sk as k when k is empty
                        if (taxon.getKingdom().equals("k__") || taxon.getKingdom().equals("")) {
                            if (forQiime) {
                                taxon.assignQiimeRank(sk, "kingdom");
                            } else {
                                taxon.assignRank(sk, "kingdom");
                            }

                        }
                        /**
                         * If doesnt contains the name of the tax_id of the
                         * original search, concat it at the end of the string
                         * like if it is a sub species
                         */
                        String taxonomy = forQiime ? taxon.toQiimeString() : taxon.toClassicString();
                        if (!taxonomy.contains(taxoName)) {
                            if (taxonomy.endsWith("s__") || taxonomy.endsWith(";")) {
                                taxonomy += taxoName;
                            } else {
                                if (forQiime) {
                                    taxonomy += " " + su.removeFromString(taxoName, taxon.getSpecies().trim().substring(3));
                                } else {
                                    taxonomy += " " + su.removeFromString(taxoName, taxon.getSpecies().trim());
                                }

                                /*if(taxoName.contains(taxon.getSpecies().trim().substring(3))){
                                    taxonomy += taxoName.substring(taxon.getSpecies().length() + 1);
                                }else{                                    
                                    System.out.println(taxonomy + " ");
                                    taxonomy += " " + taxoName; //extrange cases
                                }*/
                                //taxonomy += " " + taxon.getSpecies()  taxoName;
                            }
                            if (notAtListWriter == null) {
                                notAtListWriter = new FileWriter(output + ".not_at_names");

                            }
                            notAtListWriter.write(tidRank + outSep + splitLine[accCol - 1] + outSep + taxonomy + outSep + splitLine[taxIDCol - 1] + "\n");
                        }
                        if (appendTaxid) {
                            mappwriter.write(splitLine[accCol - 1] + outSep + taxonomy + outSep + splitLine[taxIDCol - 1] + "\n");
                        } else if (fullLine) {
                            mappwriter.write(linea + outSep + taxonomy + "\n");
                        } else {
                            mappwriter.write(splitLine[accCol - 1] + outSep + taxonomy + "\n");
                        }
                        oks++;
                        if (withHashMap) {
                            taxoMapp.put(splitLine[taxIDCol - 1], taxonomy);
                        }
                        /**
                         * Changed 17-may-2019 it use to be && taxID0 now si es
                         * 0 siempre lo incluye y hasta abajo se añade otra
                         * validación
                         */
                    } else if (taxid.equals("0")) {
                        if (appendTaxid) {
                            mappwriter.write(splitLine[accCol - 1] + outSep + taxon.toQiimeString() + outSep + splitLine[taxIDCol - 1] + "\n");
                        } else if (fullLine) {
                            mappwriter.write(linea + outSep + taxon.toQiimeString() + "\n");
                        } else {
                            mappwriter.write(splitLine[accCol - 1] + outSep + taxon.toQiimeString() + "\n");
                        }
                        oks++;

                    } else if (filter.length() > 0 && hierarchy.length() > 0 && !hierarchy.contains(filter)) {
                        filtered++;
                    } else {
                        if (notAtDBWriter == null) {
                            notAtDBWriter = new FileWriter(output + ".not_at_db");
                            notAtDBWriter.write("Acc\ttax id\n");
                        }
                        notAtDBWriter.write(splitLine[accCol - 1] + "\t" + splitLine[taxIDCol - 1] + "\n");
                        notAtDB++;
                        if (taxID0) {// esta es la otra validacion donde el taxid no es 0 pero si se pasa la bandera  --retain-not-found cambia taxID0 a true y se anota
                            if (appendTaxid) {
                                mappwriter.write(splitLine[accCol - 1] + outSep + taxon.toQiimeString() + outSep + splitLine[taxIDCol - 1] + "\n");
                            } else if (fullLine) {
                                mappwriter.write(linea + outSep + taxon.toQiimeString() + "\n");
                            } else {
                                mappwriter.write(splitLine[accCol - 1] + outSep + taxon.toQiimeString() + "\n");
                            }
                        }
                    }
                }
            } catch (IndexOutOfBoundsException iobe) {
                /* String splitLine[] = linea.split(splitChars);
                String taxid = splitLine[taxIDCol - 1];
                String accession = splitLine[1];*/
                System.err.println("IndexOutOfBoundsException while split line with char: " + splitChars + " try to find index taxid at col: " + (taxIDCol - 1) + " or index to find acc at col: " + (accCol - 1) + " for line: " + linea);
                if (errorWriter == null) {
                    errorWriter = new FileWriter(output + ".err");
                    errorWriter.write("Line\tAccession\n");
                }
                errors++;
                errorWriter.write(linea + "\n");
            }

            if (headers % 50000 == 0) {
                long current = System.currentTimeMillis();
                //finish / 1000 + " s."
                System.out.println("Accession numbers processed: " + headers + " Time: " + (current - counter5k) / 1000 + ".s ... Total time elapsed: " + (current - start) / 1000 + ".s");
                counter5k = System.currentTimeMillis();
                if (((double) oks / (double) headers) < 0.5) {
                    System.out.println("At this point your mapping yield is not so good: " + (double) oks / (double) headers
                            + "!!\nctrl+c if you want to stop this run and maybe check for a better mapping file!");
                }
            }

        }
        System.out.println("***************END***************\n"
                + "Total time: " + (System.currentTimeMillis() - start) / 1000 + " seconds\n"
                + "   -- files created --\n");
        mappReader.close();
        mappwriter.close();
        System.out.println("\nTaxonomy mapped file: " + output);
        if (notFoundWriter != null) {
            notFoundWriter.close();
            System.out.println("\nAccessions not mapped: " + output + ".not_found");
        }
        if (errorWriter != null) {
            errorWriter.close();
            System.out.println("\nError parsing mapping: " + output + ".err");

        }
        if (notAtDBWriter != null) {
            notAtDBWriter.close();
            System.out.println("\nTax ids not found into DB: " + output + ".not_at_db\n");
        }
        System.out.println("\n   -- sumary --\n"
                + "Mapping yield: " + (double) oks / (double) headers
                + "\nAccessions processed:" + headers
                + "\nAccessions mapped:" + oks
                + "\nAccessions not found on mapping file:" + notFound
                + "\nTaxids not found on reference database:" + notAtDB
                + "\nFiltered ids: " + filtered
                + "\nParsing mapping errors:" + errors);
    }

    /**
     * When the program runs on any of the possible modes, it generates an
     * output file with extension .not_at_db. The intention of this method is to
     * take that file and process against possible merged nodes, therefor it
     * will perform the mapping for the tax_id not founded. The input file.
     * myfile.not_at_db is writen as a tsv file with acc and taxid
     *
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void processNotAtDB() throws FileNotFoundException, IOException {
        BufferedReader mappReader = new BufferedReader(new FileReader(mappingFile));
        Map<String, String> taxoMapp = new HashMap<String, String>();
        String taxoLevels = getDesiredLinage();
        FileWriter mappwriter = new FileWriter(output);
        FileWriter notFoundWriter = null;
        FileWriter errorWriter = null;
        FileWriter notAtDBWriter = null;
        FileWriter mergedNotAtDBWriter = null;
        String linea;
        int headers = 0;
        int notFound = 0;
        int errors = 0;
        int notAtDB = 0;
        int mergednotAtDB = 0;
        int oks = 0;
        long start = System.currentTimeMillis();
        long counter5k = System.currentTimeMillis();
        while ((linea = mappReader.readLine()) != null) {
            if (!linea.startsWith("#")) {
                headers++;
                try {
                    String splitLine[] = linea.split(splitChars);
                    String taxid = splitLine[1];
                    String accession = splitLine[0];
                    String newTaxid = getMergedTaxID(taxid);
                    if (newTaxid.length() == 0) {
                        if (notAtDBWriter == null) {
                            notAtDBWriter = new FileWriter(output + ".not_at_db");
                            notAtDBWriter.write("#Acc\ttax id\n");
                        }
                        notAtDBWriter.write(accession + "\t" + taxid + "\n");
                        notAtDB++;
                    } else {
                        if (taxoMapp.containsKey(newTaxid)) {
                            if (appendTaxid) {
                                mappwriter.write(accession + outSep + taxoMapp.get(newTaxid) + outSep + newTaxid + "\n");
                            } else {
                                mappwriter.write(splitLine[1] + outSep + taxoMapp.get(newTaxid) + "\n");
                            }
                            oks++;
                        } else {
                            String hierarchy = transacciones.getHirarchyByTaxid(newTaxid);
                            //String hierarchy = transacciones.getHierarcheByTaxIDPrepared(Integer.parseInt(taxid.trim()));
                            if (hierarchy.length() > 0) {
                                String taxonomy = transacciones.getLiteralTaxonomy(hierarchy + "," + newTaxid, taxoLevels, sep);
                                if (appendTaxid) {
                                    mappwriter.write(accession + outSep + taxonomy + outSep + newTaxid + "\n");
                                } else {
                                    mappwriter.write(accession + outSep + taxonomy + "\n");
                                }
                                oks++;
                                if (withHashMap) {
                                    taxoMapp.put(newTaxid, taxonomy);
                                }
                            } else {//encontro el nodo en ncbi_merge, pero ese nodo no tiene una entrada en ncbi_nodes, caso rao pero puede pasar 
                                if (mergedNotAtDBWriter == null) {
                                    mergedNotAtDBWriter = new FileWriter(output + ".merged_not_at_db");
                                    mergedNotAtDBWriter.write("Acc\ttax id\n");
                                }
                                mergedNotAtDBWriter.write(accession + "\t" + newTaxid + "\n");
                                mergednotAtDB++;
                            }
                        }
                    }
                } catch (IndexOutOfBoundsException iobe) {
                    /* String splitLine[] = linea.split(splitChars);
                String taxid = splitLine[taxIDCol - 1];
                String accession = splitLine[1];*/
                    System.err.println("Split line with " + splitChars + "on index: " + (taxIDCol - 1) + " or index: 1 for line: " + linea);
                    if (errorWriter == null) {
                        errorWriter = new FileWriter(output + ".err");
                        errorWriter.write("Line\tAccession\n");
                    }
                    errors++;
                    errorWriter.write(linea + "\n");
                }

                if (headers % 50000 == 0) {
                    long current = System.currentTimeMillis();
                    //finish / 1000 + " s."
                    System.out.println("Accession numbers processed: " + headers + " Time: " + (current - counter5k) / 1000 + ".s ... Total time elapsed: " + (current - start) / 1000 + ".s");
                    counter5k = System.currentTimeMillis();
                    if (((double) oks / (double) headers) < 0.5) {
                        System.out.println("At this point your mapping yield is not so good: " + (double) oks / (double) headers
                                + "!!\nctrl+c if you want to stop this run and maybe check for a better mapping file!");
                    }
                }

            }
        }
        System.out.println("***************END***************\n"
                + "Total time: " + (System.currentTimeMillis() - start) / 1000 + " seconds\n"
                + "   -- files created --\n");
        mappReader.close();
        mappwriter.close();
        System.out.println("\nTaxonomy mapped file: " + output);
        if (notFoundWriter != null) {
            notFoundWriter.close();
            System.out.println("\nAccessions not mapped: " + output + ".not_found");
        }
        if (errorWriter != null) {
            errorWriter.close();
            System.out.println("\nError parsing mapping: " + output + ".err");

        }
        if (notAtDBWriter != null) {
            notAtDBWriter.close();
            System.out.println("\nTax ids not found into DB: " + output + ".not_at_db\n");
        }
        if (mergedNotAtDBWriter != null) {
            mergedNotAtDBWriter.close();
            System.out.println("\nTax ids found in merge table but not into nodes table: " + output + ".merged_not_at_db\n");
        }
        System.out.println("\n   -- sumary --\n"
                + "Mapping yield: " + (double) oks / (double) headers
                + "\nAccessions processed:" + headers
                + "\nAccessions mapped:" + oks
                + "\nTaxids not found on reference database:" + notAtDB
                + "\nMerged taxids not found on reference database:" + mergednotAtDB
                + "\nParsing mapping errors:" + errors);
    }

    /**
     * This method tests if any taxid is obsolete and/or if it is merged with
     * other valid taxid
     *
     * @param taxid the taxid to be tested
     * @return the new taxid or empty string if not exist
     */
    public String getMergedTaxID(String taxid) {
        String newId = transacciones.testForMergeTaxid(taxid);
        return newId;
    }

    /**
     * Method to perform the grep one by one via Process
     *
     * @param accesion the accession to be "greped" against the mapping file
     * @return
     */
    public String grepAccession(String accesion) {
        //String commandLine = "c:/Program Files/R/R-3.0.3/bin/Rscript \"" + workingDir + "scripts/scriptDiversidad.R\" \"" + workingDir + "\" " + nameMatriz + " " + sc.getRealPath("") + fileNameRare + " " + sc.getRealPath("") + fileNameRenyi + " " + betaIndex + " " + sc.getRealPath("") + fileNameBeta + " " + imgExtraTitle;
        String res = "";
        try {
            String commandLine = "grep " + accesion + " -m1 " + mappingFile;//grep X51700.1 -m1 nucl_gb.accession2taxid
            if (debug) {
                System.out.println(commandLine);
            }
            Process proc = Runtime.getRuntime().exec(commandLine);
            proc.waitFor();
            InputStreamReader inputstreamreader = new InputStreamReader(proc.getInputStream());
            BufferedReader reader = new BufferedReader(inputstreamreader);
            String line = null;
            int lines = 0;
            while ((line = reader.readLine()) != null) {
                res += line;
                lines++;
                if (debug) {
                    System.out.println("line: " + lines + ": " + line);
                }
                //or break?
            }
            proc.destroy();
            reader.close();
            //inputstreamreader.close();
        } catch (InterruptedException ie) {

        } catch (IOException ie) {

        }
        return res;
    }

    /**
     * Perform grep search with more than one accession per time
     *
     * @param accesion the accession numbers in form 'acc1\|acc2\|...\|accN'
     * @param bulk the number N of accessions to expect
     * @return the grep command result. it is expected one line per accession
     * number
     */
    public String grepBulkAccession(String accesion, int bulk) {
        //String commandLine = "c:/Program Files/R/R-3.0.3/bin/Rscript \"" + workingDir + "scripts/scriptDiversidad.R\" \"" + workingDir + "\" " + nameMatriz + " " + sc.getRealPath("") + fileNameRare + " " + sc.getRealPath("") + fileNameRenyi + " " + betaIndex + " " + sc.getRealPath("") + fileNameBeta + " " + imgExtraTitle;
        StringBuilder res = new StringBuilder();
        flagBulkNotFound = false;
        try {
            String commandLine = "grep " + accesion + " -m" + bulk + " " + mappingFile;//grep X51700.1 -m1 nucl_gb.accession2taxid
            String command[] = {"sh", "-c", "grep " + accesion + " -m" + bulk + " " + mappingFile};
            if (debug) {
                System.out.println(commandLine);
            }
            Process proc = Runtime.getRuntime().exec(command);
            proc.waitFor();
            InputStreamReader inputstreamreader = new InputStreamReader(proc.getInputStream());
            BufferedReader reader = new BufferedReader(inputstreamreader);
            String line = null;
            int lines = 0;
            while ((line = reader.readLine()) != null) {
                res.append(line).append("\n");
                lines++;
                if (debug) {
                    System.out.println("line: " + lines + ": " + line);
                }
                //or break?
            }
            proc.destroy();
            reader.close();
            if (lines != bulk) {
                flagBulkNotFound = true;
            }
            //inputstreamreader.close();
        } catch (InterruptedException ie) {

        } catch (IOException ie) {

        }
        return res.toString();
    }

    /**
     * This method take the rankType class attribute and according to its value
     * determines the linage to be searched on the DB
     *
     * @return a partial query string with the taxonomic rank to be extracted
     * from the DB
     */
    public String getDesiredLinage() {
        String taxoLevels = "";//ranktype ALL = no extra
        if (rankType.equals("KNOWN")) {//REMOVES NO RANK
            taxoLevels = " AND rank IN('kingdom','superkingdom','subkingdom','superphylum','phylum','subphylum','superclass','infraclass','class','subclass','parvorder','superorder','infraorder','order','suborder','superfamily','family','subfamily','tribe','subtribe','genus','subgenus','species','species group','species subgroup','subspecies','forma','varietas')";
        } else if (rankType.equals("CLASSIC")) {
            taxoLevels = "AND rank IN('kingdom','superkingdom','phylum','class','order','family','genus','species','subspecies')";
        } else if (rankType.equals("CUSTOM")) {
            taxoLevels = "AND rank IN(";
            boolean isFirst = true;
            for (String rank : rankValues.split(",")) {
                if (isFirst) {
                    taxoLevels += "'" + rank + "'";
                    isFirst = false;
                } else {
                    taxoLevels += ",'" + rank + "'";
                }

            }

            taxoLevels += ")";
        }
        return taxoLevels;
    }

    /**
     * This method is the "son" of mappTaxIDs() but perform a batch grep search
     * for certain amount of accession defined by bulk param
     *
     * @param bulk the number of accession to search on each grep
     * @throws FileNotFoundException
     * @throws IOException
     */
    public void mappBulkTaxIDs(int bulk) throws FileNotFoundException, IOException {
        BufferedReader fastaReader = new BufferedReader(new FileReader(fastaFile));
        HashMap<String, String> taxoMapp = new HashMap<String, String>();
        String taxoLevels = getDesiredLinage();
        File mf = new File(mappingFile);
        if (!mf.exists()) {
            throw new FileNotFoundException(mappingFile + " (No such file or directory)");
        }
        mappwriter = new FileWriter(output);
        notFoundWriter = null;
        errorWriter = null;
        notAtDBWriter = null;
        String linea;
        int headers = 0;

        long start = System.currentTimeMillis();
        long counter5k = System.currentTimeMillis();
        int cbulk = 0;
        StringBuilder accessions = null;
        while ((linea = fastaReader.readLine()) != null) {
            if (notFasta || linea.charAt(0) == '>') {
                headers++;
                if (cbulk == 0) {
                    accessions = new StringBuilder();
                }
                String accession;
                if (notFasta) {//not fasta file
                    accession = linea;
                } else {
                    accession = linea.indexOf(" ") != -1 ? linea.substring(1, linea.indexOf(" ")) : linea.substring(1);
                }
                cbulk++;
                if (cbulk == 1) {
                    accessions.append("'").append(accession);
                } else if (cbulk == bulk) {//ready to send
                    accessions.append("\\|").append(accession).append("'");

                } else {
                    accessions.append("\\|").append(accession);
                }
                if (cbulk == bulk) {
                    cbulk = 0;
                    batchAnnotation(accessions, taxoLevels, bulk, taxoMapp);
                }
                if (headers % 5000 == 0) {
                    long current = System.currentTimeMillis();
                    //finish / 1000 + " s."
                    System.out.println("Accession numbers processed: " + headers + " Time: " + (current - counter5k) / 1000 + ".s ... Total time elapsed: " + (current - start) / 1000 + ".s");
                    counter5k = System.currentTimeMillis();
                    if (((double) oks / (double) headers) < 0.5) {
                        System.out.println("At this point your mapping yield is not so good: " + (double) oks / (double) headers
                                + "!!\nctrl+c if you want to stop this run and maybe check for a better mapping file!");
                    }
                }
            }
        }
        //Finish the last ones
        if (cbulk > 0) {
            cbulk = 0;
            batchAnnotation(accessions, taxoLevels, bulk, taxoMapp);
            long current = System.currentTimeMillis();
            //finish / 1000 + " s."
            System.out.println("Accession numbers processed: " + headers + " Time: " + (current - counter5k) / 1000 + ".s ... Total time elapsed: " + (current - start) / 1000 + ".s");
        }
        System.out.println("***************END***************\n"
                + "Total time: " + (System.currentTimeMillis() - start) / 1000 + " seconds\n"
                + "   -- files created --\n");
        fastaReader.close();
        mappwriter.close();
        System.out.println("\nTaxonomy mapped file: " + output);
        if (notFoundWriter != null) {
            notFoundWriter.close();
            System.out.println("\nAccessions not mapped: " + output + ".not_found");
        }
        if (errorWriter != null) {
            errorWriter.close();
            System.out.println("\nError parsing mapping: " + output + ".err");

        }
        if (notAtDBWriter != null) {
            notAtDBWriter.close();
            System.out.println("\nTax ids not found into DB: " + output + ".not_at_db\n");
        }
        System.out.println("\n   -- sumary --\n"
                + "Mapping yield: " + (double) oks / (double) headers
                + "\nAccessions processed:" + headers
                + "\nAccessions mapped:" + oks
                + "\nAccessions not found on mapping file:" + notFound
                + "\nTaxids not found on reference database:" + notAtDB
                + "\nParsing mapping errors:" + errors);
    }

    /**
     * This method is the one in charge of write the output files according to
     * the grep search results
     *
     * @param accessions the accession number that were searched against the
     * mapping DB
     * @param taxoLevels the desired taxa level
     * @param bulk the number of accession searched on one single grep
     * @param taxoMapp If it uses hash map, the object in use
     * @throws IOException
     */
    public void batchAnnotation(StringBuilder accessions, String taxoLevels, int bulk, HashMap taxoMapp) throws IOException {
        String res = grepBulkAccession(accessions.toString(), bulk);
        String lines[] = res.split("\n");
        String accession = "";
        for (int i = 0; i < lines.length; i++) {
            //if (res.length() > 0) {
            try {
                String taxid = lines[i].split(splitChars)[taxIDCol - 1];
                accession = lines[i].split(splitChars)[1];
                if (taxoMapp.containsKey(taxid)) {
                    if (appendTaxid) {
                        mappwriter.write(accession + outSep + taxoMapp.get(taxid) + outSep + taxid + "\n");
                    } else {
                        mappwriter.write(accession + outSep + taxoMapp.get(taxid) + "\n");
                    }
                    oks++;
                } else {
                    String hierarchy = transacciones.getHirarchyByTaxid(taxid);
                    if (hierarchy.length() > 0) {
                        String taxonomy = transacciones.getLiteralTaxonomy(hierarchy + "," + taxid, taxoLevels, sep);
                        if (appendTaxid) {
                            mappwriter.write(accession + outSep + taxonomy + outSep + taxid + "\n");
                        } else {
                            mappwriter.write(accession + outSep + taxonomy + "\n");
                        }
                        oks++;
                        if (withHashMap) {
                            taxoMapp.put(taxid, taxonomy);
                        }
                    } else {
                        if (notAtDBWriter == null) {
                            notAtDBWriter = new FileWriter(output + ".not_at_db");
                            notAtDBWriter.write("Accession\ttax id\n");
                        }
                        notAtDBWriter.write(accession + "\t" + taxid + "\n");
                        notAtDB++;
                    }
                }
            } catch (IndexOutOfBoundsException iobe) {
                if (lines[i].length() > 1) {//cuando no encuentra nada regresa una linea en blanco y esa cae en este error....
                    System.err.println("No column index: " + taxIDCol + " for grep result: " + lines[i] + "\nLine: " + lines + " Accession: " + accession);
                    if (errorWriter == null) {
                        errorWriter = new FileWriter(output + ".err");
                        errorWriter.write("Line\tAccession\n");
                    }
                    errors++;
                    errorWriter.write(lines + "\n" + accession + "\n");
                }
            }
        }
        if (flagBulkNotFound) {
            if (notFoundWriter == null) {
                notFoundWriter = new FileWriter(output + ".not_found");
                notFoundWriter.write("Line\tAccession\n");
            }
            /**
             * here accessions looks like 'Access1\|Access2\|...\|AccessN'
             */
            String accs[] = accessions.toString().substring(1, accessions.length() - 1).split("\\\\|\\|");
            for (int i = 0; i < accs.length; i++) {
                if (!res.contains(accs[i])) {
                    notFound++;
                    notFoundWriter.write(accs[i] + "\n");
                    if (debug) {
                        System.out.println("ACCESSION NOT FOUND: " + accs[i]);
                    }
                }

            }

        }
    }

    public String getRankType() {
        return rankType;
    }

    public boolean isNotFasta() {
        return notFasta;
    }

    public void setNotFasta(boolean notFasta) {
        this.notFasta = notFasta;
    }

    public void setRankType(String rankType) {
        this.rankType = rankType;
    }

    public String getRankValues() {
        return rankValues;
    }

    public void setRankValues(String rankValues) {
        this.rankValues = rankValues;
    }

    public boolean isFlagBulkNotFound() {
        return flagBulkNotFound;
    }

    public void setFlagBulkNotFound(boolean flagBulkNotFound) {
        this.flagBulkNotFound = flagBulkNotFound;
    }

    public boolean isWithHashMap() {
        return withHashMap;
    }

    public void setWithHashMap(boolean withHashMap) {
        this.withHashMap = withHashMap;
    }

    public String getSep() {
        return sep;
    }

    public void setSep(String sep) {
        this.sep = sep;
    }

}
