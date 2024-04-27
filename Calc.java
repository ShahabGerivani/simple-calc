import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Arrays;

/*
 * Resources:
 * https://www.tutorialspoint.com/swing/swing_layouts.htm
 * https://www.geeksforgeeks.org/how-to-create-array-of-objects-in-java/
 * https://www.w3schools.com/java/java_hashmap.asp
 * https://docs.oracle.com/javase%2Ftutorial%2Fuiswing%2F%2F/layout/gridbag.html
 * https://stackoverflow.com/questions/24723998/can-components-of-a-gridbaglayout-fill-parent-frame-upon-resize
 * https://www.w3schools.com/java/java_regex.asp
 * https://stackoverflow.com/questions/43877296/java-map-an-array-of-strings-to-an-array-of-integers
 * https://stackoverflow.com/questions/20169127/what-is-illegalstateexception
 * https://stackoverflow.com/questions/1043872/are-there-any-built-in-methods-in-java-to-increase-font-size
 * https://stackoverflow.com/questions/33172555/how-to-set-padding-at-jlabel
 * https://stackoverflow.com/questions/12730230/set-the-same-font-for-all-component-java
 */

// This calculator can only do one operation at a time
public class Calc extends JFrame {
    private static final float DEFAULT_FONT_SIZE = 14;
    // Constants to represent the four main operators
    private static final int DIVIDE = 0;
    private static final int MULTIPLY = 1;
    private static final int SUBTRACT = 2;
    private static final int ADD = 3;
    // An array to represent the four operators as strings
    private static final String[] operators = {"/", "x", "-", "+"};

    private static int currentOperator = -1;
    // The location of the current operator in the calculator's display
    private static int currentOperatorDisplayIndex = -1;

    public static void main(String[] args) {
        // Setting up the frame
        JFrame frame = new JFrame("Calculator");
        frame.setSize(300, 400);
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        frame.setVisible(true);

        // The main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1;
        gbc.weighty = 1;
        frame.add(mainPanel);

        // The calculator's display
        JLabel display = new JLabel();
        display.setOpaque(true);
        display.setBackground(Color.white);
        display.setBorder(new EmptyBorder(0, 10, 0, 10));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 4;
        mainPanel.add(display, gbc);
        gbc.gridwidth = 1;

        // Number buttons
        JButton btn;
        for (int i = 0; i <= 9; i++) {
            btn = new JButton(String.valueOf(i));
            int finalI = i;
            btn.addActionListener(e -> appendToDisplay(String.valueOf(finalI), display));
            if (i == 0) {
                gbc.gridx = 1;
                gbc.gridy = 5;
            } else {
                gbc.gridx = (i - 1) % 3;
                gbc.gridy = (i - 1) / 3 + 2;
            }
            mainPanel.add(btn, gbc);
        }

        // The . button
        JButton dotBtn = new JButton(".");
        gbc.gridx = 2;
        gbc.gridy = 5;
        dotBtn.addActionListener(e -> {
            String currentNumber = display.getText().substring(currentOperatorDisplayIndex + 1);
            if (!currentNumber.isEmpty() && !currentNumber.contains(".")) {
                appendToDisplay(".", display);
            }
        });
        mainPanel.add(dotBtn, gbc);

        // The +/- button
        JButton negateBtn = new JButton("+/-");
        gbc.gridx = 0;
        gbc.gridy = 5;
        negateBtn.addActionListener(e -> {
            String currentNumber = display.getText().substring(currentOperatorDisplayIndex + 1);
            if (!currentNumber.isEmpty()) {
                if (currentNumber.startsWith("-")) {
                    display.setText(display.getText().substring(0, currentOperatorDisplayIndex + 1) + currentNumber.substring(1));
                } else {
                    display.setText(display.getText().substring(0, currentOperatorDisplayIndex + 1) + "-" + currentNumber);
                }
            }
        });
        mainPanel.add(negateBtn, gbc);

        // The del button
        JButton delBtn = new JButton("del");
        gbc.gridx = 0;
        gbc.gridy = 1;
        delBtn.addActionListener(e -> {
            if (display.getText().isEmpty()) return;
            for (String operator : operators) {
                if (display.getText().endsWith(operator)) {
                    resetOperator();
                    break;
                }
            }
            display.setText(display.getText().substring(0, display.getText().length() - 1));
        });
        mainPanel.add(delBtn, gbc);

        // The clr button
        JButton clrBtn = new JButton("clr");
        gbc.gridx = 1;
        gbc.gridy = 1;
        clrBtn.addActionListener(e -> {
            display.setText("");
            resetOperator();
        });
        mainPanel.add(clrBtn, gbc);

        // The / button
        JButton divideBtn = new JButton("/");
        gbc.gridx = 2;
        gbc.gridy = 1;
        divideBtn.addActionListener(e -> operatorBtnAction(DIVIDE, display));
        mainPanel.add(divideBtn, gbc);

        // The x button
        JButton multiplyBtn = new JButton("x");
        gbc.gridx = 3;
        gbc.gridy = 1;
        multiplyBtn.addActionListener(e -> operatorBtnAction(MULTIPLY, display));
        mainPanel.add(multiplyBtn, gbc);

        // The - button
        JButton subtractBtn = new JButton("-");
        gbc.gridx = 3;
        gbc.gridy = 2;
        subtractBtn.addActionListener(e -> operatorBtnAction(SUBTRACT, display));
        mainPanel.add(subtractBtn, gbc);

        // The + button
        JButton addBtn = new JButton("+");
        gbc.gridx = 3;
        gbc.gridy = 3;
        addBtn.addActionListener(e -> operatorBtnAction(ADD, display));
        mainPanel.add(addBtn, gbc);

        // The = button
        JButton calcBtn = new JButton("=");
        gbc.gridx = 3;
        gbc.gridy = 4;
        gbc.gridheight = 2;
        calcBtn.addActionListener(e -> {
            if (currentOperator == -1) return;
            String[] numsString = display.getText().split(currentOperator == ADD ? "\\+" : operators[currentOperator]);
            double[] nums = Arrays.stream(numsString).mapToDouble(Double::parseDouble).toArray();
            double result;
            switch (currentOperator) {
                case DIVIDE -> result = nums[0] / nums[1];
                case MULTIPLY -> result = nums[0] * nums[1];
                case SUBTRACT -> result = nums[0] - nums[1];
                case ADD -> result = nums[0] + nums[1];
                default -> throw new IllegalStateException("Unexpected value: " + currentOperator);
            }
            display.setText(String.valueOf(result));
            resetOperator();
        });
        mainPanel.add(calcBtn, gbc);
        gbc.gridheight = 1;

        for (Component child : mainPanel.getComponents()) {
            child.setFont(child.getFont().deriveFont(DEFAULT_FONT_SIZE));
        }

        frame.revalidate();
        frame.repaint();
    }

    private static void appendToDisplay(String content, JLabel display) {
        display.setText(display.getText() + content);
    }

    private static void operatorBtnAction(int operator, JLabel display) {
        if (currentOperator == -1 && !display.getText().isEmpty()) {
            appendToDisplay(operators[operator], display);
            currentOperator = operator;
            currentOperatorDisplayIndex = display.getText().length() - 1;
        }
    }

    private static void resetOperator() {
        currentOperator = -1;
        currentOperatorDisplayIndex = -1;
    }
}
