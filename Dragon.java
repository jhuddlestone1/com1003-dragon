/*
*   COM1003 Java Programming
*   Autumn Semester 2018-19
*   Programming Assignment 2
*
*   Jamie Huddlestone
*   170164274
*
*   Dragon.java
*/

// Requires EasyGraphics.waitSeconds (from versions later than November 2018):
import sheffield.*;

public class Dragon {

    // Custom DragonImage type; no method that returns this type should be public
    private enum DragonImage {DRAGON, FLAME, BACKGROUND};

    // Image parameters
    public static final int CANVAS_WIDTH = 188;
    public static final int CANVAS_HEIGHT = 130;
    public static final int IMAGE_SCALE = 2;

    // Dragon parameters
    public EasyGraphics canvas;     // Exposed to allow easy animation
    private DragonImage[][] imageData;
    private int[] dragonColor = {0, 255, 0};        // red, green, blue
    private int[] flameColor = {255, 0, 0};         // red, green, blue
    private int[] backgroundColor = {0, 0, 0};      // red, green, blue
    public boolean isWelsh = false;

    // Constructor functions
    public Dragon(String filename) {
        imageData = getImageData(filename);
    }

    public Dragon(String filename, boolean welsh) {
        this(filename);
        isWelsh = welsh;
        if (welsh) {
            setDragonColor(255, 0, 0);
            setFlameColor(255, 255, 0);
            setBackgroundColor(0, 160, 0);
        }
    }

    // Getter methods
    public int[] getDragonColor() {
        return dragonColor;
    }

    public int[] getFlameColor() {
        return flameColor;
    }

    public int[] getBackgroundColor() {
        return backgroundColor;
    }

    // Setter methods
    public void setDragonColor(int r, int g, int b) {
        dragonColor[0] = r;
        dragonColor[1] = g;
        dragonColor[2] = b;
    }

    public void setFlameColor(int r, int g, int b) {
        flameColor[0] = r;
        flameColor[1] = g;
        flameColor[2] = b;
    }

    public void setBackgroundColor(int r, int g, int b) {
        backgroundColor[0] = r;
        backgroundColor[1] = g;
        backgroundColor[2] = b;
    }

    // Opens an EasyGraphics window for Dragon object, sized according to its class constants
    public void makeCanvas() {
        canvas = new EasyGraphics(CANVAS_WIDTH * IMAGE_SCALE, CANVAS_HEIGHT * IMAGE_SCALE);
    }

    // Draws to Dragon object's canvas from array of image data of type DragonImage
    public void draw(boolean flames) {

        // Create an EasyGraphics window for the Dragon object if one does not already exist
        if (canvas == null)
            makeCanvas();

        // Set color and fill scaled rectangle for each pixel represented in image array
        for (int x = 0; x < imageData.length; x++) {
            for (int y = 0; y < imageData[x].length; y++) {

                switch (imageData[x][y]) {

                    case DRAGON:
                        canvas.setColor(
                            dragonColor[0],
                            dragonColor[1],
                            dragonColor[2]
                        );
                        break;

                    case FLAME:
                        if (flames) {
                            canvas.setColor(
                                flameColor[0],
                                flameColor[1],
                                flameColor[2]
                            );
                            break;
                        }   // Fall through to BACKGROUND if no flames

                    case BACKGROUND:
                        if (isWelsh && y > imageData[x].length / 2.5) {
                            canvas.setColor(255, 255, 255);
                            break;
                        }   // Fall through to default if not (roughly!) top half of Welsh flag

                    default:
                        canvas.setColor(
                            backgroundColor[0],
                            backgroundColor[1],
                            backgroundColor[2]
                        );
                }

                canvas.fillRectangle(
                    x * IMAGE_SCALE,    // x-coords
                    y * IMAGE_SCALE,    // y-coords
                    IMAGE_SCALE,        // width
                    IMAGE_SCALE         // height
                );
            }
        }
    }

    public void draw() {
        draw(false);
    }

    // Pause operations on a Dragon canvas for a (ranged) interval
    public void waitFor(int min, int max) {

        // Create an EasyGraphics window for the Dragon object if one does not already exist
        if (canvas == null)
            makeCanvas();

        int duration = (min == max) ? min : getRandomIntBetween(min, max);
        canvas.waitSeconds(duration);
    }

    public void waitFor(int seconds) {
        waitFor(seconds, seconds);
    }

    // Draws a Dragon with flames, then redraws without flames after a (ranged) interval
    public void breatheFireFor(int min, int max) {

        // Draw dragon with flame
        int duration = (min == max) ? min : getRandomIntBetween(min, max);
        if (isWelsh) {

            // If the dragon is Welsh, make its flame colour flicker every second
            for (int frame = 0; frame < duration; frame++) {
                setFlameColor(
                                        255,        // red
                    getRandomIntBetween(192, 255),  // green
                                        0           // blue
                );
                draw(true);
                canvas.waitSeconds(1);
            }

        } else {
            draw(true);
            canvas.waitSeconds(duration);
        }

        // Redraw dragon without flame
        draw(false);
    }

    public void breatheFireFor(int seconds) {
        breatheFireFor(seconds, seconds);
    }

    // Returns a random int between minimum and maximum int values (inclusive)
    private static int getRandomIntBetween(int min, int max) {
        return (int) Math.floor(Math.random() * (max - min + 1)) + min;
    }

    // Returns an image array of pixel values of type DragonImage from named text file
    private static DragonImage[][] getImageData(String filename) {

        // Get file object
        EasyReader file = new EasyReader(filename);

        // Initialise and fill image array from file data
        DragonImage[][] data = new DragonImage[CANVAS_WIDTH][CANVAS_HEIGHT];

        for (int x = 0; x < CANVAS_WIDTH; x++) {
            for (int y = 0; y < CANVAS_HEIGHT; y++) {

                int charValue = (int) file.readChar();

                // Tests for file decoding
                // dragon:      charValue is even
                // flame:       charValue is odd and divisible by 3
                // background:  charValue is odd and not divisible by 3
                boolean isDragonPixel = (charValue % 2) == 0;
                boolean isFlamePixel = !isDragonPixel && (charValue % 3) == 0;

                data[x][y] =
                    isDragonPixel ? DragonImage.DRAGON :
                    isFlamePixel ? DragonImage.FLAME :
                    DragonImage.BACKGROUND;
            }
        }

        return data;
    }

    // Main function
    public static void main(String[] args) {

        // Stretch goal: If any arguments provided, assume user wants to see a Welsh dragon!
        boolean welshness = args.length > 0;

        // Create a Dragon object
        Dragon myDragon = new Dragon("dragon.txt", welshness);

        // Draw image of dragon on loop (randomised wait between each iteration)
        final int NUMBER_OF_FLAMES = 3;
        final int MIN_DURATION_OF_FLAMES = 1;    // seconds
        final int MAX_DURATION_OF_FLAMES = 5;    // seconds
        final int MIN_WAIT_BETWEEN_FLAMES = 1;   // seconds
        final int MAX_WAIT_BETWEEN_FLAMES = 3;   // seconds

        myDragon.draw();

        for (int i = 0; i < NUMBER_OF_FLAMES; i++) {

            myDragon.waitFor(MIN_WAIT_BETWEEN_FLAMES, MAX_WAIT_BETWEEN_FLAMES);
            myDragon.breatheFireFor(MIN_DURATION_OF_FLAMES, MAX_DURATION_OF_FLAMES);
        }
    }
}