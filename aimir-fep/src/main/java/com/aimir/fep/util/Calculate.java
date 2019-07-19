// javac Calculate.java && java Calculate
// 2018/05/02 #SP-929 

package com.aimir.fep.util;

//import java.io.*;
import java.text.*;

class ExpressionParserException extends Exception {
	private static final long serialVersionUID = 1L;

	public ExpressionParserException(String message) {
        super(message);
    }
}

public class Calculate {
    public String expression;
    public Calculate left  = null;
    public Calculate right = null;

    public Calculate(String expression)
    {
        this.expression = expression;
    }

    public static void ValidateBracketBalance(String expression) throws ExpressionParserException
    {
        int nest = 0;

        for (int i = 0; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                nest++;
            }
            else if (expression.charAt(i) == ')') {
                nest--;

                if (nest < 0)
                    break;
            }
        }

        if (nest != 0)
            throw new ExpressionParserException("unbalanced bracket: " + expression);
    }

    public void parse() throws ExpressionParserException
    {
        expression = removeOuterMostBracket(expression);

        int posOperator = getOperatorPosition(expression);

        if (posOperator < 0) {
            left = null;
            right = null;
            return;
        }

        if (posOperator == 0 || posOperator == expression.length() - 1)
            throw new ExpressionParserException("invalid expression: " + expression);

        left = new Calculate(expression.substring(0, posOperator));
        left.parse();

        right = new Calculate(expression.substring(posOperator + 1));
        right.parse();

        expression = expression.substring(posOperator, posOperator + 1);
    }

    private static String removeOuterMostBracket(String expression) throws ExpressionParserException
    {
        boolean hasOuterMostBracket = false;
        int nest = 0;

        if (expression.charAt(0) == '(') {
            hasOuterMostBracket = true;
            nest = 1;
        }

        for (int i = 1; i < expression.length(); i++) {
            if (expression.charAt(i) == '(') {
                nest++;

                if (i == 0)
                    hasOuterMostBracket = true;
            }
            else if (expression.charAt(i) == ')') {
                nest--;

                if (nest == 0 && i < expression.length() - 1) {
                    hasOuterMostBracket = false;
                    break;
                }
            }
        }

        if (!hasOuterMostBracket)
            return expression;

        if (expression.length() <= 2)
            throw new ExpressionParserException("empty bracket: " + expression);

        expression = expression.substring(1, expression.length() - 1);

        if (expression.charAt(0) == '(' && expression.charAt(expression.length() - 1) == ')')
            expression = removeOuterMostBracket(expression);

        return expression;
    }

    private static int getOperatorPosition(String expression)
    {
        if (expression == null || expression.length() == 0)
            return -1;

        int posOperator     = -1;
        int currentPriority = Integer.MAX_VALUE;
        int nest            = 0;

        for (int i = 0; i < expression.length(); i++) {
            int priority = 0;

            switch (expression.charAt(i)) {
                case '=': priority = 1; break;
                case '+': priority = 2; break;
                case '-': priority = 2; break;
                case '*': priority = 3; break;
                case '/': priority = 3; break;
                case '(': nest++; continue;
                case ')': nest--; continue;
                default: continue;
            }

            if (nest == 0 && priority <= currentPriority) {
                currentPriority = priority;
                posOperator = i;
            }
        }

        return posOperator;
    }

    public void traversePostorder()
    {
        if (left != null)
            left.traversePostorder();
        if (right != null)
            right.traversePostorder();

        System.out.print(expression + " ");
    }

    public void traverseInorder()
    {
        if (left != null && right != null)
            System.out.print("(");

        if (left != null) {
            left.traverseInorder();

            System.out.print(" ");
        }

        System.out.print(expression);

        if (right != null) {
            System.out.print(" ");

            right.traverseInorder();
        }

        if (left != null && right != null)
            System.out.print(")");
    }

    public void traversePreorder()
    {
        System.out.print(expression + " ");

        if (left != null)
            left.traversePreorder();
        if (right != null)
            right.traversePreorder();
    }

    public boolean calculate()
    {
        if (left == null && right == null)
            return true;

        left.calculate();
        right.calculate();

        double leftOperand, rightOperand;

        try {
            leftOperand  = Double.parseDouble( left.expression);
            rightOperand = Double.parseDouble(right.expression);
        }
        catch (NumberFormatException ex) {
            return false;
        }

        switch (expression.charAt(0)) {
            case '+': expression = formatNumber(leftOperand + rightOperand); break;
            case '-': expression = formatNumber(leftOperand - rightOperand); break;
            case '*': expression = formatNumber(leftOperand * rightOperand); break;
            case '/': expression = formatNumber(leftOperand / rightOperand); break;
            default: return false;
        }

        left  = null;
        right = null;

        return true;
    }

    private String formatNumber(double number)
    {
        return (new DecimalFormat("#.###############")).format(number);
    }
}
