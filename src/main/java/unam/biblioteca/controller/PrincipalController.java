
package unam.biblioteca.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;


public class PrincipalController {
    
     @FXML
    private Button button;

    @FXML
    private void handleButtonClick() {
        System.out.println("Button clicked!");
    }
    
}
