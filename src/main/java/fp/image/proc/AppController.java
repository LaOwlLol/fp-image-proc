package fp.image.proc;

import fauxpas.entities.FilterableImage;
import fauxpas.filters.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.HashMap;

public class AppController {


    public FilterableImage main;
    private final HashMap<String, Filter> filterTypes;

    @FXML
    public ImageView viewport_imgv;

    @FXML
    public HBox filterBar_hbox;

    @FXML
    public Label filter_major_lbl;

    @FXML
    public Slider filter_major_sldr;

    @FXML
    public Label filter_minor_lbl;

    @FXML
    public Slider filter_minor_sldr;

    @FXML
    public ComboBox filter_cb;

    @FXML
    public Button filter_execute_btn;


    public AppController() {
        main = new FilterableImage(1080, 720);

        filterTypes = new HashMap<>();
        filterTypes.put("Canny Edges", new CannyFilter());
        filterTypes.put("Sobel Edges", new SobelFilter());
        filterTypes.put("Gaussian blur", new GaussianBlur(3, 1.0));
        filterTypes.put("Translucent", new TranslucentFilter());

    }

    public void initialize() {
        viewport_imgv.setImage(main.getImage());
        viewport_imgv.setPreserveRatio(true);

        filter_cb.getItems().addAll(
                "Canny Edges",
                "Sobel Edges",
                "Gaussian blur",
                "Translucent"
        );
        filter_cb.setValue("Translucent");

        filter_cb.setOnAction((event) -> {
            String selected = filter_cb.getSelectionModel().getSelectedItem().toString();

            if (selected.equals("Canny Edges")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(false);
                filterTypes.put("Cellular", new CannyFilter(filter_major_sldr.getValue(),
                        filter_minor_sldr.getValue()));
            }
            else if (selected.equals("Sobel Edges")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(true);
                filterTypes.put("Sobel Edges", new SobelFilter(filter_major_sldr.getValue()));
            }
            else if (selected.equals("Gaussian blur")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(true);
                filterTypes.put("Gaussian blur", new GaussianBlur( 3, filter_major_sldr.getValue() ) );
            }
            else if (selected.equals("Translucent")) {
                filter_major_sldr.setDisable(true);
                filter_minor_sldr.setDisable(true);
            }
        });

        filter_major_sldr.valueProperty().addListener((ov, old_val, new_val) -> {
            String selected = filter_cb.getSelectionModel().getSelectedItem().toString();

            if (selected.equals("Canny Edges")) {
                filterTypes.put("Cellular", new CannyFilter( Double.valueOf(new_val.toString()),
                        filter_minor_sldr.getValue()));
            }
            else if (selected.equals("Sobel Edges")) {
                filterTypes.put("Sobel Edges", new SobelFilter(Double.valueOf(new_val.toString())));
            }
            else if (selected.equals("Gaussian blur")) {
                filterTypes.put("Gaussian blur", new GaussianBlur( 3, Double.valueOf(new_val.toString()) ) );
            }
        });

        filter_minor_sldr.valueProperty().addListener((ov, old_val, new_val) -> {
            String selected = filter_cb.getSelectionModel().getSelectedItem().toString();

            if (selected.equals("Canny Edges")) {
                filterTypes.put("Cellular", new CannyFilter(filter_major_sldr.getValue(),
                        Double.valueOf(new_val.toString())));
            }
        });

        filter_execute_btn.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                filterBar_hbox.setDisable(true);
                //lastImage = main.getImage();
                //last.setDisable(false);
                main.applyFilter( filterTypes.get(filter_cb.getSelectionModel().getSelectedItem()) );
                viewport_imgv.setImage(main.getImage());
                filterBar_hbox.setDisable(false);
            });
            process.start();
        });
    }
}
