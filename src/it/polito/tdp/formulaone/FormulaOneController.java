package it.polito.tdp.formulaone;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import it.polito.tdp.formulaone.model.Driver;
import it.polito.tdp.formulaone.model.Model;
import it.polito.tdp.formulaone.model.Season;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class FormulaOneController {
	
	Model model;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ComboBox<Season> boxAnno;

    @FXML
    private TextField textInputK;

    @FXML
    private TextArea txtResult;

    @FXML
    void doCreaGrafo(ActionEvent event) {
    	
    	Season s = boxAnno.getValue();
    	if( s == null){
    		txtResult.appendText("Errore: selezionare una stagione\n");
    		return;
    	}
    	
    	model.creaGrafo(s);
    	Driver best = model.getbestDriver();
    	txtResult.appendText("Il pilota migliore della stagione " + s.getYear() + " e` " + best.toString() + "\n");
    
    }

    @FXML
    void doTrovaDreamTeam(ActionEvent event) {
    	
    	txtResult.clear();
    	String ks = textInputK.getText();
    	int k =0;
    	try{
    		k = Integer.parseInt(ks);
    	} catch (NumberFormatException s){
    		txtResult.appendText("Errore: devi inserire un numero\n");
    		return;
    	}
    	List <Driver> dreamTeam = model.getDreamTeam(k);
    	txtResult.appendText("Dream team:\n");
    	for( Driver d : dreamTeam){
    		txtResult.appendText(d + "\n");
    	}

    }

    @FXML
    void initialize() {
        assert boxAnno != null : "fx:id=\"boxAnno\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert textInputK != null : "fx:id=\"textInputK\" was not injected: check your FXML file 'FormulaOne.fxml'.";
        assert txtResult != null : "fx:id=\"txtResult\" was not injected: check your FXML file 'FormulaOne.fxml'.";

    }
    
    public void setModel(Model model){
    	this.model = model;
    	
    	boxAnno.getItems().addAll(model.getSeasons());
    }
}
