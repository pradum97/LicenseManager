package com.license.manager;

import javafx.scene.image.Image;

import java.util.Objects;

public class ImageLoader {

    public Image load(String imagePath){

        try {
         return  new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        } catch (Exception e) {
            return  new Image(Objects.requireNonNull(getClass().getResourceAsStream("icon/img_preview.png")));
        }
    }
}
