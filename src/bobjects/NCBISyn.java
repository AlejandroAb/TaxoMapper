/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bobjects;

/**
 *
 * @author Alejandro
 */
public class NCBISyn {
    private String tax_id;
    private String synonim;
    private String class_name;

    public NCBISyn(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getTax_id() {
        return tax_id;
    }

    public void setTax_id(String tax_id) {
        this.tax_id = tax_id;
    }

    public String getSynonim() {
        return synonim;
    }

    public void setSynonim(String synonim) {
        this.synonim = synonim;
    }

    public String getClass_name() {
        return class_name;
    }

    public void setClass_name(String class_name) {
        this.class_name = class_name;
    }
    public String toSQLInsertString() {
        String query = "INSERT INTO ncbi_syn VALUES ("+tax_id 
                + ",'"+synonim+"','"+class_name+"')"; 
        return query;
    }
    public String toString() {
        String query = "tax id: "+tax_id 
                + " syn: "+synonim+" class: "+class_name; 
        return query;
    }
    
}
