/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxomapper;

import bobjects.Taxon;
import bobjects.TaxonProcessor;
import database.Transacciones;

/**
 * Main clas for accession and sequence utilities, like subsetting or parsing
 * ids
 *
 * @author aabdala
 */
public class AccessionUtils {

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
        String input = "";
        String table = "";
        String output = "";
        String mode = "";
        String listType = "gi";
        String blastFile = "";
        int accCol = 0;
        int report = 5000;
        TaxonProcessor taxon = new TaxonProcessor(0);
        if (args.length == 0 || args[0].equals("-h") || args[0].equals("--help")) {
            System.out.println(help());
            System.exit(0);
        }
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-a") || args[i].equals("--accession_list")) {
                try {
                    i++;
                    input = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for accession_list  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }

            } else if (args[i].equals("-k") || args[i].equals("--kingdom")) {
                try {
                    i++;
                    taxon.setKingdom(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for kingdom  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-p") || args[i].equals("--phylum")) {
                try {
                    i++;
                    taxon.setPhylum(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for phylum  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-c") || args[i].equals("--class")) {
                try {
                    i++;
                    taxon.setClasse(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for class  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-or") || args[i].equals("--order")) {
                try {
                    i++;
                    taxon.setOrder(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for order  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-f") || args[i].equals("--family")) {
                try {
                    i++;
                    taxon.setFamily(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for family  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-g") || args[i].equals("--genus")) {
                try {
                    i++;
                    taxon.setGenus(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for order  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-s") || args[i].equals("--species")) {
                try {
                    i++;
                    taxon.setSpecies(args[i]);
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for species  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-t") || args[i].equals("--tax-id")) {
                try {
                    i++;

                    String log = taxon.parseTaxones(args[i].trim());
                    if (log.length() > 0) {
                        System.err.println(log);
                        System.exit(1);
                    }

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for tax-id  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-tf") || args[i].equals("--taxa-file")) {
                try {
                    i++;
                    String log = taxon.loadTaxonesFromFile(args[i].trim());
                    if (log.length() > 0) {
                        System.err.println(log);
                        System.exit(1);
                    }

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for species  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-l") || args[i].equals("--list")) {
                try {
                    i++;
                    if (args[i].toLowerCase().trim().equals("gi") || args[i].toLowerCase().trim().equals("acc")) {
                        listType = args[i].toLowerCase();
                    } else {
                        System.err.println("Wrong value for -l --list argument. It should be[gi | acc]  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                        System.exit(1);
                    }

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for species  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-b") || args[i].equals("--blast-file")) {
                try {
                    i++;
                    blastFile = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for blastFile  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }

            } else if (args[i].equals("-acc") || args[i].equals("--acc-column")) {
                try {
                    i++;
                    accCol = Integer.parseInt(args[i].trim());
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for blastFile  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                } catch (NumberFormatException nfe) {
                    System.err.println("Numerical parameter expected for -acc argument. Found:" + args[i]);
                }

            } else if (args[i].equals("-rep") || args[i].equals("--report-each")) {
                try {
                    i++;
                    report = Integer.parseInt(args[i].trim());
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for rep  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                } catch (NumberFormatException nfe) {
                    System.err.println("Numerical parameter expected for -rep argument. Found:" + args[i]);
                }

            } else if (args[i].equals("-db") || args[i].equals("--database")) {
                try {
                    i++;
                    table = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for database  \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }

            } else if (args[i].equals("-M") || args[i].equals("--MODE")) {
                try {
                    i++;
                    mode = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -M option \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-o") || args[i].equals("--out")) {
                try {
                    i++;
                    output = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --out option \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("--pass")) {
                try {
                    i++;
                    password = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --pass option \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-rd") || args[i].equals("--relational_database")) {
                try {
                    i++;
                    database = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --database option \nNeed help? use java ncbitax.AccessionUtils -h | --help");
                    System.exit(1);

                }
            }
        }
        if (mode.equals("PARSE")) {
            if (!input.equals("") || !table.equals("")) {
                Transacciones transacciones = new Transacciones(database, user, host, password);
                BlastdbParser bp = new BlastdbParser(transacciones);
                bp.parseFile(input, output, table);

            } else {
                System.err.println("MODE=PARSE This mode requires -a accessionList and -db ncbi_nt | ncbi_nr ");
                System.exit(1);
            }

        } else if (mode.equals("SUBSET")) {
            if (!output.equals("") || !table.equals("")) {
                Transacciones transacciones = new Transacciones(database, user, host, password);
                BlastdbParser bp = new BlastdbParser(transacciones);
                bp.setTaxon(taxon);
                bp.createTaxIdList(table, output, listType);
            } else {
                System.err.println("MODE=PARSE This mode requires -a accessionList and -db ncbi_nt | ncbi_nr ");
                System.exit(1);
            }
        } else if (mode.equals("MAPBLAST")) {
            if (!output.equals("") || !blastFile.equals("")) {
                Transacciones transacciones = new Transacciones(database, user, host, password);
                BlastdbParser bp = new BlastdbParser(transacciones);
                //bp.setTaxon(taxon);
                bp.mapBlastResult(blastFile, output, accCol, report);
            } else {
                System.err.println("MODE=MAPBLAST This mode requires -b blastFile and -o output ");
                System.exit(1);
            }
        } else {
            System.err.println("Incorrect mode operand. It should be one from: PARSE, SUBSET \nNeed help? use java ncbitax.AccessionUtils -h | --help");
        }
    }

    private static String help() {
        String help = "\n#####################################################\n"
                + "###            NCBI ACCESSION UTILS               ###\n"
                + "###                    v 1.0                      ###\n"
                + "###                             @author A. Abdala ###\n"
                + "####################################################\n\n"
                + "Usage java ncbitax.AccessionUtils -M MODE [options according to MODE]\n"
                + "Modes:\n"
                + "\tPARSE\tThis option parse a file with accessions, gis, seq_ids, tax_ids and seq tittles in order to store them into the relational database.\n\t\t*It requires  -a -db arguments\n"
                + "\tSUBSET\tThis option retrieves a list with accessions, gis or seq_ids. Depending on -id argument, default 'accession'\n\t\t*It requires  -id argument\n"
                + "Arguments for PARSE:\n"
                + "--------------------\n"
                + "  -a\t--accession_list\tfile with result from blastdbcmd with -outfmt \"%a;%g;%i;%T;%t\".\n"
                + "  -o\t--out\tName of the outfile, otherwise write directly into database.\n"
                + "  -db\t--database\tName of the target database for which the accession numbers will be loaded.\n\t\tValid databases are:"
                + "\n\t\t\t*ncbi_nt For ncbi nucleotide database \n\t\t\t*ncbi_nr For ncbi protein database\n"
                + "Arguments for SUBSET:\n"
                + "--------------------\n"
                + "  -k\t--kingdom\tTarget kingdom to retrive sequence id list.\n\t\tSingle or comma separated values [-k k1,k2,k3] no spaces!\n"
                + "  -p\t--phylum\tTarget phylum to retrive sequence id list.\n\t\tSingle or comma separated values [-p p1,p2,p3] no spaces!\n"
                + "  -c\t--class\tTarget class to retrive sequence id list.\n\t\tSingle or comma separated values [-c c1,c2,c3] no spaces!\n"
                + "  -or\t--order\tTarget order to retrive sequence id list.\n\t\tSingle or comma separated values [-or o1,o2,o3] no spaces!\n"
                + "  -f\t--family\tTarget family to retrive sequence id list.\n\t\tSingle or comma separated values [-f f1,f2,f3] no spaces!\n"
                + "  -g\t--genus\tTarget genus to retrive sequence id list.\n\t\tSingle or comma separated values [-g g1,g2,g3] no spaces!\n"
                + "  -s\t--species\tTarget species to retrive sequence id list.\n\t\tSingle or comma separated values [-s s1,s2,s3] no spaces!\n"
                + "  -t\t--tax-id\tTarget tax-id to retrive sequence id list.\n\t\tSingle or comma separated values [-t tid1,tid2,tid3] no spaces!\n"
                + "  -tf\t--tax-file\tFile containing target tax-ids to retrive sequence id list.\n\t\tFile format: one tax-id per line\n"
                + "  -l\t--list\tType of list to retrive. Use gi to retrive gi list or acc to retrive accession list.\n\t\tDefault value gi\n"
                + "  -o\t--out\tName of the outfile.\n"
                + "  -db\t--database\tName of the target database for which the accession or gi numbers will be extracted.\n\t\tValid databases are:"
                + "\n\t\t\t*ncbi_nt For ncbi nucleotide database \n\t\t\t*ncbi_nr For ncbi protein database\n"
                + "";

        return help;
    }

}
