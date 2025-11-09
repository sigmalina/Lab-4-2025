package functions.meta;

import functions.Function;

public class Power implements Function {
    private final Function f;
    private final double power;

    public Power(Function f, double power) {
        this.f = f;
        this.power = power;
    }

    @Override
    public double getLeftDomainBorder() {
        return f.getLeftDomainBorder();
    }

    @Override
    public double getRightDomainBorder() {
        return f.getRightDomainBorder();
    }

    @Override
    public double getFunctionValue(double x) {
        double value = f.getFunctionValue(x);
        return Math.pow(value, power);
    }
}