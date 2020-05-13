/**
 * Esta clase esta creada para procesar archivos del ftp de NCBI y compararlos o
 * usarlos para obtener información anotada en la BD de nuestro grupo
 *
 * @author Alejandro Abdala
 * @version 1.0
 * @date Septiembre 2015
 */
package taxomapper;

import database.Transacciones;
import bobjects.NCBINode;
import bobjects.NCBISyn;
import bobjects.Taxon;
//import dbPersistantObjects.Phylo;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.StringUtils;
//import utils.Config;
//import utils.FileUtils;

/**
 * This class is created for the creation and maintenance of the taxonomic data
 * base implemented on MySQL
 *
 * @author Alejandro
 */
public class NCBITaxCreator {

    Map<String, NCBINode> dataFile; //hash map con información de los nodos taxxonomicos del ncbi
    Transacciones transacciones; //conexion a la BD
    // Config cfg;

    public NCBITaxCreator(Transacciones transacciones) {
        this.transacciones = transacciones;
    }

    public NCBITaxCreator() {
    }

    /**
     * This method is in charge of reading ncbi taxonomy dump files and with
     * that create the database information
     *
     * @param nodesFileName nodes.dmp taken from:
     * ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdump.zip
     * @param namesFileName names.dmp taken from:
     * ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdump.zip
     */
    public String createTaxaListFromNCBI(String nodesFileName, String namesFileName, boolean toFile) {
        dataFile = new HashMap<String, NCBINode>();
        String log = "";
        try {
            BufferedReader readerNodos = new BufferedReader(new FileReader(nodesFileName));
            BufferedReader readerNames = new BufferedReader(new FileReader(namesFileName));
            String linea;
            long start = System.currentTimeMillis();
            double numLinea = 0;
            double count = 0;
            System.out.println("Processing nodes.dmp");
            /**
             * Este ciclo lee y procesa el archivo nodes.dmp Popula por primera
             * vez la tabla hash dataFile
             */
            while ((linea = readerNodos.readLine()) != null) {
                numLinea++;
                count++;
                StringTokenizer st = new StringTokenizer(linea, "|\t");
                String taxID = st.nextToken().trim();
                String parentTaxID = st.nextToken().trim();
                String rankName = st.nextToken().trim();
                //cuando no hay dato viene |\t\t| entonces todo lo lee como delim 
                //por lo cual se puede prodeucir un desfaz. el smbl_code no se usa pero
                //sirve de ayuda ya que esta antes del div_id y tienen que ser dos letras
                //si el valor es numérico en lugar de caracteres, se supone que ya es div_id
                String embCode = st.nextToken().trim();
                String div_id = st.nextToken().trim();
                try {
                    Integer.parseInt(embCode);
                    div_id = embCode;
                } catch (NumberFormatException nfe) {

                }
                NCBINode node = new NCBINode(taxID);
                node.setParent_tax_id(parentTaxID);
                node.setRank(rankName);
                node.setDiv_code(div_id);
                dataFile.put(taxID, node);
            }
            readerNodos.close();
            long finish = System.currentTimeMillis() - start;
            System.out.println("Taxonomic nodes:" + dataFile.size());
            System.out.println("Total time processing taxonomic nodes: " + finish / 1000 + " s.");
            System.out.println("************************************************");
            start = System.currentTimeMillis();
            numLinea = 0;
            count = 0;
            System.out.println("Processing names.dmp");
            /**
             * Este ciclo lee el archivo names, el cual tiene muchos nombres
             * para cada nodo taxonomico, por lo cual se busca aquel que esté
             * descrito como scientific name y ese es asociado al objeto
             * NCBINode del hashmpa dataFile
             */
            NCBINode node;
            int scientificNames = 0;
            int synonymNames = 0;
            while ((linea = readerNames.readLine()) != null) {
                numLinea++;
                count++;
                StringTokenizer st = new StringTokenizer(linea, "|\t");
                String taxIDName = st.nextToken().trim();
                String name = st.nextToken().trim().replaceAll("'", "\\\\'");
                String nameType = st.nextToken().trim();
                //en algunos casos hay una columna intermedia entre el tipo de nombre y el nombre
                //en esos casos hay que hacer la lectura de un token "extra"
                if (st.countTokens() > 0) {
                    nameType = st.nextToken().trim();
                }
                if (nameType.equals("scientific name")) {
                    node = dataFile.get(taxIDName);
                    if (node != null) {
                        node.setName(name);
                        node.setClass_name(nameType);
                        dataFile.put(taxIDName, node);
                        scientificNames++;
                    } else {
                        System.out.println("No node entry for: " + taxIDName);
                    }
                } else {
                    node = dataFile.get(taxIDName);
                    if (node != null) {
                        NCBISyn syn = new NCBISyn(taxIDName);
                        syn.setClass_name(nameType);
                        syn.setSynonim(name);
                        node.addSyn(syn);
                        dataFile.put(taxIDName, node);
                        synonymNames++;
                    } else {
                        System.out.println("No node entry for: " + taxIDName);
                    }
                }
            }
            readerNames.close();
            System.gc();
            finish = System.currentTimeMillis() - start;
            System.out.println("Names processed: " + numLinea);
            System.out.println("Scientific names assigned: " + scientificNames);
            System.out.println("Synonym names assigned: " + synonymNames);
            System.out.println("Total time processing taxonomic names: " + finish / 1000 + " s.");
            System.out.println("************************************************");
            System.out.println("Create and store organism for DB....");

            start = System.currentTimeMillis();
            int totOK = 0;
            //NCBINode node;
            //NCBINode tmpNode;
            //String hierarchy;
            //Set<String> keys = dataFile.keySet().toArray();
            Object keys[] = dataFile.keySet().toArray();
            // for (String key : dataFile.keySet()) {
            for (int i = 0; i < keys.length; i++) {
                node = dataFile.get(keys[i].toString());
                String hierarchy = node.getParent_tax_id().trim();
                NCBINode tmpNode = node;
                int counts = 0;
                while (tmpNode.getParent_tax_id() != null && !tmpNode.getParent_tax_id().equals("1") && !tmpNode.getParent_tax_id().equals(tmpNode.getTax_id())) {
                    counts++;
                    if (counts == 100) {
                        System.out.println("100 parents! NODE: " + node.getName() + " " + node.getHirarchy());
                    }
                    tmpNode = dataFile.get(tmpNode.getParent_tax_id());
                    hierarchy = tmpNode.getParent_tax_id().trim() + "," + hierarchy;
                }
                node.setHirarchy(hierarchy);
                boolean ok = transacciones.insertaQuery(node.toSQLInsertString());
                //boolean ok = transacciones.updateHierarchyNCBINode(node.getTax_id(), node.getHirarchy());
                if (ok) {
                    totOK++;
                    for (NCBISyn syn : node.getSynms()) {
                        ok &= transacciones.insertaQuery(syn.toSQLInsertString());
                        if (!ok) {
                            //  log += "Error insertando NCBI_SYN: " + syn.toString();
                            System.err.println("Error insertando NCBI_SYN: " + syn.toString());
                        }
                    }
                } else {
                    //log += "Error insertando NCBI_NODE: " + node.toString();
                    System.err.println("Error insertando NCBI_NODE: " + node.toString());
                }
            }

            finish = System.currentTimeMillis() - start;
            System.out.println("Total time: " + finish / 1000 + " s.");
            System.out.println("************************************************");
            System.out.println("Total de nodos procesados: " + this.dataFile.keySet().size()
                    + "\nTotal de nodos insertados: " + totOK
                    + "\nTotal de nodos descartados: " + (this.dataFile.keySet().size() - totOK));
            System.out.println("************************************************");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBITaxCreator.class.getName()).log(Level.SEVERE, null, ex);
            return "Error no se encontro archivo de entrada:\n" + ex.getLocalizedMessage();
        } catch (IOException ex) {
            Logger.getLogger(NCBITaxCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return log;
    }

    /**
     * Method for loading the NCBI's merged nodes into the database. At the end
     * the database store the reference of the old tax id and the new or merged
     * tax_id into the ncbi_merged table. the merged tax_id needs to have it's
     * corresponding node at ncbi_node table.
     *
     * @param mergeFile merge.dmp taken from:
     * ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdump.zip
     */
    public void loadMergedTaxs(String mergeFile) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(mergeFile));
            String linea;
            int numLines = 0;
            int errs = 0;
            int inserted = 0;
            while ((linea = reader.readLine()) != null) {
                String taxs[] = linea.split("[ \\|\t]+"/*" |\\||\t"*/);
                numLines++;
                if (taxs.length > 1) {
                    String oldTaxid = taxs[0];
                    String newTaxid = taxs[1];
                    String query = "INSERT INTO ncbi_merged(old_tax_id,tax_id) VALUES(" + oldTaxid + ", " + newTaxid + ") ON DUPLICATE KEY UPDATE tax_id='"+newTaxid+"'";
                    if (!transacciones.insertaQuery(query)) {
                        System.err.println("Error insertando: " + query);
                        errs++;
                    } else {
                        inserted++;
                    }
                } else {
                    errs++;
                }
            }
            System.out.println("######################################");
            System.out.println("#Nodes processed: " + numLines);
            System.out.println("#Nodes inserted: " + inserted);
            System.out.println("#Lines or nodes with errors: " + errs);
            reader.close();
        } catch (FileNotFoundException ex) {
            //Logger.getLogger(NCBITaxCreator.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("File with merged nodes is incorrect, check that the file exists: " + mergeFile);
            System.exit(1);
        } catch (IOException ioe) {
            System.err.println("Problems reading file: " + mergeFile);
        }
    }

    /**
     * While method createTaxaListFromNCBI is implemented in order to populate
     * ncbi_node, ncbi_div and ncbi_syn from scratch, this method is created to
     * update those references from the same NCBI's input files
     *
     * @param nodesFileName
     * @param namesFileName
     * @param toFile
     * @return
     */
    public String updateTaxaListFromNCBI(String nodesFileName, String namesFileName, boolean toFile) {
        dataFile = new HashMap<String, NCBINode>();
        //String log = "";
        StringUtils su = new StringUtils();
        try {
            BufferedReader readerNodos = new BufferedReader(new FileReader(nodesFileName));
            BufferedReader readerNames = new BufferedReader(new FileReader(namesFileName));
            String linea;
            long start = System.currentTimeMillis();
            double numLinea = 0;
            double count = 0;
            System.out.println("Processing nodes.dmp");
            /**
             * Este ciclo lee y procesa el archivo nodes.dmp Popula por primera
             * vez la tabla hash dataFile
             */
            while ((linea = readerNodos.readLine()) != null) {
                numLinea++;
                count++;
                String nodeLine[] = linea.split("[\\|\t]+");
                // StringTokenizer st = new StringTokenizer(linea, "|\t");
                String taxID = nodeLine[0].trim();
                /*st.nextToken().trim();*/
                String parentTaxID = nodeLine[1].trim();/*st.nextToken().trim();*/
                String rankName = nodeLine[2].trim();/*st.nextToken().trim();*/
                //cuando no hay dato viene |\t\t| entonces todo lo lee como delim 
                //por lo cual se puede prodeucir un desfaz. el smbl_code no se usa pero
                //sirve de ayuda ya que esta antes del div_id y tienen que ser dos letras
                //si el valor es numérico en lugar de caracteres, se supone que ya es div_id
                String embCode = nodeLine[3].trim();/*st.nextToken().trim();*/
                String div_id = nodeLine[4].trim();/*st.nextToken().trim();*/
                try {
                    Integer.parseInt(embCode);
                    div_id = embCode;
                } catch (NumberFormatException nfe) {

                }
                if (transacciones.testForTaxid(taxID).length() == 0) {
                    NCBINode node = new NCBINode(taxID);
                    node.setParent_tax_id(parentTaxID);
                    node.setRank(rankName);
                    node.setDiv_code(div_id);
                    dataFile.put(taxID, node);
                }
            }
            readerNodos.close();
            long finish = System.currentTimeMillis() - start;
            System.out.println("New taxonomic nodes:" + dataFile.size());
            System.out.println("Total time processing taxonomic nodes: " + finish / 1000 + " s.");
            System.out.println("************************************************");
            start = System.currentTimeMillis();
            numLinea = 0;
            count = 0;
            System.out.println("Processing names.dmp");
            /**
             * Este ciclo lee el archivo names, el cual tiene muchos nombres
             * para cada nodo taxonomico, por lo cual se busca aquel que esté
             * descrito como scientific name y ese es asociado al objeto
             * NCBINode del hashmpa dataFile
             */
            NCBINode node;
            int scientificNames = 0;
            int synonymNames = 0;
            while ((linea = readerNames.readLine()) != null) {
                numLinea++;
                count++;
                //StringTokenizer st = new StringTokenizer(linea, "|\t");
                String nameLine[] = linea.split("[\\|\t]+");
                String taxIDName = nameLine[0].trim();/*st.nextToken().trim();*/
                if (transacciones.testForTaxid(taxIDName).length() == 0) {
                    String name = nameLine[1].trim();/*st.nextToken().trim().replaceAll("'", "\\\\'");*/
                    String nameType = nameLine[2].trim();/*st.nextToken().trim();*/
                    //en algunos casos hay una columna intermedia entre el tipo de nombre y el nombre
                    //en esos casos hay que hacer la lectura de un token "extra"
                    if (nameLine.length > 3) {
                        nameType = nameLine[3].trim();
                    }
                    if (nameType.equals("scientific name")) {
                        node = dataFile.get(taxIDName);
                        if (node != null) {
                            node.setName(su.scapeSQL(name));
                            node.setClass_name(nameType);
                            dataFile.put(taxIDName, node);
                            scientificNames++;
                        } else {
                            System.out.println("No node entry for: " + taxIDName);
                        }
                    } else {
                        node = dataFile.get(taxIDName);
                        if (node != null) {
                            NCBISyn syn = new NCBISyn(taxIDName);
                            syn.setClass_name(nameType);
                            syn.setSynonim(su.scapeSQL(name));
                            node.addSyn(syn);
                            dataFile.put(taxIDName, node);
                            synonymNames++;
                        } else {
                            System.out.println("No node entry for: " + taxIDName);
                        }
                    }
                }
            }
            readerNames.close();
            System.gc();
            finish = System.currentTimeMillis() - start;
            System.out.println("Names processed: " + numLinea);
            System.out.println("Scientific names assigned: " + scientificNames);
            System.out.println("Synonym names assigned: " + synonymNames);
            System.out.println("Total time processing taxonomic names: " + finish / 1000 + " s.");
            System.out.println("************************************************");
            System.out.println("Create and store organism for DB....");

            start = System.currentTimeMillis();
            int totOK = 0;
            //NCBINode node;
            //NCBINode tmpNode;
            //String hierarchy;
            //Set<String> keys = dataFile.keySet().toArray();
            Object keys[] = dataFile.keySet().toArray();
            // for (String key : dataFile.keySet()) {
            for (int i = 0; i < keys.length; i++) {
                node = dataFile.get(keys[i].toString());
                String hierarchy = node.getParent_tax_id().trim();
                String dbHierarchy = transacciones.getHirarchyByTaxid(hierarchy);
                if (dbHierarchy.length() > 0) {
                    dbHierarchy += "," + hierarchy;
                    node.setHirarchy(dbHierarchy);
                } else {
                    NCBINode tmpNode = node;
                    int counts = 0;
                    while (dbHierarchy.length() == 0 && tmpNode.getParent_tax_id() != null && !tmpNode.getParent_tax_id().equals("1") && !tmpNode.getParent_tax_id().equals(tmpNode.getTax_id())) {
                        counts++;
                        if (counts == 100) {
                            System.out.println("100 parents! NODE: " + node.getName() + " " + node.getHirarchy());
                        }
                        tmpNode = dataFile.get(tmpNode.getParent_tax_id());
                        hierarchy = tmpNode.getParent_tax_id().trim() + "," + hierarchy;
                        dbHierarchy = transacciones.getHirarchyByTaxid(tmpNode.getParent_tax_id());
                        if (dbHierarchy.length() > 0) {
                            //dbHierarchy += "," + hierarchy;
                            hierarchy = dbHierarchy + "," + hierarchy;
                            // node.setHirarchy(hierarchy);
                            //break;
                        }
                    }

                    node.setHirarchy(hierarchy);
                }
                boolean ok = transacciones.insertaQuery(node.toSQLInsertString());
                //boolean ok = transacciones.updateHierarchyNCBINode(node.getTax_id(), node.getHirarchy());
                if (ok) {
                    totOK++;
                    for (NCBISyn syn : node.getSynms()) {
                        ok &= transacciones.insertaQuery(syn.toSQLInsertString());
                        if (!ok) {
                            //  log += "Error insertando NCBI_SYN: " + syn.toString();
                            System.err.println("Error insertando NCBI_SYN: " + syn.toString());
                        }
                    }
                } else {
                    //log += "Error insertando NCBI_NODE: " + node.toString();
                    System.err.println("Error insertando NCBI_NODE: " + node.toString());
                }
            }

            finish = System.currentTimeMillis() - start;
            System.out.println("Total time: " + finish / 1000 + " s.");
            System.out.println("************************************************");
            System.out.println("Total processed nodes: " + this.dataFile.keySet().size()
                    + "\nTotal inserted nodes: " + totOK
                    + "\nTotal discarded nodes: " + (this.dataFile.keySet().size() - totOK));
            System.out.println("************************************************");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(NCBITaxCreator.class.getName()).log(Level.SEVERE, null, ex);
            return "Error no se encontro archivo de entrada:\n" + ex.getLocalizedMessage();
        } catch (IOException ex) {
            Logger.getLogger(NCBITaxCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    /**
     * This method populate the taxon entity on the database, based on the
     * information stored at ncbi_node entity.
     *
     * @param outFile The output file with all the SQL commands to insert the
     * taxons. Only needed when param toFile is true
     * @param toFile true if the output is going to be printed to a file, false
     * will cause the program to write directly to the database.
     */
    public void createTaxon(String outFile, boolean toFile, String where) {
        //  ArrayList<ArrayList> nodos = transacciones.getNCBINodes(" WHERE hierarchy not like '%,2759,%'");
        System.out.println("Reading Nodos");
        ArrayList<ArrayList> nodos = transacciones.getNCBINodes(where);
        System.out.println("Nodos: " + nodos.size());
        FileWriter writer = null;
        StringUtils su = new StringUtils();
        try {
            if (toFile) {
                writer = new FileWriter(outFile, true);
            }
            ArrayList<ArrayList> linaje;
            int taxones = 0;
            for (ArrayList<String> nodo : nodos) {
                taxones++;
                int idTax = Integer.parseInt(nodo.get(0));
                String rank = nodo.get(1);
                if (rank.equals("superkingdom")) {
                    rank = "kingdom";
                }
                String name = su.scapeSQL(nodo.get(2));
                String hierarchy = nodo.get(3);
                Taxon tax = new Taxon(idTax);
                tax.setRank(rank);
                tax.setTaxon(name);
                tax.assignRank(name, rank);
                linaje = transacciones.getNCBINodes(" WHERE tax_id IN(" + hierarchy + ")");
                for (ArrayList<String> nodoPadre : linaje) {
                    String rankP = nodoPadre.get(1);
                    String nameP = su.scapeSQL(nodoPadre.get(2));
                    if (rankP.equals("superkingdom")) {
                        rankP = "kingdom";
                    }
                    tax.assignRank(nameP, rankP);
                }
                if (!toFile) {
                    if (!transacciones.insertaQuery(tax.toSQLString())) {
                        System.err.println("Error insertando: " + tax.toSQLString());
                    }
                } else {
                    writer.write(tax.toSQLString() + ";\n");
                }
                if (taxones % 25000 == 0) {
                    System.out.println("Taxones procesados: " + taxones);
                }

            }
            System.out.println("#########SE FINI###########");
            System.out.println("Taxones procesados: " + taxones);

            if (toFile) {
                writer.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(NCBITaxCreator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
