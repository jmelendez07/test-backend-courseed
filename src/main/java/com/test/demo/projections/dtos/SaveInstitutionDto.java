package com.test.demo.projections.dtos;

import java.io.Serializable;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;

public class SaveInstitutionDto implements Serializable {

    private String name;
    private FilePart image;

    public String getName() {
        return name;
    }

    public FilePart getImage() {
        return image;
    }

    public void setImage(FilePart image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isValidImage() {
        if (image == null) {
            return false;
        }

        MediaType mediaType = image.headers().getContentType();

        if (mediaType == null) {
            return false;
        }

        if (!mediaType.toString().startsWith("image/")) {
            return false;
        }

        final long MAX_SIZE = 2 * 1024 * 1024;
        return image.headers().getContentLength() <= MAX_SIZE;
    }
}
