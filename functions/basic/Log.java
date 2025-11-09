package functions.basic;

import functions.Function;

public class Log implements Function {
    private final double base;

    public Log(double base) {
        if (base <= 0 || Math.abs(base - 1) < 1e-10) {
            throw new IllegalArgumentException("Основание логарифма должно быть > 0 и != 1");
        }
        this.base = base;
    }

    @Override
    public double getLeftDomainBorder() {
        return 0;  // логарифм определен при x > 0
    }

    @Override
    public double getRightDomainBorder() {
        return Double.POSITIVE_INFINITY;
    }

    @Override
    public double getFunctionValue(double x) {
        if (x <= 0) {
            return Double.NaN;  // log(0) и log(отрицательных) не существует
        }
        return Math.log(x) / Math.log(base);
    }
}