/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bobjects;

import java.util.ArrayList;

/**
 *
 * @author aabdala
 */
public class TaxMapp {

    private String entry;
    private String path = "";
    private ArrayList<Taxon> taxons = new ArrayList<Taxon>();
    private int hits = 1;
    private int no_hits = 0;

    public void toCmpStdoString() {
        System.out.println("Entry: " + entry + " Hits: " + hits);
        System.out.println("\tOriginal path: " + path);
        for (Taxon t : taxons) {
            System.out.println("\tTaxid:" + t.getTax_id());
            System.out.println("\tPath: " + t.getPath());
            System.out.println("\tIntersepts: " + t.getInterseptedNodes() + " nodes");
            if (t.isSelected()) {
                System.out.println("\tSelected: true");
            } else {
                System.out.println("\tSelected: false");
            }
        }

    }
    public int getLength(){
        return taxons.size();
    }
    public void addTaxon(Taxon t) {
        this.taxons.add(t);
    }

    public Taxon getSelectedTaxon() {
        for (Taxon t : taxons) {
            if (t.isSelected()) {
                return t;
            }
        }
        return null;
    }
   

    public void addHit() {
        this.hits++;
    }

    public void setSelectedTaxon(String taxid) {
        for (Taxon t : taxons) {
            if (("" + t.getTax_id()).equals(taxid)) {
                t.setSelected(true);
                break;
            }
        }
    }

    public TaxMapp(String entry) {
        this.entry = entry;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public ArrayList<Taxon> getTaxons() {
        return taxons;
    }

    public void setTaxons(ArrayList<Taxon> taxons) {
        this.taxons = taxons;
    }

    public int getHits() {
        return hits;
    }

    public void setHits(int hits) {
        this.hits = hits;
    }

}
