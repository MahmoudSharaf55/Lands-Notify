package utils;

import models.ConnectionModel;
import models.StatementModel;
import oracle.jdbc.OraclePreparedStatement;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * @author Ma7MOoOD SHaRaF
 */

public class FileUtils {
    public Popup popup;
    public ConnectionModel con;
    public ArrayList<StatementModel> stmtList = new ArrayList<>();
    public int timer;
    public boolean withLog = false;
    PrintWriter writer;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

    public ConnectionModel getConnectionInfo(JSONObject obj) {
        return new ConnectionModel((String) obj.get("ip"), (String) obj.get("sid"), (String) obj.get("username"), (String) obj.get("password"));
    }

    public ArrayList<StatementModel> getStatements(JSONArray arr) {
        try {
            ArrayList<StatementModel> list = new ArrayList<>();
            for (JSONObject obj : (Iterable<JSONObject>) arr) {
                list.add(new StatementModel((String) obj.get("stmt"), (String) obj.get("imgPath"), (String) obj.get("soundPath"), (String) obj.get("exePath"), (JSONArray) obj.get("params")));
            }
            return list;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Fail to read config from file,\n" + e.getMessage());
        }
        return null;
    }

    public void readConfigFromJSON() {
        try {
            JSONParser parser = new JSONParser();
            JSONObject obj = (JSONObject) parser.parse(new FileReader("notify_config.json"));
            con = getConnectionInfo((JSONObject) obj.get("connection"));
            stmtList = getStatements((JSONArray) obj.get("statements"));
            timer = Integer.parseInt(String.valueOf(obj.get("timer")));
            withLog = (boolean) obj.get("log");
        } catch (IOException | ParseException e) {
            JOptionPane.showMessageDialog(null, "Fail to read config from file,\n" + e.getMessage());
        }
    }

    public OraclePreparedStatement setStatementParams(OraclePreparedStatement stmt, int index, Object obj) {
        System.out.println("> " + index + " : " + obj);
        try {
            if (obj instanceof String) {
                stmt.setString(index, (String) obj);
            } else if (obj instanceof Long) {
                stmt.setLong(index, (Long) obj);
            } else if (obj instanceof Double) {
                stmt.setDouble(index, (Double) obj);

            } else if (obj instanceof Boolean) {
                stmt.setBoolean(index, (Boolean) obj);
            } else if (obj == null) {
                stmt.setNull(index, java.sql.Types.NULL);
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            writeLog("Exception on setStatementParams: " + ex.getMessage());
        }
        return stmt;
    }

    public void showPopupWithSound(String imgPath, String soundPath, String exePath, int timer) {
        try {
            popup = new Popup(soundPath, imgPath, this);
            popup.show();
            if (exePath != null && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
                Desktop.getDesktop().open(new File(exePath));
            }
            Thread.sleep(timer);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            writeLog("Exception on showPopupWithSound: " + e.getMessage());
        } finally {
            popup.close();
        }
    }

    public void writeLog(String text) {
        if (withLog) {
            try {
                writer = new PrintWriter(new FileOutputStream(new File("log.txt"), true));
                writer.println("------------------- " + dtf.format(LocalDateTime.now()) + " -------------------");
                writer.println(text);
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }
}
