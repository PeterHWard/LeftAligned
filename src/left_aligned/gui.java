/* File Name : gui.java */

package left_aligned;

import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Optional;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;


public class gui extends Application {
	final FileChooser fileChooser = new FileChooser(); 
    private LeftAligned logic;
    private Stage stage;
    
    private FileType inputFileType;
    private FileType outputFileType;
    
    private static TextField inputFileField;
    private static TextField outputFileField;
    private static Button cnvrtBtn;
    private static boolean isProcessing = false;
    
    private int _col = 0;
    private int _row = 0;
   
    @Override  
    public void start(final Stage stage) {
    	logic = new LeftAligned();	
      
        stage.setTitle("LeftAligned " + GlobalConstants.version);   
        stage.setResizable(false);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        
        addFileNameFields(grid);
        addConvertBtn(grid);
        
        final Pane rootGroup = new VBox(12);
        rootGroup.getChildren().addAll(makeDescription()
        								, makeModeSelect()
        								, grid);
        
        rootGroup.setPadding(new Insets(12, 12, 12, 12));
 
        stage.setScene(new Scene(rootGroup));
        stage.show();
        setPresetOutputFile();
           
        cnvrtBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) {
            	doConvert();
            }
        });
              
        initUserPresets();
    }
    
    private VBox makeDescription() {
    	 Label header = new Label("Write in Word. Deliver in Final Draft");
    	 header.setStyle("-fx-font-size:18;-fx-font-weight: bold;");
    	 
    	 Label description = new Label("Supports Word 2007 (.docx) and to Final Draft 8 (.fdx) formats.");
    	 //description.setStyle("-fx-font-style: italic;");
    	 
    	 VBox descBox = new VBox();
    	 descBox.getChildren().addAll(header, description);
         
         return descBox;
    }
    
    private HBox makeModeSelect() {
    	final HBox pane = new HBox();
    	final VBox leftCol = new VBox();
    	final VBox rightCol = new VBox();
    	final ToggleGroup leftGroup = new ToggleGroup();
    	final ToggleGroup rightGroup = new ToggleGroup();
    	leftGroup.setUserData("I");
    	rightGroup.setUserData("O");
    	
    	String[] names = {
    			 "Word (.docx)"
    			,"Final Draft (.fdx)"
    	};
    	
    	Dictionary<String, FileType> fileTypesDict = new Hashtable<String, FileType>();
    	fileTypesDict.put("Word (.docx)", FileType.DOCX);
    	fileTypesDict.put("Final Draft (.fdx)", FileType.FDX);
    	
    	Label l_lbl = new Label("Input type:");
		Label r_lbl = new Label("Output type:");
		leftCol.getChildren().add(l_lbl);
		rightCol.getChildren().add(r_lbl);
				
    	for (String name : names) {
    		ToggleButton l_tb = new ToggleButton(name);
    		ToggleButton r_tb = new ToggleButton(name);
    		leftCol.getChildren().add(l_tb);
        	rightCol.getChildren().add(r_tb);
        	l_tb.setToggleGroup(leftGroup); 
        	r_tb.setToggleGroup(rightGroup);
 
    		ToggleButton[] tbs = {l_tb, r_tb};
    		for (ToggleButton tb : tbs) {
    			tb.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE); 		
    			assertNotEquals(fileTypesDict.get(name), null);
        		tb.setUserData(fileTypesDict.get(name));
    		}   				
    	}
    	
    	ToggleGroup[] tgs = {leftGroup, rightGroup};
    	  
    	for (ToggleGroup theGroup : tgs) {
    		theGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
        	    public void changed(ObservableValue<? extends Toggle> ov,
        	        Toggle toggle, Toggle new_toggle) {
        	    		if (new_toggle == null) {
        	    			if (theGroup.getSelectedToggle() == null) toggle.setSelected(true);
        	    			else return;
        	    		}
        	    		
        	    		String direction = (String) theGroup.getUserData();
        	    		ToggleGroup otherGroup = (direction == "I") ? rightGroup : leftGroup;
        	    		
        	    		FileType theFType = (FileType) theGroup.getSelectedToggle().getUserData();
        	    		FileType otherFType = (otherGroup.getSelectedToggle() != null) 
        	    				? (FileType) otherGroup.getSelectedToggle().getUserData()
        	    				: null;

        	    		if (otherFType == null || otherFType == theFType) {			
        	    			for (Toggle t : otherGroup.getToggles()) {
        	    				if (t.getUserData() != theFType) {
        	    					t.setSelected(true);
        	    					otherFType = (FileType) t.getUserData();
        	    					break;
        	    				}
        	    			}
        	    		}
        	    		
        	    		setConversionMode(direction, theFType, otherFType);
        	         }
        	});
    	}  	
    
    	VBox spacerCol = new VBox();
    	spacerCol.setMinWidth(40);
    	pane.getChildren().addAll(leftCol, spacerCol, rightCol);
    	leftGroup.getToggles().get(0).setSelected(true);
    	
    	return pane;
    }
    
    private void addFileNameFields(GridPane grid) {
    	 final Label inputFileLabel = new Label("Source file:");
         inputFileField = new TextField();
         inputFileField.setPrefColumnCount(20);
         inputFileField.setPromptText("Select source file");
         final Button fileSelectBtn = new Button("Open"); 
         fileSelectBtn.setMaxWidth(Double.MAX_VALUE);
              
         final Label outputFileLabel = new Label("Output file:");
         outputFileField = new TextField();
         outputFileField.setPrefColumnCount(20);
         outputFileField.setPromptText("Enter output file name");
         final Button saveAsBtn = new Button("Save As"); 
         saveAsBtn.setMaxWidth(Double.MAX_VALUE);
         
         _row++;
         grid.add(inputFileLabel, 0, _row);
         grid.add(inputFileField, 1, _row);
         grid.add(fileSelectBtn, 2, _row);
         
         _row++;
         grid.add(outputFileLabel, 0, _row);
         grid.add(outputFileField, 1, _row);
         grid.add(saveAsBtn, 2, _row);
           
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
    }
    
    private void addConvertBtn(GridPane grid) {
    	cnvrtBtn = new Button("Convert");
        cnvrtBtn.setMaxWidth(Double.MAX_VALUE);
        cnvrtBtn.setDisable(true);
        grid.add(cnvrtBtn, 2, ++_row);
    }
    
    private void setConversionMode(String direction, FileType f1, FileType f2) {
    	inputFileType = (direction == "I") ? f1 : f2;
    	outputFileType = (direction == "I") ? f2 : f1;
    	// System.out.print(inputFileType);
    	// System.out.println(outputFileType);
    }
    
    private void initUserPresets() {
        
    }
    
    private boolean doConvert() {
    	setIsProcessing(true);
    	cnvrtBtnDisable();
    	
        if (!logic.fileExists(inputFileField.getText())) {
            doFileOpenDialog();
            // Give use once chance to select file. If this 
            // fails, we alert and return false to prevent loop.
            boolean retVal = doConvert();
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
        	if (logic.make(inputFileType, inputFile) 
        			&& logic.write(outputFileType, outputFile)) {
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
    	setExtensionFilters(inputFileType);
    	
    	if (inputFileField.getText().length() > 0) {
    		fileChooser.setInitialDirectory(
    				new File(inputFileField
									.getText()
									.trim()
									.replaceAll("/[^/]*$", "/")));
        	     
        }
        
    	File file = null;
    	try {
    		file = fileChooser.showOpenDialog(stage);
    	} catch (IllegalArgumentException exp) {
    		exp.printStackTrace();
    		fileChooser.setInitialDirectory(new File(System.getProperty("user.home"))); 
    	}   	
        
        if (file != null) {
        	inputFileField.setText(file.getPath());
            setPresetOutputFile();
        }
    }
    
    private void doFileSaveAs() {
    	setExtensionFilters(outputFileType);
        
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
    
    private void setExtensionFilters(FileType fileType) {  
    	fileChooser.getExtensionFilters().removeAll(fileChooser.getExtensionFilters());
    	
    	FileTypeTup[] fTups = { GlobalConstants.getFileExtTup(fileType)
    					, GlobalConstants.getFileExtTup(FileType.ALL_FILES) };
    	
    	for (FileTypeTup t : fTups) {
    		fileChooser.getExtensionFilters().add(
    		    new ExtensionFilter(
    		        String.format("%s (*.%s)", t.name, t.extension)
    		      , String.format("*.%s", t.extension )
    		    )	    
    		);
    	}
    }
    
    private void setPresetOutputFile() {
    	String inputFilePath = inputFileField.getText().trim();
    	if (inputFilePath.length() == 0) return;
    	
    	outputFileField.setText(logic
				.newFileExension(inputFilePath, 
						GlobalConstants
						.getFileExtTup(outputFileType)  
						.extension));
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