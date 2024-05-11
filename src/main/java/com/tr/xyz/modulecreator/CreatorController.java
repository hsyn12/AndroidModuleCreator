package com.tr.xyz.modulecreator;

import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.*;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * Main controller class.
 */
public class CreatorController {
	
	public static final String                   MENU_ITEM_ANDROID_JAVA   = "Android+Java Template";
	public static final String                   MENU_ITEM_ANDROID_KOTLIN = "Android+Kotlin Template";
	public static final String                   MENU_ITEM_JAVA           = "Java Template";
	public static final String                   MENU_ITEM_KOTLIN         = "Kotlin Template";
	public static final String                   MENU_ITEM_DOMAIN         = "Package Domain";
	public final        ObjectProperty<File>     selectedFolder           = new SimpleObjectProperty<>();
	public final        BooleanProperty          makeModule               = new SimpleBooleanProperty(false);
	public final        ObservableList<MenuItem> templateMenuItems        = FXCollections.observableArrayList(
		new MenuItem(MENU_ITEM_ANDROID_JAVA),
		new MenuItem(MENU_ITEM_ANDROID_KOTLIN),
		new MenuItem(MENU_ITEM_JAVA),
		new MenuItem(MENU_ITEM_KOTLIN),
		new MenuItem(MENU_ITEM_DOMAIN)
	);
	public final        ContextMenu              settingsMenu             = new ContextMenu();
	@FXML public        Label                    selectedFolderLabel;
	@FXML public        Button                   createModule;
	@FXML public        Label                    moduleWarning;
	@FXML public        RadioButton              moduleTypeAndroid;
	@FXML public        RadioButton              moduleTypeJava;
	@FXML public        CheckBox                 kotlinSupport;
	@FXML public        RadioButton              buildTypeKotlin;
	@FXML public        RadioButton              buildTypeGradle;
	@FXML public        ToggleGroup              moduleType;
	@FXML public        ToggleGroup              buildType;
	@FXML public        Label                    packageName;
	@FXML public        TextField                moduleName;
	@FXML public        Label                    labelDone;
	@FXML public        Button                   settings;
	
	@FXML
	protected void onSelectFolder() {
		var chooser = new DirectoryChooser();
		
		if (selectedFolder.getValue() != null)
			chooser.setInitialDirectory(selectedFolder.getValue());
		
		var file = chooser.showDialog(selectedFolderLabel.getScene().getWindow());
		if (file != null) {
			selectedFolder.setValue(file);
		}
	}
	
	@FXML
	private void onCreateModule() {
		makeModule.setValue(true);
	}
	
	@FXML
	protected void onClickSettings() {
		settingsMenu.show(settings, Side.TOP, 0, 0);
	}
	
}