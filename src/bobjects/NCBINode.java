/*
 * Representa un objeto Node correspondiente a un renglon de la tabla 
 * nodes.dmp del ncbi
 */
package bobjects;

import java.util.ArrayList;

/**
 * Esta clase representa un renglon en el archivo node.dmp del NCBI. Siendo un
 * nodo la abstracción de un nivel taxonómico. El archivo node.dmp de NCBI no
 * tiene el nombre, así que para "armar" el nodo es necesario cruzar información
 * con names.dmp
 *
 * @author Alejandro
 */
public class NCBINode {

    /**
     * ncbi tax id del nodo. se lee en nodes.dmp primer campo
     */
    String tax_id = "";
    /**
     * ncbi tax id del padre de dicho nodo se lee en nodes.dmp segundo campo
     */
    String parent_tax_id = "";
    /**
     * nombre textual del rango al cual pertenece el nodo - genus, species,
     * family, etc. Es el tercer campo en nodes.dmp
     */
    String rank = "";
    /**
     * Nombre que se le asigna al nodo. Ejemplo Bacteria. Es el segundo campo en
     * names.dmp. El nombre se escoge en función de la clase.
     */
    String name = ""; //nombre del nodo (acorde a names.dmp)
    /**
     * nombre de la clase del nodo acorde a names.dmp. Hay varios nombres por
     * taxon, por lo que se busca "scientific name" los demás pueden ser
     * anotados en la tabla NCBI_SYN. Se obtiene de names.dmp
     */
    String class_name = "";
    /**
     * El código de división para clasificar las especies: -0 | BCT | Bacteria
     * -1 | INV | Invertebrates -2 | MAM | Mammals -3 | PHG | Phages -4 | PLN |
     * Plants and Fungi -5 | PRI | Primates -6 | ROD | Rodents -7 | SYN |
     * Synthetic and Chimeric -8 | UNA | Unassigned -9 | VRL | Viruses -10 | VRT
     * | Vertebrates -11 | ENV | Environmental samples. Es el quinto campo en
     * nodes.dmp
     */
    String div_code = "";//division code 
    /**
     * Lista de NCBI tax ids que representan de izq a derecha de root a leaft la
     * clasificación taxonómica del nodo. Importante. separado por comas sin
     * espacios!
     */
    String hirarchy = "";

    ArrayList<NCBISyn> synms = new ArrayList();

    /**
     * Contrsuctor de la clase
     *
     * @param tax_id ncbi tax id del nodo
     */
    public NCBINode(String tax_id) {
        this.tax_id = tax_id;
    }

    public ArrayList<NCBISyn> getSynms() {
        return synms;
    }

    public void setSynms(ArrayList<NCBISyn> synms) {
        this.synms = synms;
    }

    public String getHirarchy() {
        return hirarchy;
    }

    public void setHirarchy(String hirarchy) {
        this.hirarchy = hirarchy;
    }

    public void addSyn(NCBISyn syn) {
        synms.add(syn);
    }

    /**
     * Representación textual de un nodo para std output
     *
     * @return
     */
    public String toString() {
        String toString = "taxID: " + tax_id + " rank: " + rank + " p_taxID: " + parent_tax_id + " Name: " + name + " class: " + class_name + " div id: " + div_code;
        return toString;
    }

    public String toSQLInsertString() {
        String query = "INSERT INTO ncbi_node VALUES (" + tax_id
                + "," + parent_tax_id + ",'" + rank + "','" + name + "','" + class_name + "'," + div_code + ", '"+hirarchy+"')";
        return query;
    }
    

    public String getTax_id() {
        return tax_id;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }

    public String getDiv_code() {
        return div_code;
    }

    public void setDiv_code(String div_code) {
        this.div_code = div_code;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getParent_tax_id() {
        return parent_tax_id;
    }

    public void setParent_tax_id(String parent_tax_id) {
        this.parent_tax_id = parent_tax_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
