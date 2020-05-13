/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException;
import java.sql.*;
import java.util.*;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Conexion {

    private Connection connection; //para establecer conexión
    private Statement statement;
    private String user;
    private String con;
    private ResultSet resultSet; //para traer la información obtenida por ResultSetMetaData
    private ArrayList<String> titulos;/// = new ArrayList<String>();
    private ArrayList<ArrayList> tabla;// = new ArrayList<ArrayList>(50000);;
    public ResultSetMetaData rsmd;
    public int estatus;
    public boolean seConecto;
    public String IP;
    PreparedStatement stmt = null;
    PreparedStatement stmt1 = null;
    PreparedStatement stmt2 = null;
    private String log;

    public Connection getConnection() {
        return connection;
    }

    /**
     *
     * @param db String El nombre de la base de datos
     * @param ip String La dirección IP donde se encuentra la base de datos
     */
    public Conexion(String db, String ip, String user, String pass) {
        IP = ip;
        String manejador
                = //da de alta la base de datos en el sistema operativo
                // "jdbc:mysql://" + IP + ":3306/" + db + "?user=root&=";
                "jdbc:mysql://" + IP + ":3306/" + db + "?user=" + user + "&=";
        try {
            Class.forName("com.mysql.jdbc.Driver"); //controlador de msqlpara base de datos
            //  connection = DriverManager.getConnection(manejador, "root", "AMORPHIS"); //genera la conexión
            connection = DriverManager.getConnection(manejador, user, pass);
            seConecto = true;
            statement = connection.createStatement();

        } catch (Exception e) {
            e.printStackTrace();
            String stack = "";
            seConecto = false;
            for (int i = 0; i < e.getStackTrace().length; i++) {
                stack += ((StackTraceElement[]) e.getStackTrace())[i].getClassName()
                        + " --> "
                        + ((StackTraceElement[]) e.getStackTrace())[i].getLineNumber()
                        + "\n";
            }
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(
                        "error.log")));
                out.println(stack);
                out.close();
            } catch (Exception ex) {
            }

        }
    }

    public String getLog() {
        return log;
    }

    public void setLog(String log) {
        this.log = log;
    }

    public ArrayList<ArrayList> getTabla() {
        return tabla;
    }

    public void setPreparedStatemenS1(String query) throws SQLException {
        stmt1 = connection.prepareStatement(query);

    }

    public void setPreparedStatemenS2(String query) throws SQLException {
        stmt2 = connection.prepareStatement(query);

    }

    public void executePreparedStatementS1(int tax_id) {

        try {
            //pState = connection.prepareStatement(query);
            //statement = connection.createStatement();
            stmt1.setInt(1, tax_id);
            resultSet = stmt1.executeQuery();
            //resultSet = statement.executeQuery(query); //ponemos el resultado de la ejecución
            //en un ResultSet

            llenaResultados(resultSet);
            //stmt1.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    public void executePreparedStatementS2(String hierarchy) {

        try {
            //pState = connection.prepareStatement(query);
            //statement = connection.createStatement();
            
           // Array array = statement.getConnection().createArrayOf("VARCHAR", new Object[]{"A1", "B2","C3"});
            Array array = connection.createArrayOf("VARCHAR", hierarchy.split(","));
            stmt2.setArray(1, array);
            resultSet = stmt2.executeQuery();
            //resultSet = statement.executeQuery(query); //ponemos el resultado de la ejecución
            //en un ResultSet

            llenaResultados(resultSet);
            //stmt1.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    /**
     * Ejecuta instrucciones SQL proncipalmente de tipo SELECT
     *
     * @param query
     * @return message Mensaje de error por si falla algo
     */
    public String executeStatementToFile(String query) {
        try {
            statement = connection.createStatement();
            titulos = new ArrayList<String>();
            //Filas = new Vector();
            tabla = new ArrayList<ArrayList>();
            resultSet = statement.executeQuery(query); //ponemos el resultado de la ejecución
            //en un ResultSet

            //llenaResultados(resultSet);
            statement.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return "sin conexion";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "sin conexion";
        }
        return "";
    }

    public ArrayList<String> getTitulos() {
        return titulos;
    }

    /**
     * metodo para ejecutar peticiones de tipo insert, update, delete.
     *
     * @param peticion String
     * @return boolean
     */
    public boolean queryUpdate(String peticion) { //recive el query a ejecutarse
        try {
             statement = connection.createStatement(); //ingresa a la bd
            estatus = statement.executeUpdate(peticion); //ejecuta la petición
            if (estatus != 0) { //ejecutar un query regresa una bandera, si esta es igual a 1
                //significa que se realizó con exito
                // JOptionPane.showMessageDialog(null, "Operacion exitosa");
                return true;
            } else {
                // JOptionPane.showMessageDialog(null,
                //    "Se detecto un error\n por favor verifique los datos");
                return false;
            }
        } catch (MySQLSyntaxErrorException mysqlex) {
            //donde se genera la excepción
            // mysqlex.printStackTrace();
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            log = "Error de sintaxis: " + peticion;
            return false;
        } catch (SQLException sqle) { // si el query estaba mal formulado es aca
            //donde se genera la excepción
            // sqle.printStackTrace();
            log = "SQL Exp: " + peticion;
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            return false;
        } catch (Exception ex) {
            log = "Exp en query: " + peticion;
            log += "\n" + ex.getLocalizedMessage();
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            return false;
        }

    }

    public boolean queryUpdate2(String peticion) throws SQLException { //recive el query a ejecutarse
        try {
            statement = connection.createStatement(); //ingresa a la bd
            estatus = statement.executeUpdate(peticion); //ejecuta la petición
            if (estatus != 0) { //ejecutar un query regresa una bandera, si esta es igual a 1
                //significa que se realizó con exito
                // JOptionPane.showMessageDialog(null, "Operacion exitosa");
                return true;
            } else {
                // JOptionPane.showMessageDialog(null,
                //    "Se detecto un error\n por favor verifique los datos");
                return false;
            }
        } catch (SQLException sqle) { // si el query estaba mal formulado es aca
            //donde se genera la excepción
            //  sqle.printStackTrace();
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            return false;
        }

    }

    /**
     * metodo para ejecutar peticiones de tipo insert, update, delete. Pero
     * obtiene el ID de la llave generada
     *
     * @param peticion String
     * @return boolean
     */
    public int queryUpdateWithKey(String query) { //recive el query a ejecutarse
        try {
            statement = connection.createStatement(); //ingresa a la bd
            // estatus = statement.executeUpdate(peticion); //ejecuta la petición
            //  Statement st = conn.createStatement();
            statement.execute(query,
                    Statement.RETURN_GENERATED_KEYS);

            ResultSet keys = statement.getGeneratedKeys();
            int id = -1;
            while (keys.next()) {
                id = keys.getInt(1);
            }
            return id;
        } catch (SQLException sqle) { // si el query estaba mal formulado es aca
            //donde se genera la excepción
            sqle.printStackTrace();
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, sqle);
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            return -1;
        } catch (Exception e) { // si el query estaba mal formulado es aca
            e.printStackTrace();
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
            return -1;
        }

    }

    public String preparedUpdate(String query, ArrayList<String> values, Vector ints, Vector floats) throws SQLException { //recive el query a ejecutarse

        try {
            stmt = connection.prepareStatement(query);
            //statement = connection.createStatement(); //ingresa a la bd
            // estatus = statement.executeUpdate(peticion); //ejecuta la petición
            int cont = 0;
            for (String value : values) {
                if (ints.contains("" + cont)) {
                    try {
                        stmt.setInt((cont + 1), Integer.parseInt(value));
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parse Int" + values.toString());
                        stmt.setInt((cont + 1), 0);
                        return "ERROR INT TYPE READ VALUE (NFE) = " + value + "\nRecord:" + cont + " value set to 0";

                    }
                } else if (floats.contains("" + cont)) {
                    try {
                        stmt.setFloat((cont + 1), Float.parseFloat(value));
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parse float" + values.toString());
                        stmt.setInt((cont + 1), 0);
                        return "ERROR FLOAT TYPE READ VALUE (NFE) = " + value + "\nRecord:" + cont + " value set to 0";
                    }
                } else {
                    stmt.setString((cont + 1), value);
                }
                cont++;
            }
            estatus = stmt.executeUpdate();
            if (estatus != 0) { //ejecutar un query regresa una bandera, si esta es igual a 1
                //significa que se realizó con exito
                // JOptionPane.showMessageDialog(null, "Operacion exitosa");
                return "OK";
            } else {
                // JOptionPane.showMessageDialog(null,
                //    "Se detecto un error\n por favor verifique los datos");
                return "NOT INSERTED " + cont;
            }
        } catch (SQLException sqle) { // si el query estaba mal formulado es aca
            //donde se genera la excepción
            //  sqle.printStackTrace();
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            return "SQL EXCPETION " + sqle.getMessage();
        }

    }
    String tuplo[];
    //  int cont = 0;

    public String preparedUpdateSB(String query, StringBuilder values, Vector ints, Vector floats) throws SQLException { //recive el query a ejecutarse

        try {
            stmt = connection.prepareStatement(query);
            //statement = connection.createStatement(); //ingresa a la bd
            // estatus = statement.executeUpdate(peticion); //ejecuta la petición
            // cont = 0;
            tuplo = values.toString().split("\t");

            //for (String value : tuplo) {
            for (int i = 0; i < tuplo.length; i++) {

                if (ints.contains("" + i)) {
                    try {
                        if (tuplo[i].toLowerCase().equals("null")) {
                            stmt.setNull((i + 1), java.sql.Types.INTEGER);
                        } else {
                            stmt.setInt((i + 1), Integer.parseInt(tuplo[i]/*value*/));
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parse Int" + values.toString());
                        stmt.setInt((i + 1), 0);
                        return "ERROR INT TYPE READ VALUE (NFE) = " + tuplo[i]/*value*/ + "\nRecord:" + i + " value set to 0";

                    }
                } else if (floats.contains("" + i)) {
                    try {
                        if (tuplo[i].toLowerCase().equals("null")) {
                            stmt.setNull((i + 1), java.sql.Types.FLOAT);
                        } else {
                            stmt.setFloat((i + 1), Float.parseFloat(tuplo[i]/*value*/));
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Error parse float" + values.toString());
                        stmt.setInt((i + 1), 0);

                        return "ERROR FLOAT TYPE READ VALUE (NFE) = " + tuplo[i]/*value*/ + "\nRecord:" + i + " value set to 0";
                    }
                } else {
                    if (tuplo[i].toLowerCase().equals("null")) {
                        stmt.setNull((i + 1), java.sql.Types.VARCHAR);
                    } else {
                        stmt.setString((i + 1), tuplo[i]/*value*/);
                    }
                }
            }
            //i++;

            estatus = stmt.executeUpdate();
            if (estatus != 0) { //ejecutar un query regresa una bandera, si esta es igual a 1
                //significa que se realizó con exito
                // JOptionPane.showMessageDialog(null, "Operacion exitosa");
                return "OK";
            } else {
                // JOptionPane.showMessageDialog(null,
                //    "Se detecto un error\n por favor verifique los datos");
                return "NOT INSERTED " /*+ cont*/;
            }
        } catch (SQLException sqle) { // si el query estaba mal formulado es aca
            //donde se genera la excepción
            //  sqle.printStackTrace();
            //  JOptionPane.showMessageDialog(null, "Error\n " + sqle.toString());
            return "SQL EXCPETION " + sqle.getMessage();
        }

    }

    /**
     * Ejecuta instrucciones SQL proncipalmente de tipo SELECT
     *
     * @param query
     * @return message Mensaje de error por si falla algo
     */
    public String executeStatement(String query) {
        try {
            statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);

            /*
            statement= connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE, 
            ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
             */
            // resultSet = new ResultSet();
            //resultSet = statement.executeQuery(query); //ponemos el resultado de la ejecución
            //en un ResultSet
            llenaResultados(statement.executeQuery(query));
            statement.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return "sin conexion";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "sin conexion";
        }
        return "";
    }

    public String executeStatementSB(String query, Transacciones t, String insertMethod, Vector ints, Vector floats, String tabla, String campos, String values) {
        try {
            statement = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY,
                    java.sql.ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
            /*
            statement= connection.createStatement(
            ResultSet.TYPE_SCROLL_INSENSITIVE, 
            ResultSet.CONCUR_READ_ONLY);
            statement.setFetchSize(Integer.MIN_VALUE);
             */
            // resultSet = new ResultSet();
            //resultSet = statement.executeQuery(query); //ponemos el resultado de la ejecución
            //en un ResultSet

            String res = llenaResultadosSB(statement.executeQuery(query), t, insertMethod, ints, floats, tabla, campos, values);
            statement.close();
            return res;
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return "sin conexion";
        } catch (Exception ex) {
            ex.printStackTrace();
            return "sin conexion";
        }
        //return "";
    }

    /**
     * //rgresa la bandera de estatus
     *
     * @return int
     */
    public int getEstatus() {
        return estatus;
    }

    /**
     * Metodo para ejecutar SQL coomands de tipo SELECT basicamente
     *
     * @param query Query a ejecutar
     */
    public void executePreparedS(String query) {
        PreparedStatement pState;
        try {
            pState = connection.prepareStatement(query);
            //statement = connection.createStatement();
            resultSet = pState.executeQuery();
            //resultSet = statement.executeQuery(query); //ponemos el resultado de la ejecución
            //en un ResultSet

            llenaResultados(resultSet);
            pState.close();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }

    /**
     * Este método es invocado por el método getTable y se encarga de llenar los
     * ArrayLists con la información obtenida del query ejecutado en el método
     * que lo invocó
     *
     * @param rs ResultSet Viene con los datos generados por la petición
     * @throws SQLException
     */
    private void llenaResultados(ResultSet rs) throws SQLException {
        // rs.setFetchSize(10000);
        //posiciona el primer dato
        boolean moreRecords = rs.next();

        // si no hay datos es aca donde se desplegara el mensaje
        titulos = new ArrayList<String>();
        //titulos.clear();

        //Filas = new Vector();
        tabla = new ArrayList<ArrayList>();
        //tabla.clear();
        if (!moreRecords) { //si no hay mas datos termina

            return;
        }

        try {
            // obtiene el título de las columnas
            ResultSetMetaData rsmd = rs.getMetaData();

            for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
                //Columnas.addElement(rsmd.getColumnName(i)); //pone el nombre a las columnas de la tabla
                titulos.add(rsmd.getColumnName(i));
            }
            do {
                //Filas.addElement(getNextRow(rs, rsmd)); //al vector filas los datos
                tabla.add(getNextTuplo(rs, rsmd));
            } while (rs.next()); //mientras existan registros

        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
        }
    }
    Class[] parametertypes = new Class[]{StringBuilder.class, Vector.class, Vector.class, Integer.class};
    Object params[] = new Object[4];//{null, ints, floats, cont};
    Method m = null;
    // StringBuilder newRes = new StringBuilder();
    //String tempRes = "";

    private String llenaResultadosSB(ResultSet rs, Transacciones t, String insertMethod, Vector ints, Vector floats, String table, String campos, String values) {
        try {
            boolean moreRecords = rs.next();
            if (!moreRecords) { //si no hay mas datos termina

                return "END";
            }

            rsmd = rs.getMetaData();
            //      newRes.delete(0, newRes.length());
            int cont = 1;
            params[1] = ints;
            params[2] = floats;
            params[3] = cont;
            m = null;
            /*try {
            m = t.getClass().getMethod(insertMethod, parametertypes);
            } catch (NoSuchMethodException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SecurityException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            }*/

            do {
                //params[0] = getNextTuploSB(rs, rsmd);
                try {
                    //  m.invoke(t, params);
                    //   tempRes = ((String) m.invoke(t, params));

                    // if (!tempRes.equals("OK")) {
                    //   newRes.append(tempRes).append("\n");
                    // }
                    // t.insertPreparedPhyloDistSB(getNextTuploSB(rs, rsmd), ints, floats, cont);
                    //   t.genericInsert(getNextTuploSB(rs, rsmd), ints, floats, cont, table, campos, values);
                } catch (SecurityException ex) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception e) {
                    Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, e);
                }
                //System.out.println("" + cont);
                cont++;
            } while (rs.next()); //mientras existan registros
            return "OK";//newRes.toString();
        } catch (SQLException sqlex) {
            sqlex.printStackTrace();
            return "ERROR SQLEX";
        }
    }

    /**
     * Agrega los datos obtenidos al vector
     *
     * @param rs ResultSet
     * @param rsmd ResultSetMetaData
     * @throws SQLException
     * @return Vector el vector con la información dela BD
     */
    //ArrayList<String> tuplo = new ArrayList<String>();
    /**
     * Agrega los datos obtenidos al vector
     *
     * @param rs ResultSet
     * @param rsmd ResultSetMetaData
     * @throws SQLException
     * @return Vector el vector con la información dela BD
     */
    private ArrayList getNextTuplo(ResultSet rs, ResultSetMetaData rsmd) throws
            SQLException {
        //Vector currentRow = new Vector();
        //Vector filaActual = new Vector();
        ArrayList<String> tuplo = new ArrayList<String>();
        for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
            switch (rsmd.getColumnType(i)) {
                //Son metodos q pertenecen a java.sql.Types
                case Types.VARCHAR:
                    //filaActual.addElement(rs.getString(i));
                    tuplo.add(rs.getString(i));
                    // filaActual.addElement(rs.toString());
                    break;
                case Types.CHAR:

                    //filaActual.addElement(rs.getString(i));
                    tuplo.add(rs.getString(i));
                    break;
                case Types.LONGVARCHAR:
                    //filaActual.addElement(rs.getString(i));
                    tuplo.add(rs.getString(i));
                    break;
                case Types.INTEGER:

                    //filaActual.addElement("" + rs.getLong(i));
                    tuplo.add("" + rs.getLong(i));
                    break;
                case Types.DOUBLE:
                    //filaActual.addElement("" + rs.getDouble(i));
                    tuplo.add("" + rs.getDouble(i));
                    break;
                case Types.SMALLINT:
                    //filaActual.addElement("" + rs.getInt(i));
                    tuplo.add("" + rs.getInt(i));
                    break;
                case Types.TINYINT:
                    //filaActual.addElement("" + rs.getInt(i));
                    tuplo.add("" + rs.getInt(i));
                    break;
                case Types.OTHER:
                    //filaActual.addElement(rs.getString(i));
                    tuplo.add(rs.getString(i));
                    break;
                default:
                    //filaActual.addElement(rs.getString(i));
                    tuplo.add(rs.getString(i));
                //  System.out.println("El campo es de tipo:  " +
                //                   rsmd.getColumnTypeName(i));
            }
        }
        return tuplo;
    }
    StringBuilder sTuplo = new StringBuilder();
    //String sTuplo = "";

    private StringBuilder getNextTuploSB(ResultSet rs, ResultSetMetaData rsmd) throws
            SQLException {
        //Vector currentRow = new Vector();
        //Vector filaActual = new Vector();
        //ArrayList<String> tuplo = new ArrayList<String>();
        //  tuplo.clear();
        //  System.gc();
        //sTuplo = "";
        sTuplo = new StringBuilder();
        for (int i = 1; i <= rsmd.getColumnCount(); ++i) {
            switch (rsmd.getColumnType(i)) {
                //Son metodos q pertenecen a java.sql.Types
                case Types.VARCHAR:
                    //filaActual.addElement(rs.getString(i));
                    //tuplo.add(rs.getString(i));
                    sTuplo.append(rs.getString(i)).append("\t");
                    // filaActual.addElement(rs.toString());
                    break;
                case Types.CHAR:

                    //filaActual.addElement(rs.getString(i));
                    //tuplo.add(rs.getString(i));
                    sTuplo.append(rs.getString(i)).append("\t");
                    break;
                case Types.LONGVARCHAR:
                    //filaActual.addElement(rs.getString(i));
                    //tuplo.add(rs.getString(i));
                    sTuplo.append(rs.getString(i)).append("\t");
                    break;
                case Types.INTEGER:

                    //filaActual.addElement("" + rs.getLong(i));
                    //tuplo.add("" + rs.getLong(i));
                    sTuplo.append(rs.getLong(i)).append("\t");
                    break;
                case Types.DOUBLE:
                    //filaActual.addElement("" + rs.getDouble(i));
                    //tuplo.add("" + rs.getDouble(i));
                    sTuplo.append(rs.getDouble(i)).append("\t");
                    break;
                case Types.SMALLINT:
                    //filaActual.addElement("" + rs.getInt(i));
                    //tuplo.add("" + rs.getInt(i));
                    sTuplo.append(rs.getInt(i)).append("\t");
                    break;
                case Types.TINYINT:
                    //filaActual.addElement("" + rs.getInt(i));
                    //tuplo.add("" + rs.getInt(i));
                    sTuplo.append(rs.getInt(i)).append("\t");
                    break;
                case Types.OTHER:
                    //filaActual.addElement(rs.getString(i));
                    //tuplo.add(rs.getString(i));
                    sTuplo.append(rs.getString(i)).append("\t");
                    break;
                default:
                    //filaActual.addElement(rs.getString(i));
                    //tuplo.add(rs.getString(i));
                    sTuplo.append(rs.getString(i)).append("\t");
                //  System.out.println("El campo es de tipo:  " +
                //                   rsmd.getColumnTypeName(i));
            }
        }
        //System.out.println(sTuplo);
        return sTuplo;
    }

    /**
     * Cierra la copnexión con la BD
     */
    public void shutDown() {
        try {
            connection.close();
        } catch (SQLException sqlex) {
            System.err.println("No se puede desconectar");
            sqlex.printStackTrace();
        }
    }
}
