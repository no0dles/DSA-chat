package gui.controller;

import communication.ChatInfo;
import communication.UserInfo;
import gui.util.AlertFactory;
import org.controlsfx.control.CheckComboBox;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import gui.Main;
import gui.state.DataState;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class AddNewChatController implements IDataStateModalController {

    @FXML
    private CheckComboBox<UserInfo> memberComboBox;
    @FXML
    private TextField titleField;

    private DataState dataState;
    private Stage dialogStage;

    private boolean okClicked = false;


    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    @Override
    public void setParams(Object params) {

    }


    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            okClicked = true;
            dialogStage.close();
        }
    }


    @FXML
    private void handleCancel() {
        dialogStage.close();
    }


    private boolean isInputValid() {
        String errorMessage = "";

        try {
            ChatInfo chat = ChatInfo.New(titleField.getText());
            dataState.addChat(chat);
            List<UserInfo> invites = memberComboBox.getCheckModel().getCheckedItems();
            for (UserInfo user : invites) {
                dataState.inviteChatMember(chat, user);
            }
            System.out.println(chat.getTitle() + " wird der Chatliste hinzugefügt");

        } catch (NoSuchAlgorithmException e) {
            errorMessage += "Etwas ist schief gelaufen";
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage += "Chat konnte nicht erstellt werden";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = AlertFactory.ErrorAlert("Invalid Fields", errorMessage);
            alert.initOwner(dialogStage);
            alert.showAndWait();

            return false;
        }
    }

    @Override
    public void setState(Main mainApp, DataState state) {
        this.dataState = state;

//        memberComboBox.setConverter(new StringConverter<UserInfo>() {
//            @Override
//            public String toString(UserInfo user) {
//                return user.getUsername();
//            }
//
//            @Override
//            public UserInfo fromString(String username) {
//                try {
//                    return contactList.searchUser(username);
//                    //return client.getUserInfo(username);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    return null;
//                }
//            }
//        });
        this.memberComboBox.getItems().addAll(this.dataState.getUsers());
    }
}
