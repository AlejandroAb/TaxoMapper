# TaxoMapper

TaxoMapper is a java based application created for two main purpouses:

* Assign taxonomy paths given NCBI's taxids
* Assign taxonomy paths given taxon names. 


## Installation

To run these utilities, you only need to have [Java](https://www.java.com/en/download/) and [MySQL](https://www.mysql.com/downloads/) in your system, so you just have to download the TaxoMapper directory and follow the hereunder instruction in order to install and configure the database.


## Create database

Download NCBI taxonomy: [taxdump.tar.gz](https://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdump.tar.gz)

You need MySQL Server <= 5.7 

Access mysql and create a new database **taxomap**

```sql
create database taxomap;
```
Select the database:
```sql
use taxomap;
```
Load the file db/taxomap_db.sql
```sql
source <path_to_file>/taxomap_db.sql
```
Please notice that *<path_to_file>* corresponds to the full path where you downloaded the taxomap_db.sql file.

### Load NCBI data

In order to load the database we use the NCBILoader.jar 
```
java -jar NCBILoader.jar

#####################################################
###            NCBI TAXONOMI DB LOADER            ###
###                    v 1.0                      ###
###                             @author A. Abdala ###
####################################################

Usage java ncbitax.NCBIloader -M MODE [options according to MODE]
This program works with taxonomy database dump files from NCBI which can be downloaded from:
ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/taxdump.(zip|tar.gz)
Modes:
        LOAD    This option loads database from scratch.
                It requires -n -a -m arguments
        UPDATE  This option updates the database.
                It requires -n -a -m arguments
        MERGE   This option update the merge table for merged taxons.
                It only requires -m argument
        TAXON   This option update/populate taxon table. This option doesn't need any argument
Arguments:
  -n    --nodes NCBI nodes.dmp file.
  -a    --names NCBI names.dmp file.
  -m    --merge NCBI merged.dmp file.
Connecting to the database:
  -u    --users MySQL user name (required).
  -p    --pass  MySQL password (required).
  -d    --database      MySQL database name (default taxomap).

```

As you can see in the Help menu above, first we need to load the database, threfore, once that the MySQL db has been created, we run the following command:

```
java -jar NCBILoader.jar -M LOAD -u <user> -p <pass> -n <path_to>/nodes.dmp -a <path_to>names.dmp -m <path_to>/merged.dmp  
```

## Using the mapping tool

In order to run the mapping tool we need the TaxoMapper.jar file.
If you dont use any argument, the help menu will be displayed:

```
java -jar TaxoMapper.jar 
```

```
#################################################################
###                    NCBI TAXONOMy MAPPING                  ###
###                            v 1.5                          ###
###                                       @company       NIOZ ###
###                                       @author   A. Abdala ###
#################################################################
Program for mapping any kind of data like accesion numbers to tax ids and tax ids to taxonomic linage
usage java ncbitax.Mapptaxids -M <MODE> -m Mapping_file -f Fasta_file [options]

Modes:
------------------------------
  -M    --mode  This flag determines the search strategy. Valid option are:
          SINGLE Default value. Needs the mapping file and the fasta file. Performs the
                search by grepping element per element
          BATCH Needs the mapping file and the fasta file. Performs the search by grepping
                A BATCH of elements. The default batch number is ten but can by changed with
                -b or --batch options
          DB This option suppose that the user already have a mapped file with only all the
                required accessions and tax ids, so it only takes the maping file and search
                for the taxonomic information into the DB.
          QIIME This option suppose that the user already have a mapped file with only all the
                required accessions and tax ids, so it only takes the maping file and search for
                the taxonomic information into the DB and create a mapping file like the one
                required for QIIME assign_taxonomy.py script.
          MERGED This option takes as input the mapping file, which in this case should be the
                output file generated when the tax id is not found on reference DB, on this cases
                the file extension is '.not_at_db'.

          MATRIX This option takes a matrix as input (-m) and try to map the already present taxonomy path (column 1)
                according to the NCBI taxonomy path. This mode is very usefull to align the names to a common DB

          FASTA_SILVA This option takes a fasta file as input (-m) It shoul be on SILVA's format:
                ">ACC[space]Taxonomy_path"The program takes this taxonomy path and performs a mapping

                according to the NCBI taxonomy path which is returned on the output file (-o --out).

          FASTA Same as previous but it works for any fasta file, make sure to correctly pass values for -tc & -s
                More documentation on this MODE to be done meanwhile redmine issue #250
          MAP Mapping a regular plain file. This file will try to map accessions already mapped to any taxonomy
                Phylogeny should be located at -tc [2 by def]. Accession output at column -ac [1 by def.]
                Input is splited by -s [\t by def.]. Phylogeny path is splited by -e [; by def]
Connecting to the database:
------------------------------
  -u    --users MySQL user name (required).
  -p    --pass  MySQL password (required).
  -d    --database      MySQL database name (default taxomap).
Mandatory arguments:
------------------------------
  -m    --map   Mapping file with correspondance between any type of data and NCBI tax ids or any taxonomic path
                to be mappedThis kind of target files this program process are BLAST or Diamond outputfiles, as wel as
                mapping files like the ones found at : ftp://ftp.ncbi.nlm.nih.gov/pub/taxonomy/accession2taxid/README
  -f    --fasta Fasta file with the accession number to be mapped against tax id.
                Only for mode DB it is not mandatory!

Optional arguments:
------------------------------
  -tc   --tax-column    The column on the mapping file were the taxid is found (def
                        value equal to NCBI mapping files default col = 3)
  -ac   --acc-column    The column on the mapping file were the accession number is
                        found (def value equal to NCBI mapping files default col = 2)
  -o    --output        Name of the output file
  -e    --sep           Char to separate taxonomic levels. Default is ';'
  -s    --split         Char or regex to split mapping file. Default is tab '\t'
  -n    --not-fasta     If the input file to be mapped is not a fasta file, it should be a file with
                        a list of taxids on that case, use this flag
  -b    --batch         Used on BATCH mode. Number of elements to be searched on one single grep.
  --no-header           When the mode is matrix, do not treat the first row as the header, default false
  --no-complete-level   If the mode is MATRIX or FASTA_SILVA and try to translate the taxonomy path,
                by default it will annotate blank levels with a lower case leter corresponding to the missing level.
                i.e for genus g. for family f. and so on
  -t    --taxid If this flag is present the output file will contain an extra column with the
                taxid of the linage
  -h    --hash  If this flag is present the program will use HashMap to reduce overhead on database
                access. In order to use this option be sure to have enough memory. If you use this
                option posible HeapOverflowException
  --full        If this flag is present, the output file willcontain the COMPLETE original line from
                the mapping file and it will only append the taxonomy linage at the rigth, separated
                by -s character
  -r    --rank  Taxonomic ranks to be considered for the ouput. Valid options are:
                ALL Default value. returns all the taxonomic levels
                KNOWN Discard all taxonomic levels with label 'no rank'
                CLASSIC use 'regular' taxa levels: kingdom, phylum, class, order, family, genus, species, subspecies
                CUSTOM this option allows to select desired taxa levels, so after using this flag, comma
                separated levels should be included.
                Valid taxonomic levels are: kingdom,superkingdom,subkingdom,superphylum,phylum,subphylum,
                superclass,infraclass,class,subclass,parvorder,superorder,infraorder,order,suborder,
                superfamily,family,subfamily,tribe,subtribe,genus,subgenus,species,species group,
                species subgroup,subspecies,forma,varietas
 --min-blanks   Minimun number of blanks/gaps into a taxonomy path (assigned according to NCBI) to
                start looking for possible reconstructed paths i.e:
                NCBI mapas this: Bacteria;Proteobacteria;Gammaproteobacteria;..;..;..;Serratia;bacterium NTL337
                to this:         Bacteria;p;c;o;f;g;bacterium NTL135 with this option, the mapping tool looks
                into the next level (Serratia) so feel all the blanks
 --levels-to-look       How many levels we can continue looking. Default 2. It will continue looking only across the
                first two levels (generaly: species and genus)
--rp-sufix      If a path is recreated between the original path and NCBI we add a suffix to the path. Default _RP
 -at    --max-attempts  Maximun number of attempts to correct taxonomy path when we have a (s)kingdom change
--retain-not-found       If after analyzing the taxonomic path and there is no tax_id found or the app cannot
                resolve a kingdom shift whithin the --max-attempts by default the sequence is discarded.
                Use this flag to change the behaviour and retain the reads.
 -fi    --filter        Comma separated of colon separated rank, value pairs on the following form:
                <rank>:<value>,<rank>:<value2>,<value3>
                If the rank is missing (i.e: value3), the passed value will be filtered at any
                taxonomy rank.
 --complete-sp-with-genus       When ever the path does not at species level and we include the species on the
                result, use the genus (if available) in order to change s -> genus sp.
```
