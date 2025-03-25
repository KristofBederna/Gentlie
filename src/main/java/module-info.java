module inf.elte.hu.gameengine_javafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens inf.elte.hu.gameengine_javafx to javafx.fxml;
    exports inf.elte.hu.gameengine_javafx;
}