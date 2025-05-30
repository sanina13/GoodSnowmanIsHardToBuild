module pt.ipbeja {
    requires javafx.controls;
    requires javafx.media;

    opens pt.ipbeja.estig.po2.snowman.gui to javafx.fxml;

    requires org.jetbrains.annotations;

    exports pt.ipbeja.estig.po2.snowman.model;
    exports pt.ipbeja.estig.po2.snowman.gui;
}
