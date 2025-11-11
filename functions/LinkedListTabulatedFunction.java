package functions;

import java.io.*;

public class LinkedListTabulatedFunction implements TabulatedFunction, Serializable {
    private static final long serialVersionUID = 1L;
    private static final double EPSILON = 1e-10;

    // внутренний класс для узлов списка
    private class FunctionNode implements Serializable {
        private static final long serialVersionUID = 1L;
        FunctionPoint point;
        FunctionNode prev;
        FunctionNode next;

        // конструктор
        FunctionNode(FunctionPoint point) {
            this.point = point;
        }
    }

    private FunctionNode head; // голова списка
    private int pointsCount;

    // конструкторы
    public LinkedListTabulatedFunction(double leftX, double rightX, int pointsCount) {
        if (leftX >= rightX) throw new IllegalArgumentException("Left border >= right border");
        if (pointsCount < 2) throw new IllegalArgumentException("Less than 2 points");

        initHead(); //
        double xStep = (rightX - leftX) / (pointsCount - 1);
        for (int i = 0; i < pointsCount; i++) {
            double x = leftX + i * xStep;
            double y = 0;
            addNodeToTail().point = new FunctionPoint(x, y);
        }
        this.pointsCount = pointsCount;
    }

    public LinkedListTabulatedFunction(double leftX, double rightX, double[] values) {
        if (leftX >= rightX) throw new IllegalArgumentException("Left border >= right border");
        if (values.length < 2) throw new IllegalArgumentException("Less than 2 points");

        initHead();
        double xStep = (rightX - leftX) / (values.length - 1);
        for (int i = 0; i < values.length; i++) {
            double x = leftX + i * xStep;
            addNodeToTail().point = new FunctionPoint(x, values[i]);
        }
        this.pointsCount = values.length;
    }

    // конструктор из массива точек
    public LinkedListTabulatedFunction(FunctionPoint[] points) {
        if (points.length < 2) {
            throw new IllegalArgumentException("Количество точек не может быть меньше двух");
        }

        // проверка упорядоченности
        for (int i = 1; i < points.length; i++) {
            if (points[i].getX() - points[i-1].getX() < -EPSILON) {
                throw new IllegalArgumentException("Точки не упорядочены по возрастанию X");
            }
        }

        initHead();
        for (FunctionPoint point : points) {
            addNodeToTail().point = new FunctionPoint(point);
        }
        this.pointsCount = points.length;
    }

    // инициализация головы списка
    private void initHead() {
        head = new FunctionNode(null);
        head.next = head;
        head.prev = head;
        pointsCount = 0;
    }

    // добавление узла в конец
    private FunctionNode addNodeToTail() {
        FunctionNode newNode = new FunctionNode(null);
        FunctionNode tail = head.prev;

        tail.next = newNode;
        newNode.prev = tail;
        newNode.next = head;
        head.prev = newNode;

        pointsCount++;
        return newNode;
    }

    // добавление узла по индексу
    private FunctionNode addNodeByIndex(int index) {
        if (index < 0 || index > pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        if (index == pointsCount) {
            return addNodeToTail();
        }

        FunctionNode newNode = new FunctionNode(null);
        FunctionNode target = getNodeByIndex(index);
        FunctionNode prevNode = target.prev;

        prevNode.next = newNode;
        newNode.prev = prevNode;
        newNode.next = target;
        target.prev = newNode;

        pointsCount++;
        return newNode;
    }

    // удаление узла по индексу
    private FunctionNode deleteNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        FunctionNode toDelete = getNodeByIndex(index);
        toDelete.prev.next = toDelete.next;
        toDelete.next.prev = toDelete.prev;
        pointsCount--;

        return toDelete;
    }

    // получение узла по индексу
    private FunctionNode getNodeByIndex(int index) {
        if (index < 0 || index >= pointsCount)
            throw new FunctionPointIndexOutOfBoundsException(index);

        FunctionNode current = head.next;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }
        return current;
    }

    // реализация методов Function
    @Override
    public double getLeftDomainBorder() {
        return pointsCount > 0 ? getPointX(0) : Double.NaN;
    }

    @Override
    public double getRightDomainBorder() {
        return pointsCount > 0 ? getPointX(pointsCount - 1) : Double.NaN;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x < getLeftDomainBorder() || x > getRightDomainBorder())
            return Double.NaN;

        for (int i = 0; i < pointsCount - 1; i++) {
            double x1 = getPointX(i);
            double x2 = getPointX(i + 1);
            if (x >= x1 && x <= x2) {
                double y1 = getPointY(i);
                double y2 = getPointY(i + 1);
                return y1 + (y2 - y1) * (x - x1) / (x2 - x1);
            }
        }
        return Double.NaN;
    }

    // реализация методов TabulatedFunction
    @Override
    public int getPointsCount() {
        return pointsCount;
    }

    @Override
    public FunctionPoint getPoint(int index) {
        return new FunctionPoint(getNodeByIndex(index).point);
    }

    @Override
    public void setPoint(int index, FunctionPoint point) throws InappropriateFunctionPointException {
        setPointX(index, point.getX());
        setPointY(index, point.getY());
    }

    @Override
    public double getPointX(int index) {
        return getNodeByIndex(index).point.getX();
    }

    @Override
    public void setPointX(int index, double x) throws InappropriateFunctionPointException {
        if (index > 0 && x <= getPointX(index - 1))
            throw new InappropriateFunctionPointException("X must be greater than previous point");
        if (index < pointsCount - 1 && x >= getPointX(index + 1))
            throw new InappropriateFunctionPointException("X must be less than next point");

        getNodeByIndex(index).point.setX(x);
    }

    @Override
    public double getPointY(int index) {
        return getNodeByIndex(index).point.getY();
    }

    @Override
    public void setPointY(int index, double y) {
        getNodeByIndex(index).point.setY(y);
    }

    @Override
    public void deletePoint(int index) {
        if (pointsCount < 3) throw new IllegalStateException("Cannot delete point - less than 3 points left");
        deleteNodeByIndex(index);
    }

    @Override
    public void addPoint(FunctionPoint point) throws InappropriateFunctionPointException {
        // Проверка на дублирование
        for (int i = 0; i < pointsCount; i++) {
            if (Math.abs(getPointX(i) - point.getX()) < EPSILON)
                throw new InappropriateFunctionPointException("Point with this X already exists");
        }

        int insertIndex = 0;
        while (insertIndex < pointsCount && getPointX(insertIndex) < point.getX()) {
            insertIndex++;
        }

        FunctionNode newNode = addNodeByIndex(insertIndex);
        newNode.point = new FunctionPoint(point);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("LinkedListTabulatedFunction{pointsCount=").append(pointsCount).append(", points=[");
        FunctionNode current = head.next;
        int count = 0;
        while (current != head && count < 5) {
            sb.append("(").append(current.point.getX()).append(", ").append(current.point.getY()).append(")");
            current = current.next;
            if (current != head && count < 4) sb.append(", ");
            count++;
        }
        if (pointsCount > 5) sb.append(", ...");
        sb.append("]}");
        return sb.toString();
    }
}