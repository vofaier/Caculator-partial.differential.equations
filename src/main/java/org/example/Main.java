package org.example;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Main {
    private static Derivate eq;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> displayMainFrame());
    }

    private static void displayMainFrame() {
        JFrame mainFrame = new JFrame("Differential Equation Solver");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1000, 700);
        mainFrame.setLayout(new BorderLayout());
        mainFrame.getContentPane().setBackground(Color. GREEN); // Set background color

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(8, 4));
        inputPanel.setBackground(Color.GREEN); // Set background color

        JTextField equationField = new JTextField();
        JTextField x0Field = new JTextField();
        JTextField y0Field = new JTextField();
        JTextField hField = new JTextField();
        JTextField x1Field = new JTextField();

        inputPanel.add(createLabel("Equation:"));
        inputPanel.add(equationField);
        inputPanel.add(createLabel("x0:"));
        inputPanel.add(x0Field);
        inputPanel.add(createLabel("y0:"));
        inputPanel.add(y0Field);
        inputPanel.add(createLabel("h:"));
        inputPanel.add(hField);
        inputPanel.add(createLabel("x1:"));
        inputPanel.add(x1Field);

        JButton calculateButton = new JButton("Calculate");
        calculateButton.setBackground(Color.BLACK); // Set button background color
        calculateButton.setForeground(Color.ORANGE); // Set button text color
        inputPanel.add(calculateButton);

        mainFrame.add(inputPanel, BorderLayout.SOUTH);

        JTextArea resultTextArea = new JTextArea();
        resultTextArea.setEditable(false);
        resultTextArea.setBackground(Color.GREEN); // Set background color
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        mainFrame.add(resultScrollPane, BorderLayout.CENTER);

        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String equation = equationField.getText();
                    double x0 = Double.parseDouble(x0Field.getText());
                    double y0 = Double.parseDouble(y0Field.getText());
                    double h = Double.parseDouble(hField.getText());
                    double x1 = Double.parseDouble(x1Field.getText());

                    eq = createDifferentialEquation(equation);

                    double eulerResult = euler(x0, y0, h, x1);
                    double improvedEulerResult = improvedEuler(x0, y0, h, x1);
                    double rungeResult = runge(x0, y0, h, x1);

                    resultTextArea.setText("Euler Method : " + eulerResult + "\n"
                            + "Improved Euler Method : " + improvedEulerResult + "\n"
                            + "Runge-Kutta Method : " + rungeResult);
                } catch (NumberFormatException ex) {
                    resultTextArea.setText("Invalid input. Please enter numeric values.");
                }
            }
        });

        mainFrame.setVisible(true);
    }

    private static JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setForeground(Color.DARK_GRAY); // Set label text color
        return label;
    }
    public static double euler(double x0, double y0, double h, double x1) {
        // Implementation of the Euler method
        double x = x0;
        double y = y0;
        while (x < x1) {
            y += h * eq.computeDerivative(x, y);
            x += h;
        }
        return y;
    }

    public static double improvedEuler(double x0, double y0, double h, double x1) {
        // Implementation of the Improved Euler method
        double x = x0;
        double y = y0;
        while (x < x1) {
            double f_xy = eq.computeDerivative(x, y);
            double yTentative = y + h * f_xy;
            double f_xyTentative = eq.computeDerivative(x + h, yTentative);
            double yFinal = y + (h / 2) * (f_xy + f_xyTentative);

            y = yFinal;
            x += h;
        }
        return y;
    }

    public static double runge(double x0, double y0, double h, double x1) {
        // Implementation of the Runge-Kutta method
        double x = x0;
        double y = y0;
        while (x < x1) {
            double k1 = eq.computeDerivative(x, y);
            double k2 = eq.computeDerivative(x + h / 2, y + h / 2 * k1);
            double k3 = eq.computeDerivative(x + h / 2, y + h / 2 * k2);
            double k4 = eq.computeDerivative(x + h, y + h * k3);
            double yNext = y + (h / 6) * (k1 + 2 * k2 + 2 * k3 + k4);

            y = yNext;
            x += h;
        }
        return y;
    }

    public static Derivate createDifferentialEquation(String equation) {
        // Create and return a Derivate instance using the provided equation
        return (x, y) -> {
            Expression exp = new ExpressionBuilder(equation)
                    .variables("x", "y")
                    .build()
                    .setVariable("x", x)
                    .setVariable("y", y);
            return exp.evaluate();
        };
    }
}
