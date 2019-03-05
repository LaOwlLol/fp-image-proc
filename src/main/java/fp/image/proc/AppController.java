package fp.image.proc;

import fauxpas.entities.FilterableImage;
import fauxpas.filters.Filter;
import fauxpas.filters.noise.*;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

import java.util.HashMap;
import java.util.function.Function;

public class AppController {

    private final HashMap<String, Filter> noiseTypes;
    public FilterableImage main;

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

    }

}
