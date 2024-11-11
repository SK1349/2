package org.example;

import java.util.Scanner;
import java.util.Stack;

/**
 * Класс Math предоставляет методы для вычисления математических выражений,
 * включая поддержку переменных.
 */
public class Calculator {

    private String expression;

    /**
     * Конструктор для инициализации выражения.
     *
     * @param expression Математическое выражение в виде строки.
     */
    public Calculator(String expression) {
        this.expression = expression;
    }

    /**
     * Возвращает математическое выражение.
     *
     * @return Математическое выражение в виде строки.
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Вычисляет значение математического выражения, запрашивая значения переменных у пользователя.
     *
     * @param scanner Сканер для ввода значений переменных.
     * @return Результат вычисления выражения.
     * @throws Exception Если выражение некорректно или возникают ошибки при вычислении.
     */
    public double calculate(Scanner scanner) throws Exception {
        String postfix = toPostfix(expression);
        return doPostfix(postfix, scanner);
    }

    /**
     * Преобразует математическое выражение в постфиксную нотацию.
     *
     * @param expression Математическое выражение в виде строки.
     * @return Постфиксное выражение в виде строки.
     * @throws Exception Если выражение некорректно.
     */
    public String toPostfix(String expression) throws Exception {
        Stack<String> stack = new Stack<>();
        StringBuilder postfix = new StringBuilder();

        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (Character.isDigit(c) || Character.isLetter(c)) {
                postfix.append(c);
                while (i + 1 < expression.length() && (Character.isDigit(expression.charAt(i + 1)) || Character.isLetter(expression.charAt(i + 1)))) {
                    postfix.append(expression.charAt(i + 1));
                    i++;
                }
                postfix.append(' ');
            } else if (c == '(') {
                stack.push(String.valueOf(c));
            } else if (c == ')') {
                while (!stack.isEmpty() && !stack.peek().equals("(")) {
                    postfix.append(stack.pop()).append(' ');
                }
                if (stack.isEmpty()) {
                    throw new Exception("Недопустимое выражение: неправильно подобранные круглые скобки");
                }
                stack.pop();
            } else if (isOperator(c)) {
                while (!stack.isEmpty() && priority(stack.peek().charAt(0)) >= priority(c)) {
                    postfix.append(stack.pop()).append(' ');
                }
                stack.push(String.valueOf(c));
            } else if (isFunction(c)) {
                stack.push(String.valueOf(c));
            } else if (c != ' ') {
                throw new Exception("Недопустимый символ в выражении: " + c);
            }
        }

        while (!stack.isEmpty()) {
            if (stack.peek().equals("(")) {
                throw new Exception("Неправильно подобранные круглые скобки");
            }
            postfix.append(stack.pop()).append(' ');
        }

        return postfix.toString().trim();
    }

    /**
     * Вычисляет значение постфиксного выражения, запрашивая значения переменных у пользователя.
     *
     * @param postfix Постфиксное выражение в виде строки.
     * @param scanner Сканер для ввода значений переменных.
     * @return Результат вычисления постфиксного выражения.
     * @throws Exception Если выражение некорректно или возникают ошибки при вычислении.
     */
    public double doPostfix(String postfix, Scanner scanner) throws Exception {
        Stack<Double> stack = new Stack<>();
        String[] elements = postfix.split("\\s+");
        String[] variableNames = new String[100]; // Массив для хранения имен переменных
        double[] variableValues = new double[100]; // Массив для хранения значений переменных
        int variableCount = 0; // Счетчик для количества переменных

        for (String element : elements) {
            if (element.matches("\\d+")) {
                stack.push(Double.parseDouble(element));
            } else if (element.matches("[a-zA-Z]+")) {
                int index = -1;
                for (int i = 0; i < variableCount; i++) {
                    if (variableNames[i].equals(element)) {
                        index = i;
                        break;
                    }
                }
                if (index == -1) {
                    System.out.println("Введите значение для переменной " + element + ":");
                    double value = scanner.nextDouble();
                    variableNames[variableCount] = element;
                    variableValues[variableCount] = value;
                    variableCount++;
                    stack.push(value);
                } else {
                    stack.push(variableValues[index]);
                }
            } else if (isOperator(element.charAt(0))) {
                if (stack.size() < 2) {
                    throw new Exception("Недостаточно операндов");
                }
                double b = stack.pop();
                double a = stack.pop();
                double result = doAct(a, b, element.charAt(0));
                stack.push(result);
            } else if (isFunction(element.charAt(0))) {
                if (stack.size() < 1) {
                    throw new Exception("Недостаточно операндов для функции");
                }
                double a = stack.pop();
                double result = applyFunction(element.charAt(0), a);
                stack.push(result);
            } else {
                throw new Exception("Недопустимый символ в постфиксном выражении: " + element);
            }
        }

        if (stack.size() != 1) {
            throw new Exception("Слишком много операндов");
        }

        return stack.pop();
    }

    /**
     * Проверяет, является ли символ оператором.
     *
     * @param c Символ для проверки.
     * @return true, если символ является оператором, иначе false.
     */
    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
    }

    /**
     * Проверяет, является ли символ функцией.
     *
     * @param c Символ для проверки.
     * @return true, если символ является функцией, иначе false.
     */
    public static boolean isFunction(char c) {
        return c == 's' || c == 'c';
    }

    /**
     * Возвращает приоритет оператора.
     *
     * @param op Оператор.
     * @return Приоритет оператора.
     */
    public static int priority(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
                return 3;
            default:
                return 0;
        }
    }

    /**
     * Выполняет операцию над двумя операндами.
     *
     * @param a Первый операнд.
     * @param b Второй операнд.
     * @param op Оператор.
     * @return Результат операции.
     * @throws Exception Если возникает ошибка при выполнении операции (например, деление на ноль).
     */
    public static double doAct(double a, double b, char op) throws Exception {
        switch (op) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new Exception("Деление на ноль");
                }
                return a / b;
            case '^':
                return Math.pow(a, b);
            default:
                throw new Exception("Недопустимый оператор: " + op);
        }
    }

    /**
     * Применяет функцию к операнду.
     *
     * @param func Символ функции.
     * @param a Операнд.
     * @return Результат применения функции.
     * @throws Exception Если возникает ошибка при выполнении функции.
     */
    public static double applyFunction(char func, double a) throws Exception {
        switch (func) {
            case 's':
                return Math.sin(a);
            case 'c':
                return Math.cos(a);
            default:
                throw new Exception("Недопустимая функция: " + func);
        }
    }
}
