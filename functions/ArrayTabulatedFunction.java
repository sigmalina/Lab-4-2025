package functions;

import java.io.*;

public class ArrayTabulatedFunction implements TabulatedFunction, Externalizable {
    private static final long serialVersionUID = 1L;
    private static final double EPSILON = 1e-10;

    private FunctionPoint[] points;
    private int pointsCount;

    // Конструктор без параметров для Externalizable
    public ArrayTabulatedFunction() {
        // для Externalizable
    }

    // конструктор равномерное распределение с у=0
    public ArrayTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (pointsCount < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = pointsCount;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, 0);
        }
    }

    // конструктор равномерное распределение с заданными у
    public ArrayTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) {
            throw new IllegalArgumentException("Левая граница области определения должна быть меньше правой");
        }
        if (values.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        this.pointsCount = values.length;
        this.points = new FunctionPoint[pointsCount + 10];

        double step = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * step;
            points[i] = new FunctionPoint(x, values[i]);
        }
    }

    // конструктор из массива точек
    public ArrayTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        // проверка упорядоченности с машинным эпсилоном
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() - points[i-1].getX() < -EPSILON) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию X");
            }
        }

        this.pointsCount = points.length;
        this.points = new FunctionPoint[pointsCount + 10];

        for (int i = 0; i < pointsCount; i++) {
            this.points[i] = new FunctionPoint(points[i]);
        }
    }

    // реализация методов интерфейса Function
    @Override
    public double getLeftDomainBorder() {
        return points[0].getX();
    }

    @Override
    public double getRightDomainBorder() {
        return points[pointsCount - 1].getX();
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder()) {
            return Double.NaN;
        }

        // бинарный поиск интервала
        int left = 0;
        int right = pointsCount - 1;

        while (left <= right) {
            int mid = (left + right) / 2;
            if (Math.abs(points[mid].getX() - x) < EPSILON) {
                return points[mid].getY();
            } else if (points[mid].getX() < x) {
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }

        // линейная интерполяция
        int index = (points[left].getX() < x) ? left + 1 : left;
        FunctionPoint p1 = points[index - 1];
        FunctionPoint p2 = points[index];

        double k = (p2.getY() - p1.getY()) / (p2.getX() - p1.getX());
        return k * (x - p1.getX()) + p1.getY();
    }

    // реализация методов интерфейса TabulatedFunction
    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return new FunctionPoint(points[index]);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if ((index > 0 && point.getX() <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && point.getX() >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушена упорядоченность точек");
        }

        points[index] = new FunctionPoint(point);
    }

    @Override
    public double getPointX(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return points[index].getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }

        if ((index > 0 && x <= points[index - 1].getX()) ||
                (index < pointsCount - 1 && x >= points[index + 1].getX())) {
            throw new InappropriateFunctionPointException("Нарушена упорядоченность точек");
        }

        points[index].setX(x);
    }

    @Override
    public double getPointY(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        return points[index].getY();
    }

    @Override
    public void setPointY(int index, double y) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        points[index].setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (index < 0 || index >= pointsCount) {
            throw new FunctionPointIndexOutOfBoundsException(index);
        }
        if (pointsCount < 3) {
            throw new IllegalStateException("Нельзя удалить точку: меньше 3 точек");
        }

        System.arraycopy(points, index + 1, points, index, pointsCount - index - 1);
        pointsCount--;
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(points[i].getX() - point.getX()) < EPSILON) {
                throw new InappropriateFunctionPointException("Точка с таким X уже существует");
            }
        }

        // увеличение массива при необходимости
        if (pointsCount >= points.length) {
            FunctionPoint[] newArray = new FunctionPoint[pointsCount + 10];
            System.arraycopy(points, 0, newArray, 0, pointsCount);
            points = newArray;
        }

        // поиск позиции для вставки
        int insertIndex = 0;
        while (insertIndex < pointsCount && points[insertIndex].getX() < point.getX()) {
            insertIndex++;
        }

        // сдвиг элементов
        System.arraycopy(points, insertIndex, points, insertIndex + 1, pointsCount - insertIndex);
        points[insertIndex] = new FunctionPoint(point);
        pointsCount++;
    }

    // Реализация Externalizable
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(pointsCount);
        for (int i = 0; i < pointsCount; i++) {
            out.writeDouble(points[i].getX());
            out.writeDouble(points[i].getY());
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        pointsCount = in.readInt();
        points = new FunctionPoint[pointsCount + 10];
        for (int i = 0; i < pointsCount; i++) {
            double x = in.readDouble();
            double y = in.readDouble();
            points[i] = new FunctionPoint(x, y);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayTabulatedFunction{pointsCount=").append(pointsCount).append(", points=[");
        for (int i = 0; i < Math.min(pointsCount, 5); i++) {
            sb.append("(").append(points[i].getX()).append(", ").append(points[i].getY()).append(")");
            if (i < pointsCount - 1 && i < 4) sb.append(", ");
        }
        if (pointsCount > 5) sb.append(", ...");
        sb.append("]}");
        return sb.toString();
    }
}