package com.example.camerautil.APM.bean;


import androidx.annotation.NonNull;

public class Size implements Comparable<Size> {
    public final int width;
    public final int height;

    public Size(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Size(android.hardware.Camera.Size size) {
        if (size == null) {
            this.width = 1080;
            this.height = 1920;
        } else {
            this.width = size.width;
            this.height = size.height;
        }

    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (this == o) {
            return true;
        } else if (!(o instanceof Size)) {
            return false;
        } else {
            Size size = (Size)o;
            return this.width == size.width && this.height == size.height;
        }
    }

    public String toString() {
        return this.width + "x" + this.height;
    }

    public int hashCode() {
        return this.height ^ (this.width << 16 | this.width >>> 16);
    }

    public int compareTo(@NonNull Size another) {
        return this.width * this.height - another.width * another.height;
    }
}

