package Main;

import models.StatementModel;
import oracle.jdbc.OraclePreparedStatement;
import oracle.jdbc.dcn.DatabaseChangeEvent;
import oracle.jdbc.dcn.DatabaseChangeListener;
import oracle.jdbc.dcn.QueryChangeDescription;
import oracle.sql.ROWID;
import utils.FileUtils;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Ma7MOoOD SHaRaF
 */

public class DCNListener implements DatabaseChangeListener {

    final DBChangeNotification dcn;
    final FileUtils utils;
    StatementModel selectedQuery;
    OraclePreparedStatement stmt;
    ResultSet rs;

    DCNListener(DBChangeNotification dbCn, FileUtils utils) {
        dcn = dbCn;
        this.utils = utils;
    }

    @Override
    public void onDatabaseChangeNotification(DatabaseChangeEvent dce) {
        Thread t = Thread.currentThread();
        System.out.println("---------------------------------");
        System.out.println("DCNDemoListener: got an event (" + this + " running on thread " + t + ")");
        QueryChangeDescription[] qcds = dce.getQueryChangeDescription();
        for (QueryChangeDescription qcd : qcds) {
            utils.writeLog("DCNDemoListener: got an event with Op: " + qcd.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowOperation() + " - Rowid: " + qcd.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowid().stringValue());
            System.out.println("Op: " + qcd.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowOperation());
            System.out.println("RowId:" + qcd.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowid().stringValue());
            if (qcd.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowOperation().name().equals("DELETE"))
                continue;
            selectedQuery = getIndexOfAlarmAndPic(qcd.getTableChangeDescription()[0].getRowChangeDescription()[0].getRowid().stringValue());
            if (selectedQuery != null) {
                System.out.println("Selected St : " + selectedQuery.stmt);
                System.out.println(selectedQuery.imgPath);
                System.out.println(selectedQuery.soundPath);
                utils.writeLog(">> " + selectedQuery.stmt + " >> img: " + selectedQuery.imgPath + " >> sound: " + selectedQuery.soundPath + " >> exe: " + selectedQuery.exePath);
                utils.showPopupWithSound(selectedQuery.imgPath, selectedQuery.soundPath, selectedQuery.exePath, utils.timer);
            }
        }
        System.out.println("---------------------------------");
        synchronized (dcn) {
            dcn.notify();
        }
    }

    StatementModel getIndexOfAlarmAndPic(String rowId) {
        StatementModel query;
        int index = 1;
        for (int i = 0; i < utils.stmtList.size(); i++) {
            try {
                query = utils.stmtList.get(i);
                index = 1;
                stmt = (OraclePreparedStatement) DBChangeNotification.conn.prepareStatement(query.stmt + " AND ROWID=?");
                for (Object val : query.params) {
                    stmt = utils.setStatementParams(stmt, index, val);
                    index++;
                }
                stmt.setROWID(index, new ROWID(rowId));
                System.out.println("Query: " + query.stmt);
                rs = stmt.executeQuery();
                System.out.println("Result: " + rs.getFetchSize());
                if (rs.next())
                    return query;
            } catch (SQLException ex) {
//                System.out.println(ex.getMessage());
                if (ex.getErrorCode() != 1410) {
                    utils.writeLog("Exception on getIndexOfAlarmAndPic:" + ex.getMessage());
                }
            } finally {
                try {
                    stmt.close();
                    if (rs != null && !rs.isClosed())
                        rs.close();
                } catch (SQLException exception) {
                    exception.printStackTrace();
                }
            }
        }
        return null;
    }
}