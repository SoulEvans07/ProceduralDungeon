package com.soulevans.proceduraldungeon.model.entities.items;

import javafx.scene.image.Image;

public abstract class Item {
    protected static Image image;
    protected String name = "unknown";

    public abstract Image getImage();
}
