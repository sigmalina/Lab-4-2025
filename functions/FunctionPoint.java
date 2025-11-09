package functions;

import java.io.Serializable;

public class FunctionPoint implements Serializable {
    private static final long serialVersionUID = 1L;

    double x;
    double y;

    // конструктор с параметрами
    public FunctionPoint(double x, double y) {
        this.x = x;
        this.y = y;
    }

    // копирующий конструктор
    public FunctionPoint(FunctionPoint point) {
        this.x = point.x;
        this.y = point.y;
    }

    // конструктор по умолчанию
    public FunctionPoint() {
        x = 0;
        y = 0;
    }

    // геттеры и сеттеры
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    // методы для сериализации (опционально, для большего контроля)
    private void writeObject(java.io.ObjectOutputStream out)
            throws java.io.IOException {
        out.defaultWriteObject();
    }

    private void readObject(java.io.ObjectInputStream in)
            throws java.io.IOException, ClassNotFoundException {
        in.defaultReadObject();
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        FunctionPoint that = (FunctionPoint) obj;
        return Math.abs(that.x - x) < 1e-10 &&
                Math.abs(that.y - y) < 1e-10;
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(x, y);
    }
}