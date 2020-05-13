/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

/**
 *
 * @author Alejandro
 */
public class StringUtils {

    public StringUtils() {
    }

    /**
     * Scape text to be inserted into sql
     *
     * @param text
     * @return
     */
    public String scapeSQL(String text) {
        String scapedText = text.replace("\\", "\\\\").replace("'", "\\'");
        return scapedText;

    }

    /**
     * This method is used mainly into NCBIFastaMapping and it is designed to
     * help with taxonomy assignation. When there is some subspecie to be
     * concatened into the name, and the user dont want to rewrite the genus
     * and/or the specie for the tax_id to be mapped, this method tries to
     * extract the different part of the taxon name to be concateneted with the
     * speci at the end of the file
     *
     * @param longText
     * @param base
     * @return
     */
    public String removeFromString(String longText, String base) {
        String tokens[] = base.split(" ");
        for (String token : tokens) {
            if (longText.startsWith(token)) {
                longText = longText.substring(token.length() + 1) + " ";
            }
        }
        return longText.trim();
    }

    /**
     * Este método obtiene el reverso complementario de una secuencia. Si se
     * ingresa GGTCAT se obtiene ATGACC
     *
     * @param cadena cadena a invertir
     * @return inverso complementario de la cadena
     */
    public String reversoComplementarioNuc(String cadena) {
        String invertida = "";
        if (cadena != null) {
            for (int i = cadena.length() - 1; i >= 0; i--) {
                char base = cadena.charAt(i);
                if (base == 'A') {
                    base = 'T';
                } else if (base == 'T') {
                    base = 'A';
                } else if (base == 'C') {
                    base = 'G';
                } else if (base == 'G') {
                    base = 'C';
                } else if (base == 'N') {
                    base = 'N';
                } else {
                    System.err.println("Caracter No Esperado. SUtils.reversoComplementarioNuc: " + base + "\nSecuencia: " + cadena);
                }
                invertida += base;
            }
        }
        return invertida;
    }

}
