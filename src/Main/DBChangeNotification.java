package Main;

import models.StatementModel;
import oracle.jdbc.OracleConnection;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.dcn.DatabaseChangeRegistration;
import utils.FileUtils;

import javax.swing.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Ma7MOoOD SHaRaF
 */

public class DBChangeNotification {
    static OracleConnection conn;
    static FileUtils utils = new FileUtils();
    ResultSet rs;

    public static void main(String[] args) {
        DBChangeNotification dcn = new DBChangeNotification();
        try {
            dcn.run();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Fail to connect to Oracle Database,\n" + ex.getMessage());
        }
    }

    void run() throws SQLException {
        int index = 1;
        utils.readConfigFromJSON();
        conn = utils.connect(utils.con.ip, utils.con.sid, utils.con.username, utils.con.password);
        Properties prop = new Properties();
        prop.setProperty(OracleConnection.DCN_NOTIFY_ROWIDS, "true");
        prop.setProperty(OracleConnection.DCN_BEST_EFFORT, "true");
        prop.setProperty(OracleConnection.DCN_QUERY_CHANGE_NOTIFICATION, "true");
        DatabaseChangeRegistration dcr = conn.registerDatabaseChangeNotification(prop);
        try {
            DCNListener listener = new DCNListener(this, utils);
            dcr.addListener(listener);
            for (StatementModel query : utils.stmtList) {
                System.out.println("Query: " + query.stmt);
                index = 1;
                OraclePreparedStatement stmt = (OraclePreparedStatement) conn.prepareStatement(query.stmt);
                for (Object val : query.params) {
                    stmt = utils.setStatementParams(stmt, index, val);
                    index++;
                }
                stmt.setDatabaseChangeRegistration(dcr);
                rs = stmt.executeQuery();
//                int size = 0;
//                while (rs.next())
//                    size++;
//                System.out.println("Rs: "+size);
                stmt.close();
                rs.close();
            }
            String[] tableNames = dcr.getTables();
            for (String tableName : tableNames) {
                System.out.println(tableName + " is part of the registration.");
            }
        } catch (SQLException ex) {
            conn.unregisterDatabaseChangeNotification(dcr);
            conn.close();
            utils.writeLog("Unregister DCN and connection closed: " + ex.getMessage());
            JOptionPane.showMessageDialog(null, "Listener fail to start,\n" + ex.getMessage());
        }
    }
}