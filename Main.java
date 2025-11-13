import functions.*;
import functions.basic.*;
import functions.meta.*;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        try {
            // тестирование всех заданий
            testBasicFunctions();
            testTabulatedFunctions();
            testMetaFunctions();
            testSerialization();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testBasicFunctions() {
        System.out.println("___ вывод значений синуса и косинуса на отрезке [0, π] ___");

        Sin sin = new Sin();
        Cos cos = new Cos();

        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("sin(%.1f) = %.4f, cos(%.1f) = %.4f%n",
                    x, sin.getFunctionValue(x), x, cos.getFunctionValue(x));
        }
    }

    private static void testTabulatedFunctions() throws IOException {
        System.out.println("\n___ тестирование табулированных функций ___");

        // создание табулированных функций
        TabulatedFunction tabulatedSin = TabulatedFunctions.tabulate(new Sin(), 0, Math.PI, 10);
        TabulatedFunction tabulatedCos = TabulatedFunctions.tabulate(new Cos(), 0, Math.PI, 10);

        // сравнение аналитических и табулированных функций
        System.out.println("\n--- сравнение аналитического и табулированного синуса ---");
        Sin analyticSin = new Sin();
        for (double x = 0; x <= Math.PI; x += 0.1) {
            double analyticValue = analyticSin.getFunctionValue(x);
            double tabulatedValue = tabulatedSin.getFunctionValue(x);
            System.out.printf("x=%.1f: аналитический=%.4f, табулированный=%.4f%n",
                    x, analyticValue, tabulatedValue);
        }

        // экспонента + текстовый формат
        System.out.println("\n--- проверка экспоненты в текстовом формате ---");
        TabulatedFunction tabulatedExp = TabulatedFunctions.tabulate(new Exp(), 0, 10, 11);

        // текстовая запись
        try (FileWriter fw = new FileWriter("exp_text.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabulatedExp, fw);
        }

        // текстовое чтение и сравнение
        TabulatedFunction loadedExp;
        try (FileReader fr = new FileReader("exp_text.txt")) {
            loadedExp = TabulatedFunctions.readTabulatedFunction(fr);
        }

        System.out.println("сравнение экспоненты до и после текстовой сериализации:");
        for (double x = 0; x <= 10; x += 1) {
            double original = tabulatedExp.getFunctionValue(x);
            double loaded = loadedExp.getFunctionValue(x);
            System.out.printf("x=%.1f: original=%.4f, loaded=%.4f%n", x, original, loaded);
        }

        // запись в файлы
        try (FileOutputStream fos = new FileOutputStream("sin_binary.dat")) {
            TabulatedFunctions.outputTabulatedFunction(tabulatedSin, fos);
        }

        try (FileWriter fw = new FileWriter("cos_text.txt")) {
            TabulatedFunctions.writeTabulatedFunction(tabulatedCos, fw);
        }

        // проверка текстового формата для косинуса
        System.out.println("\n--- проверка текстового формата для функции cos ---");
        try (FileReader fr = new FileReader("cos_text.txt")) {
            TabulatedFunction loadedCos = TabulatedFunctions.readTabulatedFunction(fr);
            compareFunctions(tabulatedCos, loadedCos);
        }

        // проверка бинарного формата для синуса
        System.out.println("\n--- проверка бинарного формата для функции sin ---");
        try (FileInputStream fis = new FileInputStream("sin_binary.dat")) {
            TabulatedFunction loadedSin = TabulatedFunctions.inputTabulatedFunction(fis);
            compareFunctions(tabulatedSin, loadedSin);
        }

        // логарифм + бинарный формат
        System.out.println("\n--- проверка логарифма в бинарном формате ---");
        TabulatedFunction tabulatedLog = TabulatedFunctions.tabulate(new Log(Math.E), 1, 10, 10);

        // бинарная запись
        try (FileOutputStream fos = new FileOutputStream("log_binary.dat")) {
            TabulatedFunctions.outputTabulatedFunction(tabulatedLog, fos);
        }

        // бинарное чтение и сравнение
        TabulatedFunction loadedLog;
        try (FileInputStream fis = new FileInputStream("log_binary.dat")) {
            loadedLog = TabulatedFunctions.inputTabulatedFunction(fis);
        }

        System.out.println("сравнение логарифма до и после бинарной сериализации:");
        for (double x = 1; x <= 10; x += 1) {
            double original = tabulatedLog.getFunctionValue(x);
            double loaded = loadedLog.getFunctionValue(x);
            System.out.printf("x=%.1f: original=%.4f, loaded=%.4f%n", x, original, loaded);
        }
    }

    private static void testMetaFunctions() {
        System.out.println("\n___ тестирование комбинаций функций ___");

        Sin sin = new Sin();
        Cos cos = new Cos();

        // сумма квадратов синуса и косинуса
        Function sumOfSquares = Functions.sum(
                Functions.power(sin, 2),
                Functions.power(cos, 2)
        );

        System.out.println("проверка тождества sin²(x) + cos²(x) = 1:");
        for (double x = 0; x <= Math.PI; x += 0.1) {
            System.out.printf("sin²(%.1f) + cos²(%.1f) = %.4f%n",
                    x, x, sumOfSquares.getFunctionValue(x));
        }
    }

    private static void testSerialization() throws IOException, ClassNotFoundException {
        System.out.println("\n=== тестирование сериализации ===");

        // создание композиции log(exp(x)) = x
        Function composition = Functions.composition(new Log(Math.E), new Exp());


        TabulatedFunction arrayFunc = TabulatedFunctions.tabulate(composition, 0, 10, 11);
        LinkedListTabulatedFunction listFunc = new LinkedListTabulatedFunction(0, 10, 11);
        for (int i = 0; i < listFunc.getPointsCount(); i++) {
            double x = listFunc.getPointX(i);
            listFunc.setPointY(i, composition.getFunctionValue(x));
        }

        System.out.println("исходные функции (log(exp(x))):");
        for (double x = 0; x <= 10; x += 1) {
            System.out.printf("x=%.1f: array=%.4f, list=%.4f%n",
                    x, arrayFunc.getFunctionValue(x), listFunc.getFunctionValue(x));
        }

        // ArrayTabulatedFunction - Externalizable
        System.out.println("\n--- ArrayTabulatedFunction (Externalizable) ---");


        if (arrayFunc instanceof ArrayTabulatedFunction) {
            ArrayTabulatedFunction array = (ArrayTabulatedFunction) arrayFunc;

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("array_externalizable.ser"))) {
                array.writeExternal(oos);
            }

            ArrayTabulatedFunction deserializedArray = new ArrayTabulatedFunction();
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("array_externalizable.ser"))) {
                deserializedArray.readExternal(ois);
            }

            System.out.println("сравнение Array после Externalizable:");
            compareFunctions(array, deserializedArray);
        } else {
            System.out.println("ОШИБКА: arrayFunc не является ArrayTabulatedFunction");
        }

        // LinkedListTabulatedFunction - Serializable
        System.out.println("\n--- LinkedListTabulatedFunction (Serializable) ---");

        //
        if (listFunc instanceof LinkedListTabulatedFunction) {
            LinkedListTabulatedFunction list = (LinkedListTabulatedFunction) listFunc;

            try (ObjectOutputStream oos = new ObjectOutputStream(
                    new FileOutputStream("list_serializable.ser"))) {
                oos.writeObject(list);
            }

            LinkedListTabulatedFunction deserializedList;
            try (ObjectInputStream ois = new ObjectInputStream(
                    new FileInputStream("list_serializable.ser"))) {
                deserializedList = (LinkedListTabulatedFunction) ois.readObject();
            }

            System.out.println("сравнение List после Serializable:");
            compareFunctions(list, deserializedList);
        } else {
            System.out.println("ОШИБКА: listFunc не является LinkedListTabulatedFunction");
        }

    }

    private static void compareFunctions(TabulatedFunction original, TabulatedFunction loaded) {
        System.out.println("сравнение исходной и восстановленной функции:");
        for (int i = 0; i < original.getPointsCount(); i++) {
            double x = original.getPointX(i);
            double originalValue = original.getFunctionValue(x);
            double loadedValue = loaded.getFunctionValue(x);

            System.out.printf("x=%4.1f: original=%7.4f, loaded=%7.4f%n",
                    x, originalValue, loadedValue);
        }
    }
}