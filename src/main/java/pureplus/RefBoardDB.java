package pureplus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import javax.imageio.ImageIO;

public class RefBoardDB {
    String  dbname;

    public void init() {
        try
        (
          // create a database connection
          Connection connection = DriverManager.getConnection(dbname);
          Statement statement = connection.createStatement();
        )
        {
            statement.executeUpdate("create table if not exists imagecont (id INTEGER PRIMARY KEY, content BLOB);");
            statement.executeUpdate("create table if not exists position (id INTEGER PRIMARY KEY, x INTEGER, y INTEGER, width INTEGER, height INTEGER);");
            statement.executeUpdate("create table if not exists layer (id INTEGER PRIMARY KEY, img_id INTEGER);");
            statement.executeUpdate("create table if not exists config (id INTEGER PRIMARY KEY, view_x INTEGER, view_y INTEGER);");
        }
        catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private int insertStream(InputStream instm) {
        String SQL_INS = "INSERT INTO imagecont(content) VALUES (?)";

        try
        (
            // create a database connection
            Connection connection = DriverManager.getConnection(dbname);
            PreparedStatement pstmt = connection.prepareStatement(SQL_INS);
        )
        {
            pstmt.setBinaryStream(1, instm);

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();

            int  res_id = -1;
            while(rs.next())
            {
                // read the result set
                res_id = rs.getInt(1);
                System.out.println("id = " + res_id);
            }

            return res_id;
        } catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }

        return -1;
    }
    
    void insertFile(File path, ImagePanel imgpane) {
        try (FileInputStream  fis = new FileInputStream(path))
        {
            int  id = insertStream(fis);
            imgpane.setId(id);
            insertSetting(imgpane);
        } catch(IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    void insertImage(BufferedImage img, ImagePanel imgpane) {
        ImageStreamFactory   isf = new ImageStreamFactory();

        try {
            InputStream  in = isf.getImageImageStream(img);

            int  id = insertStream(in);
            imgpane.setId(id);
            insertSetting(imgpane);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
    }

    private void insertSetting(ImagePanel imgpane) {
        String insertQuery = "INSERT INTO position (id, x, y, width, height) VALUES (?,?,?,?,?);";

        try
        (
            // create a database connection
            Connection connection = DriverManager.getConnection(dbname);
            PreparedStatement pstmt = connection.prepareStatement(insertQuery);
        )
        {
            Rectangle  bounds = imgpane.getBounds();
            pstmt.setInt(1,imgpane.getId());
            pstmt.setInt(2,bounds.x);
            pstmt.setInt(3,bounds.y);
            pstmt.setInt(4,bounds.width);
            pstmt.setInt(5,bounds.height);

            pstmt.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public void updateSetting(ImagePanel imgpane) {
        String updateQuery = "UPDATE position SET x = ?, y = ?, width = ?, height = ? WHERE id = ?";

        if (imgpane.getId()<0 ) {
            System.err.println("Unknown ID?!");
            return;
        }

        try
        (
            // create a database connection
            Connection connection = DriverManager.getConnection(dbname);
            PreparedStatement pstmt = connection.prepareStatement(updateQuery);
        )
        {
            Rectangle  bounds = imgpane.getBounds();
            pstmt.setInt(1,bounds.x);
            pstmt.setInt(2,bounds.y);
            pstmt.setInt(3,bounds.width);
            pstmt.setInt(4,bounds.height);
            pstmt.setInt(5,imgpane.getId());

            pstmt.executeUpdate();
        } catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }
    }

    public LinkedList<ImagePanel> loadDB() {
        String selectQuery = "SELECT position.id, x, y, width, height, content FROM position "+
                             "INNER JOIN imagecont ON position.id = imagecont.id;";

        try
        (
            // create a database connection
            Connection connection = DriverManager.getConnection(dbname);
            Statement stmt = connection.createStatement();
        )
        {
            ResultSet  rs = stmt.executeQuery(selectQuery);
            LinkedList<ImagePanel>      list = new LinkedList<>();

            while(rs.next())
            {
                int  id = rs.getInt("id");
                int  x = rs.getInt("x");
                int  y = rs.getInt("y");
                int  width = rs.getInt("width");
                int  height = rs.getInt("height");
                InputStream  instm = rs.getBinaryStream("content");
                try {
                    BufferedImage   img = ImageIO.read(instm);
                    ImagePanel  imgp = new ImagePanel(img, id, x, y, width, height);

                    list.add(imgp);
                } catch (IOException ex) {
                    ex.printStackTrace(System.err);
                }
            }

            return list;
        } catch(SQLException ex) {
            ex.printStackTrace(System.err);
        }

        return null;
    }

    public RefBoardDB(String path) {
        dbname = "jdbc:sqlite:" + path;
    }
}
