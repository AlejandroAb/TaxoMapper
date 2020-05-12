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
