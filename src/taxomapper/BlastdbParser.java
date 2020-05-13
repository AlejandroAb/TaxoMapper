/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxomapper;

import bobjects.Taxon;
import bobjects.TaxonProcessor;
import database.Transacciones;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.StringUtils;

/**
 * This class is created to parse output files extracted from blast dbs with
 * blastdbcmd, like the following one: blastdbcmd -entry all -db
 * ncbi_nt/blast/nt -outfmt "%a;%g;%i;%T;%t" -out nt.idlist. In order to parse
 * it an load different tables like ncbi_nt, ncbi_nr
 *
 * @author aabdala
 */
public class BlastdbParser {

    private Transacciones transacciones;
    private TaxonProcessor taxon;

    public Transacciones getTransacciones() {
        return transacciones;
    }

    public void setTransacciones(Transacciones transacciones) {
        this.transacciones = transacciones;
    }

    public TaxonProcessor getTaxon() {
        return taxon;
    }

    public void setTaxon(TaxonProcessor taxon) {
        this.taxon = taxon;
    }

    public BlastdbParser(Transacciones transacciones) {
        this.transacciones = transacciones;
    }

    public BlastdbParser() {
        this.transacciones = null;
    }

    public void mapBlastResult(String input, String output, int accCol, int report) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            FileWriter writer = new FileWriter(output);
            FileWriter writerNot = new FileWriter(output + ".notFound");
            int total = 0;
            int ok = 0;
            int nok = 0;
            String linea;
            long start = System.currentTimeMillis();
            long currentLaps = System.currentTimeMillis();
            while ((linea = reader.readLine()) != null) {
                String tmpL[] = linea.split("\t");
                String accs[] = tmpL[accCol].split("\\|");
                try {
                    String acc = accs[1];
                    String query;
                    if (acc.length() > 0) {
                        query = "SELECT CONCAT('" + linea + "\t',name,'\t',ncbi_node.tax_id,'\t', ncbi_node.rank) "
                                + "FROM ncbi_nt inner join ncbi_node on ncbi_node.tax_id = ncbi_nt.tax_id where ncbi_gi = '" + acc + "'";
                    } else {
                        acc = accs[3];
                        query = "SELECT CONCAT('" + linea + "\t',name,'\t',ncbi_node.tax_id,'\t', ncbi_node.rank) "
                                + "FROM ncbi_nt inner join ncbi_node on ncbi_node.tax_id = ncbi_nt.tax_id where accession = '" + acc + "'";
                    }
                    

                    String res = transacciones.executeStringQuery(query);
                    if (res.length() > 0) {
                        writer.write(res + "\n");
                        ok++;
                    } else {
                        writerNot.write(linea + "\n");
                        nok++;
                    }
                } catch (Exception e) {

                }
                total++;
                if (total % report == 0) {
                    //System.out.println("query");
                    long current = System.currentTimeMillis();
                    System.out.println("Rows proccessed: " + total + " OK: " + ok + " NOK: " + nok + " Time: " + ((current - currentLaps) / 1000) + "s");
                    currentLaps = System.currentTimeMillis();
                }

            }
            reader.close();
            writer.close();
            writerNot.close();
            System.out.println("END");
            System.out.println("Rows proccessed: " + total + " OK: " + ok + " NOK: " + nok);

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BlastdbParser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BlastdbParser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createTaxIdList(String database, String output, String list) {
        String taxoQueryBase = "SELECT distinct tax_id FROM taxon ";
        String taxidList = "";
        if (taxon.getTaxones() == null) {
            if (taxon.getKingdom().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "kingdom", taxon.getKingdom());
            }
            if (taxon.getPhylum().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "phylum", taxon.getPhylum());
            }
            if (taxon.getClasse().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "class", taxon.getClasse());
            }
            if (taxon.getOrder().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "orden", taxon.getOrder());
            }
            if (taxon.getFamily().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "family", taxon.getFamily());
            }
            if (taxon.getGenus().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "genus", taxon.getGenus());
            }
            if (taxon.getSpecies().length() > 0) {
                taxoQueryBase = taxoQuery(taxoQueryBase, "species", taxon.getSpecies());
            }
            taxidList = getDBTaxidlist(taxoQueryBase);
        } else {
            taxidList = taxon.getTaxones(); //created with arguments 
        }
        if (taxidList.length() > 0) {
            createSubsetList(taxidList, output, list);
        } else {
            System.err.println("Your search didnt return any match!\nPlease make sure you write the correct taxon name for the rank you are looking for");
        }
    }

    /**
     * Takes de taxid list and perform the query to retrive all the sequence ids
     *
     * @param taxidList
     * @param output
     * @param list
     */
    public void createSubsetList(String taxidList, String output, String list) {
        FileWriter writer = null;
        try {
            String query = "SELECT accession, ncbi_gi, seq_id FROM ncbi_nt "
                    + "WHERE tax_id IN (" + taxidList + ")";
            ArrayList<ArrayList> secuencias = transacciones.executeQuery(query);
            int index = 0;
            if (list.equals("gi")) {
                index = 1;
            } else if (list.equals("acc")) {
                index = 0;
            }
            writer = new FileWriter(output);
            for (ArrayList<String> secs : secuencias) {
                writer.write(secs.get(index) + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(BlastdbParser.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                writer.close();
            } catch (IOException ex) {
                Logger.getLogger(BlastdbParser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    /**
     * Takes the pre formated query for taxon table and execute the query
     *
     * @param query query to taxon table
     * @return comma separated list with tax-ids
     */
    public String getDBTaxidlist(String query) {
        String taxidList = "";
        ArrayList<ArrayList> result = transacciones.executeQuery(query);
        if (result != null && result.size() > 0) {
            boolean isfirst = true;
            for (ArrayList<String> tax : result) {
                if (isfirst) {
                    isfirst = false;
                    taxidList = tax.get(0);
                } else {
                    taxidList += "," + tax.get(0);
                }
            }
        }
        return taxidList;
    }

    /**
     * Creates WHERE statement for taxon list substraction
     *
     * @param baseQuery the base query (SELECT statement or concatenated where)
     * @param rank phylo level kingdom, phylum, class, order,...
     * @param list comma separated list with desired values
     * @return
     */
    public String taxoQuery(String baseQuery, String rank, String list) {

        String names[] = list.split(",");
        if (names.length > 0) {
            boolean isfirst = true;
            if (baseQuery.contains("WHERE")) {
                isfirst = false;
            }
            for (String name : names) {
                if (isfirst) {
                    baseQuery += "WHERE " + rank + " = '" + name + "'";
                    isfirst = false;
                } else {
                    baseQuery += " OR " + rank + " = '" + name + "'";
                }
            }
        }
        return baseQuery;
    }

    /**
     * Method for parse and insert/write nt or nr records into relational
     * database
     *
     * @param input input file with blastdbcmd result. Expected format: -outfmt
     * "%a;%g;%i;%T;%t"
     * @param output if present queries are writen into outfile, otherwise
     * directly inserted into DBf
     * @param table the name of the table where the records are writen, for the
     * moment possible values are: nt, nr
     */
    public void parseFile(String input, String output, String table) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(input));
            FileWriter writer = null;
            boolean toOF = false;
            if (output.length() > 0) {
                writer = new FileWriter(output);
                toOF = true;
            }
            String linea;
            StringUtils su = new StringUtils();
            int ok = 0;
            int nok = 0;
            int line = 0;
            System.out.println("*********************\n"
                    + "Processing file:" + input);
            while ((linea = reader.readLine()) != null) {
                line++;
                String tokens[] = linea.split(";");
                String title = tokens[4];
                //if title contains ; it will be splited on more tokens
                for (int i = 5; i < tokens.length; i++) {
                    title += ";" + tokens[i];
                }
                String query = "INSERT IGNORE INTO " + table + " VALUES('" + tokens[0] + "', " + tokens[1] + ", '" + tokens[2] + "'," + tokens[3] + ", '" + su.scapeSQL(title) + "')";
                if (toOF) {
                    writer.write(query + ";\n");
                    ok++;
                } else {
                    if (transacciones.insertaQuery(query)) {
                        ok++;
                    } else {
                        System.err.println("Error inserting query: " + query + "\nline:" + line);
                        nok++;
                    }
                }
            }
            System.out.println("Lines processed: " + line);
            if (toOF) {
                System.out.println("Queries created: " + ok);
                System.out.println("Outputfile: " + output);
            } else {
                System.out.println("Records inserted: " + ok);
                System.out.println("Records NOK: " + nok);
            }
            System.out.println("*********************");

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BlastdbParser.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("There is no file: " + input);
        } catch (IOException ex) {
            Logger.getLogger(BlastdbParser.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Error writing file: " + output);
        }
    }
}
