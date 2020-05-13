/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bobjects;

/**
 *
 * @author aabdala
 */
public class TaxFilter {

    private String rankFilter = "";
    private String filter;

    public TaxFilter(String filter) {
        this.filter = filter;
        rankFilter = "any";
    }

    public TaxFilter(String filter, String rankFilter) {
        this.filter = filter;
        this.rankFilter = rankFilter;
    }

    public String getRankFilter() {
        return rankFilter;
    }

    public void setRankFilter(String rankFilter) {
        this.rankFilter = rankFilter;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
