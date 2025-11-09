package functions;

public class FunctionPointIndexOutOfBoundsException extends IndexOutOfBoundsException {
    public FunctionPointIndexOutOfBoundsException() {
        super();
    } //создает исключение без сообщения об ошибке

    public FunctionPointIndexOutOfBoundsException(String message) {
        super(message);
    } //cоздает искл с пользовательским сообщением об ошибке

    public FunctionPointIndexOutOfBoundsException(int index) {
        super("Index out of range: " + index);
    }
}
