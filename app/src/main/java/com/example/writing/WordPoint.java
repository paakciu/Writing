package com.example.writing;

public class WordPoint {
    public float x;
    public float y;
    public float width;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public WordPoint() {
    }
    public WordPoint(float x, float y) {
        this.x = x;
        this.y = y;
    }
    public void set(float x, float y, float w) {
        this.x = x;
        this.y = y;
        this.width = w;
    }
    public void set(WordPoint wordPoint) {
        this.x = wordPoint.x;
        this.y = wordPoint.y;
        this.width = wordPoint.width;
    }
    public String toString() {
        return "X = " + x + "; Y = " + y + "; W = " + width;
    }
}
