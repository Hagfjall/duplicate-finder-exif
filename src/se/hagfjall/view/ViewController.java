package se.hagfjall.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import se.hagfjall.model.DuplicateFinderExif;
import se.hagfjall.model.ExifData;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Wycheproof on 15-06-14.
 */
public class ViewController implements Initializable {
    private DuplicateFinderExif duplicateFinderExif;
    private AbstractExifDataForView duplicateFileListMarked;
    private AbstractExifDataForView originalFileListMarked;


    @FXML
    private ListView originalFileList;
    @FXML
    private ImageView imageView;
    @FXML
    private ListView duplicateFileList;
    @FXML
    private MenuBar menuBar;
    @FXML
    private MenuItem moveAllDupMI;
    @FXML
    private MenuItem changeMoveFolderIM;
    @FXML
    private CheckMenuItem verificationCheckbox;
    @FXML
    private MenuItem moveDuplicatesOnSelectedOriginalMI;
    @FXML
    private MenuItem openSelectedFileIM;
    @FXML
    private MenuItem changeToOriginalIM;

    @Override // This method is called by the FXMLLoader when initialization is complete
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {
        assert originalFileList != null : "fx:id=\"originalFileList\" was not injected: check your FXML file 'simple.fxml'.";
        assert imageView != null : "fx:id=\"imageView\" was not injected: check your FXML file 'simple.fxml'.";
        assert duplicateFileList != null : "fx:id=\"duplicateFileList\" was not injected: check your FXML file 'simple.fxml'.";
        assert menuBar != null : "fx:id=\"menuBar\" was not injected: check your FXML file 'simple.fxml'.";
        assert verificationCheckbox != null :
                "fx:id=\"verificationCheckbox\" was not injected: check your FXML file 'simple.fxml'.";
        assert moveDuplicatesOnSelectedOriginalMI != null :
                "fx:id=\"moveDuplicatesOnSelectedOriginalMI\" was not injected: check your FXML file 'simple.fxml'.";
        assert openSelectedFileIM != null :
                "fx:id=\"openSelectedFileIM\" was not injected: check your FXML file 'simple.fxml'.";
        assert changeToOriginalIM != null :
                "fx:id=\"changeToOriginalIM\" was not injected: check your FXML file 'simple.fxml'.";

        duplicateFinderExif = new DuplicateFinderExif(View.parameters[0], View.parameters[1]);
        duplicateFinderExif.findDuplicates();

        originalFileList.setItems(FXCollections.observableList(parseOriginals(duplicateFinderExif.getOriginals())));
        originalFileList.setOnMouseReleased(event -> originalFileListClicked(
                (AbstractExifDataForView) originalFileList.getSelectionModel().getSelectedItems().get(0)));
        duplicateFileList.setOnMouseReleased(event ->
                duplicateFileListClicked((AbstractExifDataForView) duplicateFileList.getSelectionModel()
                        .getSelectedItems().get(0)));
        setupMenu();

    }

    private void setupMenu() {
        moveAllDupMI.setOnAction(event -> moveAllDuplicates());
        changeMoveFolderIM.setOnAction(event -> changeMoveFolder());
        openSelectedFileIM.setOnAction(event -> openSelectedFileClicked());
        changeToOriginalIM.setOnAction(event -> changeToOriginalClicked());
        moveDuplicatesOnSelectedOriginalMI.setOnAction(event -> moveDuplicatesOnSelectedOriginalClicked());

    }

    private void openSelectedFileClicked() {
        AbstractExifDataForView selectedFile = getSelectedFileInList();
        if (selectedFile == null) {
            return;

        }
        //TODO open this in the OS
    }


    private void changeMoveFolder() {
        System.out.println("change output folder");
    }

    private void originalFileListClicked(AbstractExifDataForView absExifData) {
        changeToOriginalIM.setDisable(true);
        updateImageView(absExifData.getExifData().getCanonicalPath());
        List<AbstractExifDataForView> duplicates = convertExifDataToView(duplicateFinderExif.
                getDuplicates(absExifData.exifData));

        duplicateFileList.setItems(FXCollections.observableList(duplicates));
        duplicateFileListMarked = null;
        originalFileListMarked = absExifData;
    }

    private void duplicateFileListClicked(AbstractExifDataForView abstractExifDataForView) {
        changeToOriginalIM.setDisable(false);
        updateImageView(abstractExifDataForView.getExifData().getCanonicalPath());
        duplicateFileListMarked = abstractExifDataForView;
    }

    private void changeToOriginalClicked() {
        System.out.println("Set " + duplicateFileListMarked.exifData.toString() + " as ORIGINAL");
    }

    private void moveAllDuplicates() {
        if (verificationCheckbox.isSelected())
            System.out.println("Verification");
        duplicateFinderExif.moveDuplicates();
        duplicateFinderExif.findDuplicates();
        originalFileList.setItems(FXCollections.observableList(parseOriginals(duplicateFinderExif.getOriginals())));
        duplicateFileList.getItems().clear();
    }

    private void moveDuplicatesOnSelectedOriginalClicked() {
        if (originalFileListMarked == null) {
            //TODO show error
        }
        System.out.println("Delete all images for " + originalFileListMarked.exifData.toString());
        duplicateFinderExif.moveDuplicates(originalFileListMarked.exifData);
        duplicateFinderExif.findDuplicates();
        originalFileList.setItems(FXCollections.observableList(parseOriginals(duplicateFinderExif.getOriginals())));
        duplicateFileList.getItems().clear();
    }


    private void updateImageView(String file) {
        imageView.setImage(new Image(new File(file).toURI().toString(), true));
        imageView.setFitWidth(559);
        imageView.setFitHeight(315);
        imageView.setSmooth(true);
        imageView.setPreserveRatio(true);
    }

    private List<AbstractExifDataForView> parseOriginals(List<ExifData> originals) {
        List<AbstractExifDataForView> ret = new ArrayList<>();
        for (ExifData original : originals) {
            if (duplicateFinderExif.getNumberOfDuplicates(original) > 0) {
                ret.add(new ExifDataForView(original));
            } else {
                ret.add(new ExifDataForViewUnique(original));
            }
        }
        return ret;
    }

    private List<AbstractExifDataForView> convertExifDataToView(List<ExifData> images) {
        List<AbstractExifDataForView> ret = new ArrayList<>();
        for (ExifData e : images) {
            ret.add(new ExifDataForView(e));
        }
        return ret;
    }

    /**
     * @return the selected ExifData or NULL if nothing is selected
     */
    private AbstractExifDataForView getSelectedFileInList() {
        if (duplicateFileListMarked != null) {
            return duplicateFileListMarked;
        } else {
            return originalFileListMarked;
        }
    }
}
