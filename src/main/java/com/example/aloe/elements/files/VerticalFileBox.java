package com.example.aloe.elements.files;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

import java.io.File;

/**
 * A visual file representation arranged vertically,
 * showing the file's icon and name stacked top to bottom.
 *
 * <p>Used typically in grid or thumbnail views of a file manager,
 * where compact vertical alignment is preferred.</p>
 *
 * @since 2.8.5
 */
class VerticalFileBox extends FileBox {

    /** The main vertical container displaying the icon and file name. */
    private final VBox content = new VBox();

    /**
     * Constructs a {@code VerticalFileBox} for a given file or directory.
     *
     * @param file the file or folder to be displayed
     */
    public VerticalFileBox(File file) {
        super(file);
        initContent();
        this.getChildren().add(content);
    }

    /**
     * Initializes layout properties and adds icon and name to the vertical box.
     */
    private void initContent() {
        content.setMinWidth(100 * scale);
        content.setPrefWidth(100 * scale);
        content.setMaxWidth(100 * scale);
        content.setMinHeight(125 * scale);
        content.setMaxHeight(125 * scale);
        content.setAlignment(Pos.TOP_CENTER);
        content.setSpacing(5 * scale);
        content.setPadding(new Insets(0, 5, 10, 5));

        content.getChildren().addAll(getImageBox(60, new Insets(8, 2, 5, 2)), getName());
    }
}
