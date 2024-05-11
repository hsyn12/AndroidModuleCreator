package com.tr.xyz.modulecreator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Main class. The App starts here.
 */
public class Creator extends Application {
	
	@SuppressWarnings ( {"FieldCanBeLocal", "unused"})
	private ControllerHandler handler;
	private double            xOffset = 0;
	private double            yOffset = 0;
	
	public static void main(String[] args) {
		launch();
	}
	
	@SuppressWarnings ("DataFlowIssue")
	@Nullable
	public static String getPackageDomain(Object o) {
		String domain = null;
		
		var customContentFile = new File(".", "package_domain.txt");
		if (customContentFile.exists()) return Dev.readFile(customContentFile);
		
		try (var file = o.getClass().getResource("templates/package_domain.txt").openStream()) {
			domain = new String(file.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ignore) { }
		
		return domain;
	}
	
	/**
	 * Returns the content of the Gradle build file for an android module.
	 * This module does not support kotlin.
	 * If the file exists in the root folder, it will be used.
	 * Else the default content will be used.
	 * (Default content is done for my project,
	 * edit the content through the template file in the setting menu for your own.)
	 * The name of the file for java module is "android_java.txt",
	 * and also it can be changed directly in the app root folder,
	 * create a file named "android_java.txt" in the app root folder and fill your content.
	 *
	 * @return content of the build.gradle file
	 */
	@SuppressWarnings ("DataFlowIssue")
	@Nullable
	public static String getAndroidJavaBuildGradleContent(Object o) {
		String androidBuildGradleContent = null;
		
		var customContentFile = new File(".", "android_java.txt");
		if (customContentFile.exists()) {
			androidBuildGradleContent = Dev.readFile(customContentFile);
			return androidBuildGradleContent;
		}
		
		try (var file = o.getClass().getResource("templates/android_java.txt").openStream()) {
			androidBuildGradleContent = new String(file.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ignore) { }
		
		return androidBuildGradleContent;
	}
	
	/**
	 * Returns the content of the Gradle build file for an android module.
	 * This module also supports kotlin.
	 * If the file exists in the root folder, it will be used.
	 * Else the default content will be used.
	 * (Default content is done for my project,
	 * edit the content through the template file in the setting menu for your own.)
	 * The name of the file for java module is "android_kotlin.txt",
	 * and also it can be changed directly in the app root folder,
	 * create a file named "android_kotlin.txt" in the app root folder and fill your content.
	 *
	 * @return content of the build.gradle file
	 */
	@SuppressWarnings ("DataFlowIssue")
	@Nullable
	public static String getAndroidKotlinBuildGradleContent(Object o) {
		var customContentFile = new File(".", "android_kotlin.txt");
		if (customContentFile.exists()) {
			return Dev.readFile(customContentFile);
		}
		
		try (var file = o.getClass().getResource("templates/android_kotlin.txt").openStream()) {
			return new String(file.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ignore) {
			return null;
		}
		
	}
	
	/**
	 * Returns the content of the gradle build file for kotlin module.
	 * If the file exists in the root folder, it will be used.
	 * Else the default content will be used.
	 * (Default content is done for my project,
	 * edit the content through the template file in the setting menu for your own.)
	 * The name of the file for java module is "kotlin.txt",
	 * and also it can be changed directly in the app root folder,
	 * create a file named "kotlin.txt" in the app root folder and fill your content.
	 *
	 * @return content of the build.gradle file
	 */
	@SuppressWarnings ("DataFlowIssue")
	@Nullable
	public static String getKotlinBuildGradleContent(Object o) {
		var customContentFile = new File(".", "kotlin.txt");
		if (customContentFile.exists()) {
			return Dev.readFile(customContentFile);
		}
		
		try (var file = o.getClass().getResource("templates/kotlin.txt").openStream()) {
			return new String(file.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ignore) {
			return null;
		}
	}
	
	/**
	 * Returns the content of the gradle build file for java module.
	 * If the file exists in the root folder, it will be used.
	 * Else the default content will be used.
	 * (Default content is done for my project,
	 * edit the content through the template file in the setting menu for your own.)
	 * The name of the file for java module is "java.txt",
	 * and also it can be changed directly in the app root folder,
	 * create a file named "java.txt" in the app root folder and fill your content.
	 *
	 * @return content of the build.gradle file
	 */
	@SuppressWarnings ("DataFlowIssue")
	@Nullable
	public static String getJavaBuildGradleContent(Object o) {
		var customContentFile = new File(".", "java.txt");
		if (customContentFile.exists()) {
			return Dev.readFile(customContentFile);
		}
		
		try (var file = o.getClass().getResource("templates/java.txt").openStream()) {
			return new String(file.readAllBytes(), StandardCharsets.UTF_8);
		} catch (IOException ignore) {
			return null;
		}
	}
	
	@Override
	public void start(@NotNull Stage stage) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader(Creator.class.getResource("creator.fxml"));
		
		Scene scene = new Scene(fxmlLoader.load());
		stage.setTitle("Crate Module");
		stage.setScene(scene);
		handler = new ControllerHandler(fxmlLoader.getController());
		stage.show();
		centerWindow(stage);
		setMenu();
	}
	
	/**
	 * Sets the menu for the settings.
	 * This menu contains the templates for the build.gradle files.
	 * Provides a way to edit and save the selected template.
	 */
	private void setMenu() {
		handler.controller.settingsMenu.getItems().addAll(handler.controller.templateMenuItems);
		handler.controller.templateMenuItems.forEach(item -> item.setOnAction(event -> {
			FXMLLoader fxmlLoader = new FXMLLoader(Creator.class.getResource("settings.fxml"));
			Stage      stage      = new Stage();
			try {
				stage.setScene(new Scene(fxmlLoader.load()));
				stage.initModality(Modality.APPLICATION_MODAL);
				Settings settings = fxmlLoader.getController();
				String   name     = item.getText();
				settings.onSaveTemplate = () -> onSaveTemplate(name, settings);
				stage.setTitle(name);
				
				switch (name) {
					case CreatorController.MENU_ITEM_ANDROID_JAVA -> settings.templateTextArea.setText(getAndroidJavaBuildGradleContent(Creator.this));
					case CreatorController.MENU_ITEM_ANDROID_KOTLIN -> settings.templateTextArea.setText(getAndroidKotlinBuildGradleContent(Creator.this));
					case CreatorController.MENU_ITEM_JAVA -> settings.templateTextArea.setText(getJavaBuildGradleContent(Creator.this));
					case CreatorController.MENU_ITEM_KOTLIN -> settings.templateTextArea.setText(getKotlinBuildGradleContent(Creator.this));
					case CreatorController.MENU_ITEM_DOMAIN -> settings.templateTextArea.setText(getPackageDomain(Creator.this));
				}
				
				stage.show();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}));
		handler.controller.settings.setContextMenu(handler.controller.settingsMenu);
	}
	
	private void centerWindow(@NotNull Stage stage) {
		/* Rectangle2D screenBounds = Screen.getPrimary().getBounds();
		var         w            = screenBounds.getWidth() *//*  * 0.6 *//* ;
		var         h            = screenBounds.getHeight()  *//* * 0.5 *//* ;
		var         x            = (screenBounds.getWidth() - w) / 2;
		var         y            = (screenBounds.getHeight() - h) / 2;
		stage.setX(x - 50);
		stage.setY(y - 100);
		stage.setWidth(w);
		stage.setHeight(h); */
		
		final Stage _stage = stage;
		_stage.getScene().setOnMousePressed(e -> {
			xOffset = e.getScreenX() - _stage.getX();
			yOffset = e.getScreenY() - _stage.getY();
		});
		_stage.getScene().setOnMouseDragged(e -> {
			_stage.setX(e.getScreenX() - xOffset);
			_stage.setY(e.getScreenY() - yOffset);
		});
	}
	
	/**
	 * Saves the template in a file.
	 * A saved template will be used after saving.
	 *
	 * @param name     name of the template
	 * @param settings {@link Settings} controller
	 */
	private void onSaveTemplate(@NotNull String name, Settings settings) {
		File file = null;
		
		switch (name) {
			case CreatorController.MENU_ITEM_ANDROID_JAVA -> file = new File(".", "android_java.txt");
			case CreatorController.MENU_ITEM_ANDROID_KOTLIN -> file = new File(".", "android_kotlin.txt");
			case CreatorController.MENU_ITEM_JAVA -> file = new File(".", "java.txt");
			case CreatorController.MENU_ITEM_KOTLIN -> file = new File(".", "kotlin.txt");
			case CreatorController.MENU_ITEM_DOMAIN -> file = new File(".", "package_domain.txt");
		}
		
		if (file != null) {
			if (Dev.writeFile(file, settings.templateTextArea.getText())) {
				Dev.print("%s saved", name);
				settings.settingsLabel.setText(name + " saved");
			}
		}
	}
}