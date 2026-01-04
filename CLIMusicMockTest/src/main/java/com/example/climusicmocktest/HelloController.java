package com.example.climusicmocktest;

import com.example.climusicmocktest.repository.DatabaseManager;
import com.example.climusicmocktest.domain.MusicTrack;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.stream.Collectors;

public class HelloController {

    @FXML
    private TableView<MusicTrack> trackTableView;
    @FXML
    private TableColumn<MusicTrack, Integer> idColumn;
    @FXML
    private TableColumn<MusicTrack, String> bandColumn;
    @FXML
    private TableColumn<MusicTrack, String> titleColumn;
    @FXML
    private TableColumn<MusicTrack, String> genreColumn;
    @FXML
    private TableColumn<MusicTrack, String> lengthColumn;
    @FXML
    private ComboBox<String> genreComboBox;
    @FXML
    private Button resetButton;
    @FXML
    private TextField playlistNameField;
    @FXML
    private Button generateButton;
    @FXML
    private Button showPlaylistsButton;
    @FXML
    private ComboBox<String> playlistComboBox;


    private ObservableList<MusicTrack> allTracks;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        bandColumn.setCellValueFactory(new PropertyValueFactory<>("band"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        lengthColumn.setCellValueFactory(new PropertyValueFactory<>("length"));

        loadTracks();
        populateGenreFilter();

        genreComboBox.setOnAction(event -> handleGenreFilter());
        resetButton.setOnAction(event -> resetFilter());
        generateButton.setOnAction(event -> handleGeneratePlaylist());
        showPlaylistsButton.setOnAction(event -> handleShowPlaylists());
        playlistComboBox.setOnAction(event -> handlePlaylistSelection());
    }

    private void handleShowPlaylists() {
        ObservableList<String> playlists = FXCollections.observableArrayList(DatabaseManager.getPlaylistNames());
        playlistComboBox.setItems(playlists);
    }

    private void handlePlaylistSelection() {
        String selectedPlaylist = playlistComboBox.getValue();
        if (selectedPlaylist != null) {
            ObservableList<MusicTrack> playlistTracks = FXCollections.observableArrayList(DatabaseManager.getTracksFromPlaylist(selectedPlaylist));
            trackTableView.setItems(playlistTracks);
        }
    }

    private void loadTracks() {
        DatabaseManager.initializeDatabase();
        allTracks = FXCollections.observableArrayList(DatabaseManager.getAllTracks());
        trackTableView.setItems(allTracks);
    }

    private void populateGenreFilter() {
        ObservableList<String> genres = FXCollections.observableArrayList(DatabaseManager.getUniqueGenres());
        genreComboBox.setItems(genres);
    }

    private void handleGenreFilter() {
        String selectedGenre = genreComboBox.getValue();
        if (selectedGenre != null) {
            filterTracksByGenre(selectedGenre);
        }
    }

    private void filterTracksByGenre(String genre) {
        ObservableList<MusicTrack> filteredTracks = allTracks.stream()
                .filter(track -> track.getGenre().equalsIgnoreCase(genre))
                .collect(Collectors.toCollection(FXCollections::observableArrayList));
        trackTableView.setItems(filteredTracks);
    }

    private void resetFilter() {
        genreComboBox.setValue(null);
        trackTableView.setItems(allTracks);
    }

    private void handleGeneratePlaylist() {
        String playlistName = playlistNameField.getText();
        if (playlistName == null || playlistName.trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Playlist Name Required");
            alert.setContentText("Please enter a name for the playlist.");
            alert.showAndWait();
            return;
        }

        try {
            DatabaseManager.generatePlaylist(playlistName);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Playlist Generated");
            alert.setContentText("Playlist '" + playlistName + "' has been generated successfully.");
            alert.showAndWait();
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Generating Playlist");
            alert.setContentText("An error occurred while generating the playlist: " + e.getMessage());
            alert.showAndWait();
        }
    }
}
