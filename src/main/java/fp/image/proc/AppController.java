package fp.image.proc;

import fauxpas.entities.FilterableImage;
import fauxpas.filters.Filter;
import fauxpas.filters.noise.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import fauxpas.filters.*;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.HashMap;


public class AppController {

    public FilterableImage main;
    private final HashMap<String, Filter> noiseTypes;
    private final HashMap<String, Filter> filterTypes;

    @FXML
    public ImageView viewport_imgv;

    @FXML

    public HBox noiseBar_hbox;

    @FXML
    public Slider xFreq_sldr;

    @FXML
    public Slider yFreq_sldr;

    @FXML
    public ComboBox<String> noise_cb;

    @FXML
    public Button generateNoise_btn;

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


        noiseTypes = new HashMap<>();
        noiseTypes.put("Cellular", new CellularNoise());
        noiseTypes.put("Cubic", new CubicNoise());
        noiseTypes.put("Perlin", new PerlinNoise());
        noiseTypes.put("SimplexFractal", new SimplexFractalNoise());
        noiseTypes.put("Simplex", new SimplexNoise());
        noiseTypes.put("Value", new ValueNoise());
        noiseTypes.put("White", new WhiteNoise());

        filterTypes = new HashMap<>();
        filterTypes.put("Canny Edges", new CannyFilter());
        filterTypes.put("Sobel Edges", new SobelFilter());
        filterTypes.put("Gaussian blur", new GaussianBlur(3, 1.0));
        filterTypes.put("Translucent", new TranslucentFilter());

    }

    public void initialize() {
        viewport_imgv.setImage(main.getImage());
        viewport_imgv.setPreserveRatio(true);

        noise_cb.getItems().addAll(
            "Cellular",
            "Cubic",
            "Perlin",
            "SimplexFractal",
            "Simplex",
            "Value",
            "White"
        );
        noise_cb.setValue("Cellular");

        //TODO: could be better by not generating new filters on every user input here and on sliders.
        noise_cb.setOnAction((event) -> {
            noiseTypes.put("Cellular", new CellularNoise(xFreq_sldr.valueProperty().floatValue(),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Cubic", new CubicNoise(xFreq_sldr.valueProperty().floatValue(),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Perlin", new PerlinNoise(xFreq_sldr.valueProperty().floatValue(),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("SimplexFractal", new SimplexFractalNoise(xFreq_sldr.valueProperty().floatValue(),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Simplex", new SimplexNoise(xFreq_sldr.valueProperty().floatValue(),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Value", new ValueNoise(xFreq_sldr.valueProperty().floatValue(),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("White", new WhiteNoise());
        });

        xFreq_sldr.valueProperty().addListener((ov, old_val, new_val) -> {
            noiseTypes.put("Cellular", new CellularNoise(Float.valueOf(new_val.toString()),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Cubic", new CubicNoise(Float.valueOf(new_val.toString()),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Perlin", new PerlinNoise(Float.valueOf(new_val.toString()),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("SimplexFractal", new SimplexFractalNoise(Float.valueOf(new_val.toString()),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Simplex", new SimplexNoise(Float.valueOf(new_val.toString()),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("Value", new ValueNoise(Float.valueOf(new_val.toString()),
                    yFreq_sldr.valueProperty().floatValue()));
            noiseTypes.put("White", new WhiteNoise());
        });

        yFreq_sldr.valueProperty().addListener((ov, old_val, new_val) -> {
            noiseTypes.put("Cellular", new CellularNoise(xFreq_sldr.valueProperty().floatValue(),
                    Float.valueOf(new_val.toString())));
            noiseTypes.put("Cubic", new CubicNoise(xFreq_sldr.valueProperty().floatValue(),
                    Float.valueOf(new_val.toString())));
            noiseTypes.put("Perlin", new PerlinNoise(xFreq_sldr.valueProperty().floatValue(),
                    Float.valueOf(new_val.toString())));
            noiseTypes.put("SimplexFractal", new SimplexFractalNoise(xFreq_sldr.valueProperty().floatValue(),
                    Float.valueOf(new_val.toString())));
            noiseTypes.put("Simplex", new SimplexNoise(xFreq_sldr.valueProperty().floatValue(),
                    Float.valueOf(new_val.toString())));
            noiseTypes.put("Value", new ValueNoise(xFreq_sldr.valueProperty().floatValue(),
                    Float.valueOf(new_val.toString())));
            noiseTypes.put("White", new WhiteNoise());
        });

        generateNoise_btn.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                noiseBar_hbox.setDisable(true);
                //lastImage = main.getImage();
                //last.setDisable(false);
                main.applyFilter( noiseTypes.get(noise_cb.getSelectionModel().getSelectedItem()) );
                viewport_imgv.setImage(main.getImage());
                noiseBar_hbox.setDisable(false);
            });
            process.start();
        });

        filter_cb.getItems().addAll(
                "Canny Edges",
                "Sobel Edges",
                "Gaussian blur",
                "Translucent"
        );
        filter_cb.setValue("Translucent");

        filter_major_sldr.setDisable(true);
        filter_minor_sldr.setDisable(true);
        filter_major_lbl.setDisable(true);
        filter_minor_lbl.setDisable(true);
        filter_major_lbl.setText("disabled");
        filter_minor_lbl.setText("disabled");
        filter_cb.setOnAction((event) -> {
            String selected = filter_cb.getSelectionModel().getSelectedItem().toString();

            if (selected.equals("Canny Edges")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(false);
                filter_major_lbl.setDisable(false);
                filter_minor_lbl.setDisable(false);
                filter_major_lbl.setText("Lower Threshold");
                filter_minor_lbl.setText("Upper Threshold");
                filterTypes.put("Cellular", new CannyFilter(filter_major_sldr.getValue(),
                        filter_minor_sldr.getValue()
                ));
            }
            else if (selected.equals("Sobel Edges")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(true);
                filter_major_lbl.setDisable(false);
                filter_minor_lbl.setDisable(true);
                filter_major_lbl.setText("Threshold");
                filter_minor_lbl.setText("disabled");
                filterTypes.put("Sobel Edges", new SobelFilter(filter_major_sldr.getValue()));
            }
            else if (selected.equals("Gaussian blur")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(true);
                filter_major_lbl.setDisable(false);
                filter_minor_lbl.setDisable(true);
                filter_major_lbl.setText("Std. Deviation");
                filter_minor_lbl.setText("disabled");
                filterTypes.put("Gaussian blur", new GaussianBlur( 3, filter_major_sldr.getValue() ) );
            }
            else if (selected.equals("Translucent")) {
                filter_major_sldr.setDisable(true);
                filter_minor_sldr.setDisable(true);
                filter_major_lbl.setDisable(true);
                filter_minor_lbl.setDisable(true);
                filter_major_lbl.setText("disabled");
                filter_minor_lbl.setText("disabled");
            }
        });

        filter_major_sldr.valueProperty().addListener((ov, old_val, new_val) -> {
            String selected = filter_cb.getSelectionModel().getSelectedItem().toString();

            if (selected.equals("Canny Edges")) {
                filterTypes.put("Cellular", new CannyFilter(Double.valueOf(new_val.toString()),
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
                        Double.valueOf(new_val.toString())
                ));
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
