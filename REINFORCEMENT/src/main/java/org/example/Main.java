package org.example;

import javax.xml.crypto.Data;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {

        DataTable table = new DataTable();
        ResultTable resultTable = new ResultTable();

       /* table.addData(0.6, 0.8f,0.4f,Genres.rock);
        table.addData(0.8, 0.85f,0.7f,Genres.rock);
        table.addData(0.3, 0.82f,0.3f,Genres.rock);
        table.addData(0.3, 0.3f,0.1f,Genres.rock);
        table.addData(0.9, 0.7f,0.2f,Genres.pop_dance);
        table.addData(0.7, 0.92f,0.3f,Genres.rock);
        table.addData(0.2, 0.5f,0.1f,Genres.lounge);
        table.addData(0.1, 0.4f,0.2f,Genres.lounge);
        table.addData(0.7, 0.52f,0.25f,Genres.rock);
        table.addData(0.4, 0.62f,0.12f,Genres.rock);
        table.addData(0.3, 0.60f,0.11f,Genres.rock);
        table.addData(0.1, 0.1f,0.1f,Genres.lounge);
        table.addData(0.5, 0.80f,0.6f,Genres.rock);
        table.addData(0.8, 0.72f,0.2f,Genres.rock);
        table.addData(0.2, 0.9f,0.7f,Genres.rock);
        table.addData(0.31, 0.4f,0.2f,Genres.rap);
        table.addData(0.4, 0.5f,0.1f,Genres.rock);
        table.addData(0.25, 0.3f,0.1f,Genres.pop_dance);
        table.addData(0.4, 0.71f,0.4f,Genres.rock);
        table.addData(0.6, 0.82f,0.35f,Genres.rock);
        table.addData(0.0, 0.71f,0.8f,Genres.rap);
        table.addData(0.1, 0.75f,0.3f,Genres.rap);
        table.addData(0.5, 0.52f,0.2f,Genres.rap);
        table.addData(0.1, 0.50f,0.1f,Genres.rap);
        table.addData(0.0, 0.22f,0.1f,Genres.pop);
        table.addData(0.5, 0.32f,0.2f,Genres.rock);
        table.addData(0.2, 0.42f,0.1f,Genres.lounge);
        table.addData(0.2, 0.92f,0.5f,Genres.rock);
        table.addData(0.05, 0.62f,0.2f,Genres.pop);
        table.addData(0.3, 0.72f,0.1f,Genres.pop_dance);
*/
/*
        table.addData(1,0.8f,0.272f,Genres.metal);
        table.addData(1,0.859f,0.354f, Genres.metalcore);
        table.addData(1,0.89f,0.692f, Genres.alternative_metal);
        table.addData(1,0.987f,0.297f, Genres.alternative_metal);
        table.addData(1, 0.858f, 0.506f,Genres.ohio_hip_hop);
        table.addData(1, 0.944f, 0.248f,Genres.rap_metal);
*/
        DBConnection conn = new DBConnection();



            ArrayList<Song> foundSongs = conn.listener();
            System.out.println(foundSongs);
            if (!foundSongs.isEmpty()) {
                for (Song s : foundSongs) {
                    s.PrettyPrint();
                    table.addData(1, (float) s.getEnergy(), (float) s.getValence(), s.getGenre());
                }
                //Song s = new Song(foundSongs.get(0).getSongName(),foundSongs.get(0).getArtist(),foundSongs.get(0).getGenre(),(float)foundSongs.get(0).getValence(),(float)foundSongs.get(0).getEnergy());

                resultTable.updateData(table);
                Recommendation recommendation = new Recommendation(resultTable);
                recommendation.generateProfile(resultTable);
                recommendation.generateTopGenres();
                ArrayList<Song> recSongs = recommendation.findSongs();
                recommendation.printRecommended(recSongs);
                conn.insertIntoRecommend(recSongs);
            }

        //table.PrettyPrint();


    }
}
