/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.util.ArrayList;

/**
 *
 * @author aabdala
 */
public class Transacciones {

    Conexion conexion;
    boolean estamosConectados = true;
    String tipoConexion = "";
    private String database;
    private String user;
    private String ip;
    private String password;
    private String query;
    private boolean debug = false;

    public Conexion getConexion() {
        return conexion;
    }

    public Transacciones() {
        conecta(true);
    }

    public Transacciones(boolean local) {
        conecta(local);
    }

    public Transacciones(String database, String user, String ip, String password) {
        this.database = database;
        this.user = user;
        this.ip = ip;
        this.password = password;
        conecta(true);
    }

    public void desconecta() {
        conexion.shutDown();
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public String getIp() {
        return ip;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void conecta(boolean conex) {
        if (conex) {
            // ArchivoIP aip = new ArchivoIP();
            //String[]config =  aip.obtieneIP();
            // conexion = new Conexion(config[1], config[0]);
            conexion = new Conexion(database, ip, user, password);
            //System.out.println(config[1] + "  " + config[0]);
            //  JOptionPane.showMessageDialog(null, config[1],config[0],JOptionPane.INFORMATION_MESSAGE);
            estamosConectados = conexion.seConecto;
            tipoConexion = "remota";
        } else {
            //conexion = new Conexion("mantenimiento", "localhost");
            // conexion = new Conexion("bio", "localhost", "root", "AMORPHIS");
            estamosConectados = conexion.seConecto;
            tipoConexion = "local";
        }
    }

    public boolean updateHierarchyNCBINode(String taxid, String hierarchy) {
        String query = "UPDATE ncbi_node SET hierarchy = '" + hierarchy + "' WHERE tax_id =" + taxid;
        if (conexion.queryUpdate(query)) {
            return true;
        } else {
            System.out.println(conexion.getLog());
            return false;
        }
    }

    /**
     * This method get all taxonomy node information given a condition
     *
     * @param where the condition for retrieving the taxonomy nodes
     * @return an ArrayList of nodes
     */
    public ArrayList<ArrayList> getNCBINodes(String where) {
        String query = "SELECT ncbi_node.tax_id, ncbi_node.rank, ncbi_node.name, hierarchy FROM ncbi_node " + where;
        conexion.executeStatement(query);
        ArrayList<ArrayList> dbResult = conexion.getTabla();
        return dbResult;
    }

    /**
     * Executes preformatted query
     *
     * @param query query to be executed
     * @return
     */
    public ArrayList<ArrayList> executeQuery(String query) {
        conexion.executeStatement(query);
        ArrayList<ArrayList> dbResult = conexion.getTabla();
        //System.out.println(query);
        return dbResult;
    }

    public String executeStringQuery(String query) {
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    /**
     * This method provid a query interface to find the full hierarchy (with
     * comma separated taxids), gicen a target taxid node
     *
     * @param taxid
     * @return the complete taxids linage to the given node
     */
    public String getHirarchyByTaxid(String taxid) {
        String query = "SELECT hierarchy FROM ncbi_node WHERE tax_id = " + taxid;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    public String getNCBINameByTaxID(String taxid) {
        String query = "SELECT name FROM ncbi_node WHERE tax_id = " + taxid;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    public ArrayList getNCBISynonim(String name) {
        String query = "SELECT tax_id, syn_name, class_name FROM ncbi_syn WHERE syn_name ='" + name + "'";
        conexion.executeStatement(query);
        return conexion.getTabla();
    }

    public String getTaxidByName(String name, int level) {
        String like = "and (hierarchy like  '1,131567,2,%' or hierarchy like  '1,131567,2157,%' )";
        if (level == 1) {
            like = "and hierarchy like '1,131567,2%'";
        } else if (level < 1) {
            like = " ";
        }
        String query = "SELECT tax_id, rank FROM ncbi_node WHERE name = '" + name + "' " + like;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            if (conexion.getTabla().size() > 1) {
                for (ArrayList<String> tax : conexion.getTabla()) {
                    if (tax.get(1).equals("species") && level == 6) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("genus") && level == 5) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("family") && level == 4) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("order") && level == 3) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("class") && level == 2) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("phylum") && level == 1) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("superkingdom") && level == 1) {
                        return tax.get(0);
                    }
                }
                System.out.println("Entry: " + name + " have hits with the following nodes: ");
                for (ArrayList<String> tax : conexion.getTabla()) {
                    System.out.print(" " + tax.get(0));
                    if (tax.get(1).equals("species") && level == 6) {
                        return tax.get(0);
                    }
                }
                System.out.println("");
            }
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    /**
     * This method retrives the taxonomy path given a node name
     *
     * @param name the name of the node
     * @param level this is a flag to allow different kind of searchs: 0 = all,
     * 1 =
     * @return
     */
    public String getProkaTaxidByName(String name, int level) {
        String like = "and (hierarchy like  '1,131567,2,%' or hierarchy like  '1,131567,2157,%' )";
        if (level == 1) {
            like = "and hierarchy like '1,131567,2%'";
        } else if (level < 1) {
            like = " ";
        }
        String query = "SELECT tax_id, rank, hierarchy FROM ncbi_node WHERE name = '" + name + "' " + like;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            if (conexion.getTabla().size() > 1) {
                /* for (ArrayList<String> tax : conexion.getTabla()) {
                    if (tax.get(1).equals("species") && level == 6) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("genus") && level == 5) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("family") && level == 4) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("order") && level == 3) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("class") && level == 2) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("phylum") && level == 1) {
                        return tax.get(0);
                    }else if (tax.get(1).equals("superkingdom") && level == 1) {
                        return tax.get(0);
                    }
                }
                
                 */
                System.out.println("Entry: " + name + " have hits with the following nodes: ");
                //si tiene mas de un hit regresa un string taxid:hierarchy;taxid2:hierar2...
                String result = "";
                for (ArrayList<String> tax : conexion.getTabla()) {
                    System.out.print(" " + tax.get(0));
                    if (result.length() == 0) {
                        result += tax.get(0) + ":" + tax.get(2);
                    } else {
                        result += ";" + tax.get(0) + ":" + tax.get(2);
                    }
                }
                System.out.println("");
                return result;
            }
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    public boolean isNameOnPath(String name, String taxidPath) {
        String query = "select group_concat(name) as c "
                + "from ncbi_node where tax_id in(" + taxidPath + ") "
                + " having c like '%" + name + "%'";
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public String getEukTaxidByName(String name, int level) {

        String query = "SELECT tax_id, rank, hierarchy FROM ncbi_node WHERE name = '" + name + "' ";
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            if (conexion.getTabla().size() > 1) {
                /* for (ArrayList<String> tax : conexion.getTabla()) {
                    if (tax.get(1).equals("species") && level == 6) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("genus") && level == 5) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("family") && level == 4) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("order") && level == 3) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("class") && level == 2) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("phylum") && level == 1) {
                        return tax.get(0);
                    } else if (tax.get(1).equals("superkingdom") && level == 1) {
                        return tax.get(0);
                    }
                }*/
                System.out.println("Entry: " + name + " have hits with the following nodes: ");
                //si tiene mas de un hit regresa un string taxid:hierarchy;taxid2:hierar2...
                String result = "";
                for (ArrayList<String> tax : conexion.getTabla()) {
                    System.out.print(" " + tax.get(0));
                    if (result.length() == 0) {
                        result += tax.get(0) + ":" + tax.get(2);
                    } else {
                        result += ";" + tax.get(0) + ":" + tax.get(2);
                    }
                }
                System.out.println("");
                return result;
            }
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    public String getTaxPathByName(String name, boolean debug) {

        String query = "SELECT tax_id, rank, hierarchy, div_id FROM ncbi_node WHERE name = '" + name + "' ";
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {

            //si tiene mas de un hit regresa un string taxid:hierarchy;taxid2:hierar2...
            String result = "";
            for (ArrayList<String> tax : conexion.getTabla()) {
                if (debug) {
                    System.out.print(" " + tax.get(0));
                }
                if (result.length() == 0) {
                    result += tax.get(0) + ":" + tax.get(2) + ":" + tax.get(3);
                } else {
                    result += ";" + tax.get(0) + ":" + tax.get(2) + ":" + tax.get(3);
                }
            }            
            return result;

        } else {
            return "";
        }
    }

    public String getTaxPathByTaxid(String taxid, boolean debug) {

        String query = "SELECT tax_id, rank, hierarchy, div_id FROM ncbi_node WHERE tax_id = " + taxid;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {

           
            //si tiene mas de un hit regresa un string taxid:hierarchy;taxid2:hierar2...
            String result = "";
            for (ArrayList<String> tax : conexion.getTabla()) {
                if (debug) {
                    System.out.print(" " + tax.get(0));
                }
                if (result.length() == 0) {
                    result += tax.get(0) + ":" + tax.get(2) + ":" + tax.get(3);
                } else {
                    result += ";" + tax.get(0) + ":" + tax.get(2) + ":" + tax.get(3);
                }
            }
            if (debug) {
                System.out.println("");
            }
            return result;

        } else {
            return "";
        }
    }

    /**
     * This method provid a query interface to find the full hierarchy (with
     * comma separated taxids), gicen a target taxid node
     *
     * @param taxid
     * @return the complete taxids linage to the given node
     */
    public ArrayList getHirarchyAndNameByTaxid(String taxid) {
        String query = "SELECT name, hierarchy, rank FROM ncbi_node WHERE tax_id = " + taxid;
        conexion.executeStatement(query);
        return conexion.getTabla();
    }

    /**
     * Method to see if a taxid has been merged into other node
     *
     * @param taxid the tax id to be tested
     * @return the new taxid if it has been merged, blank other wise
     */
    public String testForMergeTaxid(String taxid) {
        String query = "SELECT tax_id FROM ncbi_merged WHERE old_tax_id = " + taxid;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    /**
     * Method to test if a taxid exists on the ncbi_node table
     *
     * @param taxid
     * @return
     */
    public String testForTaxid(String taxid) {
        String query = "SELECT tax_id FROM ncbi_node WHERE tax_id = " + taxid;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    public String getHierarcheByTaxIDPrepared(int taxid) {
        conexion.executePreparedStatementS1(taxid);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }

    /**
     * This query retrieves a taxon names string, given a list of tax ids comma
     * separated
     *
     * @param taxidHierarchy comma separated list as the one on hierarchy field
     * of ncbi_node table
     * @param extra any extra where condition (like the desired taxonomy ranks)
     * @param sep char separator for the return list.
     * @return list whit all the names on the same order as the taxidHierarchy
     * list and separated by sep
     */
    public String getLiteralTaxonomy(String taxidHierarchy, String extra, String sep) {
        String query = "SELECT GROUP_CONCAT(name ORDER BY FIELD(tax_id, " + taxidHierarchy + ") SEPARATOR '" + sep + "') "
                + "FROM ncbi_node WHERE tax_id IN (" + taxidHierarchy + ") " + extra;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }
 public String getLiteralTaxonomyWNull(String taxidHierarchy, String extra, String sep, String null_val) {
        String query = "SELECT GROUP_CONCAT(name ORDER BY FIELD(tax_id, " + taxidHierarchy + ") SEPARATOR '" + sep + "' "
                + "IFNULL(name, '"+null_val+"')) "
                + "FROM ncbi_node WHERE tax_id IN (" + taxidHierarchy + ") " + extra;
        conexion.executeStatement(query);
        if (conexion.getTabla() != null && conexion.getTabla().size() > 0) {
            return (String) conexion.getTabla().get(0).get(0);
        } else {
            return "";
        }
    }
    /**
     * This method brings a specific taxonomic path given a tax_id
     *
     * @param levels the desired levels (any level in the taxon table)
     * @param tax_id the target tax id
     * @return list with all the target levels
     */
    public ArrayList<ArrayList> getTaxonomicPathByLevels(String levels, String tax_id) {
        String query = "SELECT " + levels + " FROM taxon WHERE tax_id = " + tax_id;
        conexion.executeStatement(query);
        return conexion.getTabla();
    }

    public ArrayList<ArrayList> getCompleteTaxonomicPathByHierarchy(String hierarchy) {
        String query = "SELECT tax_id, name, rank FROM ncbi_node "
                + "where tax_id IN(" + hierarchy + ") order by field (tax_id," + hierarchy + ")";
        conexion.executeStatement(query);
        return conexion.getTabla();
    }

    /**
     * Gets all the taxons with tax_id, tax_name and rank given a taxid list
     *
     * @param taxidHierarchy comma separated list as the one on hierarchy field
     * of ncbi_node table
     * @param extra any extra where condition (like the desired taxonomy ranks)
     * @return
     */
    public ArrayList getTaxonomybyTaxIDList(String taxidHierarchy, String extra) {
        String query = "SELECT tax_id, rank, name FROM ncbi_node "
                + "WHERE tax_id IN (" + taxidHierarchy + ") " + extra;
        conexion.executeStatement(query);
        return conexion.getTabla();
    }

    public boolean insertaQuery(String query) {
        if (debug) {
            System.out.println(query);
        }
        if (conexion.queryUpdate(query)) {
            return true;
        } else {
            System.err.println(conexion.getLog());
            return false;
        }

    }
}
