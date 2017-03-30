package com.ifnoelse.pdf.gui;

import com.ifnoelse.common.Validate;
import com.ifnoelse.pdf.PDFUtil;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by ifnoelse on 2017/3/2 0002.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("pdf bookmark");

        BorderPane bottomPane = new BorderPane();
        Button contentsGenerator = new Button("生成目录");
        bottomPane.setCenter(contentsGenerator);
        Button fileSelectorBtn = new Button("选择文件");


        BorderPane vBox = new BorderPane();
        TextField filePath = new TextField();

        filePath.setEditable(false);
        BorderPane topPane = new BorderPane();
        topPane.setCenter(filePath);


        TextField pageIndexOffset = new TextField();
        topPane.setRight(new HBox(pageIndexOffset, fileSelectorBtn));
        vBox.setTop(topPane);

        pageIndexOffset.setPromptText("页码偏移量");
        pageIndexOffset.setPrefWidth(100);


        TextArea textArea = new TextArea();
        textArea.setPromptText("请在此填入目录内容");
        vBox.setCenter(textArea);

        vBox.setBottom(bottomPane);
        Scene scene = new Scene(vBox, 600, 400);
        primaryStage.setScene(scene);


        fileSelectorBtn.setOnAction(event -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("pdf", "*.pdf"));
            File file = fileChooser.showOpenDialog(null);
            if (file != null) {
                filePath.setText(file.getPath());
            }


        });


        pageIndexOffset.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!observable.getValue()) {
                String offset = pageIndexOffset.getText();
                if (offset != null && offset.length() > 0 && !offset.matches("[0-9]+")) {
                    showDialog("错误","偏移量设置错误","页码偏移量只能为整数", Alert.AlertType.ERROR);
                }

            }
        });

        contentsGenerator.setOnAction(event -> {
            String fp = filePath.getText();
            if(Validate.isEmpty(fp)){
                showDialog("错误","pdf文件路径为空","pdf文件路径不能为空，请选择pdf文件", Alert.AlertType.ERROR);
                return;
            }
            String srcFile = fp.replaceAll("\\\\", "/");
            String srcFileName = srcFile.substring(srcFile.lastIndexOf("/") + 1);
            String ext = srcFileName.substring(srcFileName.lastIndexOf("."));
            String destFile = srcFile.substring(0, srcFile.lastIndexOf(srcFileName)) + srcFileName.substring(0, srcFileName.lastIndexOf(".")) + "_含目录" + ext;

            String offset = pageIndexOffset.getText();
            String content = textArea.getText();
            if (content != null && !content.isEmpty()) {
                PDFUtil.addBookmark(textArea.getText(), srcFile, destFile, Integer.parseInt(offset != null && !offset.isEmpty() ? offset : "0"));
                showDialog("通知","添加目录成功！","文件存储在" + destFile,Alert.AlertType.INFORMATION);
            } else {
                showDialog("错误","目录内容为空","目录能容不能为空,请填写pdf书籍目录url或者填入目录文本",Alert.AlertType.ERROR);
            }


        });
        primaryStage.show();
    }

    private void showDialog(String title,String header, String content,Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setContentText(content);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }
}
