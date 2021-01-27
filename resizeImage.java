// Ben Wickens - 988947 - All code in this project, apart from the framework provided is my own.
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

//Implementation of bilinear interpolation and nearest neighbour.
public class resizeImage {
    private short[][][] imageData;
    private short min, max;
    private WritableImage medical_image;

    public resizeImage(short[][][] data, WritableImage image, short min, short max) {
        this.imageData = data;
        this.medical_image = image;
        this.min = min;
        this.max = max;
    }

    //bilinear interopolation resizing.
    public void bilinear(int oldWidth, int oldHeight, int slideVal, String sliderUsed, float xSliderVal,
                         float ySliderVal, boolean mipEnabled, String mipUsed) {
        PixelWriter image_writer = medical_image.getPixelWriter();
        float resizeFacX = xSliderVal / 100; //calculates the resize factor
        float resizeFacY = ySliderVal / 100;
        int xAxis, yAxis;

        final int maxHeight = 480;
        final int maxWidth = 460;

        int newHeight = (int) (maxHeight * resizeFacY);
        int newWidth = (int) (maxWidth * resizeFacX);

        if (newHeight > maxHeight) { //makes sure it doesn't go out of bounds.
            newHeight = maxHeight;
        }

        if (newWidth > maxWidth) {
            newWidth = maxWidth;
        }

        float[][] datum = null;
        if (!mipEnabled) {
            datum = readImage(slideVal, sliderUsed); //reads data using the dataset
        } else { //if MIP is used the data being resized is put to the mip values.
            mip mipImage = new mip(medical_image, imageData, min, max);
            datum = mipImage.mipToArray(mipUsed);
        }


        for (int j = 0; j < newHeight - 1; j++) {
            for (int i = 0; i < newWidth - 1; i++) {

                //calculates the exact point for the colour to be calculated
                float yTarget = ((((float) j) / (float) newHeight) * ((float) oldHeight));
                float xTarget = ((((float) i) / (float) newWidth) * ((float) oldWidth));

                xAxis = (int) xTarget; //rounds down to the left value, 2.7 = 2.
                yAxis = (int) yTarget;
                //two lines are interpolated. xA,xB are the top lines.
                float xA = datum[yAxis][xAxis];//
                float xB = datum[yAxis][xAxis + 1];
                //this the bottom line.
                float yA = datum[yAxis + 1][xAxis];
                float yB = datum[yAxis + 1][xAxis + 1];

                //find colour between lines
                float xDiff = xTarget - (float) xAxis; //between 0-1 (ratio)
                float yDiff = yTarget - (float) yAxis;
                float topPos = xA + (xB - xA) * (xDiff); //linear interpolation for top line.
                float botPos = yA + (yB - yA) * (xDiff);//bottom line
                float col = topPos + (botPos - topPos) * yDiff; //linear interopolation between
                //the calculated values.

                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
            }
        }
    }

    //nearest neighbour resizing. Similar in parts to bilinear.
    public void resize(int oldWidth, int oldHeight, int slideVal, String sliderUsed, float xSliderVal,
                       float ySliderVal, boolean mipEnabled, String mipUsed) {
        PixelWriter image_writer = medical_image.getPixelWriter();

        float resizeFacX = xSliderVal / 100;
        float resizeFacY = ySliderVal / 100;
        final int maxHeight = 480;
        final int maxWidth = 460;

        int newHeight = (int) (maxHeight * resizeFacY);
        int newWidth = (int) (maxWidth * resizeFacX);

        if (newHeight > maxHeight) { //makes sure it doesn't go out of bounds.
            newHeight = maxHeight;
        }

        if (newWidth > maxWidth) {
            newWidth = maxWidth;
        }
        float[][] datum = null;

        if (!mipEnabled) {
            datum = readImage(slideVal, sliderUsed);//reads data using the dataset
        } else { //if MIP is used the data being resized is put to the mip values..

            mip mipImage = new mip(medical_image, imageData, min, max);
            datum = mipImage.mipToArray(mipUsed);
        }

        for (int j = 0; j < newHeight - 1; j++) {
            for (int i = 0; i < newWidth - 1; i++) {
                //Calculates the location of the new pixel.
                float y = j * (float) oldHeight / (float) newHeight;
                float x = i * (float) oldWidth / (float) newWidth;
                int roundY = Math.round(y); // Nearest y
                int roundX = Math.round(x); // Nearest x
                float col = datum[roundY][roundX]; //gets the colour from the nearest values
                image_writer.setColor(i, j, Color.color(col, col, col, 1.0));//writes the colour.
            }
        }
    }

    //modified nearest neighbour to output an array of float colours, used for resizing thumbnails.
    public float[][] thumbNailResize(int oldWidth, int oldHeight, int slideVal, String sliderUsed, int thumbnailSize) {
        float[][] newImage = new float[thumbnailSize][thumbnailSize];
        float[][] datum = null;

        datum = readImage(slideVal, sliderUsed);

        float scaleX = ((float) thumbnailSize / (float) oldWidth);
        float scaleY = ((float) thumbnailSize / (float) oldHeight);

        for (int j = 0; j < thumbnailSize; j++) {
            for (int i = 0; i < thumbnailSize; i++) {
                int y = (int) (j / scaleY);
                int x = (int) (i / scaleX);
                float col = datum[y][x];
                newImage[j][i] = col;
            }
        }
        return newImage;
    }

    //this converts the cthead 3d array to a 2D array of colours. USed to write images with.
    private float[][] readImage(int slideVal, String sliderUsed) {
        //Get image dimensions, and declare loop variables
        int w = (int) medical_image.getWidth(), h = (int) medical_image.getHeight(), i, j;
        int jLoop = 0;
        int iLoop = 0;
        float[][] displayedImage = new float[255][255];
        float col;
        short datum;
        //loops change depending on width and height of the image.
        switch (sliderUsed) {
            case "z":
            case "x":
                jLoop = 113;
                iLoop = 255;
                break;
            case "y":
                jLoop = 255;
                iLoop = 255;
                break;
        }
        //variable slice in the 3d at depends on orientation required.
        for (j = 0; j < jLoop; j++) {
            for (i = 0; i < iLoop; i++) {
                datum = 0;
                switch (sliderUsed) {
                    case "z":
                        datum = imageData[j][slideVal][i];
                        break;
                    case "x":
                        datum = imageData[j][i][slideVal];
                        break;
                    case "y":
                        datum = imageData[slideVal][j][i];
                        break;
                }
                col = (((float) datum - (float) min) / ((float) (max - min)));
                displayedImage[j][i] = col;
            } // column loop
        } // row loop
        return displayedImage;
    }
}

