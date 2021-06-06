package com.example.camerautil.APM.bean;

public class ParamterRange {
    public final int min;
    public final int max;

    public ParamterRange(int min, int max) {
        this.min = min;
        this.max = max;
        if (min > max) {
            throw new IllegalArgumentException("min=" + min + ",max=" + max + ", that min is greater than max is illegal");
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            ParamterRange fpsRange = (ParamterRange)o;
            return this.min == fpsRange.min && this.max == fpsRange.max;
        } else {
            return false;
        }
    }

    public String toString() {
        return "[" + this.min + "," + this.max + "]";
    }

    public int hashCode() {
        int result = this.min;
        result = 31 * result + this.max;
        return result;
    }
}
