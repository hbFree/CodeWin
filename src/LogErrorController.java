import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.control.*;

import java.awt.*;
import java.awt.event.InputEvent;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;


public class LogErrorController implements Initializable {
    @FXML
    Pane main ;
    @FXML
    VBox msgbox ;

    public static ArrayList<Auth_Controller.Msg> list = new ArrayList<>() ;

    public  void updMsg(){
        for (Auth_Controller.Msg m :list) {
            String valid = m.b?"valide":"invalid";
            Label t = new Label("\t"+m.msg);
            Image image = new Image(getClass().getResourceAsStream("img/"+valid+".jpg"));
            ImageView img = new ImageView(image);
            img.setFitHeight(25);
            img.setFitWidth(25);
            t.setGraphic(img);
            msgbox.getChildren().add(t);

             /*
            Robot robot = null;

            //region find another way
            try {
                robot = new Robot();
            } catch (AWTException e) {
                e.printStackTrace();
            }
            Point2D p =  Auth_Controller.ctrl.localToScreen(0,0);
            robot.mouseMove((int)p.getX(),(int)p.getY());
            robot.mousePress(InputEvent.BUTTON1_MASK);
            robot.mouseRelease(InputEvent.BUTTON1_MASK);
            //endregion
   */
             Main.appSettings.getAppStage().requestFocus();

        }

        }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        updMsg();

        main.setStyle( "-fx-background-size: 1200 900;"+
                "-fx-background-radius: 18 18 18 18;"+
                "-fx-border-radius: 18 18 18 18;"+
                "-fx-background-color: #12F423;");


    }

}