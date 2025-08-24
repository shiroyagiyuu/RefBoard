package pureplus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.util.ArrayList;

public class SystemConfig {
    ArrayList<String>   recent;

    public String getDBName() {
        return "jdbc:sqlite:RefBoardConfig.db";
    }

    public void init() {
        try
        (
          // create a database connection
          Connection connection = DriverManager.getConnection(getDBName());
          Statement statement = connection.createStatement();
        )
        {
            statement.executeUpdate("create table if not exists recent (id INTEGER PRIMARY KEY, dbpath STRING);");
        }
        catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void addRecent(String recent) {
        String SQL_DEL = "DELETE FROM recent WHERE dbpath = (?);";
        String SQL_INS = "INSERT INTO recent(dbpath) VALUES (?);";

        try
        (
            // create a database connection
            Connection connection = DriverManager.getConnection(getDBName());
        )
        {
            PreparedStatement   pstmt = connection.prepareStatement(SQL_DEL);
            pstmt.setString(1, recent);
            pstmt.executeUpdate();
            
            pstmt = connection.prepareStatement(SQL_INS);
            pstmt.setString(1, recent);
            pstmt.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public ArrayList<String> loadRecent() {
        String selectQuery = "SELECT id, dbpath FROM recent";
        ArrayList<String>   list = new ArrayList<>();

        try
        (
            // create a database connection
            Connection connection = DriverManager.getConnection(getDBName());
            Statement stmt = connection.createStatement();
        )
        {
            ResultSet  rs = stmt.executeQuery(selectQuery);
            
            while(rs.next())
            {
                String  fname = rs.getString(2);
                list.add(fname);
                System.out.println("dbpath="+fname);
            }

            this.recent = list;
            return list;
        } catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }

        return list;
    }

    public String getLastDB() {
        if (recent.size()>0) {
            return recent.get(recent.size()-1);
        } else {
            return "refboarddata.db";
        }
    }

    public SystemConfig() {
        init();
    }
}
