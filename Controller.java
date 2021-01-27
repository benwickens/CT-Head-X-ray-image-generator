// Ben Wickens - 988947 - All code in this project, apart from the framework provided is my own.
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

//Controller for the main page, calls functions, handles inputs and displays the ct head data implementing resizing and thumbnail view.
public class Controller implements Initializable {
    private @FXML
    Button xButton, yButton, zButton, thumbnailViewer;
    private @FXML
    ImageView imageViewer;
    private @FXML
    Slider xSlider, ySlider, zSlider, Xresize, Yresize;
    private short cthead[][][];
    private short min, max;
    private final int height = 256;
    private final int width = 256;
    private WritableImage medical_image;
    private mip mipBut;
    private String lastSlider = "";
    private float resizeSlideX, resizeSlideY = 0;
    private @FXML
    RadioButton toggleResize;
    private boolean resizeType = false;
    private resizeImage resizer;
    private @FXML
    MenuButton SelectionView;
    private @FXML
    MenuItem TopView, SideView, FrontView;
    private final int thumbnailSize = 100;
    private @FXML
    ScrollPane ScrollPaneBox;
    private @FXML
    HBox HBoxPane;
    int sliderValue;
    boolean mipPressed = false;
    String mipValue = "";


    @Override
    //initialises data required and sets up event handlers for sliders and thumbnail selection.
    public void initialize(URL location, ResourceBundle resources) {
        ReadFile reader = new ReadFile();
        try {
            reader.ReadData("src/CTHead");
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.cthead = reader.getImage();
        this.max = reader.getMax();
        this.min = reader.getMin();
        this.medical_image = new WritableImage(460, 480);
        imageViewer.setImage(medical_image);
        mipBut = new mip(medical_image, cthead, min, max);
        SliderView slideView = new SliderView(this.medical_image, this.cthead, this.min, max);

        //IMAGE RESIZE
        resizer = new resizeImage(cthead, medical_image, min, max);

        //sliders all follow the same pattern, this paticular slider goes through the data from
        //the side, y through the top and z from the front to the back.
        xSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        mipPressed = false; //This is how the program tells if its displaying the
                        // mip pictures or not. Only true when mip pressed
                        lastSlider = "x"; //So functions know how to traverse the array as x,y,z
                        //all have different requirements due to difference in width and height.
                        sliderValue = newValue.intValue();
                        resetDisplay(); //Clears the imageview
                        if ((resizeSlideX == 0) && (resizeSlideY == 0)) { //if the resize sliders
                            //are at 0 it displays the original image.
                            slideView.SliderX(sliderValue);
                        } else {
                            resizeFunc(); //if resize sliders have a value it allows you to scroll
                            //the dataset using the resize you used.
                        }
                    }
                });

        ySlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        mipPressed = false;
                        lastSlider = "y";
                        sliderValue = newValue.intValue();
                        resetDisplay();
                        if ((resizeSlideX == 0) && (resizeSlideY == 0)) {
                            slideView.SliderY(sliderValue);
                        } else {
                            resizeFunc();
                        }
                    }
                });

        zSlider.valueProperty().addListener(
                new ChangeListener<Number>() {
                    public void changed(ObservableValue<? extends Number>
                                                observable, Number oldValue, Number newValue) {
                        mipPressed = false;
                        lastSlider = "z";
                        sliderValue = newValue.intValue();
                        resetDisplay();
                        if ((resizeSlideX == 0) && (resizeSlideY == 0)) {
                            slideView.SliderZ(sliderValue);
                        } else {
                            resizeFunc();
                        }

                    }
                });

        //uses a slider to call the resize function using the private global values of x and y to
        // which the new image is to be resized. Goes up to 100% of the image viewer.
        Xresize.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number>
                                        observable, Number oldValue, Number newValue) {
                resetDisplay();
                resizeSlideX = newValue.intValue(); //assigns to the private global to be used elsewhere.
                resizeFunc();
            }

        });

        Yresize.valueProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number>
                                        observable, Number oldValue, Number newValue) {
                resetDisplay();
                resizeSlideY = newValue.intValue();
                resizeFunc();
            }
        });

        //responsible for getting the mouse location inside of the hBox. As the thumbnails are
        // 100x100 px dividing the resulting x coordinate allows the target slice to be calculated.
        javafx.event.EventHandler<MouseEvent> mouseLocation = new javafx.event.EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                resetDisplay();
                int slice = (int) event.getX() / 100; //Gets the slice number.
                mipPressed = false;
                sliderValue = slice;
                //enable thumbnail to be resized
                switch (lastSlider) {
                    case "x":
                        slideView.SliderX(slice);
                        break;

                    case "y":
                        slideView.SliderY((slice));
                        break;

                    case "z":
                        slideView.SliderZ(slice);
                        break;
                }
            }
        };

        HBoxPane.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseLocation);

    }

    /*The 3 buttons follow the same format. Sets the mipPressed to true meaning it can be resized*/
    @FXML
    private void handleButtonClick_X() {
        resetDisplay();
        mipPressed = true;
        mipValue = "x";
        imageViewer.setImage(mipBut.MIP(mipValue));
    }

    @FXML
    private void handleButtonClick_Y() {
        resetDisplay();
        mipPressed = true;
        mipValue = "y";
        imageViewer.setImage(mipBut.MIP(mipValue));
    }

    @FXML
    private void handleButtonClick_Z() {
        resetDisplay();
        mipPressed = true;
        mipValue = "z";
        imageViewer.setImage(mipBut.MIP(mipValue));
    }

    //allows the toggle between nearest neighbour and bilinear interpolation.
    @FXML
    private void onClickResize() {
        resetDisplay();
        resizeType = !resizeType;
        resizeFunc();
    }

    //Loads the thumbnails for the selected option. All 3 follow the same format
    @FXML
    private void TopClick() {
        HBoxPane.getChildren().clear(); //clears the hBox in case its already been used.
        lastSlider = "y"; //So other functions know what direction is being used, used to display
        //the image later on in the image viewer if they click on an image.
        for (int i = 0; i < 113; i++) { /*created an array of thumb nails, resized to the value of the
         private global variable*/
            WritableImage thumbNail = new WritableImage(thumbnailSize, thumbnailSize);
            resizeImage topBox = new resizeImage(cthead, thumbNail, this.min, this.max);
            //adds to the array a resized thumbnail using a modified nearest neighbour resizing.
            // currImage is an array of colours stored as floats.
            float[][] currImage = topBox.thumbNailResize(255, 255, i, "y", thumbnailSize);
            ImageView viewThumb = new ImageView(createThumbNail(currImage));
            HBoxPane.getChildren().add(viewThumb); //adds thumbnail to the display hbox.
        }
    }

    @FXML
    private void SideClick() {
        lastSlider = "x";
        HBoxPane.getChildren().clear();
        for (int i = 0; i < 255; i++) {
            WritableImage thumbNail = new WritableImage(thumbnailSize, thumbnailSize);
            resizeImage topBox = new resizeImage(cthead, thumbNail, this.min, this.max);

            float[][] currImage = topBox.thumbNailResize(255, 113, i, "x", thumbnailSize);
            ImageView viewThumb = new ImageView(createThumbNail(currImage));
            HBoxPane.getChildren().add(viewThumb);
        }
    }

    @FXML
    private void FrontClick() {
        lastSlider = "z";
        HBoxPane.getChildren().clear();
        for (int i = 0; i < 255; i++) {
            WritableImage thumbNail = new WritableImage(thumbnailSize, thumbnailSize);
            resizeImage topBox = new resizeImage(cthead, thumbNail, this.min, this.max);
            float[][] currImage = topBox.thumbNailResize(255, 113, i, "z", thumbnailSize);
            ImageView viewThumb = new ImageView(createThumbNail(currImage));
            HBoxPane.getChildren().add(viewThumb);
        }
    }

    //creates the writeable image to the displayed using a 2D array of colour values stored as floats.
    private WritableImage createThumbNail(float[][] thumbNailData) {
        WritableImage newImage = new WritableImage(thumbnailSize, thumbnailSize);
        PixelWriter image_writer = newImage.getPixelWriter();
        int j = thumbNailData.length;
        int i = thumbNailData[0].length;
        for (j = 0; j < thumbnailSize; j++) {
            for (i = 0; i < thumbnailSize; i++) {
                float col = thumbNailData[j][i]; //gets the colour a the position in the array
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0)); /*writes colour at the
                 position.*/
            }
        }
        return newImage;
    }

    //Responsible for calling the resize function.
    private void resizeFunc() {
        if (!(lastSlider.equals(""))) {/*ensures resizing isn't done to no image allows for mip to
         be resized if clicked.*/
            if (mipPressed) { //if true, resizes the mip image.
                if (!resizeType) { //bilinear resizing is used if true, otherwise nearest neighbour
                    if (mipValue.equals("x") || (mipValue.equals("z"))) { /*image dimensions vary
                    depending on the way you're traversing the image.*/
                        resizer.resize(255, 113, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
                    } else if (mipValue.equals("y")) {
                        resizer.resize(255, 255, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
                    }
                } else {
                    if ((mipValue.equals("x")) || (mipValue.equals("z"))) {
                        resizer.bilinear(255, 113, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
                    } else if (mipValue.equals("y")) {
                        resizer.bilinear(255, 255, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
                    }
                }
            }
        }

        if ((resizeType) && (!mipPressed)) {
            if ((lastSlider.equals("x")) || (lastSlider.equals("z"))) {
                resizer.bilinear(255, 113, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
            } else if (lastSlider.equals("y")) {
                resizer.bilinear(255, 255, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
            }
        } else if (!mipPressed) {
            if ((lastSlider.equals("x")) || (lastSlider.equals("z"))) {
                resizer.resize(255, 113, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
            } else if (lastSlider.equals("y")) {
                resizer.resize(255, 255, sliderValue, lastSlider, resizeSlideX, resizeSlideY, mipPressed, mipValue);
            }
        }
    }

    //used to clear the display to the colour of the background.
    private void resetDisplay() {
        int w = (int) medical_image.getWidth(), h = (int) medical_image.getHeight(), i, j;
        PixelWriter image_writer = medical_image.getPixelWriter();
        for (j = 0; j < h; j++) {
            for (i = 0; i < w; i++) {
                image_writer.setColor(i, j, Color.color(0.769, 0.749, 0.745, 1.0));
            }
        }
    }
}
