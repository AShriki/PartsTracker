package PartsBox;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Stack;
import javafx.scene.input.MouseEvent;
import PartsBox.Structs.LibraryGenerator;
import PartsBox.Structs.Part;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TabPane.TabClosingPolicy;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Callback;

public class Main extends Application{

    private int prefWidth = 1000;
    private int prefHeight = 900;
    private int screenWidth = (int) Screen.getPrimary().getBounds().getWidth();
    private int screenHeight = (int) Screen.getPrimary().getBounds().getHeight();
    private int minWidth = 600;
    private int minHeight = 600;

    private LibraryGenerator pLib;
    private TableView<Part> table_lib;
    Label libErrorText = new Label("");

    int selectionIndex;
    
    private Hashtable<String,Stage> openWindows;
    private Stack<LibraryGenerator> undoStack;
    private Stack<LibraryGenerator> redoStack;

    
	public static void main(String[] args) {

		launch(args);
	}

	@Override
	public void start(Stage stage) throws IOException {

			openWindows = new Hashtable<String,Stage>();
			undoStack = new Stack<LibraryGenerator>();
			redoStack = new Stack<LibraryGenerator>();
			table_lib = new TableView<Part>();
			pLib = new LibraryGenerator();
			
			selectionIndex = -1;
		
			if(prefWidth > screenWidth){prefWidth = screenWidth;}
			if(prefHeight > screenHeight){prefHeight = screenHeight;}

	        table_lib.setEditable(true);
	        table_lib.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

	        final KeyCombination undo = new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN);
	        final KeyCombination redo = new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN);
	        
		 	Scene scene = new Scene(new Group());

	        AnchorPane masterPane = new AnchorPane();
	        TabPane tabPane = new TabPane();

			stage.setOnCloseRequest( e -> Platform.exit() );

	        Tab libTab = new Tab("Library");
	        Tab setTab = new Tab("Settings");

	        // - - - - - - - - - - lib tab containers & buttons

	        HBox masterPane_lib = new HBox();
	        VBox controlsPane_lib = new VBox();
	        
	        VBox dataPane_lib = new VBox();

	        ScrollPane tableScroll_lib = new ScrollPane(table_lib);

	        tableScroll_lib.setFitToHeight(true);
	        tableScroll_lib.setFitToWidth(true);
	        
	        ArrayList<Button> libTabButtons = new ArrayList<Button>();

	        libTabButtons.add(new Button("Add/Import"));
	        libTabButtons.add(new Button("Export Library"));
	        libTabButtons.add(new Button("Add Parts"));
	        libTabButtons.add(new Button("Delete Selection"));
	        libTabButtons.add(new Button("Delete Library"));
	        libTabButtons.add(new Button("Save"));
	        libTabButtons.add(new Button("Edit Columns"));


	        // - - - - - - stage settings
	        stage.setTitle("Parts Box");
	        stage.setWidth(prefWidth);
	        stage.setHeight(prefHeight);

	        masterPane.isResizable();

	        tabPane.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);
	        tabPane.isResizable();
	        tabPane.getTabs().addAll(libTab,/*proTab,*/setTab);

	        stage.setMinHeight(minHeight);
	        stage.setMinWidth(minWidth);
	        stage.setMaxWidth(screenWidth);
	        stage.setMaxHeight(screenHeight);
	        // - - - - - - - - - -
	        controlsPane_lib.setMinWidth(120);
	        controlsPane_lib.setPrefWidth(120);
	        
	        dataPane_lib.setMinSize(minWidth, minHeight);
	        dataPane_lib.setPrefSize(prefWidth, prefHeight);
	        masterPane_lib.setPrefSize(prefWidth, prefHeight);

	        table_lib.setMinSize(minWidth, minHeight);
	        masterPane_lib.setMinSize(minWidth, minHeight);
	        tabPane.setMinSize(minWidth, minHeight);
	        masterPane.setMinSize(minWidth, minHeight);
	        
	        dataPane_lib.setMaxWidth(screenWidth-screenWidth*0.05);
	        table_lib.setMaxSize(prefWidth-150, prefHeight);
	        // - - - - - - - - - -

	        // - - - - - - - - - AutoResizing for lib- tab

	        controlsPane_lib.prefHeightProperty().bind(masterPane_lib.heightProperty()); // fixed width and variable height

	        table_lib.prefHeightProperty().bind(tableScroll_lib.heightProperty());
	        table_lib.prefWidthProperty().bind(tableScroll_lib.widthProperty());
	        	        
	        tableScroll_lib.prefWidthProperty().bind(dataPane_lib.widthProperty());
	        tableScroll_lib.prefHeightProperty().bind(dataPane_lib.heightProperty());
	      
	        dataPane_lib.prefHeightProperty().bind(masterPane_lib.heightProperty());
	        dataPane_lib.prefWidthProperty().bind(masterPane_lib.widthProperty());

	        masterPane_lib.prefHeightProperty().bind(tabPane.heightProperty());
	        masterPane_lib.prefWidthProperty().bind(tabPane.widthProperty());

	        tabPane.prefHeightProperty().bind(masterPane.heightProperty());
	        tabPane.prefWidthProperty().bind(masterPane.widthProperty());

	        masterPane.prefHeightProperty().bind(stage.heightProperty());
	        masterPane.prefWidthProperty().bind(stage.widthProperty());

	        // - - - - - - - - - - - - - - - - lib tab buttons

	        dataPane_lib.setPadding(new Insets(5));
	        
	        Label libLabel_lib = new Label("Component Library");
	        
	        HBox searchContainer_lib = new HBox();
	        
	        Label searchLabel_lib = new Label("Search: ");
	        
	        TextField search_lib = new TextField();
	        search_lib.setPromptText("Search");
	        
	        ComboBox<String> selectHeaderComboBox = new ComboBox<String>(pLib.getHeaderObservableList());
	        
	        search_lib.textProperty().addListener((observable, oldValue, newValue) -> { // search function TODO: Fix it
	        	boolean a = newValue != null && !newValue.isEmpty();
				boolean c = selectHeaderComboBox.getValue()!= null && !selectHeaderComboBox.getValue().isEmpty();
				if( a && c) {
					pLib.search(newValue, selectHeaderComboBox.getValue());
					table_lib.refresh();
				}else {
					pLib.syncPartsLists();
					table_lib.refresh();
				}
	        });
	        selectHeaderComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
	        	boolean a = search_lib.getText() != null && !search_lib.getText().isEmpty();
				boolean c = newValue!= null && !newValue.isEmpty();
				if( a && c) {
					pLib.search(search_lib.getText(), newValue);
					table_lib.refresh();
				}else {
					pLib.syncPartsLists();
					table_lib.refresh();
				}
	        });
	        
	        searchContainer_lib.getChildren().addAll(searchLabel_lib,search_lib,selectHeaderComboBox);
	        
	        libErrorText.setWrapText(true);
	        
	        libLabel_lib.setWrapText(true);
	        dataPane_lib.getChildren().addAll(libLabel_lib,searchContainer_lib,table_lib);
	        libLabel_lib.setFont(new Font(24));


	        int libTabButtonY = 70;
	        int libTabButtonX = 112;

	        for(Button b : libTabButtons){
	        	b.setPrefHeight(libTabButtonY);
		        b.setPrefWidth(libTabButtonX);
		        b.setWrapText(true);
		        b.setTextAlignment(TextAlignment.CENTER);
	        }

	        // - - - - - - - - - - - - - - - - - - - - - setting up table_lib

	        table_lib.setEditable(true);
	        
	        ArrayList<TableColumn<Part,String>> cols = new ArrayList<>();
	        int i = 0;
	       
	        
	        for(String cat : pLib.getHeaderList()){
	        	
	        	cols.add(new TableColumn<Part,String>(cat));
	        	cols.get(i).setSortable(false);
	        	cols.get(i).setEditable(true);
	        	cols.get(i).setCellValueFactory(new Callback<CellDataFeatures<Part, String>, ObservableValue<String>>() {
	        	     public ObservableValue<String> call(CellDataFeatures<Part, String> p) {
	        	         return new ReadOnlyObjectWrapper<String>(p.getValue().getDetail(pLib.getHeaderIndex(cat)));
	        	     }

	        	  });

	        	cols.get(i).setCellFactory(TextFieldTableCell.forTableColumn());

	        	cols.get(i).setOnEditCommit(
	        		    new EventHandler<CellEditEvent<Part, String>>() {
	        		        @Override
	        		        public void handle(CellEditEvent<Part, String> t) {
	        		        	undoStack.push(new LibraryGenerator(pLib));
	        		        	pLib.editPart(t.getTablePosition().getRow(), cat, t.getNewValue());
	        		        }
	        		    }
	        	);
	        	

	        	i++;
	        }
        	table_lib.setItems(pLib.getPartsObservableList()); // associating the table with the list



	        table_lib.getColumns().addAll(cols);

	        // - - - - - - - - - -

	        // - - - - - - - - - - settings tab
	        HBox masterPane_set = new HBox();
	        VBox headerFormattingBox_set = new VBox();
	        HBox buttonsBox_set = new HBox();

	        masterPane_set.isResizable();
	        headerFormattingBox_set.isResizable();

	        headerFormattingBox_set.setMinHeight(120);
	        headerFormattingBox_set.setMinHeight(120);
	        headerFormattingBox_set.setPrefHeight(120);
	        headerFormattingBox_set.setPrefHeight(120);

	        buttonsBox_set.prefWidthProperty().bind(masterPane_set.widthProperty());
	        headerFormattingBox_set.prefWidthProperty().bind(masterPane_set.widthProperty());
	        masterPane_set.prefWidthProperty().bind(masterPane.widthProperty());

	        Label headerFormattingTitle = new Label("Import Formatting for Header Column Order");
	        headerFormattingTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

	        final TextField headerFormatImportField = new TextField();

	        int buttonx = 80;
	        int buttony = 15;

	        ArrayList<Button> headerAddButtons_set = new ArrayList<Button>();
	        for(String s : pLib.getHeaderList()){
	        	Button b = new Button(s);
	        	b.setPrefSize(buttonx, buttony);
	        	b.setOnAction((event) -> {
		        	String [] str = headerFormatImportField.getText().split(",",-1);

		        	if(str[str.length-1].equals("") && !searchTextField(headerFormatImportField,s)){
			        	headerFormatImportField.appendText(s);
		        	}
		        	else if(searchTextField(headerFormatImportField,s)){

		        	}
		        	else{
		        		headerFormatImportField.appendText(","+s);
		        	}

		        });
	        	b.setTooltip(new Tooltip(s));
	        	headerAddButtons_set.add(b);
	        }

	        Button skipCol_set = new Button("Skip");
	        skipCol_set.setPrefSize(buttonx, buttony);
	        skipCol_set.setWrapText(true);
	        Button clrImport_set = new Button("Clear");
	        clrImport_set.setPrefSize(buttonx, buttony);

	        headerFormatImportField.prefWidthProperty().bind(masterPane_set.widthProperty());
	        headerFormatImportField.setEditable(false);

	        Label exportTitle = new Label("File Export Formatting");
	        exportTitle.setFont(Font.font("Verdana", FontWeight.BOLD, 18));

	        final TextField exportField = new TextField();
	        exportField.setText(pLib.getHeaderString());
	        exportField.prefWidthProperty().bind(masterPane_set.widthProperty());

	        buttonsBox_set.getChildren().add(skipCol_set);
	        buttonsBox_set.getChildren().addAll(headerAddButtons_set);
	        headerFormattingBox_set.getChildren().addAll(headerFormattingTitle,buttonsBox_set,headerFormatImportField,clrImport_set,exportTitle,exportField);
	        masterPane_set.getChildren().addAll(headerFormattingBox_set);
	        setTab.setContent(masterPane_set);

	        // - - - - - - - - - -

	        // - - - - - - - - - -
	        controlsPane_lib.getChildren().addAll(libTabButtons);
	        controlsPane_lib.getChildren().add(libErrorText);
	        
	        masterPane_lib.getChildren().addAll(controlsPane_lib,dataPane_lib);
	        libTab.setContent(masterPane_lib);

	        masterPane.getChildren().addAll(tabPane); // add all to pane
	        
	        scene.addEventHandler(KeyEvent.KEY_RELEASED, new EventHandler<KeyEvent>() {

				@Override
				public void handle(KeyEvent event) {
					
					if(undo.match(event)) {
   		        		if(!undoStack.isEmpty()) {
   		        			redoStack.push(new LibraryGenerator(pLib));
   		        			pLib.restore(undoStack.pop());
   		        			table_lib.getItems().clear();
   		        			pLib.syncPartsLists();
   		        			table_lib.refresh();
   		        			
   		        		}
   		        		else
   		        			System.out.println("Undo Stack is Empty");
   		        		
   		        	}
   		        	else if(redo.match(event)){
   		        		if(!redoStack.isEmpty()) {
   		        			undoStack.push(new LibraryGenerator(pLib));
   		        			pLib.restore(redoStack.pop());
   		        			table_lib.getItems().clear();
   		        			pLib.syncPartsLists();
   		        			table_lib.refresh();
   		        		}
   		        		else
   		        			System.out.println("Redo Stack is Empty");
   		        	}	
					event.consume();
				}
	        	
	        });
	        
	        ((Group) scene.getRoot()).getChildren().addAll(masterPane); // master add statement
	        stage.setScene(scene);
	        stage.show();
	        // - - - - - - - - - -
	        
	        table_lib.getSelectionModel().setCellSelectionEnabled(true);

	        table_lib.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> { // TODO  fix the selection technique
	            if(event.isShortcutDown() || event.isShiftDown())
	                event.consume();
	        });
	        
	        table_lib.getFocusModel().focusedCellProperty().addListener((obs, oldVal, newVal) -> {
	        	
	            if(newVal.getTableColumn() != null){
	            	table_lib.getSelectionModel().selectRange(0, newVal.getTableColumn(), table_lib.getItems().size(), newVal.getTableColumn());
	            	selectionIndex = newVal.getColumn();
	            }
	        });

	        table_lib.addEventFilter(MouseEvent.MOUSE_PRESSED, (event) -> {
	            if(event.isShortcutDown() || event.isShiftDown())
	                event.consume();
	        });
	        
	        // - - - - - - - - - -

	        libTabButtons.get(6).setOnAction((event) -> { // column editor
	        	libErrorText.setText("");
	        	String stageID = "columneditor";

	        	if(openWindows.containsKey(stageID)){
	        		openWindows.get(stageID).toFront();
	        		return;
	        	}
	        	
	        	VBox columnEditorPane = new VBox();
	        	
	        	HBox columnEditorButtons = new HBox();
	        	HBox columnEditorTextField = new HBox();
	        	
	        	        	
                Scene libTabButtonScene = new Scene(columnEditorPane, 100, 100);

                // New window (Stage)
                Stage newWindow = new Stage();
                newWindow.setTitle("Column Editor");
                newWindow.setScene(libTabButtonScene);
                
                
                
                // Set position of second window, related to primary window.
                columnEditorPane.prefWidthProperty().bind(newWindow.widthProperty());
                newWindow.setX(scene.getX());
                newWindow.setY(scene.getY() + 100);
                newWindow.setHeight(115);
                newWindow.setWidth(350);

                final Label colLabel = new Label("Column Name: ");
                final Label colEditorErrorLabel = new Label("");
                final Button colAddButton = new Button("Add");
                final Button colRenameButton = new Button("Rename");
                final Button colRemoveButton = new Button("Remove");
                final Button colRemoveAllButton = new Button("Remove All");
                
                final TextField addCol = new TextField();
                
                colEditorErrorLabel.setTextFill(Color.RED);
                
                colAddButton.setPrefSize(80, 30);
                colRenameButton.setPrefSize(80, 30);
                colRemoveButton.setPrefSize(80, 30);
                colRemoveAllButton.setPrefSize(80, 30);
                addCol.setPromptText("Column Name");

                
                
                colAddButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
                    public void handle(ActionEvent e) {
						colEditorErrorLabel.setText("");
                    	if(addCol.getText().isEmpty()){
                    		return;
                    	}
                    	
                    	String headerText = addCol.getText();

                    	if(pLib.getHeaderIndex(headerText) != -1) {
                    		colEditorErrorLabel.setText("Column Already Exists");
                    		addCol.clear();
                    		return;
                    	}
                    	
                    	undoStack.push(new LibraryGenerator(pLib));
                    	
                    	pLib.addHeader(headerText);
                    	selectHeaderComboBox.getItems().add(headerText);
                    	
                    	addColumn(headerText);
                    	
                        if(openWindows.containsKey("manualadd")){
                        	openWindows.get("manualadd").close();
                        	openWindows.remove("manualadd");
                        }

                        addCol.clear();
                        
                    }

                });
                colRenameButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
                    public void handle(ActionEvent e) {
						
						colEditorErrorLabel.setText("");
						
                    	if(addCol.getText().isEmpty() || table_lib.getColumns().isEmpty()){
                    		addCol.clear();
                    		return;
                    	}
                    	
                    	String columnName = addCol.getText();
                    	
                    	renameColumn(colEditorErrorLabel,addCol,selectHeaderComboBox,columnName,selectionIndex);
                    	
                        addCol.clear();
                        
                        table_lib.refresh();
                    }

                });
                colRemoveButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
                    public void handle(ActionEvent e) {
						colEditorErrorLabel.setText("");

                    	String columnName = addCol.getText();
                    	
                    	if(table_lib.getItems().isEmpty() && !addCol.getText().isEmpty() && pLib.getHeaderIndex(columnName) != -1) {
                    		undoStack.push(new LibraryGenerator(pLib));
                    		table_lib.getColumns().remove(pLib.getHeaderIndex(columnName));
                    		pLib.removeHeader(columnName);
                    		selectHeaderComboBox.getItems().remove(columnName);
                    	} 
                    	else if(selectionIndex > -1 && selectionIndex < pLib.getNumHeaders()){
                    		undoStack.push(new LibraryGenerator(pLib));
	                    	table_lib.getColumns().remove(selectionIndex);
	                    	pLib.removeHeader(selectionIndex);
	                    	selectHeaderComboBox.getItems().remove(selectionIndex);
                    	}
                    	else {
                    		colEditorErrorLabel.setText("Remove failed");
                    		return;
                    	}
                        if(openWindows.containsKey("manualadd")){
                        	openWindows.get("manualadd").close();
                        	openWindows.remove("manualadd");
                        }
                        
                        
                        addCol.clear();
                        
                        table_lib.refresh();
                    }

                });
                colRemoveAllButton.setOnAction(new EventHandler<ActionEvent>() {
					@Override
                    public void handle(ActionEvent e) {
						colEditorErrorLabel.setText("");
						
						undoStack.push(new LibraryGenerator(pLib));
                    	
                    	pLib.removeAll();
						
						for(int i = 0; i < pLib.getNumHeaders() ; i++) {
                    		pLib.removeHeader(i);
                    	}
                    	
                		table_lib.getColumns().removeAll(table_lib.getColumns());
                    	
                    	if(openWindows.containsKey("manualadd")){
                        	openWindows.get("manualadd").close();
                        	openWindows.remove("manualadd");
                        }
                    	
                    	selectHeaderComboBox.getItems().removeAll(selectHeaderComboBox.getItems());
                    	
                        addCol.clear();
                        
                        table_lib.refresh();
                    }

                });
                
                
                columnEditorTextField.getChildren().addAll(colLabel,addCol);
                columnEditorButtons.getChildren().addAll(colAddButton,colRenameButton,colRemoveButton,colRemoveAllButton);

                columnEditorPane.getChildren().addAll(columnEditorTextField,columnEditorButtons,colEditorErrorLabel);
                
                openWindows.put(stageID, newWindow);

                newWindow.setOnCloseRequest(e -> {
                	openWindows.remove(stageID, newWindow);
                });
                newWindow.setAlwaysOnTop(true);
                newWindow.show();
	        });
	        libTabButtons.get(0).setOnAction((event) -> { // file add import

	        	libErrorText.setText("");

	        	FileChooser fileChooser = new FileChooser();
	        	fileChooser.setTitle("Open Resource Files");
	        	fileChooser.getExtensionFilters().addAll(
	        	        new ExtensionFilter(".txt/.csv/.tsv", "*.txt","*.csv","*.tsv"));
	        	List<File> files = fileChooser.showOpenMultipleDialog(stage);
	        	if(files != null && !files.isEmpty()){
	        		undoStack.push(new LibraryGenerator(pLib));
	        		pLib.addFiles(files,table_lib);
	        	}


	        });

	        libTabButtons.get(1).setOnAction((event) -> { // export file
	        	libErrorText.setText("");
	            // Button was clicked, do something...
	        });
	        libTabButtons.get(2).setOnAction((event) -> { // manual part add button
	        	libErrorText.setText("");
	        	String stageID = "manualadd";

	        	if(openWindows.containsKey(stageID)) {
	        		openWindows.get(stageID).toFront();
	        		return;
	        	}

	            ArrayList<TextField> manuAddTextFields_set = new ArrayList<TextField>();
	        	
	        	Stage manualAddWindow = new Stage();

	        	HBox manualAddPartBox = new HBox(10);

                Scene secondScene = new Scene(manualAddPartBox, 230, 100);

                // New window (Stage)
                manualAddWindow.setTitle("Part Adder Bar");
                manualAddWindow.setScene(secondScene);

                // Set position of second window, related to primary window.
                manualAddWindow.setX(scene.getX());
                manualAddWindow.setY(scene.getY() + 300);
                manualAddWindow.setHeight(100);
                manualAddWindow.setWidth(prefWidth + 25);

                final Button manualAddPartAddButton = new Button("Add");

                manuAddTextFields_set.clear();

                for(String h : pLib.getHeaderList()){

                	TextField tempField = new TextField();
                	tempField.setMaxWidth(100);
                	tempField.setPromptText(h);
                	manuAddTextFields_set.add(tempField);
                }

                manualAddPartBox.getChildren().add(manualAddPartAddButton);
                manualAddPartBox.getChildren().addAll(manuAddTextFields_set);
                manualAddPartBox.setSpacing(3);

                openWindows.put(stageID, manualAddWindow);

                manualAddWindow.setOnCloseRequest(e -> {
                	openWindows.remove(stageID, manualAddWindow);
                	manualAddWindow.close();
                });

                manualAddWindow.show();

                manualAddPartAddButton.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent e) {
                    	int emptyCount = 0;
                    	for(TextField t : manuAddTextFields_set){ // count empty text fields
                    		if(t.getText().isEmpty())
                    			emptyCount++;
                    	}

                    	if(emptyCount == manuAddTextFields_set.size())
                    		return;

                    	Part p = new Part((ArrayList<String>)pLib.getHeaderList());
                    	int i = 0;
                    	for(TextField t : manuAddTextFields_set){
                    		String stri = t.getText();
                    		p.setDetail(stri,i);
                    		i++;
                    	}
                    	undoStack.push(new LibraryGenerator(pLib));
                    	pLib.addPart(p);

                    	for(TextField t : manuAddTextFields_set){
                    		t.clear();
                    	}
                    }
                });
	        });

	        libTabButtons.get(3).setOnAction((event) -> {
	        	libErrorText.setText("");
	        	if(!table_lib.getSelectionModel().isEmpty()) {
	        		undoStack.push(new LibraryGenerator(pLib));
	        		pLib.removePart(table_lib.getSelectionModel().getSelectedIndex());
	        	}

	        });
	        libTabButtons.get(4).setOnAction((event) -> { // remove Library
	        	undoStack.push(new LibraryGenerator(pLib));
	        	libErrorText.setText("");
	        	search_lib.clear();
	            pLib.removeAll();
	            table_lib.refresh();
	        });
	        libTabButtons.get(5).setOnAction((event) -> { // save button
	        	table_lib.setVisible(false);
	        });
	        skipCol_set.setOnAction((event) -> {
	        	String s = ",";
	        	String [] str = headerFormatImportField.getText().split(",",-1);
	        	headerFormatImportField.appendText(s);
	        	if(str[str.length-1].equals("")){

	        	}
	        	else{
	        		headerFormatImportField.appendText(s);
	        	}

	        });
	        clrImport_set.setOnAction((event) -> {
	        	headerFormatImportField.clear();
	        });

	}

	public boolean searchTextField(TextField t,String s){

		String[] str = t.getText().split(",",-1);

		for(String x : str){
			if(x.equals(s))
				return true;
		}

		return false;
	}
	
	private void addColumn(String headerText) {
		TableColumn<Part,String> col = new TableColumn<Part,String>(headerText);

       	col.setSortable(true);
       	col.setEditable(true);
       	col.setCellValueFactory(new Callback<CellDataFeatures<Part, String>, ObservableValue<String>>() {
	   	    public ObservableValue<String> call(CellDataFeatures<Part, String> p) {
	   	        return new ReadOnlyObjectWrapper<String>(p.getValue().getDetail(pLib.getHeaderIndex(headerText)));
	   	    }

       	});

       	col.setCellFactory(TextFieldTableCell.forTableColumn());
       	
       	col.setOnEditCommit(
       		    new EventHandler<CellEditEvent<Part, String>>() {
       		        @Override
       		        public void handle(CellEditEvent<Part, String> t) {
       		        	undoStack.push(new LibraryGenerator(pLib));
       		        	pLib.editPart(t.getTablePosition().getRow(), headerText, t.getNewValue());
       		        }
       		    }
       	);

    	table_lib.getColumns().add(col);
	}
	
	private void renameColumn(Label colEditorErrorLabel, TextField addCol, ComboBox<String> selectHeaderComboBox , String columnName, int selectionIndex) {
		
    	if(selectionIndex == -1) {
    		colEditorErrorLabel.setText("No column selected");
    		addCol.clear();
    		return;
    	}
    	if((selectionIndex > -1 && selectionIndex < pLib.getNumHeaders()) ) {
    		undoStack.push(new LibraryGenerator(pLib));
			pLib.replaceHeader(table_lib.getColumns().get(selectionIndex).getText(), columnName);
			table_lib.getColumns().get(selectionIndex).setText(columnName);    
			selectHeaderComboBox.getItems().add(selectionIndex, columnName);
    	}
    	
        if(openWindows.containsKey("manualadd")){
        	openWindows.get("manualadd").close();
        	openWindows.remove("manualadd");
        }
	}
	
	public void handle(ActionEvent arg0) {

	}
}
