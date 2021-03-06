package gui;

import communication.UserInfo;
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import communication.ChatClient;
import gui.controller.*;
import gui.state.DataState;

import java.io.*;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;

    private DataState dataState;
    private ChatClient client;

    public Main() throws Exception {
        client = new ChatClient();
        client.discoverOnInet("152.96.236.224", 4000);
        //client.discoverOnLocalhost(4000);

        dataState = new DataState(client);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("ChatApp");

        if (this.dataState.init()) {
            initRootLayout();
            showChatOverview();
            client.setOnlineStatus("Online");
        } else {
            showStartupDialog();
        }
    }

    @Override
    public void stop() throws IOException {
       client.setOnlineStatus("Offline");
    }

    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/RootLayout.fxml"));
            rootLayout = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this, this.dataState);

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showOverview(String resource) throws Exception {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        AnchorPane personOverview = loader.load();

        rootLayout.setCenter(personOverview);

        IDataStateController controller = loader.getController();
        controller.setState(this, this.dataState);
    }

    private boolean showModal(String resource, String title, Object params) throws Exception {
        // Load the fxml file and create a new stage for the popup dialog.
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource(resource));
        AnchorPane page = loader.load();

        // Create the dialog Stage.
        Stage dialogStage = new Stage();
        dialogStage.setTitle(title);
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(page);
        dialogStage.setScene(scene);

        IDataStateModalController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.setParams(params);
        controller.setState(this, this.dataState);

        // Show the dialog and wait until the user closes it
        dialogStage.showAndWait();

        return controller.isOkClicked();
    }

    @FXML
    public void showPersonOverview() throws Exception {
        showOverview("/view/PersonOverview.fxml");
    }

    @FXML
    public void showChatOverview() throws Exception {
        showOverview("/view/ChatOverview.fxml");
    }

    public boolean addNewContactDialog() throws Exception {
        return showModal("/view/AddNewContact.fxml", "Edit Person", null);
    }

    public boolean addNewChatDialog() throws Exception {
        return showModal("/view/ChatEditDialog.fxml", "New Chat", null);
    }

    public boolean showChatInvites() throws Exception {
        return showModal("/view/ChatInvites.fxml", "Chat invites", null);
    }

    public boolean addNotariatFile(UserInfo user) throws Exception{
        return showModal("/view/AddNotariatFile.fxml", "Add notariat file", user);
    }


    @FXML
    public void showStartupDialog() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/view/StartupDialog.fxml"));
            AnchorPane startupDialog = loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(startupDialog);

            IDataStateController controller = loader.getController();
            controller.setState(this, this.dataState);

            primaryStage.setScene(scene);
            primaryStage.setResizable(false);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
