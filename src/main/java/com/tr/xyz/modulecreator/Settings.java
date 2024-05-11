package com.tr.xyz.modulecreator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

/**
 * Controller class for {@code settings.fxml}.
 * Contains the elements of the settings window.
 * Provides methods to edit and save the selected template.
 * {@link #templateTextArea} is {@link TextArea} that contains the selected template.
 * {@link #onSaveTemplate} is a {@link Runnable} that is called when the user saves the template,
 * you need to set this object to save the template.
 * {@link #settingsLabel} is {@link Label} that can be used to display actions or whatever.
 */
public class Settings {
	
	/**
	 * Template content.
	 */
	@FXML public TextArea templateTextArea;
	@FXML public Button   btnSaveAndClose;
	@FXML public Button   btnSave;
	@FXML public Button   btnClose;
	@FXML public GridPane settingsRoot;
	@FXML public Label    settingsLabel;
	public       Runnable onSaveTemplate;
	
	private Stage getStage() {
		return (Stage) settingsRoot.getScene().getWindow();
	}
	
	/**
	 * Closes the settings window.
	 * This method does not save the template.
	 */
	@FXML
	private void closeTemplateWindow() {
		getStage().close();
	}
	
	/**
	 * Closes the settings window and saves the template.
	 */
	@FXML
	private void onSaveAndClose() {
		if (onSaveTemplate != null) onSaveTemplate.run();
		closeTemplateWindow();
	}
	
	/**
	 * Saves the template.
	 * This method only saves the template and does not close the settings window.
	 */
	@FXML
	private void onSave() {
		if (onSaveTemplate != null) onSaveTemplate.run();
	}
	
	@FXML
	private void onEditTemplate() {
		settingsLabel.setText("Use %s for namespace");
	}
	
}
