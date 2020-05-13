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
public class Taxon {

    private int tax_id;
    private String taxon = "";
    private String rank = "";
    private String superkingdom = "";
    private String kingdom = "";
    private String subkingdom = "";
    private String superphylum = "";
    private String phylum = "";
    private String subphylum = "";
    private String superclass = "";
    private String infraclass = "";
    private String classe = "";
    private String subclass = "";
    private String parvorder = "";
    private String superorder = "";
    private String infraorder = "";
    private String order = "";
    private String suborder = "";
    private String superfamily = "";
    private String family = "";
    private String subfamily = "";
    private String tribe = "";
    private String subtribe = "";
    private String genus = "";
    private String subgenus = "";
    private String species = "";
    private String species_group = "";
    private String species_subgroup = "";
    private String subspecies = "";
    private String forma = "";
    private String varietas = "";
    private String no_rank = "";
    //helpers to mapping
    private String path = "";
    private int interseptedNodes = 0;
    private boolean selected = false;
    private int blanks = 0;
    private int not_blanks = 0;
    private String divID = "";
    private String taxidPath = "";
    private boolean filtered = false;
    boolean completSP = false;

    public boolean isCompletSP() {
        return completSP;
    }

    public void setCompletSP(boolean completSP) {
        this.completSP = completSP;
    }

    public Taxon(int tax_id) {
        //new Taxon(tax_id, false);
        this.tax_id = tax_id;

    }

    public boolean isFiltered() {
        return filtered;
    }

    public void setFiltered(boolean filtered) {
        this.filtered = filtered;
    }

    public String getDivID() {
        return divID;
    }

    public void setDivID(String divID) {
        this.divID = divID;
    }

    public String getTaxidPath() {
        return taxidPath;
    }

    public void setTaxidPath(String taxidPath) {
        this.taxidPath = taxidPath;
    }

    public String getSuperkingdom() {
        return superkingdom;
    }

    public void setSuperkingdom(String superkingdom) {
        this.superkingdom = superkingdom;
    }

    public int getNot_blanks() {
        return not_blanks;
    }

    public void setNot_blanks(int not_blanks) {
        this.not_blanks = not_blanks;
    }

    public void incrementBlank() {
        blanks++;
    }

    public void incrementNotBlank() {
        not_blanks++;
    }

    public int getBlanks() {
        return blanks;
    }

    public void setBlanks(int blanks) {
        this.blanks = blanks;
    }

    public int getInterseptedNodes() {
        return interseptedNodes;
    }

    public void setInterseptedNodes(int interseptedNodes) {
        this.interseptedNodes = interseptedNodes;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Taxon(int tax_id, boolean qiime) {
        this.tax_id = tax_id;
        if (qiime) {
            this.kingdom = "k__";
            this.phylum = "p__";
            this.classe = "c__";
            this.order = "o__";
            this.family = "f__";
            this.genus = "g__";
            this.species = "s__";
        }
    }

    public String getTaxonSummary() {
        return tax_id + ":" + taxidPath + ":" + divID;
    }

    public int getTax_id() {
        return tax_id;
    }

    public void setTax_id(int tax_id) {
        this.tax_id = tax_id;
    }

    public String getTaxon() {
        return taxon;
    }

    public void setTaxon(String taxon) {
        this.taxon = taxon;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getKingdom() {
        return kingdom;
    }

    public void setKingdom(String kingdom) {
        this.kingdom = kingdom;
    }

    public String getSubkingdom() {
        return subkingdom;
    }

    public void setSubkingdom(String subkingdom) {
        this.subkingdom = subkingdom;
    }

    public String getSuperphylum() {
        return superphylum;
    }

    public void setSuperphylum(String superphylum) {
        this.superphylum = superphylum;
    }

    public String getPhylum() {
        return phylum;
    }

    public void setPhylum(String phylum) {
        this.phylum = phylum;
    }

    public String getSubphylum() {
        return subphylum;
    }

    public void setSubphylum(String subphylum) {
        this.subphylum = subphylum;
    }

    public String getSuperclass() {
        return superclass;
    }

    public void setSuperclass(String superclass) {
        this.superclass = superclass;
    }

    public String getInfraclass() {
        return infraclass;
    }

    public void setInfraclass(String infraclass) {
        this.infraclass = infraclass;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public String getSubclass() {
        return subclass;
    }

    public void setSubclass(String subclass) {
        this.subclass = subclass;
    }

    public String getParvorder() {
        return parvorder;
    }

    public void setParvorder(String parvorder) {
        this.parvorder = parvorder;
    }

    public String getSuperorder() {
        return superorder;
    }

    public void setSuperorder(String superorder) {
        this.superorder = superorder;
    }

    public String getInfraorder() {
        return infraorder;
    }

    public void setInfraorder(String infraorder) {
        this.infraorder = infraorder;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getSuborder() {
        return suborder;
    }

    public void setSuborder(String suborder) {
        this.suborder = suborder;
    }

    public String getSuperfamily() {
        return superfamily;
    }

    public void setSuperfamily(String superfamily) {
        this.superfamily = superfamily;
    }

    public String getFamily() {
        return family;
    }

    public void setFamily(String family) {
        this.family = family;
    }

    public String getSubfamily() {
        return subfamily;
    }

    public void setSubfamily(String subfamily) {
        this.subfamily = subfamily;
    }

    public String getTribe() {
        return tribe;
    }

    public void setTribe(String tribe) {
        this.tribe = tribe;
    }

    public String getSubtribe() {
        return subtribe;
    }

    public void setSubtribe(String subtribe) {
        this.subtribe = subtribe;
    }

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSubgenus() {
        return subgenus;
    }

    public void setSubgenus(String subgenus) {
        this.subgenus = subgenus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getSpecies_group() {
        return species_group;
    }

    public void setSpecies_group(String species_group) {
        this.species_group = species_group;
    }

    public String getSpecies_subgroup() {
        return species_subgroup;
    }

    public void setSpecies_subgroup(String species_subgroup) {
        this.species_subgroup = species_subgroup;
    }

    public String getSubspecies() {
        return subspecies;
    }

    public void setSubspecies(String subspecies) {
        this.subspecies = subspecies;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public String getVarietas() {
        return varietas;
    }

    public void setVarietas(String varietas) {
        this.varietas = varietas;
    }

    public String getNo_rank() {
        return no_rank;
    }

    public void setNo_rank(String no_rank) {
        this.no_rank = no_rank;
    }

    /**
     * Create and return a insert sql representation of this string
     *
     * @return
     */
    public String toSQLString() {
        String query = "INSERT INTO taxon (tax_id, taxon, rank, kingdom, subkingdom, superphylum, phylum, subphylum, superclass, infraclass, class, subclass, parvorder, superorder, infraorder, orden, suborder, superfamily, family, subfamily, tribe, subtribe, genus, subgenus, species, species_group, species_subgroup, subspecies, forma, varietas, no_rank, degradadora) "
                + "VALUES (" + tax_id + ",'" + taxon + "','" + rank + "','" + kingdom + "','" + subkingdom + "','" + superphylum + "','" + phylum + "','" + subphylum + "','" + superclass + "','" + infraclass + "','" + classe + "','" + subclass + "','" + parvorder + "','" + superorder + "','" + infraorder + "','" + order + "','" + suborder + "','" + superfamily + "','" + family + "','" + subfamily + "','" + tribe + "','" + subtribe + "','" + genus + "','" + subgenus + "','" + species + "','" + species_group + "','" + species_subgroup + "','" + subspecies + "','" + forma + "','" + varietas + "','" + no_rank + "',0)";
        return query;
    }

    /**
     * Create and return a string with qiime name format
     *
     * @return
     */
    public String toQiimeString() {
        String query = kingdom + "; " + phylum + "; " + classe + "; " + order + "; " + family + "; " + genus + "; " + species;
        return query;
    }

    /**
     * Create String with all the classic taxonomyNames and leave blank spaces
     * when there is no level
     *
     * @return
     */
    public String toClassicString() {
        String query = kingdom + "; " + phylum + "; " + classe + "; " + order + "; " + family + "; " + genus + "; " + species;
        return query;
    }

    public String toClassicLevelString(String level, String sep, boolean completeBlank) {
        String taxoPath = "";
        switch (level) {
            case "kingdom":
                taxoPath = getKingdom(completeBlank);
                break;

            case "phylum":
                taxoPath = getKingdom(completeBlank) + sep + getPhylum(completeBlank);
                break;

            case "class":
                taxoPath = getKingdom(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank);
                break;

            case "order":
                taxoPath = getKingdom(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank);
                break;
            case "family":
                taxoPath = getKingdom(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                        + getFamily(completeBlank);
                break;
            case "genus":
                taxoPath = getKingdom(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                        + getFamily(completeBlank) + sep + getGenus(completeBlank);
                break;
            case "species":
                taxoPath = getKingdom(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                        + getFamily(completeBlank) + sep + getGenus(completeBlank) + sep + getSpecies(completeBlank);
                break;

        }

        return taxoPath;
    }

    /**
     * The idea is to fill the blanks with the name of the next redmine issue
     * #333
     *
     * @param level
     * @param sep
     * @return
     */
    public String toMapSeqLikeLevelString(String levels[], String sep) {
        //
        String taxoPath = "";
        for (int i = levels.length - 1; i >= 0; i--) {
            String tmp_val = this.getRank(levels[i]);
            if (tmp_val.length() > 1) {
                if (i == levels.length - 1) {
                    taxoPath += tmp_val;
                } else {
                    taxoPath = tmp_val + sep + taxoPath;
                }
            } else if (i != levels.length - 1) { //It is not a leaft 
                int tmp_i = i;
                boolean found_rigth = false;
                tmp_i++;
                while (tmp_i <= levels.length - 1 && !found_rigth) {
                    tmp_val = this.getRank(levels[tmp_i]);
                    if (tmp_val.length() > 1) {
                        found_rigth = true;
                    }
                    tmp_i++;
                }
                if (found_rigth) {
                    taxoPath = tmp_val + "[" + levels[i] + "]" + sep + taxoPath;
                } else {
                    taxoPath = "ND[" + levels[i] + "]" + sep + taxoPath;
                }
            } else {//is leaft (sp)
                taxoPath += "sp.";
            }
        }

        return taxoPath;
    }

    /**
     * Equal to toClassicLevelString but use a improved getKingdom() which is
     * getKingdomSK. This change retrieve the superkingdom;and kingdom in case
     * that exist
     *
     * @param level
     * @param sep
     * @param completeBlank
     * @return
     */
    public String toClassicLevelStringSK(String level, String sep, boolean completeBlank) {
        String taxoPath = "";
        switch (level) {
            case "kingdom":
                taxoPath = getKingdomSK(completeBlank);
                break;

            case "phylum":
                taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank);
                break;

            case "class":
                taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank);
                break;

            case "order":
                taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank);
                break;
            case "family":
                taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                        + getFamily(completeBlank);
                break;
            case "genus":
                taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                        + getFamily(completeBlank) + sep + getGenus(completeBlank);
                break;
            case "species":
                taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                        + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                        + getFamily(completeBlank) + sep + getGenus(completeBlank) + sep + getSpecies(completeBlank);
                break;

        }

        return taxoPath;
    }

    /*public void fillBlanks(String levels[]) {
     
        //Taxon t = new Taxon(0);
        int i = 0;
        for (String rank : levels) {
            switch (rank) {
                case "kingdom":
                    taxoPath = getKingdomSK(completeBlank);
                    break;

                case "phylum":
                    taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank);
                    break;

                case "class":
                    taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                            + getClasse(completeBlank);
                    break;

                case "order":
                    taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                            + getClasse(completeBlank) + sep + getOrder(completeBlank);
                    break;
                case "family":
                    taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                            + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                            + getFamily(completeBlank);
                    break;
                case "genus":
                    if () {
                        taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                                + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                                + getFamily(completeBlank) + sep + getGenus(completeBlank);
                    }
                    break;
                case "species":
                    taxoPath = getKingdomSK(completeBlank) + sep + getPhylum(completeBlank) + sep
                            + getClasse(completeBlank) + sep + getOrder(completeBlank) + sep
                            + getFamily(completeBlank) + sep + getGenus(completeBlank) + sep + getSpecies(completeBlank);
                    break;

            }

            if (this.getRank(rank).length() < 1) {
                this.incrementBlank();
            } else {
                this.incrementNotBlank();
            }
        }
    }*/
    public void computeBlanks(String levels[]) {
        //String levels[] = {"skingdom", "phylum", "class", "order", "family", "genus", "species"/*, "subspecies"*/};
        //Taxon t = new Taxon(0);
        int i = 0;
        for (String rank : levels) {
            if (this.getRank(rank).length() < 1) {
                this.incrementBlank();
            } else {
                this.incrementNotBlank();
            }
        }
    }

    public String getKingdom(boolean complete) {
        if (complete && kingdom.length() < 1) {
            return "k";
        } else {
            return kingdom;
        }
    }

    public String getSuperKingdom(boolean complete) {
        if (complete && superkingdom.length() < 1) {
            return "sk";
        } else {
            return superkingdom;
        }
    }

    public String getKingdomSK(boolean complete) {
        String sKingdom = "";
        if (complete && superkingdom.length() < 1) {
            sKingdom += "sk";
        } else {
            sKingdom += superkingdom;
        }
        if (kingdom.length() > 1) {
            sKingdom += ";" + kingdom;
        } else {
            sKingdom += ";k";
        }
        return sKingdom;
    }

    public String getPhylum(boolean complete) {
        if (complete && phylum.length() < 1) {
            return "p";
        } else {
            return phylum;
        }
    }

    public String getClasse(boolean complete) {
        if (complete && classe.length() < 1) {
            return "c";
        } else {
            return classe;
        }
    }

    public String getOrder(boolean complete) {
        if (complete && order.length() < 1) {
            return "o";
        } else {
            return order;
        }
    }

    public String getFamily(boolean complete) {
        if (complete && family.length() < 1) {
            return "f";
        } else {
            return family;
        }
    }

    public String getGenus(boolean complete) {
        if (complete && genus.length() < 1) {
            return "g";
        } else {
            return genus;
        }
    }

    public String getSpecies(boolean complete) {
        if (complete && species.length() < 1) {
            if (completSP && genus.length() > 1) {
                return this.getGenus() + " sp.";
            } else {
                return "s";
            }
        } else {
            return species;
        }
    }

    public String getRank(String rank) {
        switch (rank.toLowerCase()) {
            case "kingdom":
                return this.getKingdom();
            case "superkingdom":
                return this.getSuperkingdom();
            case "subkingdom":
                return this.getSubkingdom();

            case "superphylum":
                return this.getSubphylum();
            case "phylum":
                return this.getPhylum();
            case "subphylum":
                return this.getSubphylum();
            case "superclass":
                return this.getSuperclass();
            case "infraclass":
                return this.getInfraclass();
            case "class":
                return this.getClasse();
            case "subclass":
                return this.getSubclass();
            case "parvorder":
                return this.getParvorder();
            case "superorder":
                return this.getSuperorder();
            case "infraorder":
                return this.getInfraorder();
            case "order":
                return this.getOrder();
            case "suborder":
                return this.getSuborder();
            case "superfamily":
                return this.getSuperfamily();
            case "family":
                return this.getFamily();
            case "subfamily":
                return this.getSubfamily();
            case "tribe":
                return this.getTribe();
            case "subtribe":
                return this.getSubtribe();
            case "genus":
                return this.getGenus();
            case "subgenus":
                return this.getSubgenus();
            case "species":
                return this.getSpecies();
            case "species group":
                return this.getSpecies_group();
            case "species subgroup":
                return this.getSpecies_subgroup();
            case "subspecies":
                return this.getSubspecies();
            case "forma":
                return this.getForma();
            case "varietas":
                return this.getVarietas();
            case "no rank":
                return this.getNo_rank();
            default:
                return "";

        }
    }

    public void assignRank(String name, String rank) {
        switch (rank.toLowerCase()) {
            case "kingdom":
                this.setKingdom(name);
                break;
            case "superkingdom":
                this.setSuperkingdom(name);
                break;
            case "subkingdom":
                this.setSubkingdom(name);
                break;
            case "superphylum":
                this.setSubphylum(name);
                break;
            case "phylum":
                this.setPhylum(name);
                break;
            case "subphylum":
                this.setSubphylum(name);
                break;
            case "superclass":
                this.setSuperclass(name);
                break;
            case "infraclass":
                this.setInfraclass(name);
                break;
            case "class":
                this.setClasse(name);
                break;
            case "subclass":
                this.setSubclass(name);
                break;
            case "parvorder":
                this.setParvorder(name);
                break;
            case "superorder":
                this.setSuperorder(name);
                break;
            case "infraorder":
                this.setInfraorder(name);
                break;
            case "order":
                this.setOrder(name);
                break;
            case "suborder":
                this.setSuborder(name);
                break;
            case "superfamily":
                this.setSuperfamily(name);
                break;
            case "family":
                this.setFamily(name);
                break;
            case "subfamily":
                this.setSubfamily(name);
                break;
            case "tribe":
                this.setTribe(name);
                break;
            case "subtribe":
                this.setSubtribe(name);
                break;
            case "genus":
                this.setGenus(name);
                break;
            case "subgenus":
                this.setSubgenus(name);
                break;
            case "species":
                this.setSpecies(name);
                break;
            case "species_group":
                this.setSpecies_group(name);
                break;
            case "species_subgroup":
                this.setSpecies_subgroup(name);
                break;
            case "subspecies":
                this.setSubspecies(name);
                break;
            case "forma":
                this.setForma(name);
                break;
            case "varietas":
                this.setVarietas(name);
                break;
            case "no_rank":
                this.no_rank += name + ";";
                break;
        }
    }

    /**
     * This method assign taxonomy levels according to qiime nomenclature
     * template (qiime/97_otu_taxonomy.txt)
     *
     * @param name the name of the taxon
     * @param rank the rank of such taxon
     */
    public void assignQiimeRank(String name, String rank) {
        switch (rank) {
            case "kingdom":
                this.kingdom = kingdom + (name);
                break;
            case "superkingdom":
                this.superkingdom = superkingdom +(name);
                break;
            case "phylum":
                this.phylum = phylum + (name);
                break;

            case "class":
                this.classe = classe + (name);
                break;

            case "order":
                this.order = order + (name);
                break;
            case "family":
                this.family = family + (name);
                break;
            case "genus":
                this.genus = genus + (name);
                break;
            case "species":
                this.species = species + (name);
                break;

        }
    }

    public String getKingdom(boolean complete, String value) {
        if (complete && kingdom.length() < 1) {
            return value;
        } else {
            return kingdom;
        }
    }

    public String getKingdomSK(boolean complete, String value) {
        String sKingdom = "";
        if (complete && superkingdom.length() < 1) {
            sKingdom += "sk";
        } else {
            sKingdom += superkingdom;
        }
        if (kingdom.length() > 1) {
            sKingdom += ";" + kingdom;
        }
        return sKingdom;
    }

    public String getPhylum(boolean complete, String value) {
        if (complete && phylum.length() < 1) {
            return value;
        } else {
            return phylum;
        }
    }

    public String getClasse(boolean complete, String value) {
        if (complete && classe.length() < 1) {
            return value;
        } else {
            return classe;
        }
    }

    public String getOrder(boolean complete, String value) {
        if (complete && order.length() < 1) {
            return value;
        } else {
            return order;
        }
    }

    public String getFamily(boolean complete, String value) {
        if (complete && family.length() < 1) {
            return value;
        } else {
            return family;
        }
    }

    public String getGenus(boolean complete, String value) {
        if (complete && genus.length() < 1) {
            return value;
        } else {
            return genus;
        }
    }

    public String getSpecies(boolean complete, String value) {
        if (complete && species.length() < 1) {
            return value;
        } else {
            return species;
        }
    }

}
