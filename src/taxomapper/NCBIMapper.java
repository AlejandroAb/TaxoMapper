/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxomapper;

import bobjects.TaxFilter;
import bobjects.TaxMapp;
import bobjects.Taxon;
import database.Transacciones;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aabdala
 */
public class NCBIMapper {

    private Transacciones transacciones;
    private boolean searchEukaryot = false;
    private HashMap<String, TaxMapp> conflictNodes = new HashMap<String, TaxMapp>();
    boolean debug = false;
    private int levels_to_look = 2; //for how many levels we can continue descending, with 1 you only do it if the blanks are founf within the species assignation...
    private int blanks = 5; //minimun number of blanks to try to reconstruct the path
    private String rp_sufix = "_RP"; //sufix used to annotate recosntructed path.  
    private int max_attempts = 0;
    private boolean discard_not_found = true;
    private boolean verbose = false;
    private String filter = "";
    private boolean completeSP = false;
    private String rankType = "ALL";
    private String rankValues = "";
    private int taxCol = 2;//which column of the spplited line contains the phylum
    private int accCol = 1;//which column of the spplited line contains the accession
    //MapSeq opts
    private String mapSeq_cutoff = "0.00:0.08 0.70:0.35 0.70:0.35 0.70:0.35 0.80:0.25 0.92:0.08 0.95:0.05";
    private String mapSeq_name = "";
    private String mapSeq_levels = "Kingdom Phylum Class Order Family Genus Species";
    boolean mapSeq_out = false;
    boolean disscard_chloroplasts = true;
    public boolean isMapSeq_out() {
        return mapSeq_out;
    }

    public void setMapSeq_out(boolean mapSeq_out) {
        this.mapSeq_out = mapSeq_out;
    }

    public String getRankType() {
        return rankType;
    }

    public int getTaxCol() {
        return taxCol;
    }

    public void setTaxCol(int taxCol) {
        this.taxCol = taxCol;
    }

    public int getAccCol() {
        return accCol;
    }

    public void setAccCol(int accCol) {
        this.accCol = accCol;
    }

    public String getMapSeq_cutoff() {
        return mapSeq_cutoff;
    }

    public void setMapSeq_cutoff(String mapSeq_cutoff) {
        this.mapSeq_cutoff = mapSeq_cutoff;
    }

    public String getMapSeq_name() {
        return mapSeq_name;
    }

    public void setMapSeq_name(String mapSeq_name) {
        this.mapSeq_name = mapSeq_name;
    }

    public String getMapSeq_levels() {
        return mapSeq_levels;
    }

    public void setMapSeq_levels(String mapSeq_levels) {
        this.mapSeq_levels = mapSeq_levels;
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

    public NCBIMapper(Transacciones transacciones) {
        this.transacciones = transacciones;
    }

    public boolean isCompleteSP() {
        return completeSP;
    }

    public void setCompleteSP(boolean completeSP) {
        this.completeSP = completeSP;
    }

    public boolean isDiscard_not_found() {
        return discard_not_found;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setDiscard_not_found(boolean discard_not_found) {
        this.discard_not_found = discard_not_found;
    }

    public String getRp_sufix() {
        return rp_sufix;
    }

    public HashMap<String, TaxMapp> getConflictNodes() {
        return conflictNodes;
    }

    public void setConflictNodes(HashMap<String, TaxMapp> conflictNodes) {
        this.conflictNodes = conflictNodes;
    }

    public int getMax_attempts() {
        return max_attempts;
    }

    public void setMax_attempts(int max_attempts) {
        this.max_attempts = max_attempts;
    }

    public void setRp_sufix(String rp_sufix) {
        this.rp_sufix = rp_sufix;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public int getLevels_to_look() {
        return levels_to_look;
    }

    public void setLevels_to_look(int levels_to_look) {
        this.levels_to_look = levels_to_look;
    }

    public int getBlanks() {
        return blanks;
    }

    public void setBlanks(int blanks) {
        this.blanks = blanks;
    }

    public Transacciones getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(Transacciones transacciones) {
        this.transacciones = transacciones;
    }

    public boolean isSearchEukaryot() {
        return searchEukaryot;
    }

    public void setSearchEukaryot(boolean searchEukaryot) {
        this.searchEukaryot = searchEukaryot;
    }

    /**
     * This method takes n abundance matrix. It supose that the taxonomy path of
     * the matrix is located on the first column, and then process the taxonomy
     * path in order to map the taxons to the NCBI taxonomy DB reference
     *
     * @param fileIn file with thew matrix
     * @param fileOut target output file
     * @param fieldSep the separator between fields in the input matrix file,
     * default tab
     * @param level the taxonomic level reported insuch matrix, possible values
     * are: "kingdom", "phylum", "class", "orden", "family", "genus", "species",
     * "subspecies"
     * @param taxSep the character wich separate the taxons into the taxonomy
     * path. i.e,
     * Bacteria;__Bacteroidetes;__Cytophagia;__Cytophagales;__Cyclobacteriaceae;__g
     * in this case the separator should be ;__
     * @param completeLevels this flag tells the program to append a lower case
     * letter pointing at the level in case that this level is empty or could
     * not be mapped. Taking the previous example, the genus is completed to "g"
     * @param hasHeader if this flag is true, it will take the first line as a
     * header and output it as it is, otherwise it will try to process such
     * header as a regular line in the file
     */
    public void mapAbundanceMatrix(String fileIn, String fileOut, String fieldSep, String level, String taxSep, boolean completeLevels, boolean hasHeader, boolean removeCandidatus, boolean writeTaxid) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileIn));
            if (fieldSep.equals("\\t")) {
                fieldSep = "\t";
            }
            String linea;
            int lnum = 0;
            FileWriter writer = new FileWriter(fileOut);
            FileWriter writer2 = new FileWriter(fileOut + ".detailed");
            FileWriter writer3 = new FileWriter(fileOut + ".different");
            int found = 0;
            int notFound = 0;
            int all = 0;
            String nl = System.getProperty("line.separator");
            //por ahira fixed!!
            String path = getDesiredLinage("CLASSIC", "");
            while ((linea = reader.readLine()) != null) {
                lnum++;
                if (lnum == 1 && hasHeader) {
                    writer.write(linea + nl);
                } else {
                    all++;
                    String phyla = linea.substring(0, linea.indexOf(fieldSep));
                    phyla = phyla.replaceAll("\"", "");
                    boolean foundMatch = false;
                    String taxones[] = phyla.split(taxSep);
                    for (int i = taxones.length - 1; i >= 0; i--) {
                        String name = taxones[i].trim();
                        name = name.replaceAll("_", " ");
                        //name = name.startsWith("__") ? name.substring(2) : name;
                        if (name.length() > 1) {
                            String taxid = transacciones.getProkaTaxidByName(name, i);
                            //String taxid = transacciones.getProkaTaxidByName(name, i);
                            if (taxid.length() == 0 && removeCandidatus && name.contains("Candidatus")) {
                                taxid = transacciones.getProkaTaxidByName(name.replace("Candidatus", "").trim(), i);
                            }
                            if (taxid.length() == 0 && searchEukaryot) {
                                taxid = transacciones.getEukTaxidByName(name.replace("Candidatus", "").trim(), i);
                            }
                            if (taxid.length() == 0 && removeCandidatus && name.contains("Candidatus") && searchEukaryot) {
                                taxid = transacciones.getEukTaxidByName(name.replace("Candidatus", "").trim(), i);
                            }

                            //look for synms
                            if (taxid.length() == 0 && !name.equals("Other") && !name.equals("Unassigned") && !name.equals("Undeffined")) {
                                ArrayList<ArrayList> tmp = transacciones.getNCBISynonim(name);
                                if (tmp != null && tmp.size() > 0) {
                                    taxid = (String) tmp.get(0).get(0);
                                    String correctName = transacciones.getNCBINameByTaxID(taxid);
                                    String synType = (String) tmp.get(0).get(2);
                                    writer3.write("Line: " + lnum + " -- " + name + " Syn: " + correctName + " - Syn Type: " + synType + nl);
                                }
                            }
                            if (taxid.contains(";")) {//hay mas de un taxid
                                taxid = getValidTaxIDFromDuplicates(taxid, new ArrayList<>(Arrays.asList(taxones)));

                            }
                            if (taxid.length() > 0) {
                                ArrayList<ArrayList> niveles = transacciones.getTaxonomicPathByLevels(path, taxid);
                                Taxon t = getTaxonPath(niveles.get(0));
                                if (writeTaxid) {
                                    writer.write(t.toClassicLevelString(level, taxSep, completeLevels));
                                    writer.write(fieldSep);
                                    writer.write(linea.substring(linea.indexOf(fieldSep) + 1) + fieldSep + taxid + nl);
                                } else {
                                    writer.write(t.toClassicLevelString(level, taxSep, completeLevels));
                                    writer.write(fieldSep);
                                    writer.write(linea.substring(linea.indexOf(fieldSep) + 1) + nl);
                                }

                                writer2.write(lnum + ":" + phyla + "   -->   " + t.toClassicLevelString(level, taxSep, completeLevels) + nl);
                                foundMatch = true;
                                break;
                            } else {
                                writer3.write("Line: " + lnum + " -- " + name + " Not found" + nl);
                            }
                        }
                    }
                    if (!foundMatch) {
                        writer.write(linea + nl);
                        writer2.write(lnum + ":" + phyla + "   -->   " + " No match - stay the same" + nl);
                        writer3.write("Line: " + lnum + " -- " + phyla + " No match at any level" + nl);
                    }
                }

            }
            writer.close();
            writer2.close();
            writer3.close();
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("File: " + fileIn + " Does not exist");
        } catch (IOException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mapFastaSilvaFile(String fileIn, String fileOut, String fieldSep, String level, String taxSep, boolean completeLevels, boolean removeCandidatus) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(fileIn));
            //if (fieldSep.equals("\\t")) {
            fieldSep = "\t";
            //}
            String linea;
            int lnum = 0;
            FileWriter writer = new FileWriter(fileOut);
            FileWriter writer2 = new FileWriter(fileOut + ".detailed");
            FileWriter writer3 = new FileWriter(fileOut + ".different");
            int found = 0;
            int notFound = 0;
            int all = 0;
            String nl = System.getProperty("line.separator");
            //por ahora fixed!!
            String path = getDesiredLinage("CLASSIC", "");
            while ((linea = reader.readLine()) != null) {
                lnum++;
                if (linea.startsWith(">")) {
                    all++;
                    int accidx = linea.indexOf(" ");
                    String fullACC = linea.substring(1, accidx);
                    String phyla = linea.substring(accidx + 1).trim();
                    //phyla = phyla.replaceAll("\"", "");
                    boolean foundMatch = false;
                    phyla = phyla.endsWith(taxSep) ? phyla.substring(0, phyla.length() - 1) : phyla;
                    String taxones[] = phyla.split(taxSep);
                    String tmp_sp = "";
                    for (int i = taxones.length - 1; i >= 0; i--) {
                        String name = taxones[i].trim();
                        name = name.replaceAll("_", " ");
                        name = name.replaceAll("'", "\\\\'");
                        String parent = "";
                        if (i >= 1) {
                            parent = taxones[i - 1].trim();
                        }
                        if (name.equals("Homo sapiens (human)")) {
                            name = "Homo sapiens";
                            //bacterium UFLA04-287
                            //bacterium NTL223
                            //bacterium NTL126
                        }
                        //name = name.startsWith("__") ? name.substring(2) : name;Homo sapiens (human)
                        if (name.length() > 1 && !name.contains("unidentified") && !name.contains("uncultured") && !name.contains("metagenome")) {
                            String taxid = transacciones.getProkaTaxidByName(name, i);
                            if (taxid.length() == 0 && removeCandidatus && name.contains("Candidatus")) {
                                taxid = transacciones.getProkaTaxidByName(name.replace("Candidatus", "").trim(), i);
                            }
                            if (taxid.length() == 0 && searchEukaryot) {
                                taxid = transacciones.getEukTaxidByName(name.trim(), i);
                            }
                            if (taxid.length() == 0 && removeCandidatus && name.contains("Candidatus") && searchEukaryot) {
                                taxid = transacciones.getEukTaxidByName(name.replace("Candidatus", "").trim(), i);
                            }
                            //look for synms
                            if (taxid.length() == 0 && !name.equals("Other") && !name.equals("Unassigned") && !name.equals("Undeffined")) {
                                ArrayList<ArrayList> tmp = transacciones.getNCBISynonim(name);

                                if (tmp != null && tmp.size() > 0) {
                                    taxid = (String) tmp.get(0).get(0);
                                    String correctName = transacciones.getNCBINameByTaxID(taxid);
                                    String synType = (String) tmp.get(0).get(2);
                                    writer3.write("Line: " + lnum + " -- " + name + " Syn: " + correctName + " - Syn Type: " + synType + nl);
                                }
                            }
                            if (taxid.contains(";")) {//hay mas de un taxid
                                taxid = getValidTaxIDFromDuplicates(taxid, new ArrayList<>(Arrays.asList(taxones)));

                            }
                            if (taxid.trim().length() > 0) {
                                try {
                                    ArrayList<ArrayList> niveles = transacciones.getTaxonomicPathByLevels(path, taxid);

                                    Taxon t = getTaxonPath(niveles.get(0));
                                    if (tmp_sp.length() > 0) {
                                        t.setSpecies(tmp_sp);
                                    }
                                    writer.write(fullACC + fieldSep);
                                    writer.write(t.toClassicLevelString(level, taxSep, completeLevels));
                                    writer.write(fieldSep + taxid + nl);
                                    //writer.write(linea.substring(linea.indexOf(fieldSep) + 1) + nl);
                                    writer2.write(lnum + ":" + phyla + "   -->   " + t.toClassicLevelString(level, taxSep, completeLevels) + nl);

                                    foundMatch = true;
                                    break;
                                } catch (Exception aio) {
                                    System.err.println("Error on line (" + lnum + "): " + linea);
                                    System.err.println("\tgetTaxonomicPathByLevels ");
                                    System.err.println("\t\tpath: " + path + "\n\t\ttaxid: " + taxid);
                                }
                            } else {
                                writer3.write("Line: " + lnum + " -- " + name + " Not found" + nl);
                            }
                        } else if (i == taxones.length - 1 && (name.contains("unidentified") || name.contains("uncultured"))) {
                            tmp_sp = name;
                        }
                    }
                    if (!foundMatch) {
                        // writer.write(linea + nl);
                        writer.write(fullACC + fieldSep);
                        writer.write(phyla);
                        writer.write(fieldSep + "0" + nl);
                        writer2.write(lnum + ":" + phyla + "   -->   " + " No match - stay the same" + nl);
                        writer3.write("Line: " + lnum + " -- " + phyla + " No match at any level" + nl);
                    }
                }

            }
            writer.close();
            writer2.close();
            writer3.close();
            reader.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("File: " + fileIn + " Does not exist");
        } catch (IOException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void mapRegularFile(String fileIn, String fileOut, String fieldSep, String level, String taxSep, boolean completeLevels, boolean removeCandidatus) {
        try {
            String nl = System.getProperty("line.separator");
            long all_start = System.currentTimeMillis();
            BufferedReader reader = new BufferedReader(new FileReader(fileIn));
            if (fieldSep.equals("\\t")) {
                fieldSep = "\t";
            }
            ArrayList<TaxFilter> filters = null;
            if (filter.length() > 0) {
                filters = processFilter();
            }

            fieldSep = "\t";
            String linea;
            int lnum = 0;
            FileWriter mapWriter = new FileWriter(fileOut + ".map");
            if (mapSeq_out) {
                mapWriter.write("#cutoff:" + mapSeq_cutoff+nl);
                mapWriter.write("#name:" + mapSeq_name+nl);
                mapWriter.write("#levels:" + mapSeq_levels+nl);
            }
            FileWriter disscarded_fastaWriter = new FileWriter(fileOut + ".disscarded");
            FileWriter kingdomWriter = null;
            FileWriter writerBlanks = null;
            FileWriter vWriter = null;
            FileWriter disscardedLog = null;
            FileWriter filterWriter = null;
            // if (verbose) {
            kingdomWriter = new FileWriter(fileOut + ".kingdom");
            filterWriter = new FileWriter(fileOut + ".filtered");
            writerBlanks = new FileWriter(fileOut + ".blank.paths");
            if (verbose) {
                vWriter = new FileWriter(fileOut + ".detailed");
                disscardedLog = new FileWriter(fileOut + ".disscarded.log");
            }
            int found = 0;
            int filtered = 0;
            int notFound = 0;
            int all = 0;
            int kShitf = 0;
            HashMap<Integer, Integer> foundAtCounter = new HashMap<>();

            writerBlanks.write("#ACC\ttaxon\tOriginalPhylo" + nl);

            //por ahora fixed!!
            // String path = getDesiredLinage("CLASSIC", "");
            boolean foundMatch = false;
            boolean taxonFilter = false;
            while ((linea = reader.readLine()) != null) {
                int found_at = 0;
                lnum++;
                boolean shiftK = false;

                taxonFilter = false;
                all++;
                //int accidx = linea.indexOf(" ");
                String fields[] = linea.split(fieldSep);
                String fullACC = fields[accCol - 1];
                String phyla = fields[taxCol - 1];
                int attempts = 0; //kingdom shift
                //phyla = phyla.replaceAll("\"", "");
                foundMatch = false;
                //boolean reconstructed = false; //flag to see if we continue looking in depper taxonos to assign a complete path (case Bacteria;;;;;bacterim NT3370)
                phyla = phyla.endsWith(taxSep) ? phyla.substring(0, phyla.length() - 1) : phyla;
                String taxones[] = phyla.split(taxSep);
                String tmp_sp = "";
                String tmp_taxid = "";
                Taxon tmp_taxon = null;
                for (int i = taxones.length - 1; i >= 0; i--) {
                    found_at++;
                    String name = taxones[i].trim();
                    name = name.replaceAll("_", " ").trim();
                    name = name.replaceAll("'", "\\\\'");
                    /*String parent = "";
                        if (i >= 1) {
                            parent = taxones[i - 1].trim();
                        }
                        /*if (name.equals("Homo sapiens (human)")) {
                            name = "Homo sapiens";
                        }*/
                    //name = name.startsWith("__") ? name.substring(2) : name;Homo sapiens (human)
                    if (name.length() > 1 && !name.toLowerCase().contains("unidentified") && !name.toLowerCase().contains("uncultured")
                            && !name.toLowerCase().contains("metagenome")) {
                        String taxid = transacciones.getTaxPathByName(name.trim(), debug);
                        if (taxid.length() == 0 && removeCandidatus && name.toLowerCase().contains("candidatus")) {
                            taxid = transacciones.getTaxPathByName(name.replace("(?i)candidatus", "").trim(), debug);
                        }
                        if (taxid.length() == 0 && name.indexOf("(") > 1) { //remove parenthesis
                            taxid = transacciones.getTaxPathByName(name.replaceAll("\\(.*\\)", "").trim(), debug);
                        }
                        if (taxid.length() == 0 && name.indexOf("sp.") > 1) { //remove sp. 
                            taxid = transacciones.getTaxPathByName(name.substring(0, name.indexOf("sp.")).trim(), debug);
                        }
                        //look for synms
                        if (taxid.length() == 0 && !name.toLowerCase().equals("other") && !name.toLowerCase().equals("unassigned")
                                && !name.toLowerCase().equals("undeffined") && !name.toLowerCase().equals("unclassified sequences")
                                && !name.toLowerCase().equals("unclassified")) {
                            ArrayList<ArrayList> tmp = transacciones.getNCBISynonim(name);
                            if (tmp != null && tmp.size() > 0) {
                                taxid = transacciones.getTaxPathByTaxid((String) tmp.get(0).get(0), debug);//(String) tmp.get(0).get(0);
                                String correctName = transacciones.getNCBINameByTaxID((String) tmp.get(0).get(0));
                                String synType = (String) tmp.get(0).get(2);
                                if (verbose) {
                                    vWriter.write("Line: " + lnum + "\t" + name + "\tSyn: " + correctName + "\tSyn Type: " + synType + nl);
                                }
                            }
                        }
                        if (taxid.contains(";")) {//hay mas de un taxid
                            //taxid = getValidTaxIDFromDuplicates(taxid, new ArrayList<>(Arrays.asList(taxones)));
                            taxid = getValidTaxIDFromDuplicatesWithHash(name, taxid, new ArrayList<>(Arrays.asList(taxones)), debug);

                        }
                        //no encontro nada subiendo a otro nivel (i.e de sp a genus), pero en un nivel anterios (sp) encontro la clasificacion..con varios blank, pero valida al fin
                        if (taxid.length() == 0 && tmp_taxid.length() > 0) {
                            taxid = tmp_taxid;
                            //       System.out.println("Nothing found for the next level: " + name);
                            //      System.out.println("Returning to first hit: " + tmp_taxid);
                        }
                        if (taxid.trim().length() > 0) {
                            try {
                                //ArrayList<ArrayList> niveles = transacciones.getTaxonomicPathByLevels(path, taxid);
                                // Taxon t = getTaxonPath(niveles.get(0));
                                ArrayList<ArrayList> niveles = transacciones.getCompleteTaxonomicPathByHierarchy((taxid.substring(taxid.indexOf(":") + 1, taxid.lastIndexOf(":"))) + "," + taxid.substring(0, taxid.indexOf(":")));

                                Taxon t = createTaxonWithPath(taxid.substring(0, taxid.indexOf(":")), niveles, filters);
                                t.setCompletSP(completeSP);
                                if (tmp_sp.length() > 0 && t != null) {
                                    t.setSpecies(tmp_sp);
                                }
                                if (t != null && t.isFiltered()) {
                                    filterWriter.write(fullACC + "\t" + phyla + "\t" + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                    filtered++;
                                    taxonFilter = true;//por ahora no sirve de nada...
                                    break;
                                } //Si hay un shift de kingdom superkingdom
                                else if (t != null && !(t.getKingdom() + t.getSuperkingdom()).contains(taxones[0])) {
                                    kingdomWriter.write(fullACC + "\t" + phyla + "\t" + name + "" + t.getTax_id() + "\t" + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                    if (max_attempts <= attempts) {
                                        kShitf++;
                                        shiftK = true;
                                        break;
                                    } else {
                                        attempts++;
                                    }
                                } //getTaxonPath evalua 7 niveles kingdom -> sp 
                                //el caso mas comun que queremos evitar es: Bacteria;p;c;o;f;g;bacterium NTL237
                                else if (t != null && t.getBlanks() >= blanks && found_at <= levels_to_look && i > 0) {
                                    //System.out.println("#To many blanks for ACC : " + fullACC + " TAXON: " + name + " PHYLA: " + phyla + "\n# mapps to" + taxid + ":" + t.toClassicLevelString(level, taxSep, completeLevels));
                                    writerBlanks.write(fullACC + "\t" + name + "\t" + phyla + "\t" + taxid.substring(taxid.indexOf(":")) + ":" + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                    // System.out.println("To many blanks for " + name + " with phyla: "+ phyla + "(" + taxid + "): " + fullACC + " " + t.toClassicLevelString(level, taxSep, completeLevels));
                                    tmp_taxid = taxid;
                                    tmp_taxon = t;
                                    if (i == taxones.length - 1) {
                                        tmp_sp = name + rp_sufix;
                                    }
                                } else {
                                    if (tmp_taxon != null) {
                                        if (tmp_taxon.getNot_blanks() >= t.getNot_blanks()) {
                                            t = tmp_taxon;
                                            //System.out.println("#No better resolution was found");
                                            //writerBlanks.write(taxid);
                                        } else {
                                            writerBlanks.write("Taxon: " + name + "\t" + t.toClassicLevelStringSK(level, taxSep, completeLevels));
                                            //System.out.println("#Taxon: " + name + " resolves better: " + t.toClassicLevelString(level, taxSep, completeLevels));
                                        }
                                    }
                                    mapWriter.write(fullACC + fieldSep);
                                    // mapWriter.write(t.toClassicLevelStringSK(level, taxSep, completeLevels));
                                    String levels[] = {"superkingdom", "phylum", "class", "order", "family", "genus", "species"/*, "subspecies"*/};
                                    mapWriter.write(t.toMapSeqLikeLevelString(levels, taxSep) + nl);
                                    //mapWriter.write(fieldSep + t.getTax_id() + nl);
                                    //fastaWriter.write(">" + fullACC + " ");
                                    //fastaWriter.write(t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                    //fastaWriter.write(fieldSep + taxid + nl);
                                    //writer.write(linea.substring(linea.indexOf(fieldSep) + 1) + nl);
                                    if (verbose) {
                                        vWriter.write("Line: " + lnum + "\t" + name + "\tTAX: " + t.getTax_id() + "\tPath: " + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);

                                    }
                                    foundMatch = true;
                                    found++;
                                    if (foundAtCounter.get(found_at) != null) {
                                        int num = foundAtCounter.get(found_at);
                                        foundAtCounter.remove(found_at);
                                        num++;
                                        foundAtCounter.put(found_at, num);
                                    } else {
                                        foundAtCounter.put(found_at, 1);
                                    }

                                    break;
                                }
                            } catch (Exception aio) {
                                System.err.println("Error on line (" + lnum + "): " + linea);
                                System.err.println("\tgetTaxonomicPathByLevels ");
                                aio.printStackTrace();
                                //   System.err.println("\t\tpath: " + path + "\n\t\ttaxid: " + taxid);
                            }
                        } else {
                            if (verbose) {
                                vWriter.write("Line: " + lnum + "\t" + name + "Not_found" + nl);
                            }
                        }
                    } else if (i == taxones.length - 1 && (name.contains("unidentified") || name.contains("uncultured") || name.contains("metagenome"))) {
                        tmp_sp = name;
                    }
                }
                if (!foundMatch) {

                    // writer.write(linea + nl);
                    if (!discard_not_found) {
                        //     fastaWriter.write(">" + fullACC + " " + phyla + nl);
                        mapWriter.write(fullACC + fieldSep);
                        mapWriter.write(phyla + nl);
                        //mapWriter.write(fieldSep + "0" + nl);
                    } else {
                        disscarded_fastaWriter.write(fullACC + "\t" + phyla + nl);
                    }

                    if (verbose) {
                        if (shiftK) {
                            disscardedLog.write("Discard by kingdom shift\t" + fullACC + "\t" + phyla + nl);
                        } else if (taxonFilter) {
                            disscardedLog.write("Discard by filter\t" + fullACC + "\t" + phyla + nl);
                        } else {
                            notFound++;
                            disscardedLog.write("Discard by no match\t" + fullACC + "\t" + phyla + nl);
                        }
                    }

                    if (foundAtCounter.get(0) != null) {
                        int num = foundAtCounter.get(0);
                        foundAtCounter.remove(0);
                        foundAtCounter.put(0, num++);
                    } else {
                        foundAtCounter.put(0, 1);
                    }
                }

            }//end While read file
            if (verbose) {
                for (HashMap.Entry<String, TaxMapp> entry : conflictNodes.entrySet()) {
                    String key = entry.getKey();
                    TaxMapp tax = entry.getValue();
                    tax.toCmpStdoString();
                    // ...
                }
            }
            System.out.println("-----Some data----");
            Object[] keys = foundAtCounter.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                System.out.println("Paths solved on the " + key + " element: " + foundAtCounter.get((Integer) key));
            }
            System.out.println("\nNumber of processed paths: " + all);
            System.out.println("\nNumber of solved paths: " + found);
            System.out.println("\nNumber of kingdom shifts: " + kShitf);
            System.out.println("\nNumber of paths with no hits: " + notFound);
            System.out.println("\nNumber of filtered sequences: " + filtered);
            long all_end = ((System.currentTimeMillis() - all_start) / 1000);
            System.out.println("Finish in: " + all_end + " s.");
            mapWriter.close();
            if (verbose) {
                vWriter.close();
                disscardedLog.close();
            }
            reader.close();
            kingdomWriter.close();
            writerBlanks.close();
            filterWriter.close();
            disscarded_fastaWriter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("File: " + fileIn + " Does not exist");
        } catch (IOException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Initialize a data structure with all the filters for the search. The
     * spected format of the filter is comma separated string of values, if the
     * user wants to specify the rank to filter, the value should be rank:value,
     * otherwise the filter given by the value will be applied at any taxonomy
     * rank level
     *
     * @return
     */
    public ArrayList<TaxFilter> processFilter() {
        ArrayList<TaxFilter> taxFilter = new ArrayList();
        String splitfilter[] = filter.split(",");
        for (String f : splitfilter) {
            String rank_val[] = f.split(":");
            if (rank_val.length > 1) {
                taxFilter.add(new TaxFilter(rank_val[1], rank_val[0]));
            } else {
                taxFilter.add(new TaxFilter(rank_val[0]));
            }
        }
        return taxFilter;
    }

    public void mapFastaFile(String fileIn, String fileOut, String fieldSep, String level, String taxSep, boolean completeLevels, boolean removeCandidatus) {
        try {
            long all_start = System.currentTimeMillis();
            BufferedReader reader = new BufferedReader(new FileReader(fileIn));
            if (fieldSep.equals("\\t")) {
                fieldSep = "\t";
            }
            ArrayList<TaxFilter> filters = null;
            if (filter.length() > 0) {
                filters = processFilter();
            }

            fieldSep = "\t";
            String linea;
            int lnum = 0;
            FileWriter mapWriter = new FileWriter(fileOut + ".map");
            FileWriter fastaWriter = new FileWriter(fileOut + ".fasta");
            FileWriter disscarded_fastaWriter = new FileWriter(fileOut + ".disscarded.fasta");

            FileWriter kingdomWriter = null;
            FileWriter writerBlanks = null;
            FileWriter vWriter = null;
            FileWriter disscardedLog = null;
            FileWriter filterWriter = null;
            // if (verbose) {
            kingdomWriter = new FileWriter(fileOut + ".kingdom");
            filterWriter = new FileWriter(fileOut + ".filtered");
            writerBlanks = new FileWriter(fileOut + ".blank.paths");
            if (verbose) {
                vWriter = new FileWriter(fileOut + ".detailed");
                disscardedLog = new FileWriter(fileOut + ".disscarded.log");
            }

            // }
            String seq = "";
            int found = 0;
            int filtered = 0;
            int notFound = 0;
            int all = 0;
            int kShitf = 0;
            HashMap<Integer, Integer> foundAtCounter = new HashMap<>();
            String nl = System.getProperty("line.separator");

            writerBlanks.write("#ACC\ttaxon\tOriginalPhylo" + nl);

            //por ahora fixed!!
            // String path = getDesiredLinage("CLASSIC", "");
            boolean foundMatch = false;
            boolean taxonFilter = false;
            while ((linea = reader.readLine()) != null) {
                int found_at = 0;
                lnum++;
                boolean shiftK = false;
                if (linea.startsWith(">")) {
                    if (seq.length() > 0 && (foundMatch || !discard_not_found) && !taxonFilter) {
                        fastaWriter.write(seq + nl);
                        seq = "";
                    } else if (seq.length() > 0) {
                        disscarded_fastaWriter.write(seq + nl);
                        seq = "";
                    }
                    taxonFilter = false;
                    all++;
                    int accidx = linea.indexOf(" ");
                    String fullACC = accidx > 0 ? linea.substring(1, accidx) : linea.substring(1);
                    String phyla = linea.substring(accidx + 1).trim();
                    int attempts = 0; //kingdom shift
                    //phyla = phyla.replaceAll("\"", "");
                    foundMatch = false;
                    //boolean reconstructed = false; //flag to see if we continue looking in depper taxonos to assign a complete path (case Bacteria;;;;;bacterim NT3370)
                    phyla = phyla.endsWith(taxSep) ? phyla.substring(0, phyla.length() - 1) : phyla;
                    String taxones[] = phyla.split(taxSep);
                    String tmp_sp = "";
                    String tmp_taxid = "";
                    Taxon tmp_taxon = null;
                    for (int i = taxones.length - 1; i >= 0; i--) {
                        found_at++;
                        String name = taxones[i].trim();
                        name = name.replaceAll("_", " ");
                        name = name.replaceAll("'", "\\\\'");
                        /*String parent = "";
                        if (i >= 1) {
                            parent = taxones[i - 1].trim();
                        }
                        /*if (name.equals("Homo sapiens (human)")) {
                            name = "Homo sapiens";
                        }*/
                        //name = name.startsWith("__") ? name.substring(2) : name;Homo sapiens (human)
                        if (name.length() > 1 && !name.toLowerCase().contains("unidentified") && !name.toLowerCase().contains("uncultured")
                                && !name.toLowerCase().contains("metagenome")) {
                            String taxid = transacciones.getTaxPathByName(name.trim(), debug);
                            if (taxid.length() == 0 && removeCandidatus && name.toLowerCase().contains("candidatus")) {
                                taxid = transacciones.getTaxPathByName(name.replace("(?i)candidatus", "").trim(), debug);
                            }
                            if (taxid.length() == 0 && name.indexOf("(") > 1) { //remove parenthesis
                                taxid = transacciones.getTaxPathByName(name.replaceAll("\\(.*\\)", "").trim(), debug);
                            }
                            if (taxid.length() == 0 && name.indexOf("sp.") > 1) { //remove sp. 
                                taxid = transacciones.getTaxPathByName(name.substring(0, name.indexOf("sp.")).trim(), debug);
                            }
                            //look for synms
                            if (taxid.length() == 0 && !name.toLowerCase().equals("other") && !name.toLowerCase().equals("unassigned")
                                    && !name.toLowerCase().equals("undeffined") && !name.toLowerCase().equals("unclassified sequences")
                                    && !name.toLowerCase().equals("unclassified")) {
                                ArrayList<ArrayList> tmp = transacciones.getNCBISynonim(name);
                                if (tmp != null && tmp.size() > 0) {
                                    taxid = transacciones.getTaxPathByTaxid((String) tmp.get(0).get(0), debug);//(String) tmp.get(0).get(0);
                                    String correctName = transacciones.getNCBINameByTaxID((String) tmp.get(0).get(0));
                                    String synType = (String) tmp.get(0).get(2);
                                    if (verbose) {
                                        vWriter.write("Line: " + lnum + "\t" + name + "\tSyn: " + correctName + "\tSyn Type: " + synType + nl);
                                    }
                                }
                            }
                            if (taxid.contains(";")) {//hay mas de un taxid
                                //taxid = getValidTaxIDFromDuplicates(taxid, new ArrayList<>(Arrays.asList(taxones)));
                                taxid = getValidTaxIDFromDuplicatesWithHash(name, taxid, new ArrayList<>(Arrays.asList(taxones)), debug);

                            }
                            //no encontro nada subiendo a otro nivel (i.e de sp a genus), pero en un nivel anterios (sp) encontro la clasificacion..con varios blank, pero valida al fin
                            if (taxid.length() == 0 && tmp_taxid.length() > 0) {
                                taxid = tmp_taxid;
                                //       System.out.println("Nothing found for the next level: " + name);
                                //      System.out.println("Returning to first hit: " + tmp_taxid);
                            }
                            if (taxid.trim().length() > 0) {
                                try {
                                    //ArrayList<ArrayList> niveles = transacciones.getTaxonomicPathByLevels(path, taxid);
                                    // Taxon t = getTaxonPath(niveles.get(0));
                                    ArrayList<ArrayList> niveles = transacciones.getCompleteTaxonomicPathByHierarchy((taxid.substring(taxid.indexOf(":") + 1, taxid.lastIndexOf(":"))) + "," + taxid.substring(0, taxid.indexOf(":")));

                                    Taxon t = createTaxonWithPath(taxid.substring(0, taxid.indexOf(":")), niveles, filters);
                                    t.setCompletSP(completeSP);
                                    if (tmp_sp.length() > 0 && t != null) {
                                        t.setSpecies(tmp_sp);
                                    }
                                    if (t != null && t.isFiltered()) {
                                        filterWriter.write(fullACC + "\t" + phyla + "\t" + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                        filtered++;
                                        taxonFilter = true;//por ahora no sirve de nada...
                                        break;
                                    } //Si hay un shift de kingdom superkingdom
                                    else if (t != null && !(t.getKingdom() + t.getSuperkingdom()).contains(taxones[0])) {
                                        kingdomWriter.write(fullACC + "\t" + phyla + "\t" + name + "" + t.getTax_id() + "\t" + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                        if (max_attempts <= attempts) {
                                            kShitf++;
                                            shiftK = true;
                                            break;
                                        } else {
                                            attempts++;
                                        }
                                    } //getTaxonPath evalua 7 niveles kingdom -> sp 
                                    //el caso mas comun que queremos evitar es: Bacteria;p;c;o;f;g;bacterium NTL237
                                    else if (t != null && t.getBlanks() >= blanks && found_at <= levels_to_look && i > 0) {
                                        //System.out.println("#To many blanks for ACC : " + fullACC + " TAXON: " + name + " PHYLA: " + phyla + "\n# mapps to" + taxid + ":" + t.toClassicLevelString(level, taxSep, completeLevels));
                                        writerBlanks.write(fullACC + "\t" + name + "\t" + phyla + "\t" + taxid.substring(taxid.indexOf(":")) + ":" + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                        // System.out.println("To many blanks for " + name + " with phyla: "+ phyla + "(" + taxid + "): " + fullACC + " " + t.toClassicLevelString(level, taxSep, completeLevels));
                                        tmp_taxid = taxid;
                                        tmp_taxon = t;
                                        if (i == taxones.length - 1) {
                                            tmp_sp = name + rp_sufix;
                                        }
                                    } else {
                                        if (tmp_taxon != null) {
                                            if (tmp_taxon.getNot_blanks() >= t.getNot_blanks()) {
                                                t = tmp_taxon;
                                                //System.out.println("#No better resolution was found");
                                                //writerBlanks.write(taxid);
                                            } else {
                                                writerBlanks.write("Taxon: " + name + "\t" + t.toClassicLevelStringSK(level, taxSep, completeLevels));
                                                //System.out.println("#Taxon: " + name + " resolves better: " + t.toClassicLevelString(level, taxSep, completeLevels));
                                            }
                                        }
                                        mapWriter.write(fullACC + fieldSep);
                                        mapWriter.write(t.toClassicLevelStringSK(level, taxSep, completeLevels));
                                        mapWriter.write(fieldSep + t.getTax_id() + nl);
                                        fastaWriter.write(">" + fullACC + " ");
                                        fastaWriter.write(t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);
                                        //fastaWriter.write(fieldSep + taxid + nl);
                                        //writer.write(linea.substring(linea.indexOf(fieldSep) + 1) + nl);
                                        if (verbose) {
                                            vWriter.write("Line: " + lnum + "\t" + name + "\tTAX: " + t.getTax_id() + "\tPath: " + t.toClassicLevelStringSK(level, taxSep, completeLevels) + nl);

                                        }
                                        foundMatch = true;
                                        found++;
                                        if (foundAtCounter.get(found_at) != null) {
                                            int num = foundAtCounter.get(found_at);
                                            foundAtCounter.remove(found_at);
                                            num++;
                                            foundAtCounter.put(found_at, num);
                                        } else {
                                            foundAtCounter.put(found_at, 1);
                                        }

                                        break;
                                    }
                                } catch (Exception aio) {
                                    System.err.println("Error on line (" + lnum + "): " + linea);
                                    System.err.println("\tgetTaxonomicPathByLevels ");
                                    //   System.err.println("\t\tpath: " + path + "\n\t\ttaxid: " + taxid);
                                }
                            } else {
                                if (verbose) {
                                    vWriter.write("Line: " + lnum + "\t" + name + "Not_found" + nl);
                                }
                            }
                        } else if (i == taxones.length - 1 && (name.contains("unidentified") || name.contains("uncultured") || name.contains("metagenome"))) {
                            tmp_sp = name;
                        }
                    }
                    if (!foundMatch) {

                        // writer.write(linea + nl);
                        if (!discard_not_found) {
                            fastaWriter.write(">" + fullACC + " " + phyla + nl);
                            mapWriter.write(fullACC + fieldSep);
                            mapWriter.write(phyla);
                            mapWriter.write(fieldSep + "0" + nl);
                        } else {
                            disscarded_fastaWriter.write(">" + fullACC + " " + phyla + nl);
                        }

                        if (verbose) {
                            if (shiftK) {
                                disscardedLog.write("Discard by kingdom shift\t" + fullACC + "\t" + phyla + nl);
                            } else if (taxonFilter) {
                                disscardedLog.write("Discard by filter\t" + fullACC + "\t" + phyla + nl);
                            } else {
                                notFound++;
                                disscardedLog.write("Discard by no match\t" + fullACC + "\t" + phyla + nl);
                            }
                        }

                        if (foundAtCounter.get(0) != null) {
                            int num = foundAtCounter.get(0);
                            foundAtCounter.remove(0);
                            foundAtCounter.put(0, num++);
                        } else {
                            foundAtCounter.put(0, 1);
                        }
                    }
                } else { //if stars with > 
                    seq += linea;
                }

            }//end While read file
            if (seq.length() > 0 && (foundMatch || !discard_not_found) && !taxonFilter) {
                fastaWriter.write(seq + nl);
                seq = "";
            }
            for (HashMap.Entry<String, TaxMapp> entry : conflictNodes.entrySet()) {
                String key = entry.getKey();
                TaxMapp tax = entry.getValue();
                tax.toCmpStdoString();
                // ...
            }
            System.out.println("-----Some data----");
            Object[] keys = foundAtCounter.keySet().toArray();
            Arrays.sort(keys);
            for (Object key : keys) {
                System.out.println("Paths solved on the " + key + " element: " + foundAtCounter.get((Integer) key));
            }
            System.out.println("\nNumber of processed paths: " + all);
            System.out.println("\nNumber of solved paths: " + found);
            System.out.println("\nNumber of kingdom shifts: " + kShitf);
            System.out.println("\nNumber of paths with no hits: " + notFound);
            System.out.println("\nNumber of filtered sequences: " + filtered);
            long all_end = ((System.currentTimeMillis() - all_start) / 1000);
            System.out.println("Finish in: " + all_end + " s.");
            mapWriter.close();
            if (verbose) {
                vWriter.close();
                disscardedLog.close();
            }
            reader.close();
            fastaWriter.close();
            kingdomWriter.close();
            writerBlanks.close();
            filterWriter.close();
            disscarded_fastaWriter.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("File: " + fileIn + " Does not exist");
        } catch (IOException ex) {
            Logger.getLogger(NCBIMapper.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String getValidTaxIDFromDuplicatesSimple(String allPaths, String parent) {
        String taxid = "";
        String results[] = allPaths.split(";");
        int found = 0;
        //String lastres = "";
        for (String result : results) {
            String taxpath = result.substring(result.indexOf(":") + 1);
            if (transacciones.isNameOnPath(parent, taxpath)) {
                found++;
                taxid = result.substring(0, result.indexOf(":"));
            }
        }
        if (found > 1) {
            System.out.println("Different taxid same parent!!");
        } else if (found == 0) {
            System.out.println("Any parent found at the linage!!");
        }

        return taxid;
    }

    public String getValidTaxIDFromDuplicates(String allPaths, ArrayList<String> silvapath) {
        String taxid = "";
        String results[] = allPaths.split(";");
        int max = 0;
        int i = 0;
        int index = 0;

        System.out.print("\t-Silva taxonomy:\n\t  ");
        for (String path : silvapath) {
            System.out.print(path + ";");
        }
        System.out.println("");
        for (String result : results) {
            String taxpath = result.substring(result.indexOf(":") + 1);
            String ncbi_path = transacciones.getLiteralTaxonomy(taxpath, "", ";");
            int intersect = intersect(silvapath, ncbi_path);
            if (intersect > max) {
                max = intersect;
                index = i;
            }

            i++;
            System.out.println("\t-tax id: " + result.substring(0, result.indexOf(":")));
            System.out.println("\t  " + ncbi_path);
            System.out.println("\t  #Intersected taxons: " + intersect);

        }

        if (max == 0) {
            System.out.println("Any parent found at the linage!!");

        } else {
            taxid = results[index].substring(0, results[index].indexOf(":"));
            System.out.println("--Selection: " + taxid);
        }
        System.out.println("***********************************************");

        return taxid;
    }

    /**
     * This method evaluate the result, whenever a taxon name retrieves more
     * than one entry for the database.
     *
     * @param entry the name of the taxon
     * @param taxids semicolon separated values of the taxid and the hirarchy in
     * the form: taxid:hierarchy. i.e:173374:1,131567,2,2323,49928
     * @param originalpath The complete taxonomy that it is been evaluated.
     * @return the selected taxid to properly describe the taxon given by the
     * entry.
     */
    public String getValidTaxIDFromDuplicatesWithHash(String entry, String taxids, ArrayList<String> originalpath, boolean debug) {
        String taxid = "";
        String taxones[] = taxids.split(";");
        int max = 0;
        int i = 0;
        int index = 0;
        if (conflictNodes.containsKey(originalpath.get(0) + ":" + entry)) {
            conflictNodes.get(originalpath.get(0) + ":" + entry).addHit();
            return "" + conflictNodes.get(originalpath.get(0) + ":" + entry).getSelectedTaxon().getTaxonSummary();

        }
        TaxMapp tmap = new TaxMapp(entry);
        if (debug) {
            System.out.print("\t-Original taxonomy:\n\t  ");
        }
        String p = "";
        for (String path : originalpath) {
            p += p.length() == 0 ? path : ";" + path;
            if (debug) {
                System.out.print(path + ";");
            }
        }
        tmap.setPath(p);
        if (debug) {
            System.out.println("");
        }
        for (String tid : taxones) {
            String taxpath = tid.substring(tid.indexOf(":") + 1, tid.lastIndexOf(":"));
            String div_id = tid.substring(tid.lastIndexOf(":") + 1);
            String ncbi_path = transacciones.getLiteralTaxonomy(taxpath + "," + tid.substring(0, tid.indexOf(":")), "", ";");
            int intersect = intersect(originalpath, ncbi_path);
            if (intersect > max) {
                max = intersect;
                index = i;
            }
            Taxon t = new Taxon(Integer.parseInt(tid.substring(0, tid.indexOf(":"))));
            t.setDivID(div_id);
            t.setTaxidPath(taxpath);
            t.setInterseptedNodes(intersect);
            t.setPath(ncbi_path);
            tmap.addTaxon(t);
            i++;
            if (debug) {
                System.out.println("\t-tax id: " + tid.substring(0, tid.indexOf(":")));
                System.out.println("\t  " + ncbi_path);
                System.out.println("\t  #Intersected taxons: " + intersect);
            }

        }
        if (max == 0) {
            System.out.println("Any parent found at the linage: " + p+"\nAssessing the path with entry: "+entry);      
            taxid = taxones[0];//recien agregado
            tmap.setSelectedTaxon(taxones[0].substring(0, taxones[0].indexOf(":")));//recien agregado
        } else {
            taxid = taxones[index];
            if (debug) {
                System.out.println("--Selection: " + taxones[index].substring(0, taxones[index].indexOf(":")));
            }
            tmap.setSelectedTaxon(taxones[index].substring(0, taxones[index].indexOf(":")));
            //taxid = taxones[index];
        }
        conflictNodes.put(originalpath.get(0) + ":" + entry, tmap);
        //System.out.println("***********************************************");

        return taxid;
    }

    private int intersect(ArrayList<String> silva_path, String ncbi_path) {
        int intersecs = 0;
        for (String taxon : silva_path) {
            if (ncbi_path.contains(taxon)) {
                intersecs++;
            }
        }
        return intersecs;
    }

    public Taxon getTaxonPath(ArrayList<String> taxon) {
        String levels[] = {"kingdom", "phylum", "class", "order", "family", "genus", "species"/*, "subspecies"*/};
        Taxon t = new Taxon(0);
        int i = 0;
        for (String rank : levels) {
            t.assignRank(taxon.get(i), rank);
            if (taxon.get(i).trim().length() < 1) {
                t.incrementBlank();
            } else {
                t.incrementNotBlank();;
            }
            i++;
        }
        return t;
    }

    public Taxon createTaxonWithPath(String taxid, ArrayList<ArrayList> taxones, ArrayList<TaxFilter> filters) {
        try {

            // ArrayList<ArrayList> niveles = transacciones.getCompleteTaxonomicPathByHierarchy(path);
            Taxon t = new Taxon(Integer.parseInt(taxid.trim()));
            for (ArrayList<String> tax : taxones) {
                t.assignRank(tax.get(1), tax.get(2));
                if (filters != null && !t.isFiltered()) {
                    for (TaxFilter f : filters) {
                        if (f.getRankFilter().equals("any")) {
                            if (tax.get(1).toLowerCase().equals(f.getFilter().toLowerCase())) {
                                t.setFiltered(true);
                                break;
                            }
                        } else {
                            if (tax.get(1).toLowerCase().equals(f.getFilter().toLowerCase())
                                    && tax.get(2).toLowerCase().equals(f.getRankFilter().toLowerCase())) {
                                t.setFiltered(true);
                                break;
                            }
                        }
                    }
                }
            }
            String levels[] = {"superkingdom", "phylum", "class", "order", "family", "genus", "species"/*, "subspecies"*/};
            t.computeBlanks(levels);
            return t;
        } catch (Exception e) {
            System.err.println("Error creating taxon: taxid="+taxid);
            return null;
        }
    }

    public Taxon createTaxonWithPathFillBlanks(String taxid, ArrayList<ArrayList> taxones, ArrayList<TaxFilter> filters) {
        try {

            // ArrayList<ArrayList> niveles = transacciones.getCompleteTaxonomicPathByHierarchy(path);
            Taxon t = new Taxon(Integer.parseInt(taxid.trim()));
            for (ArrayList<String> tax : taxones) {
                t.assignRank(tax.get(1), tax.get(2));
                if (filters != null && !t.isFiltered()) {
                    for (TaxFilter f : filters) {
                        if (f.getRankFilter().equals("any")) {
                            if (tax.get(1).toLowerCase().equals(f.getFilter().toLowerCase())) {
                                t.setFiltered(true);
                                break;
                            }
                        } else {
                            if (tax.get(1).toLowerCase().equals(f.getFilter().toLowerCase())
                                    && tax.get(2).toLowerCase().equals(f.getRankFilter().toLowerCase())) {
                                t.setFiltered(true);
                                break;
                            }
                        }
                    }
                }
            }
            String levels[] = {"superkingdom", "phylum", "class", "order", "family", "genus", "species"/*, "subspecies"*/};
            t.computeBlanks(levels);
            return t;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * This method take the rankType class attribute and according to its value
     * determines the linage to be searched on the DB
     *
     * @return a partial query string with the taxonomic rank to be extracted
     * from the DB
     */
    public String getDesiredLinage(String rankType, String rankValues) {
        String taxoLevels = "";//ranktype ALL = no extra
        if (rankType.equals("KNOWN")) {//REMOVES NO RANK
            taxoLevels = " 'kingdom','superkingdom','subkingdom','superphylum','phylum','subphylum','superclass','infraclass','class','subclass','parvorder','superorder','infraorder','order','suborder','superfamily','family','subfamily','tribe','subtribe','genus','subgenus','species','species group','species subgroup','subspecies','forma','varietas')";
        } else if (rankType.equals("CLASSIC")) {
            //taxoLevels = "'kingdom','superkingdom','phylum','class','order','family','genus','species','subspecies')";
            taxoLevels = " kingdom,phylum,class,orden,family,genus,species,subspecies";
        } else if (rankType.equals("CUSTOM")) {
            taxoLevels = "";
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

    public String getSQLFieldsForMatrixByLabel(String nivel, String sep) {
        if (nivel.toLowerCase().equals("kingdom")) {
            return " DISTINCT (kingdom)";
        } else if (nivel.toLowerCase().equals("phylum")) {
            return " DISTINCT (CONCAT(kingdom ,'" + sep + "', phylum ))";
        } else if (nivel.toLowerCase().equals("class")) {
            return " DISTINCT (CONCAT(kingdom ,'" + sep + "', phylum,'" + sep + "',class ))";
        } else if (nivel.toLowerCase().equals("orden")) {
            return " DISTINCT (CONCAT(kingdom ,'" + sep + "', phylum,'" + sep + "',class,'" + sep + "',orden))";
        } else if (nivel.toLowerCase().equals("family")) {
            return " DISTINCT (CONCAT(kingdom ,'" + sep + "', phylum,'" + sep + "',class,'" + sep + "',orden,'" + sep + "',family))";
        } else if (nivel.toLowerCase().equals("genus")) {
            return " DISTINCT (CONCAT(kingdom ,'" + sep + "', phylum,'" + sep + "',class,'" + sep + "',orden,'" + sep + "',family,'" + sep + "',genus))";
        } else if (nivel.toLowerCase().equals("species")) {
            return " DISTINCT (CONCAT(kingdom ,'" + sep + "', phylum,'" + sep + "',class,'" + sep + "',orden,'" + sep + "',family,'" + sep + "',genus,'" + sep + "',species))";
        } else {
            return "";
        }
    }
}
