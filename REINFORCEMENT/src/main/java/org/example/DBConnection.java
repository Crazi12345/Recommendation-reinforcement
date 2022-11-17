package org.example;

import org.postgresql.shaded.com.ongres.scram.common.bouncycastle.pbkdf2.Strings;
import java.util.*;
import java.io.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection {

    public Connection connection;
    public String connectionString;
    public String user;
    public String password;
    public String port;
    public ArrayList<String> genresList = new ArrayList<>();
    ResultSet resultSet;

    public DBConnection() {
        connection = null;

        try (InputStream input = new FileInputStream("src/main/java/org/example/config.properties")) {
            Properties p = new Properties();
            p.load(input);
            connectionString = p.getProperty("db.url");
            user = p.getProperty("db.user");
            password = p.getProperty("db.password");
            port = p.getProperty("db.port");
        } catch (FileNotFoundException e) {
            System.out.println("FILE NOT FOUND");
        } catch (IOException e) {
            System.out.println(e);
        }

        this.connect();


    }

    private void connect() {
        Connection c = null;
        try {
            //Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection(connectionString, user, password);
            System.out.println("Connected to Database");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        connection = c;
    }

    public Connection getConnection() {
        return connection;
    }

    public ResultSet getAllResults() {
        ResultSet rs = null;
        try {
            Statement stmt = connection.createStatement();
            resultSet = stmt.executeQuery("select uri, track_name ,artist_individual, energy,valence, artist_genre from final group by uri, track_name, artist_individual, energy, valence, artist_genre;");
            //resultSet.next();
            //System.out.println(resultSet.getString("track_name"));
        } catch (SQLException e) {
            System.out.println(e);
        }
        return resultSet;
    }

    public boolean Contains(String s) {
        for (int i = 0; i < genresList.size(); i++) {
            if (genresList.get(i) == s) {
                return true;
            }

        }
        return false;
    }

    public void insertIntoRecommend(ArrayList<Song> songs){
        try {
            Collections.shuffle(songs);
            ArrayList<String> songNames = new ArrayList<>();
            ArrayList<String> artistNames = new ArrayList<>();
            for (int i = 0; i< songs.size();i++){
                songNames.add(songs.get(i).getSongName());
                artistNames.add(songs.get(i).getArtist());
            }
            String[] strings1 = songNames.toArray(new String[songNames.size()]);
            Array songArr = connection.createArrayOf("VARCHAR",strings1);
            String[] strings2 = artistNames.toArray(new String[artistNames.size()]);
            Array artistArr = connection.createArrayOf("VARCHAR",strings2);
            PreparedStatement stmt = connection.prepareStatement("insert into suggested_songs(songs,artists,model,updated_at,created_at) values(?,?,?,now(),now());");
            stmt.setArray(1,songArr );
            stmt.setArray(2,artistArr);
            stmt.setString(3,songs.get(0).getModel() );
            stmt.execute();

            //System.out.println(resultSet.getString("track_name"));
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public ArrayList<Song> listener() {

        //int id = -1;
        boolean runs = false;
        String[] eachSong;
        int songID;
        ArrayList<Song> songs = new ArrayList<>();
        Statement queryAddedSongs;
        Statement queryFinal;

        while (true) {
            try {
                queryAddedSongs = connection.createStatement();
                ResultSet rsAddedSongs = queryAddedSongs.executeQuery("SELECT * FROM added_songs");
                while (rsAddedSongs.next()) {
                    //id = rsAddedSongs.getInt("id");
                    eachSong = rsAddedSongs.getString("songs").split(";");
                    for (String song : eachSong) {
                        songID = Integer.parseInt(song.substring(song.indexOf('_') + 1));
                        queryFinal = connection.createStatement();
                        String query = "select track_name ,artist_individual, energy,valence, artist_genre from final where id='" + songID + "' group by id, uri, track_name, artist_individual, energy, valence, artist_genre";
                        ResultSet rs = queryFinal.executeQuery(query);
                        while (rs.next()) {
                            songs.add(new Song(rs.getString("track_name"), rs.getString("artist_individual"), rs.getString("artist_genre"), rs.getDouble("valence"), rs.getDouble("energy")));
                        }
                    }
                    runs = true;
                }
                if (runs) break;
            } catch (Exception e) {
                System.out.println(e);
            }
        }

//        if (id != -1) {
//            try {
//                id = -1;
//                queryAddedSongs.executeQuery("DELETE FROM added_songs ");
//            } catch (SQLException e) {
//                throw new RuntimeException(e);
//            }
//        }
        return songs;
    }

}





