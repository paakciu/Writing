package com.example.writing;

class Test {
    private String dot;
    private int x;
    private int y;
    private int f;
    private int p;
    private int s;

    public String getDot() {
        return dot;
    }

    public void setDot(String dot) {
        this.dot = dot;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getF() {
        return f;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getP() {
        return p;
    }

    public void setP(int p) {
        this.p = p;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    @Override
    public String toString() {
        return "Test{" +
                "dot='" + dot + '\'' +
                ", x=" + x +
                ", y=" + y +
                ", f=" + f +
                ", p=" + p +
                ", s=" + s +
                '}';
    }
}
