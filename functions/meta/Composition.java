package functions.meta;

import functions.Function;

public class Composition implements Function {
    private final Function f1, f2;

    public Composition(Function f1, Function f2) {
        this.f1 = f1;
        this.f2 = f2;
    }

    @Override
    public double getLeftDomainBorder() {
        return f2.getLeftDomainBorder();  // f2 определяет границы входа
    }

    @Override
    public double getRightDomainBorder() {
        return f2.getRightDomainBorder();  // f2 определяет границы входа
    }

    @Override
    public double getFunctionValue(double x) {
        // f1(f2(x)) - сначала f2, потом f1
        double innerValue = f2.getFunctionValue(x);  // f2(x)
        return f1.getFunctionValue(innerValue);      // f1(f2(x))
    }
}