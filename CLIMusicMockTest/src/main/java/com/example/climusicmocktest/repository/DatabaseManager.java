package com.example.climusicmocktest.repository;

import com.example.climusicmocktest.domain.MusicTrack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:h2:mem:musicdb;DB_CLOSE_DELAY=-1";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS tracks (" +
                    "id INT PRIMARY KEY, " +
                    "band VARCHAR(255), " +
                    "title VARCHAR(255), " +
                    "genre VARCHAR(255), " +
                    "length VARCHAR(10))";
            stmt.execute(createTableSql);

            stmt.execute("DELETE FROM tracks");
            stmt.execute("INSERT INTO tracks VALUES (1, 'Queen', 'Bohemian Rhapsody', 'Rock', '5:55')");
            stmt.execute("INSERT INTO tracks VALUES (2, 'Led Zeppelin', 'Stairway to Heaven', 'Rock', '8:02')");
            stmt.execute("INSERT INTO tracks VALUES (3, 'The Beatles', 'Hey Jude', 'Rock', '7:11')");
            stmt.execute("INSERT INTO tracks VALUES (4, 'Nirvana', 'Smells Like Teen Spirit', 'Grunge', '5:01')");
            stmt.execute("INSERT INTO tracks VALUES (5, 'ABBA', 'Dancing Queen', 'Pop', '3:51')");
            stmt.execute("INSERT INTO tracks VALUES (6, 'Queen', 'Don''t Stop Me Now', 'Rock', '3:29')");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<MusicTrack> getAllTracks() {
        List<MusicTrack> tracks = new ArrayList<>();
        String sql = "SELECT * FROM tracks ORDER BY band, title";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tracks.add(new MusicTrack(
                        rs.getInt("id"),
                        rs.getString("band"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("length")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tracks;
    }

    public static List<String> getUniqueGenres() {
        List<String> genres = new ArrayList<>();
        String sql = "SELECT DISTINCT genre FROM tracks";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return genres;
    }

    public static void generatePlaylist(String playlistName) throws SQLException {
        List<MusicTrack> allTracks = getAllTracks();
        List<MusicTrack> playlist = new ArrayList<>();
        long totalLengthInSeconds = 0;
        MusicTrack lastTrack = null;
        int maxAttempts = 1000;
        int attempts = 0;

        while ((playlist.size() < 3 || totalLengthInSeconds < 900) && attempts < maxAttempts) {
            MusicTrack candidate = allTracks.get((int) (Math.random() * allTracks.size()));
            if (lastTrack == null || (!candidate.getBand().equals(lastTrack.getBand()) || !candidate.getGenre().equals(lastTrack.getGenre()))) {
                playlist.add(candidate);
                totalLengthInSeconds += parseLengthToSeconds(candidate.getLength());
                lastTrack = candidate;
            }
            attempts++;
        }

        if (playlist.size() < 3 || totalLengthInSeconds < 900) {
            throw new SQLException("Could not generate a valid playlist with the given constraints.");
        }

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createTableSql = "CREATE TABLE IF NOT EXISTS \"" + playlistName + "\" (" +
                    "id INT PRIMARY KEY, " +
                    "band VARCHAR(255), " +
                    "title VARCHAR(255), " +
                    "genre VARCHAR(255), " +
                    "length VARCHAR(10))";
            stmt.execute(createTableSql);

            stmt.execute("DELETE FROM \"" + playlistName + "\"");

            for (MusicTrack track : playlist) {
                String insertSql = "INSERT INTO \"" + playlistName + "\" VALUES (" +
                        track.getId() + ", '" +
                        track.getBand().replace("'", "''") + "', '" +
                        track.getTitle().replace("'", "''") + "', '" +
                        track.getGenre().replace("'", "''") + "', '" +
                        track.getLength() + "')";
                stmt.execute(insertSql);
            }
        }
    }

    private static int parseLengthToSeconds(String length) {
        String[] parts = length.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return minutes * 60 + seconds;
    }

    public static List<String> getPlaylistNames() {
        List<String> playlistNames = new ArrayList<>();
        String sql = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = 'PUBLIC' AND TABLE_NAME != 'TRACKS'";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                playlistNames.add(rs.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return playlistNames;
    }

    public static List<MusicTrack> getTracksFromPlaylist(String playlistName) {
        List<MusicTrack> tracks = new ArrayList<>();
        String sql = "SELECT * FROM \"" + playlistName + "\"";

        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                tracks.add(new MusicTrack(
                        rs.getInt("id"),
                        rs.getString("band"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("length")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tracks;
    }
}

