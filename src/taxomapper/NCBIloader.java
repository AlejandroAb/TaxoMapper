/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxomapper;

import database.Transacciones;

/**
 *
 * @author aabdala
 */
public class NCBIloader {

    /**
     * CLASSPATH: $export
     * CLASSPATH=/home/NIOZ.NL/aabdala/NetBeansProjects/NCBITAX/build/classes/:/home/NIOZ.NL/aabdala/javalibs/
     *
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /* String database = "ncbitax";
        String user = "root";
        String host = "localhost";
        String password = "amorphis";
         */
        String database = "taxomap";
        String user = "";
        String host = "localhost";
        String password = "";

        String log = "";
        String nodes = "";
        String names = "";
        String merge = "";
        String mode = "";
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            System.out.println(help());
            System.exit(0);
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-n") || args[i].equals("--nodes")) {
                try {
                    i++;
                    nodes = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for nodes dmp file \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-a") || args[i].equals("--names")) {
                try {
                    i++;
                    names = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for names dmp file \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-m") || args[i].equals("--merge")) {
                try {
                    i++;
                    merge = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for merge dmp file \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-M") || args[i].equals("--MODE")) {
                try {
                    i++;
                    mode = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -M option \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-u") || args[i].equals("--user")) {
                try {
                    i++;
                    user = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --user option \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-p") || args[i].equals("--pass")) {
                try {
                    i++;
                    password = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --pass option \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-d") || args[i].equals("--database")) {
                try {
                    i++;
                    database = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --database option \nNeed help? use java ncbitax.NCBIloader -h | --help");
                    System.exit(1);

                }
            }
        }
        if (mode.equals("LOAD")) {
            if (!nodes.equals("") && !names.equals("") && !merge.equals("")) {
                Transacciones transacciones = new Transacciones(database, user, host, password);
                NCBITaxCreator ncbi = new NCBITaxCreator(transacciones);
                log += ncbi.createTaxaListFromNCBI(nodes, names, true);
                ncbi.loadMergedTaxs(merge);
            } else {
                System.err.println("MODE=LOAD This mode requires -n nodes.dmp -a names.dmp and -m merged.dmp files");
            }

        } else if (mode.equals("MERGE")) {
            if (!merge.equals("")) {
                Transacciones transacciones = new Transacciones(database, user, host, password);
                NCBITaxCreator ncbi = new NCBITaxCreator(transacciones);
                ncbi.loadMergedTaxs(merge);
            } else {
                System.err.println("MODE=MERGE This mode requires -m merged.dmp file");
            }
        } else if (mode.equals("UPDATE")) {
            if (!nodes.equals("") && !names.equals("") && !names.equals("")) {
                Transacciones transacciones = new Transacciones(database, user, host, password);
                NCBITaxCreator ncbi = new NCBITaxCreator(transacciones);
                log += ncbi.updateTaxaListFromNCBI(nodes, names, true);
                ncbi.loadMergedTaxs(merge);
            } else {
                System.err.println("MODE=UPDATE This mode requires -n nodes.dmp -a names.dmp and -m merged.dmp files");
            }
        } else if (mode.equals("TAXON")) {
            Transacciones transacciones = new Transacciones(database, user, host, password);
            NCBITaxCreator ncbi = new NCBITaxCreator(transacciones);
            ncbi.createTaxon("", false, "");
        } else {
            System.err.println("Incorrect mode operand. It should be one from: LOAD, MERGE, UPDATE \nNeed help? use java ncbitax.NCBIloader -h | --help");
        }
    }

    private static String help() {
        String help = "\n#####################################################\n"
                + "###            NCBI TAXONOMI DB LOADER            ###\n"
                + "###                    v 1.0                      ###\n"
                + "###                             @author A. Abdala ###\n"
                + "####################################################\n\n"
                + "Usage java ncbitax.NCBIloader -M MODE [options according to MODE]\n"
                + "This program works with taxonomy database dump files from NCBI which can be downloaded from:\n"
                + "ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdump.(zip|tar.gz)\n"
                + "Modes:\n"
                + "\tLOAD\tThis option loads database from scratch.\n\t\tIt requires -n -a -m arguments\n"
                + "\tUPDATE\tThis option updates the database.\n\t\tIt requires -n -a -m arguments\n"
                + "\tMERGE\tThis option update the merge table for merged taxons.\n\t\tIt only requires -m argument\n"
                + "\tTAXON\tThis option update/populate taxon table. This option doesn't need any argument\n"
                + "Arguments:\n"
                + "  -n\t--nodes\tNCBI nodes.dmp file.\n"
                + "  -a\t--names\tNCBI names.dmp file.\n"
                + "  -m\t--merge\tNCBI merged.dmp file.\n"
                + "Connecting to the database:\n"
                + "  -u\t--users\tMySQL user name (required).\n"
                + "  -p\t--pass\tMySQL password (required).\n"
                + "  -d\t--database\tMySQL database name (default taxomap).\n";

        return help;
    }

}
