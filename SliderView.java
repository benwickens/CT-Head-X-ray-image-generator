// Ben Wickens - 988947 - All code in this project, apart from the framework provided is my own.
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

public class SliderView {
    private WritableImage image;
    private short[][][] data;
    private short min, max;

    public SliderView(WritableImage image, short[][][] data, short min, short max) {
        this.image = image;
        this.data = data;
        this.min = min;
        this.max = max;
    }

    //The 3 slider functions follow the same format. The slideVal is the slice that is being looked
    //at in the array.
    public WritableImage SliderX(int slideVal) {
        //Get image dimensions, and declare loop variables
        int w = (int) image.getWidth(), h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();

        float col;
        short datum;
        //Shows how to loop through each pixel and colour
        //Try to always use j for loops in y, and i for loops in x
        //as this makes the code more readable
        for (j = 0; j < 113; j++) {
            for (i = 0; i < 255; i++) {
                datum = data[j][i][slideVal]; //slideVal placement in the data depends on orientation.
                col = (((float) datum - (float) min) / ((float) (max - min)));
                for (c = 0; c < 3; c++) {
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                } // colour loop
            } // column loop
        } // row loop
        return image;
    }

    public WritableImage SliderY(int slideVal) {
        //Get image dimensions, and declare loop  variables
        int w = (int) image.getWidth(), h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();
        float col;
        short datum;
        for (j = 0; j < 255; j++) {
            for (i = 0; i < 255; i++) {
                datum = data[slideVal][j][i];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                for (c = 0; c < 3; c++) {
                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));
                } // colour loop
            } // column loop
        } // row loop
        return image;
    }

    public WritableImage SliderZ(int slideVal) {
        //Get image dimensions, and declare loop variables
        int w = (int) image.getWidth(), h = (int) image.getHeight(), i, j, c, k;
        PixelWriter image_writer = image.getPixelWriter();
        float col;
        short datum;
        for (j = 0; j < 113; j++) {
            for (i = 0; i < 255; i++) {

                datum = data[j][slideVal][i];
                col = (((float) datum - (float) min) / ((float) (max - min)));
                for (c = 0; c < 3; c++) {

                    image_writer.setColor(i, j, Color.color(col, col, col, 1.0));

                } // colour loop
            } // column loop
        } // row loop
        return image;
    }
}
