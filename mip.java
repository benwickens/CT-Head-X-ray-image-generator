// Ben Wickens - 988947 - All code in this project, apart from the framework provided is my own.
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class mip {
    private WritableImage image;
    private short[][][] data;
    private short min, max;

    public mip(WritableImage image, short[][][] data, short min, short max) {
        this.image = image;
        this.data = data;
        this.min = min;
        this.max = max;
    }

    //Maximum intensity projection implemented
    public WritableImage MIP(String mipValue) {
        //Get image dimensions, and declare loop variables
        int w = (int) image.getWidth(), h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();

        int jLoop, iLoop, kLoop;

        switch (mipValue) { //loops depend on the orientation.
            case "x":
                jLoop = 113;
                iLoop = 255;
                kLoop = 255;
                break;
            case "y":
                jLoop = 255;
                iLoop = 255;
                kLoop = 113;
                break;
            case "z":
                jLoop = 255;
                iLoop = 113;
                kLoop = 255;
                break;
            default:
                jLoop = 0;
                iLoop = 0;
                kLoop = 0;
        }
        float col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j = 0; j < jLoop; j++) { //height of image in the side view is 113
            for (i = 0; i < iLoop; i++) {
                float maxCol = 0;
                for (k = 0; k < kLoop; k++) {

                    //
                    switch (mipValue) {
                        case "x":
                            datum = data[j][i][k];
                            break;
                        case "y":
                            datum = data[k][j][i];

                            break;
                        case "z":
                            datum = data[i][k][j];
                            break;
                        default:
                            datum = data[j][i][k];
                            break;
                    }

                    col = (((float) datum - (float) min) / ((float) (max - min)));
                    if (maxCol < col) {
                        maxCol = col; //sets the colour to the new colour as its larger.
                    }
                }
                for (c = 0; c < 3; c++) {
                    //and now we are looping through the bgr components of the pixel
                    //set the colour component c of pixel (i,j)
                    if (mipValue.equals("x")) {
                        image_writer.setColor(i, j, Color.color(maxCol, maxCol, maxCol, 1.0));
                    } else {
                        image_writer.setColor(j, i, Color.color(maxCol, maxCol, maxCol, 1.0));
                    }
                } // colour loop
            } // column loop
        } // row loop
        return image;
    }

    //puts the result of MIP to a 2D array of colours to be used with the resizing functions.
    //same function, different output.
    public float[][] mipToArray(String mipValue) {
        //Get image dimensions, and declare loop variables
        float[][] colArray = new float[255][255];
        int w = (int) image.getWidth(), h = (int) image.getHeight(), i, j, c, k;

        int jLoop, iLoop, kLoop;

        switch (mipValue) {
            case "x":
                jLoop = 113;
                iLoop = 255;
                kLoop = 255;
                break;
            case "y":
                jLoop = 255;
                iLoop = 255;
                kLoop = 113;
                break;
            case "z":
                jLoop = 255;
                iLoop = 113;
                kLoop = 255;
                break;
            default:
                jLoop = 0;
                iLoop = 0;
                kLoop = 0;
        }
        float col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j = 0; j < jLoop; j++) { //height of image in the side view is 113
            for (i = 0; i < iLoop; i++) {
                float maxCol = 0;
                for (k = 0; k < kLoop; k++) {

                    switch (mipValue) {
                        case "x":
                            datum = data[j][i][k];
                            break;
                        case "y":
                            datum = data[k][j][i];

                            break;
                        case "z":
                            datum = data[i][k][j];
                            break;
                        default:
                            datum = data[j][i][k];
                            break;
                    }
                    col = (((float) datum - (float) min) / ((float) (max - min)));
                    if (maxCol < col) {
                        maxCol = col;
                    }
                }
                for (c = 0; c < 3; c++) {
                    //and now we are looping through the bgr components of the pixel
                    //set the colour component c of pixel (i,j)
                    if (mipValue.equals("x")) {
                        colArray[j][i] = maxCol;
                    } else {
                        colArray[i][j] = maxCol;
                    }
                } // colour loop
            } // column loop
        } // row loop
        return colArray;
    }
}
