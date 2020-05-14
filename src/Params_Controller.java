import DialogBoxes.ErrorBox_Controller;
import DialogBoxes.SuccessBox_Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;

import java.sql.Date;
import java.util.Calendar;

public class Params_Controller {

    @FXML
    private ImageView productKeyExpander;
    @FXML
    private BorderPane productKeyExtensionBPane;

    @FXML
    private ImageView accountExpander;
    @FXML
    private BorderPane accountExtensionBPane;

    @FXML
    private ImageView appearanceExpander;
    @FXML
    private BorderPane appearanceExtensionBPane;

    @FXML
    private Label surnameLabel;
    @FXML
    private Label firstnameLabel;
    @FXML
    private Label mobileLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private Label pKeyLabel;
    @FXML
    private Label freetrialEndLabel;
    @FXML
    private Label aboutLabel;

    @FXML
    private Button sendCodeButton;
    @FXML
    private Button saveEmailButton;
    @FXML
    private Button savePwdButton;
    @FXML
    private Button deleteAccountButton;
    @FXML
    private Button activateButton;

    @FXML
    private TextField newEmailTField;
    @FXML
    private TextField productKeyTField;
    @FXML
    private PasswordField codePField;
    @FXML
    private PasswordField ancientPwdPField;
    @FXML
    private PasswordField newPwdPField;
    @FXML
    private PasswordField deleteAccountPField;

    @FXML
    private Pane activatedPane;
    @FXML
    private Pane notActivatedPane;
    @FXML
    private Pane enterKeyPane;
    @FXML
    private Pane freetrialPane;

    private ArrayList<ImageView> expanders = new ArrayList<>();
    private ArrayList<BorderPane> extensions = new ArrayList<>();
    private ArrayList<Boolean> expandersState = new ArrayList<>(); // false -> not expanded| true -> expanded


    // non FXML attributes

    private String genratedCode;
    private String providedEmail;

    @FXML
    public void initialize() {

        syncUserInfos();

        expanders.add(productKeyExpander);
        extensions.add(productKeyExtensionBPane);

        expanders.add(accountExpander);
        extensions.add(accountExtensionBPane);

        expanders.add(appearanceExpander);
        extensions.add(appearanceExtensionBPane);

        expandersState.add(false);
        expandersState.add(false);
        expandersState.add(false);

        for (ImageView img : expanders) {
            img.setOnMouseClicked(mouseEvent -> {
                int expanderID = expanders.indexOf(img);
                boolean isExpanded = getState(img);
                String newImgPath = "img\\" + (isExpanded ? "expand.png" : "unexpand.png");
                Image image = null;
                try {
                    image = new Image(new FileInputStream(Settings.projectPath + newImgPath));
                } catch (FileNotFoundException e) {
                    Debug.debugException(e);
                }
                img.setImage(image);
                BorderPane ext = getExtension(img);
                Design.setVisible(ext, !isExpanded);
                expandersState.set(expanderID, !isExpanded); // toggle state
            });
        }

        sendCodeButton.setOnMouseClicked(mouseEvent -> {
            if (Settings.ACTIVE_EMAIL_CONFIRM) {
                String email = newEmailTField.getText();
                if (email.equals(LoggedIn_Controller.getUser().getEmail()))
                    DialogLauncher.launchDialog("emailSameError", DialogLauncher.ERROR_BOX);
                else {
                    Boolean isValid = true;
                    if (isValid) {
                        providedEmail = email;
                        this.genratedCode = EmailConfirm_Controller.generate6Digits();
                        MailSender ms = new MailSender(Settings.CodeWinEmail, Settings.CodeWinEmailPwd);
                        boolean sentState = ms.sendConfirmationMail(email, genratedCode);
                        if (!sentState)
                            Checker.showConnexionError();
                    } else
                        DialogLauncher.launchDialog("emailInvalidError", DialogLauncher.ERROR_BOX);

                }

            }
        });

        saveEmailButton.setOnMouseClicked(mouseEvent -> {
            if (providedEmail == null || genratedCode == null) {

            } else {
                String enteredCode = codePField.getText();

                if (enteredCode.equals(genratedCode)) {
                    User u = LoggedIn_Controller.getUser();
                    boolean success = true;
                    if (Settings.ACTIVE_DB_MODE)
                        success = User.updateEmail(u, providedEmail);
                    if (success) {
                        DialogLauncher.launchDialog("emailUpdateSuccess", DialogLauncher.SUCCESS_BOX);
                        LoggedIn_Controller.getUser().setEmail(providedEmail);
                        emailLabel.setText(providedEmail); // empty
                    } else
                        Checker.showConnexionError();
                } else
                    DialogLauncher.launchDialog("codeInvalidError", DialogLauncher.ERROR_BOX);

                // empty
                newEmailTField.setText("");
                codePField.setText("");
            }

            providedEmail = null;
            genratedCode = null;
        });

        savePwdButton.setOnMouseClicked(mouseEvent -> {
            String ancientProvided = ancientPwdPField.getText();
            String newPwd = newPwdPField.getText();
            User u = LoggedIn_Controller.getUser();

            if (newPwd.equals(u.getPassword()))
                DialogLauncher.launchDialog("pwdSameError", DialogLauncher.ERROR_BOX);
            else if (ancientProvided.equals(u.getPassword())) {
                boolean state = User.updatePassword(u, newPwd);
                if (state)
                    DialogLauncher.launchDialog("pwdUpdateSuccess", DialogLauncher.SUCCESS_BOX);
                else
                    Checker.showConnexionError();
            } else
                Checker.showPwdError();

            ancientPwdPField.setText("");
            newPwdPField.setText("");
        });

        deleteAccountButton.setOnMouseClicked(mouseEvent -> {
            User u = LoggedIn_Controller.getUser();
            String providedPwd = deleteAccountPField.getText();
            if (providedPwd.equals(u.getPassword())) {
                boolean state = User.deleteUser(u);
                if (state) {
                    SceneLoader.loadAuthenticator();
                } else
                    DialogLauncher.launchDialog("accountDelError", DialogLauncher.ERROR_BOX);

            } else
                Checker.showPwdError();
        });

        aboutLabel.setOnMouseClicked(mouseEvent -> {
            Stage aboutStage = new Stage(StageStyle.UNDECORATED);
            aboutStage.initOwner(Settings.appStage);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("AboutUs.fxml"));
            Parent root = null;
            try {
                root = loader.load();
                LanguageManager.resyncLanguage(loader, "about");
            } catch (IOException e) {
                Debug.debugException(e);
            }
            AboutUs_Controller ctr = loader.getController();
            ctr.setStage(aboutStage);
            aboutStage.setScene(new Scene(root));
            aboutStage.centerOnScreen();
            aboutStage.show();
        });

        Design.setVisible(productKeyExtensionBPane, false);
        Design.setVisible(accountExtensionBPane, false);
        Design.setVisible(appearanceExtensionBPane, false);

        User u = LoggedIn_Controller.getUser();
        boolean isAccountActivate = u.isAccountActivated();

        Design.setVisible(activatedPane, isAccountActivate);
        Design.setVisible(notActivatedPane, !isAccountActivate);
        Design.setVisible(enterKeyPane, !isAccountActivate);

        pKeyLabel.setText(User.getFormattedPKey(u.getPkey()));

        Design.setVisible(freetrialPane, !u.isFreetrialEnded());

        String validPKey = User.generatePKey(u.getUsername());
        productKeyTField.setText(User.getFormattedPKey(validPKey));

        Date ftEndDate = u.getFreetrialEndDate();
        freetrialEndLabel.setText(ftEndDate.toLocalDate().getDayOfMonth() + "/" + ftEndDate.toLocalDate().getMonthValue() + "/" + ftEndDate.toLocalDate().getYear());

        activateButton.setOnMouseClicked(mouseEvent -> {
            String userPKey = User.getFormattedPKey(User.generatePKey(u.getUsername()));
            String enteredPKey = productKeyTField.getText();

            if (enteredPKey.equals(userPKey) || enteredPKey.equals(validPKey)) {
                boolean state = User.updatePKey(u, validPKey);
                if (state) {
                    Design.setVisible(notActivatedPane, false);
                    Design.setVisible(enterKeyPane, false);
                    DialogLauncher.launchDialog("productActivatedSuccess", DialogLauncher.SUCCESS_BOX);
                } else
                    Checker.showConnexionError();
            } else
                DialogLauncher.launchDialog("pkeyError", DialogLauncher.ERROR_BOX);
        });
    }

    private boolean getState(ImageView img) {
        int i = expanders.indexOf(img);
        return expandersState.get(i);
    }

    private BorderPane getExtension(ImageView img) {
        int i = expanders.indexOf(img);
        return extensions.get(i);
    }

    private void syncUserInfos() {
        User u = LoggedIn_Controller.getUser();

        firstnameLabel.setText(u.getFirstname());
        surnameLabel.setText(u.getLastname());
        mobileLabel.setText(u.getMobile());
        emailLabel.setText(u.getEmail());
    }

}
