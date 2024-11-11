package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Scanner;

public class TestCalculator {

    @Test
    public void testToPostfix() throws Exception {
        Calculator calculator = new Calculator("3 + 2 * (5 - x)");
        String expectedPostfix = "3 2 5 x - * +";
        String actualPostfix = calculator.toPostfix(calculator.getExpression());
        assertEquals(expectedPostfix, actualPostfix.trim());
    }

    @Test
    public void testDoPostfix() throws Exception {
        Calculator calculator = new Calculator("3 2 5 2 - * +");
        Scanner scanner = new Scanner("2"); // Simulate user input for variable 'x'
        double expectedResult = 9.0;
        double actualResult = calculator.doPostfix(calculator.getExpression(), scanner);
        assertEquals(expectedResult, actualResult, 0.001);
    }

    @Test
    public void testCalculate() throws Exception {
        Calculator calculator = new Calculator("3 + 2 * (5 - x)");
        Scanner scanner = new Scanner("2"); // Simulate user input for variable 'x'
        double expectedResult = 9.0;
        double actualResult = calculator.calculate(scanner);
        assertEquals(expectedResult, actualResult, 0.001);
    }

    @Test
    public void testIsOperator() {
        assertTrue(Calculator.isOperator('+'));
        assertTrue(Calculator.isOperator('-'));
        assertTrue(Calculator.isOperator('*'));
        assertTrue(Calculator.isOperator('/'));
        assertTrue(Calculator.isOperator('^'));
        assertFalse(Calculator.isOperator('a'));
        assertFalse(Calculator.isOperator('1'));
    }

    @Test
    public void testPriority() {
        assertEquals(1, Calculator.priority('+'));
        assertEquals(1, Calculator.priority('-'));
        assertEquals(2, Calculator.priority('*'));
        assertEquals(2, Calculator.priority('/'));
        assertEquals(3, Calculator.priority('^'));
        assertEquals(0, Calculator.priority('a'));
    }

    @Test
    public void testDoAct() throws Exception {
        assertEquals(5.0, Calculator.doAct(2.0, 3.0, '+'), 0.001);
        assertEquals(-1.0, Calculator.doAct(2.0, 3.0, '-'), 0.001);
        assertEquals(6.0, Calculator.doAct(2.0, 3.0, '*'), 0.001);
        assertEquals(2.0 / 3.0, Calculator.doAct(2.0, 3.0, '/'), 0.001);
        assertEquals(8.0, Calculator.doAct(2.0, 3.0, '^'), 0.001);
        assertThrows(Exception.class, () -> Calculator.doAct(2.0, 0.0, '/'));
    }

    @Test
    public void testApplyFunction() throws Exception {
        assertEquals(Math.sin(Math.PI / 2), Calculator.applyFunction('s', Math.PI / 2), 0.001);
        assertEquals(Math.cos(0), Calculator.applyFunction('c', 0), 0.001);
    }
}
