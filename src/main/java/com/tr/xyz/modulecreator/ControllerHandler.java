package com.tr.xyz.modulecreator;

import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.tr.xyz.modulecreator.Dev.print;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings ("FieldCanBeLocal")
public class ControllerHandler {
	
	public final  List<String>      moduleNameParts         = new ArrayList<>();
	public final  CreatorController controller;
	private final String            settingsGradle          = "settings.gradle";
	private final String            settingsGradleKts       = "settings.gradle.kts";
	private final String            buildGradle             = "build.gradle";
	private final String            buildGradleKts          = "build.gradle.kts";
	private final BooleanProperty   existSameDirectory      = new SimpleBooleanProperty(false);
	private final BooleanProperty   isValidProjectDirectory = new SimpleBooleanProperty(false);
	private       String            lastSelectedFolder      = getLastSelectedFolder();
	
	public ControllerHandler(@NotNull CreatorController controller) {
		this.controller = controller;
		initSelectedFolder();
		listenFolderSelection();
		checkGradleFile();
		controller.packageName.setText(getPackageName());
		
		var isModuleEmpty           = Bindings.isEmpty(controller.moduleName.textProperty());
		var isNotEmptyModuleWarning = Bindings.isNotEmpty(controller.moduleWarning.textProperty());
		controller.createModule.disableProperty().bind(
			isNotEmptyModuleWarning
				.or(isModuleEmpty)
				.or(existSameDirectory)
				.or(isValidProjectDirectory.not()));
		controller.selectedFolderLabel.textProperty().bind(controller.selectedFolder.asString());
		
		controller.moduleName.setOnKeyReleased(event -> {
			controller.packageName.setText("package : " + getPackageName());
			checkModuleName();
			if (event.getCode().equals(javafx.scene.input.KeyCode.ENTER)) {
				if (!controller.createModule.isDisable())
					makeModule();
			}
			controller.labelDone.setText("");
		});
		
		controller.moduleName.disableProperty().bind(isValidProjectDirectory.not());
		
		existSameDirectory.addListener((observable, oldValue, newValue) -> {
			if (newValue) {
				// controller.labelDone.setTextFill(Color.RED);
				controller.moduleWarning.setText("* Module name already exists");
			}
			else controller.moduleWarning.setText("");
			
		});
		
		controller.makeModule.addListener((observable, oldValue, newValue) -> makeModule());
	}
	
	private void checkModuleName() {
		var name = controller.moduleName.getText();
		if (name.isEmpty()) {
			controller.moduleWarning.setText("");
			existSameDirectory.setValue(false);
		}
		else {
			var file = new File(lastSelectedFolder, controller.moduleName.getText());
			existSameDirectory.setValue(file.exists());
		}
	}
	
	@SuppressWarnings ("DataFlowIssue")
	private void makeModule() {
		
		var moduleRootDirectory = new File(lastSelectedFolder, controller.moduleName.getText());
		if (!moduleRootDirectory.exists()) {
			if (moduleRootDirectory.mkdir()) print("Module root created : " + moduleRootDirectory.getAbsolutePath());
			else {
				print("Root dir fucked");
				return;
			}
		}
		
		File buildGradle;
		
		if (controller.buildTypeGradle.isSelected()) buildGradle = new File(moduleRootDirectory, this.buildGradle);
		else buildGradle = new File(moduleRootDirectory, this.buildGradleKts);
		
		var    git           = new File(moduleRootDirectory, ".gitignore");
		String defaultDomain = Creator.getPackageDomain(controller);
		assert defaultDomain != null;
		String domainDir = defaultDomain.replaceAll("\\.", "/");
		
		if (!buildGradle.exists()) {
			try {
				if (buildGradle.createNewFile()) {
					print("File created : " + buildGradle.getAbsolutePath());
					var srcDir      = String.format("src/main/java/%s/%s", domainDir, controller.moduleName.getText().replaceAll("-", "_"));
					var srcMainJava = new File(moduleRootDirectory, srcDir);
					if (!srcMainJava.exists())
						if (srcMainJava.mkdirs())
							print("Source dirs created : " + srcMainJava.getAbsolutePath());
						else {
							print("Source dirs fucked");
							return;
						}
					
					if (git.createNewFile()) {
						print("File created : " + git.getAbsolutePath());
						Dev.writeFile(git, "/build");
					}
					
					if (controller.moduleTypeAndroid.isSelected()) {
						if (controller.kotlinSupport.isSelected()) Dev.writeFile(buildGradle, Creator.getAndroidKotlinBuildGradleContent(controller).formatted(String.format("%s.%s", defaultDomain, getPackageName())));
						else Dev.writeFile(buildGradle, Creator.getAndroidJavaBuildGradleContent(controller).formatted(String.format("%s.%s", defaultDomain, getPackageName())));
					}
					else {
						if (controller.kotlinSupport.isSelected()) Dev.writeFile(buildGradle, Creator.getKotlinBuildGradleContent(controller));
						else Dev.writeFile(buildGradle, Creator.getJavaBuildGradleContent(controller));
					}
					
					var settingsGradleFile = new File(getGradleRoot(controller.selectedFolder.getValue()), controller.buildTypeGradle.isSelected() ? settingsGradle : settingsGradleKts);
					
					if (settingsGradleFile.exists()) {
						print("File exists : " + settingsGradleFile.getAbsolutePath());
						var include = String.format("\ninclude ':%s'", controller.packageName.getText());
						var result  = Dev.appendFile(settingsGradleFile, include);
						
						if (result)
							print("File appended : " + settingsGradleFile.getAbsolutePath());
						else
							print("File append failed : " + settingsGradleFile.getAbsolutePath());
					}
					else {
						print("Can not found settings.gradle : " + settingsGradleFile.getAbsolutePath());
					}
				}
				
				print("All good");
				controller.labelDone.setTextFill(Color.GREEN);
				controller.labelDone.setText("* All Done");
				controller.makeModule.setValue(false);
			} catch (IOException e) {
				e.printStackTrace();
				controller.labelDone.setTextFill(Color.ORANGE);
				controller.labelDone.setText("* Failed : " + e.getMessage());
			}
		}
		
	}
	
	private void initSelectedFolder() {
		if (Dev.isNotNullOrBlank(lastSelectedFolder)) {
			controller.selectedFolder.setValue(new File(lastSelectedFolder));
			print("Last selected folder : %s", lastSelectedFolder);
		}
	}
	
	private void listenFolderSelection() {
		controller.selectedFolder.addListener(
			(observable, oldValue, newValue) -> onSelectedFolder(newValue));
	}
	
	private void onSelectedFolder(@NotNull File folder) {
		Dev.writeFile(getSavedFolderFile(), folder.getAbsolutePath());
		print("Selected folder : " + folder);
		newFolderSaved(folder);
	}
	
	private void newFolderSaved(@NotNull File folder) {
		lastSelectedFolder = folder.getAbsolutePath();
		checkGradleFile();
		controller.packageName.setText(getPackageName());
	}
	
	private @NotNull String getPackageName() {
		return getModuleString().replaceAll(":", ".").replaceAll("-", "_");
	}
	
	@NotNull
	private File getSavedFolderFile() {
		File file = new File("selected_folder.txt");
		if (!file.exists()) {
			try {
				if (file.createNewFile()) return file;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		else return file;
		
		throw new RuntimeException("File can not be created");
	}
	
	private String getLastSelectedFolder() {
		return Dev.readFile(getSavedFolderFile());
	}
	
	public void checkGradleFile() {
		moduleNameParts.clear();
		var rootGradle = getGradleRoot(controller.selectedFolder.getValue());
		if (rootGradle == null) {
			controller.moduleWarning.setText("No settings.gradle found");
			isValidProjectDirectory.setValue(false);
			return;
		}
		
		print("Root gradle : " + rootGradle.getAbsolutePath());
		var buildGradleFile    = new File(controller.selectedFolder.getValue(), buildGradle);
		var buildGradleKtsFile = new File(controller.selectedFolder.getValue(), buildGradleKts);
		
		if (!buildGradleFile.exists() && !buildGradleKtsFile.exists()) {
			controller.moduleWarning.setText("No build.gradle found");
			isValidProjectDirectory.setValue(false);
		}
		else {
			controller.moduleWarning.setText("");
			isValidProjectDirectory.setValue(true);
		}
	}
	
	/**
	 * Returns the module name.
	 *
	 * @return module name
	 */
	public String getModuleString() {
		var moduleName = controller.moduleName.getText();
		
		if (this.moduleNameParts.isEmpty()) return moduleName;
		
		else if (this.moduleNameParts.size() == 1) {
			return Dev.format(
				"%s%s",
				this.moduleNameParts.get(0),
				moduleName.isBlank() ? "" : String.format(":%s", controller.moduleName.getText()));
		}
		else {
			return Dev.format(
				"%s%s",
				String.join(":", this.moduleNameParts),
				moduleName.isBlank() ? "" : String.format(":%s", moduleName));
		}
	}
	
	/**
	 * Returns the root gradle file of the project ({@code settings.gradle} or {@code settings.gradle.kts}).
	 * If selected folder does not belong to a Gradle project, returns {@code null}.
	 * The selected folder can be nested module folder that is deeply inside into the project;
	 * this method recursively searches through up until the very top folder.
	 *
	 * @param currentFolder the current folder that searching in
	 * @return the root gradle file or {@code null} if not found
	 */
	@Nullable
	private File getGradleRoot(File currentFolder) {
		if (currentFolder == null) return null;
		var settingsGradleFile    = new File(currentFolder, settingsGradle);
		var settingsGradleKtsFile = new File(currentFolder, settingsGradleKts);
		
		if (!settingsGradleFile.exists() && !settingsGradleKtsFile.exists()) {
			var builFile = new File(currentFolder, buildGradle);
			if (builFile.exists()) {
				moduleNameParts.add(currentFolder.getName());
			}
			else {
				builFile = new File(currentFolder, buildGradleKts);
				if (builFile.exists()) moduleNameParts.add(currentFolder.getName());
			}
			
			return getGradleRoot(currentFolder.getParentFile());
		}
		return currentFolder;
		
	}
	
}
