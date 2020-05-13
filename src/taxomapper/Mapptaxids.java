/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package taxomapper;

import database.Transacciones;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * This program is created to work as an interface between the user and the
 * taxonomy database. It offers multiple options in order to allow the user
 * create different mapping files, as well as retrieve taxonomic linage, LCA,
 * among other utilities.
 *
 * @author aabdala
 */
public class Mapptaxids {

    /**
     * Main method
     *
     * @param args arguments, run program with -h or --help
     */
    public static void main(String[] args) {
        String database = "taxomap";
        String user = "";
        String host = "localhost";
        String password = "";

        String log = "";
        String mappFile = "";
        String fastaFile = "";
        String filter = "";
        String sep = ";";
        String outFile = "mappedFile.txt";
        String split = "\t"; //how to split
        String rankType = "";//ALL or blanck | KNOWN | CLASSIC | CUSTOM  
        String rankValues = ""; //used for custom rankType
        String mode = "SINGLE";//SINGLE or blank | BATCH  | DB
        String level = "genus";
        int batch = 10;
        boolean debug = false;
        boolean notFasta = false;
        boolean appendTaxID = false;
        boolean withHashMap = false;
        boolean fullLine = false;
        boolean hasHeader = true;
        boolean removeCandidatus = false;
        boolean addTaxID = false;
        boolean completeSP = false;

        boolean completeLevels = true;
        boolean searchEukaryot = false;
        int taxCol = -1;//which column of the spplited line contains the taxid (from 1)
        int accCol = -1;//which column of the spplited line contains the acc (from 1)
        int min_blanks = -1;
        int levels_to_look = 2;
        int max_attempts = 0;
        boolean discard_not_found = true;
        String rp_sufix = "_RP";
        String mapSeq_cutoff = "";
        String mapSeq_name = "";
        String mapSeq_levels = "";
        boolean mapSeq_out = false;
        boolean disscard_chloroplasts = true;
        if (args.length == 0 || args[0].equals(
                "-h") || args[0].equals("--help")) {
            System.out.println(help());
            System.exit(0);
        }
        for (int i = 0;
                i < args.length;
                i++) {
            if (args[i].equals("-m") || args[i].equals("--map")) {
                try {
                    i++;
                    mappFile = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for mapping file \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-f") || args[i].equals("--fasta")) {
                try {
                    i++;
                    fastaFile = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -f (fasta file) option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-fi") || args[i].equals("--filter")) {
                try {
                    i++;
                    filter = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -fI (filter) option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-u") || args[i].equals("--user")) {
                try {
                    i++;
                    user = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --user option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-p") || args[i].equals("--pass")) {
                try {
                    i++;
                    password = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --pass option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-d") || args[i].equals("--database")) {
                try {
                    i++;
                    database = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --database option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-e") || args[i].equals("--sep")) {
                try {
                    i++;
                    sep = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -sep option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-M") || args[i].equals("--mode")) {
                try {
                    i++;
                    mode = args[i].toUpperCase();
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -m (mode) option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-o") || args[i].equals("--out")) {
                try {
                    i++;
                    outFile = args[i];
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -o option (output file) \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-r") || args[i].equals("--rank")) {
                try {
                    i++;
                    rankType = args[i].toUpperCase();
                    if (rankType.equals("CUSTOM")) { //se espera una linea de string comma separated con los niveles deseados
                        try {
                            i++;
                            rankValues = args[i];
                        } catch (ArrayIndexOutOfBoundsException aoie) {
                            System.err.println("Argument expected for -r with option CUSTOM (rankType) i.e -r CUSTOM kingdom,phylum,class \nNeed help? use Mapptaxids -h | --help");
                            System.exit(1);

                        }
                    }
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -r option (rankType) \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-tc") || args[i].equals("--tax-column")) {
                try {
                    i++;
                    taxCol = Integer.parseInt(args[i]);

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -c option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                } catch (NumberFormatException nfe) {
                    System.err.println("Numeric argument expected for -tc option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-ac") || args[i].equals("--acc-column")) {
                try {
                    i++;
                    accCol = Integer.parseInt(args[i]);

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -ac option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                } catch (NumberFormatException nfe) {
                    System.err.println("Numeric argument expected for -ac option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-b") || args[i].equals("--batch")) {
                try {
                    i++;
                    batch = Integer.parseInt(args[i]);

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -b option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                } catch (NumberFormatException nfe) {
                    System.err.println("Numeric argument expected for -b option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-s") || args[i].equals("--split")) {
                try {
                    i++;
                    split = args[i];

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -s option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("-at") || args[i].equals("--max-attempts")) {
                try {
                    i++;
                    max_attempts = Integer.parseInt(args[i]);

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -at option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                } catch (NumberFormatException nfe) {
                    System.err.println("Numeric argument expected for -at option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("-l") || args[i].equals("--level")) {
                try {
                    i++;
                    level = args[i];

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -l | --level option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("--complete-sp-with-genus")) {
                completeSP = true;
            } else if (args[i].equals("-v") || args[i].equals("--verbose")) {
                debug = true;
            } else if (args[i].equals("-n") || args[i].equals("--not-fasta")) {
                notFasta = true;
            } else if (args[i].equals("-h") || args[i].equals("--hash")) {
                withHashMap = true;
            } else if (args[i].equals("-t") || args[i].equals("--taxid")) {
                appendTaxID = true;
            } else if (args[i].equals("--full")) {
                fullLine = true;
            } else if (args[i].equals("--no-header")) {
                hasHeader = false;
            } else if (args[i].equals("--no-complete-level")) {
                completeLevels = false;
            } else if (args[i].equals("--remove-candidatus")) {
                removeCandidatus = true;
            } else if (args[i].equals("--search-eukaryota")) {
                searchEukaryot = true;
            } else if (args[i].equals("--add-taxid")) {
                addTaxID = true;
            } else if (args[i].equals("--retain-not-found")) {
                discard_not_found = false;
            } else if (args[i].equals("--min-blanks")) {
                try {
                    i++;
                    min_blanks = Integer.parseInt(args[i]);

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --min-blanks \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                } catch (NumberFormatException nfe) {
                    System.err.println("Numeric argument expected for --min-blanks option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("--levels-to-look")) {
                try {
                    i++;
                    levels_to_look = Integer.parseInt(args[i]);

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for -levels-to-look \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                } catch (NumberFormatException nfe) {
                    System.err.println("Numeric argument expected for -levels-to-look option \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);

                }
            } else if (args[i].equals("--rp-sufix")) {
                try {
                    i++;
                    rp_sufix = args[i];

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --rp-sufix option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("--maps-cutoff")) {
                try {
                    i++;
                    mapSeq_cutoff = args[i];

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --maps-cutoff option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("--maps-levels")) {
                try {
                    i++;
                    mapSeq_levels = args[i];

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --maps-levels option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("--maps-name")) {
                try {
                    i++;
                    mapSeq_name = args[i];

                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --maps-name option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else if (args[i].equals("--maps-out")) {
                try {
                    mapSeq_out = true;
                } catch (ArrayIndexOutOfBoundsException aoie) {
                    System.err.println("Argument expected for --maps-name option  \nNeed help? use Mapptaxids -h | --help");
                    System.exit(1);
                }
            } else {
                System.err.println("Wrong argument: " + args[i] + "\nNeed help? use Mapptaxids -h | --help");
                System.exit(1);
            }

        }

        if (mode.equals("MATRIX")) {
            Transacciones transacciones = new Transacciones(database, user, host, password);
            NCBIMapper mapper = new NCBIMapper(transacciones);
            mapper.setSearchEukaryot(searchEukaryot);
            mapper.mapAbundanceMatrix(mappFile, outFile, split, level, sep, completeLevels, hasHeader, removeCandidatus, addTaxID);
        } else if (mode.equals("FASTA_SILVA")) {
            Transacciones transacciones = new Transacciones(database, user, host, password);
            NCBIMapper mapper = new NCBIMapper(transacciones);
            mapper.setSearchEukaryot(searchEukaryot);
            mapper.mapFastaSilvaFile(mappFile, outFile, "\t", level, sep, completeLevels, removeCandidatus);
            //mapper.mapFastaFile(mappFile, outFile, "\t", level, sep, completeLevels, removeCandidatus);
        } else if (mode.equals("MAP")) {
            Transacciones transacciones = new Transacciones(database, user, host, password);
            NCBIMapper mapper = new NCBIMapper(transacciones);
            if (mapSeq_out) {
                mapper.setMapSeq_out(mapSeq_out);
                if (mapSeq_name.length() > 0) {
                    mapper.setMapSeq_name(mapSeq_name);
                }
                if (mapSeq_cutoff.length() > 0) {
                    mapper.setMapSeq_cutoff(mapSeq_cutoff);
                }
                if (mapSeq_levels.length() > 0) {
                    mapper.setMapSeq_levels(mapSeq_levels);
                }
            }
            mapper.setSearchEukaryot(searchEukaryot);
            if (rankType.length() > 0) {
                mapper.setRankType(rankType);
            }
            if (rankValues.length() > 0) {
                mapper.setRankValues(rankValues);
            }
            if (min_blanks != -1) {
                mapper.setBlanks(min_blanks);
            }
            if (levels_to_look != -1) {
                mapper.setLevels_to_look(levels_to_look);
            }
            if (filter.length() > 0) {
                mapper.setFilter(filter);
            }
            mapper.setMax_attempts(max_attempts);
            mapper.setRp_sufix(rp_sufix);
            mapper.setSearchEukaryot(searchEukaryot);
            mapper.setDiscard_not_found(discard_not_found);
            mapper.setCompleteSP(completeSP);
            mapper.mapRegularFile(mappFile, outFile, "\t", level, sep, completeLevels, removeCandidatus);
            //mapper.mapFastaFile(mappFile, outFile, "\t", level, sep, completeLevels, removeCandidatus);
        } else if (mode.equals("FASTA")) {
            Transacciones transacciones = new Transacciones(database, user, host, password);
            NCBIMapper mapper = new NCBIMapper(transacciones);
            if (min_blanks != -1) {
                mapper.setBlanks(min_blanks);
            }
            if (levels_to_look != -1) {
                mapper.setLevels_to_look(levels_to_look);
            }
            if (filter.length() > 0) {
                mapper.setFilter(filter);
            }
            mapper.setMax_attempts(max_attempts);
            mapper.setRp_sufix(rp_sufix);
            mapper.setSearchEukaryot(searchEukaryot);
            mapper.setDiscard_not_found(discard_not_found);
            mapper.setCompleteSP(completeSP);
            // mapper.mapFastaSilvaFile(mappFile, outFile, "\t", level, sep, completeLevels, removeCandidatus);
            mapper.mapFastaFile(mappFile, outFile, "\t", level, sep, completeLevels, removeCandidatus);
        } else if ((!mappFile.equals("") && !fastaFile.equals("") && !mode.equals("DB")) || (!mappFile.equals("") && mode.equals("DB")) || (!mappFile.equals("") && fastaFile.equals("") && mode.equals("MERGED")) || (!mappFile.equals("") && mode.equals("QIIME"))) {
            Transacciones transacciones = new Transacciones(database, user, host, password);
            // NCBITaxCreator ncbi = new NCBITaxCreator(transacciones);
            // log += ncbi.createTaxaListFromNCBI(nodes, names,true);
            NCBIFastaMapping fmapp = new NCBIFastaMapping(mappFile, fastaFile, transacciones);
            fmapp.setOutput(outFile);
            fmapp.setAppendTaxid(appendTaxID);
            fmapp.setWithHashMap(withHashMap);
            fmapp.setSep(sep);
            fmapp.setNotFasta(notFasta);
            fmapp.setFullLine(fullLine);
            fmapp.setTaxID0(!discard_not_found);
            if (rankType.length() > 0) {
                fmapp.setRankType(rankType);
            }
            if (rankValues.length() > 0) {
                fmapp.setRankValues(rankValues);
            }
            if (taxCol > 0) {
                fmapp.setTaxIDCol(taxCol);
            }
            if (accCol > 0) {
                fmapp.setAccCol(accCol);
            }
            if (split.length() > 0) {
                fmapp.setSplitChars(split);
            }
            if (debug) {
                fmapp.setDebug(debug);
            }
            try {
                if (mode.equals("SINGLE")) {
                    fmapp.mappTaxIDs();
                } else if (mode.equals("BATCH")) {
                    fmapp.mappBulkTaxIDs(batch);
                } else if (mode.equals("DB")) {
                    if (filter.length() > 0) {
                        fmapp.setFilter(filter);
                    }
                    if (fmapp.getRankType().equals("CLASSIC")) {
                        fmapp.completeTaxaQiime(false);
                    } else {
                        fmapp.completeTaxa();
                    }

                } else if (mode.equals("MERGED")) {
                    fmapp.processNotAtDB();
                } else if (mode.equals("QIIME")) {
                    fmapp.completeTaxaQiime(true);
                } else {
                    System.err.println("Wrong mode: " + mode + "\nValid modes are: SINGLE, BATCH and DB. Need help? use Mapptaxids -h | --help");
                    System.exit(1);
                }

            } catch (FileNotFoundException fnfe) {
                System.err.println("Some of the input files can not be found. Please see the following log for more details:\n" + fnfe.getMessage());
            } catch (IOException ioe) {
                System.err.println("Error accessing file. Please see the following log for more details:\n" + ioe.getMessage());
            }
        } else {
            System.err.println("Invalid option(s)! please run java ncbitax.Mapptaxids --help");
        }
    }

    /**
     * Method to display the help menu
     *
     * @return
     */
    private static String help() {
        String help = "\n#################################################################\n"
                + "###                    NCBI TAXONOMy MAPPING                  ###\n"
                + "###                            v 1.5                          ###\n"
                + "###                                       @company       NIOZ ###\n"
                + "###                                       @author   A. Abdala ###\n"
                + "#################################################################\n"
                + "Program for mapping any kind of data like accesion numbers to tax ids and tax ids to taxonomic linage\n"
                + "usage java -jar TaxoMapper.jar -M <MODE>  [options]\n\n"
                + "Modes:\n"
                + "------------------------------\n"
                + "  -M\t--mode\tThis flag determines the search strategy. Valid option are:"
                + "\n  \t  SINGLE Default value. Needs the mapping file and the fasta file. Performs the "
                + "\n    \t    \tsearch by grepping element per element"
                + "\n  \t  BATCH Needs the mapping file and the fasta file. Performs the search by grepping "
                + "\n    \t    \tA BATCH of elements. The default batch number is ten but can by changed with "
                + "\n    \t    \t-b or --batch options"
                + "\n  \t  DB This option suppose that the user already have a mapped file with only all the "
                + "\n    \t    \trequired accessions and tax ids, so it only takes the maping file and search "
                + "\n    \t    \tfor the taxonomic information into the DB."
                + "\n  \t  QIIME This option suppose that the user already have a mapped file with only all the "
                + "\n    \t    \trequired accessions and tax ids, so it only takes the maping file and search for "
                + "\n    \t    \tthe taxonomic information into the DB and create a mapping file like the one "
                + "\n    \t    \trequired for QIIME assign_taxonomy.py script."
                + "\n  \t  MERGED This option takes as input the mapping file, which in this case should be the "
                + "\n    \t    \toutput file generated when the tax id is not found on reference DB, on this cases "
                + "\n    \t    \tthe file extension is '.not_at_db'.\n"
                + "\n  \t  MATRIX This option takes a matrix as input (-m) and try to map the already present taxonomy path (column 1)"
                + "\n    \t    \taccording to the NCBI taxonomy path. This mode is very usefull to align the names to a common DB\n"
                + "\n  \t  FASTA_SILVA This option takes a fasta file as input (-m) It shoul be on SILVA's format:"
                + "\n    \t    \t\">ACC[space]Taxonomy_path\"The program takes this taxonomy path and performs a mapping\n"
                + "\n    \t    \taccording to the NCBI taxonomy path which is returned on the output file (-o --out).\n"
                + "\n  \t  FASTA Same as previous but it works for any fasta file, make sure to correctly pass values for -tc & -s \n"
                + "    \t      \tMore documentation on this MODE to be done meanwhile redmine issue #250\n"
                + "    \t  MAP Mapping a regular plain file. This file will try to map accessions already mapped to any taxonomy\n"
                + "    \t      \tPhylogeny should be located at -tc [2 by def]. Accession output at column -ac [1 by def.]\n"
                + "    \t      \tInput is splited by -s [\\t by def.]. Phylogeny path is splited by -e [; by def]\n"
                + "Connecting to the database:\n"
                + "------------------------------\n"
                + "  -u\t--users\tMySQL user name (required).\n"
                + "  -p\t--pass\tMySQL password (required).\n"
                + "  -d\t--database\tMySQL database name (default taxomap).\n"
                + "Mandatory arguments:\n"
                + "------------------------------\n"
                + "  -m\t--map\tMapping file with correspondance between any type of data and NCBI tax ids or any taxonomic path"
                + "\n    \t    \tto be mapped"
                + "This kind of target files this program process are BLAST or Diamond outputfiles, as wel as "
                + "\n    \t    \tmapping files like the ones found at : ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/accession2taxid/README\n"
                + "  -f\t--fasta\tFasta file with the accession number to be mapped against tax id. "
                + "\n    \t    \tOnly for mode DB it is not mandatory!\n"
                + "\nOptional arguments:\n"
                + "------------------------------\n"
                + "  -tc\t--tax-column\tThe column on the mapping file were the taxid is found (def "
                + "\n       \t    \t\tvalue equal to NCBI mapping files default col = 3)\n"
                + "  -ac\t--acc-column\tThe column on the mapping file were the accession number is "
                + "\n   \t       \t\tfound (def value equal to NCBI mapping files default col = 2)\n"
                + "  -o\t--output\tName of the output file\n"
                + "  -e\t--sep   \tChar to separate taxonomic levels. Default is ';'\n"
                + "  -s\t--split \tChar or regex to split mapping file. Default is tab '\\t'\n"
                + "  -n\t--not-fasta\tIf the input file to be mapped is not a fasta file, it should be a file with "
                + "\n    \t    \t\ta list of taxids on that case, use this flag\n"
                + "  -b\t--batch   \tUsed on BATCH mode. Number of elements to be searched on one single grep.\n"
                + "  --no-header\t\tWhen the mode is matrix, do not treat the first row as the header, default false\n"
                + "  --no-complete-level\tIf the mode is MATRIX or FASTA_SILVA and try to translate the taxonomy path,\n"
                + "\t\tby default it will annotate blank levels with a lower case leter corresponding to the missing level.\n"
                + "\t\ti.e for genus g. for family f. and so on\n"
                + "  -t\t--taxid\tIf this flag is present the output file will contain an extra column with the"
                + "\n    \t    \ttaxid of the linage\n"
                + "  -h\t--hash\tIf this flag is present the program will use HashMap to reduce overhead on database "
                + "\n    \t    \taccess. In order to use this option be sure to have enough memory. If you use this "
                + "\n    \t    \toption posible HeapOverflowException\n"
                + "  --full\tIf this flag is present, the output file willcontain the COMPLETE original line from "
                + "\n    \t    \tthe mapping file and it will only append the taxonomy linage at the rigth, separated "
                + "\n    \t    \tby -s character\n"
                + "  -r\t--rank\tTaxonomic ranks to be considered for the ouput. Valid options are:"
                + "\n\t\tALL Default value. returns all the taxonomic levels "
                + "\n\t\tKNOWN Discard all taxonomic levels with label 'no rank' "
                + "\n\t\tCLASSIC use 'regular' taxa levels: kingdom, phylum, class, order, family, genus, species, subspecies"
                + "\n\t\tCUSTOM this option allows to select desired taxa levels, so after using this flag, comma "
                + "\n    \t    \tseparated levels should be included."
                + "\n  \t\tValid taxonomic levels are: kingdom,superkingdom,subkingdom,superphylum,phylum,subphylum,"
                + "\n    \t    \tsuperclass,infraclass,class,subclass,parvorder,superorder,infraorder,order,suborder,"
                + "\n    \t    \tsuperfamily,family,subfamily,tribe,subtribe,genus,subgenus,species,species group,"
                + "\n    \t    \tspecies subgroup,subspecies,forma,varietas"
                + "\n --min-blanks\tMinimun number of blanks/gaps into a taxonomy path (assigned according to NCBI) to"
                + "\n\t\tstart looking for possible reconstructed paths i.e: "
                + "\n\t\tNCBI mapas this: Bacteria;Proteobacteria;Gammaproteobacteria;..;..;..;Serratia;bacterium NTL337"
                + "\n\t\tto this:         Bacteria;p;c;o;f;g;bacterium NTL135 with this option, the mapping tool looks "
                + "\n\t\tinto the next level (Serratia) so feel all the blanks"
                + "\n --levels-to-look\tHow many levels we can continue looking. Default 2. It will continue looking only across the "
                + "\n\t\tfirst two levels (generaly: species and genus)"
                + "\n--rp-sufix\tIf a path is recreated between the original path and NCBI we add a suffix to the path. Default _RP"
                + "\n -at\t--max-attempts\tMaximun number of attempts to correct taxonomy path when we have a (s)kingdom change"
                + "\n--retain-not-found\t If after analyzing the taxonomic path and there is no tax_id found or the app cannot"
                + "\n\t\tresolve a kingdom shift whithin the --max-attempts by default the sequence is discarded."
                + "\n\t\tUse this flag to change the behaviour and retain the reads."
                + "\n -fi\t--filter\tComma separated of colon separated rank, value pairs on the following form:\n"
                + " \t\t<rank>:<value>,<rank>:<value2>,<value3>\n "
                + " \t\tIf the rank is missing (i.e: value3), the passed value will be filtered at any\n"
                + " \t\ttaxonomy rank.\n"
                + " --complete-sp-with-genus\tWhen ever the path does not at species level and we include the species on the\n"
                + "\t\tresult, use the genus (if available) in order to change s -> genus sp.\n";

        return help;
    }
}
