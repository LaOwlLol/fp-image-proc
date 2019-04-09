package fp.image.proc;

import fauxpas.entities.FilterableImage;
import fauxpas.fastnoise.FastNoise;
import fauxpas.filters.Filter;
import fauxpas.filters.noise.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Slider;
import fauxpas.filters.*;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.apache.commons.io.FilenameUtils;


public class AppController {

    public FilterableImage main;
    public FilterableImage last;
    public FilterableImage buffer;
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

    @FXML
    public VBox imageControls_vbox;

    @FXML
    public Button cache_btn;

    @FXML
    public Button undo_btn;

    @FXML
    public Button save_btn;

    @FXML
    public Button load_btn;

    @FXML
    public HBox mixerBar_hbox;

    @FXML
    public ComboBox mix_f1_cb;

    @FXML
    public ComboBox mix_f2_cb;

    @FXML
    public ComboBox mix_type_cb;

    @FXML
    public Button mix_btn;


    public AppController() {
        main = new FilterableImage(1080, 720);
        last = new FilterableImage(main.getImage());
        buffer = new FilterableImage(1080, 720);

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
        filterTypes.put("Grayscale", new GrayscaleFilter());
        filterTypes.put("Translucent", new TranslucentFilter());
    }

    public void initialize() {
        viewport_imgv.setImage(main.getImage());
        viewport_imgv.setPreserveRatio(true);

        setupNoiseTab();
        setupFilterTab();
        setupMixerTab();
        setupImageControls();
    }

    private void setupMixerTab() {

        mix_f1_cb.getItems().addAll( "Main", "Cache", "Last" );
        mix_f1_cb.getItems().addAll(noiseTypes.keySet());
        mix_f1_cb.setValue("Main");

        mix_f2_cb.getItems().addAll( "Main", "Cache", "Last" );
        mix_f2_cb.getItems().addAll(noiseTypes.keySet());
        mix_f2_cb.setValue("Cache");

        mix_type_cb.getItems().addAll( "Sum", "Blend", "Reflection" );
        mix_type_cb.setValue("Blend");

        mix_btn.setOnMouseClicked((event -> {
            Thread process = new Thread(() -> {
                mixerBar_hbox.setDisable(true);

                Filter f1;
                Filter f2;

                if (mix_f1_cb.getSelectionModel().getSelectedItem().toString().equals("Main")) {
                    f1 = (image) -> main.getImage();
                }
                else if (mix_f1_cb.getSelectionModel().getSelectedItem().toString().equals("Cache")) {
                    f1 = (image) -> buffer.getImage();
                }
                else if (mix_f1_cb.getSelectionModel().getSelectedItem().toString().equals("Last")) {
                    f1 = (image) -> last.getImage();
                }
                else {
                    f1 = noiseTypes.get(mix_f1_cb.getSelectionModel().getSelectedItem().toString());
                }

                if (mix_f2_cb.getSelectionModel().getSelectedItem().toString().equals("Main")) {
                    f2 = (image) -> main.getImage();
                }
                else if (mix_f2_cb.getSelectionModel().getSelectedItem().toString().equals("Cache")) {
                    f2 = (image) -> buffer.getImage();
                }
                else if (mix_f2_cb.getSelectionModel().getSelectedItem().toString().equals("Last")) {
                    f2 = (image) -> last.getImage();
                }
                else {
                    f2 = noiseTypes.get(mix_f2_cb.getSelectionModel().getSelectedItem().toString());
                }

                if ( mix_type_cb.getSelectionModel().getSelectedItem().toString().equals("Sum") ) {
                    main.applyFilter( new SumFilter(0.5, 0.5).apply(f1, f2) );
                    viewport_imgv.setImage(main.getImage());
                }
                else if ( mix_type_cb.getSelectionModel().getSelectedItem().toString().equals("Blend") ) {
                    main.applyFilter( new BlendFilter().apply(f1, f2) );
                    viewport_imgv.setImage(main.getImage());
                }
                else if ( mix_type_cb.getSelectionModel().getSelectedItem().toString().equals("Reflection") ) {
                    main.applyFilter( new ReflectionFilter().apply(f1, f2) );
                    viewport_imgv.setImage(main.getImage());
                }

                mixerBar_hbox.setDisable(false);
            });
            process.start();
        }));

    }

    private void setupImageControls() {
        save_btn.setOnMouseClicked((event) -> {
            imageControls_vbox.setDisable(true);
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG Image","*.png"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("bitmap Image, ","*.bmp"));
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JPG Image","*.jpg"));
            File file =  fileChooser.showSaveDialog(save_btn.getScene().getWindow());
            if (file != null) {
                writeImageToFile(main.getImage(), file);
            }
            else {
                System.out.println("File selection for save image returned null!");
            }
            imageControls_vbox.setDisable(false);
        });

        load_btn.setOnMouseClicked((event) -> {
            imageControls_vbox.setDisable(true);
            last =  new FilterableImage(main.getImage());
            undo_btn.setDisable(false);
            FileChooser fileChooser = new FileChooser();
            File file =  fileChooser.showOpenDialog(load_btn.getScene().getWindow());
            if (file != null) {
                main.setImage(new Image(file.toURI().toString()));
                viewport_imgv.setImage(main.getImage());
            }
            imageControls_vbox.setDisable(false);
        });

        undo_btn.setOnMouseClicked((event -> {
            Thread process = new Thread(() -> {
                imageControls_vbox.setDisable(true);
                main.setImage(last.getImage());
                viewport_imgv.setImage(last.getImage());
                undo_btn.setDisable(true);
                imageControls_vbox.setDisable(false);
            });
            process.start();
        }));

        cache_btn.setOnMouseClicked((event) -> {
            Thread process = new Thread(() -> {
                imageControls_vbox.setDisable(true);
                buffer.setImage(main.getImage());
                imageControls_vbox.setDisable(false);
            });
            process.start();
        });
    }

    private void setupFilterTab() {
        filter_cb.getItems().addAll(
                "Canny Edges",
                "Sobel Edges",
                "Gaussian blur",
                "Smooth",
                "Grayscale",
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
            else if (selected.equals("Smooth")) {
                filter_major_sldr.setDisable(false);
                filter_minor_sldr.setDisable(true);
                filter_major_lbl.setDisable(false);
                filter_minor_lbl.setDisable(true);
                filter_major_lbl.setText("Power");
                filter_minor_lbl.setText("disabled");
                filterTypes.put("Smooth", new RedistributionFilter( filter_major_sldr.getValue() * 5.0 ) );
            }
            else if (selected.equals("Grayscale")) {
                filter_major_sldr.setDisable(true);
                filter_minor_sldr.setDisable(true);
                filter_major_lbl.setDisable(true);
                filter_minor_lbl.setDisable(true);
                filter_major_lbl.setText("disabled");
                filter_minor_lbl.setText("disabled");
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
            else if (selected.equals("Smooth")) {
                filterTypes.put("Smooth", new RedistributionFilter( filter_major_sldr.getValue() * 5.0 ) );
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
                last =  new FilterableImage(main.getImage());
                undo_btn.setDisable(false);
                main.applyFilter( filterTypes.get(filter_cb.getSelectionModel().getSelectedItem()) );
                viewport_imgv.setImage(main.getImage());
                filterBar_hbox.setDisable(false);
            });
            process.start();
        });
    }

    private void setupNoiseTab() {
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
                last =  new FilterableImage(main.getImage());
                undo_btn.setDisable(false);
                main.applyFilter( noiseTypes.get(noise_cb.getSelectionModel().getSelectedItem()) );
                viewport_imgv.setImage(main.getImage());
                noiseBar_hbox.setDisable(false);
            });
            process.start();
        });
    }

    private void writeImageToFile(Image img, File file) {
        String extension = FilenameUtils.getExtension(file.getPath());

        try {
            ImageIO.write(SwingFXUtils.fromFXImage(img, null), extension, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
