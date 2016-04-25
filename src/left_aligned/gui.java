/* File Name : gui.java */

package left_aligned;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;


public class gui extends Application {
	final FileChooser fileChooser = new FileChooser(); 
    private LeftAligned logic;
    private Stage stage;
    
    private static TextField inputFileField;
    private static TextField outputFileField;
    private static Button cnvrtBtn;
    private static boolean isProcessing = false;
   
    @Override  
    public void start(final Stage stage) {
    	logic = new LeftAligned();	
      
        stage.setTitle("LeftAligned");   
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        
        Label description = new Label();
        HBox descHb = new HBox();
        description.setText("Convert Word (.docx) file to Final Draft (.fdx)");
        description.setStyle("-fx-font-weight: bold;");
        descHb.getChildren().addAll(description);
              
        final Label inputFileLabel = new Label("Source file:");
        inputFileField = new TextField();
        inputFileField.setPrefColumnCount(10);
        inputFileField.setPromptText("Select source file");
        final Button fileSelectBtn = new Button("Open"); 
        fileSelectBtn.setMaxWidth(Double.MAX_VALUE);
             
        final Label outputFileLabel = new Label("Output file:");
        outputFileField = new TextField();
        outputFileField.setPrefColumnCount(10);
        outputFileField.setPromptText("Enter output file name");
        final Button saveAsBtn = new Button("Save As"); 
        saveAsBtn.setMaxWidth(Double.MAX_VALUE);
       
        cnvrtBtn = new Button("Convert");
        cnvrtBtn.setMaxWidth(Double.MAX_VALUE);
        cnvrtBtn.setDisable(true);
        
        grid.add(inputFileLabel, 0, 0);
        grid.add(inputFileField, 1, 0);
        grid.add(fileSelectBtn, 2, 0);
        
        grid.add(outputFileLabel, 0, 1);
        grid.add(outputFileField, 1, 1);
        grid.add(saveAsBtn, 2, 1);
        
        grid.add(cnvrtBtn, 2, 2);

        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(description, grid);
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
 
        stage.setScene(new Scene(rootGroup));
        stage.show();
        setPresetOutputFile();
        
        inputFileField.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	tryCnvrtBtnEnable();
            }
        });    
        
        fileSelectBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	doFileOpenDialog();
            	tryCnvrtBtnEnable();
            }
        });    
        
        saveAsBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	 doFileSaveAs();
            }
        });
        
        cnvrtBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	doConvertToFDX();
            }
        });
              
        initUserPresets();
    }
    
    private void initUserPresets() {
        
    }
    
    private boolean doConvertToFDX() {
    	setIsProcessing(true);
    	cnvrtBtnDisable();
    	
        if (!logic.fileExists(inputFileField.getText())) {
            doFileOpenDialog();
            // Give use once chance to select file. If this 
            // fails, we alert and return false to prevent loop.
            boolean retVal = doConvertToFDX();
            if (!retVal) alert(GlobalConstants.ASK_VALID_WORD_FILE);
            return retVal;
        }
        
        File inputFile = new File(inputFileField.getText());
        File outputFile = new File(outputFileField.getText());
        
        if (!logic.isValidFile(inputFile, "docx")) {
        	alert(GlobalConstants.INVALID_FILE_ERR, AlertType.ERROR);
        }
        
        if (logic.fileExists(outputFile) && !overwriteConfirm()) {
                outputFile = null;
                doFileSaveAs();
        }
        
        try {
        	if (logic.makeFDX(inputFile) && logic.writeFDX(outputFile)) {
        		alert("Sucess!");	 
        		setIsProcessing(false);
        		tryCnvrtBtnEnable();
        		
        		return true;
        	}  
        } catch (Exception exp) {
        	exp.printStackTrace();
        }
        
        // If we got here something didn't work out.
	    alert("An error occured!", AlertType.ERROR);
	    setIsProcessing(false);
	    return false;
    }
    
    private void doFileOpenDialog() {
    	fileChooser.getExtensionFilters().retainAll(fileChooser.getExtensionFilters());
    	fileChooser.getExtensionFilters()
    		.add(new ExtensionFilter(GlobalConstants.WORD_FILE_NAME, 	
    									GlobalConstants.WORD_FILE_EXT));
    	
    	fileChooser.getExtensionFilters()
    		.add(new ExtensionFilter(GlobalConstants.ALL_FILES_NAME, 
    									GlobalConstants.ALL_FILES_EXT));  	
    	
    	if (inputFileField.getText().length() > 0) {
        	try {
        		fileChooser.setInitialDirectory(new File(inputFileField.getText()));
        	} catch (Exception exp) {
                exp.printStackTrace();
            }      
        }
        
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
        	inputFileField.setText(file.getPath());
            setPresetOutputFile();
        }
    }
    
    private void doFileSaveAs() {
    	fileChooser.getExtensionFilters().retainAll(fileChooser.getExtensionFilters());
    	fileChooser.getExtensionFilters()
    		.add(new ExtensionFilter(GlobalConstants.FINAL_DRAFT_NAME, 
    									GlobalConstants.FINAL_DRAFT_EXT));
    	
    	fileChooser.getExtensionFilters()
    		.add(new ExtensionFilter(GlobalConstants.ALL_FILES_NAME, 
    									GlobalConstants.ALL_FILES_EXT));
        
    	if (outputFileField.getText().length() > 0) {
    		if (outputFileField.getText().length() != 0) {
    			fileChooser.setInitialFileName(outputFileField.getText());
    		}
    	}
    	
    	File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
        	outputFileField.setText(file.getPath());
            setPresetOutputFile();
        }
    }
    
    private boolean overwriteConfirm() {
    	Alert alert = new Alert(AlertType.CONFIRMATION, 
    							GlobalConstants.OVERWRITE_ASK_OK);
    	
    	Optional<ButtonType> result = alert.showAndWait();
    	 if (result.isPresent() && result.get() == ButtonType.OK) {
    		 return true;
    	 }
         
         return false;
    }
    
    private void alert(String msg, AlertType aType) {
    	Alert alert = new Alert(aType, msg);
    	alert.showAndWait();
    }
    
    private void alert(String msg) {
    	alert(msg, AlertType.INFORMATION);
    }
    
    private void setPresetOutputFile() {
    	String inputFilePath = inputFileField.getText().trim();
    	if (inputFilePath.length() == 0) return;
    	
    	outputFileField.setText(logic
				.newFileExension(inputFilePath, "fdx"));
    }
    
    private void cnvrtBtnDisable() {
    	cnvrtBtn.setDisable(true);
    }
     
    private void tryCnvrtBtnEnable() {
    	if (isProcessing()) return;
    	if (inputFileField.getText().trim().length() == 0) return; 
    	if (outputFileField.getText().trim().length() == 0) return; 
    	
    	cnvrtBtn.setDisable(false);
    }
    
    public void setIsProcessing(boolean bool) {
    	isProcessing = bool;
    }
    
    public boolean isProcessing() {
    	return isProcessing;
    }
    
    public static void main(String[] args) {
    	Application.launch(args);  
    }
    
}