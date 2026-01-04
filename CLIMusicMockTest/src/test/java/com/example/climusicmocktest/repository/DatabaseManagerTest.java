package com.example.climusicmocktest.repository;

import com.example.climusicmocktest.domain.MusicTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DatabaseManagerTest {

    @BeforeEach
    public void setUp() {
        DatabaseManager.initializeDatabase();
    }

    @Test
    public void testGeneratePlaylist_Success() {
        try {
            String playlistName = "test_playlist_success";
            DatabaseManager.generatePlaylist(playlistName);
            List<MusicTrack> playlist = DatabaseManager.getTracksFromPlaylist(playlistName);

            assertNotNull(playlist);
            assertTrue(playlist.size() >= 3);

            long totalLength = 0;
            for (MusicTrack track : playlist) {
                totalLength += parseLengthToSeconds(track.getLength());
            }
            assertTrue(totalLength > 900);

            for (int i = 0; i < playlist.size() - 1; i++) {
                MusicTrack currentTrack = playlist.get(i);
                MusicTrack nextTrack = playlist.get(i + 1);
                assertNotEquals(currentTrack.getBand(), nextTrack.getBand());
                assertNotEquals(currentTrack.getGenre(), nextTrack.getGenre());
            }

        } catch (SQLException e) {
            fail("Should not have thrown an exception: " + e.getMessage());
        }
    }

    @Test
    public void testGeneratePlaylist_Impossible() {
        // Overwrite the database with a very limited set of tracks
        try (var conn = java.sql.DriverManager.getConnection("jdbc:h2:mem:musicdb;DB_CLOSE_DELAY=-1", "sa", "");
             var stmt = conn.createStatement()) {
            stmt.execute("DELETE FROM tracks");
            stmt.execute("INSERT INTO tracks VALUES (1, 'Band1', 'Track1', 'Genre1', '5:00')");
        } catch (SQLException e) {
            fail("Failed to set up impossible scenario: " + e.getMessage());
        }

        assertThrows(SQLException.class, () -> {
            DatabaseManager.generatePlaylist("test_playlist_impossible");
        });
    }

    private int parseLengthToSeconds(String length) {
        String[] parts = length.split(":");
        return Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
    }
}
