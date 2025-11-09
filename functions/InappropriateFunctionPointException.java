package functions;

public class InappropriateFunctionPointException extends Exception {

    public InappropriateFunctionPointException() {
        super("Некорректная операция с точкой функции");
    }

    public InappropriateFunctionPointException(String message) {
        super(message);
    }
}