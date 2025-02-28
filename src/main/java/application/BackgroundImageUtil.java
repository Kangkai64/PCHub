package application;

import javafx.scene.image.Image;
import javafx.scene.layout.*;

import java.util.Objects;

public class BackgroundImageUtil {

    /**
     * Sets a background image on a Region (such as an AnchorPane, VBox, HBox, etc.)
     *
     * @param region The JavaFX Region to set the background on
     * @param imagePath The path to the image resource
     * @param cover Whether the image should cover the entire region (true) or maintain aspect ratio (false)
     */
    public static void setBackgroundImage(Region region, String imagePath, boolean cover) {
        try {
            Image image = new Image(Objects.requireNonNull(
                    BackgroundImageUtil.class.getResourceAsStream(imagePath)));

            BackgroundSize backgroundSize;

            if (cover) {
                // Image will cover the entire region
                backgroundSize = new BackgroundSize(
                        1.0, 1.0,                // 100% width and height
                        true, true,              // Width and height are percentages
                        false, false             // Don't preserve ratio or cover
                );
            } else {
                // Image will maintain its aspect ratio
                backgroundSize = new BackgroundSize(
                        BackgroundSize.AUTO, BackgroundSize.AUTO,
                        false, false,            // Width and height are not percentages
                        true, false              // Preserve ratio but don't cover
                );
            }

            BackgroundImage backgroundImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    backgroundSize
            );

            region.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.err.println("Failed to load background image: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Sets a tiled/repeated background image on a Region
     *
     * @param region The JavaFX Region to set the background on
     * @param imagePath The path to the image resource
     */
    public static void setTiledBackgroundImage(Region region, String imagePath) {
        try {
            Image image = new Image(Objects.requireNonNull(
                    BackgroundImageUtil.class.getResourceAsStream(imagePath)));

            BackgroundImage backgroundImage = new BackgroundImage(
                    image,
                    BackgroundRepeat.REPEAT,
                    BackgroundRepeat.REPEAT,
                    BackgroundPosition.DEFAULT,
                    BackgroundSize.DEFAULT
            );

            region.setBackground(new Background(backgroundImage));
        } catch (Exception exception) {
            System.err.println("Failed to load tiled background image: " + exception.getMessage());
            exception.printStackTrace();
        }
    }
}