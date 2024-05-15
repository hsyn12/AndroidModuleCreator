module com.tr.xyz.modulecreator {
	requires javafx.controls;
	requires javafx.fxml;
	requires org.jetbrains.annotations;
	requires java.desktop;
	opens com.tr.xyz.modulecreator to javafx.fxml;
	exports com.tr.xyz.modulecreator;
}