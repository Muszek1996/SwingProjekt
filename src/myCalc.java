import javafx.scene.input.KeyCode;
import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.MessageFormat;
import java.util.AbstractMap;

import static javax.swing.JOptionPane.ERROR_MESSAGE;

public class myCalc extends JFrame {
        private DefaultListModel<Pair> defaultListModel;
        private JPanel mainPanel;
        private JScrollPane scrollContainerPane = new JScrollPane();
        private JTextArea historyTextArea;
        private JTextField formulaInput;
        private JPanel listPanel;
        private JList<String> functionList;
        private JButton evalButton;
        private String lastFormulaInput= new String ();
        private String lastResult = new String();
        private JMenuBar menuBar;
        private JMenu options;
        private JMenuItem reset,exit;

    private JMenuBar glueMenuTogether(){
        menuBar = new JMenuBar();
        options = new JMenu("Options");
        reset = new JMenuItem("Reset");
            reset.addActionListener(e -> {
                lastResult="";
                lastFormulaInput ="";
                formulaInput.setText("");
                historyTextArea.setText("");
                defaultListModel.lastElement().stringsPair.setValue(lastFormulaInput);
            });
        exit = new JMenuItem("Exit");
            exit.addActionListener(e -> {
                dispose();
                System.exit(0);
            });
        options.add(reset);
        options.add(exit);
        menuBar.add(options);
        return menuBar;
    }


    public myCalc() throws HeadlessException {
        super("JavaScript Calculator");



        listPanel = new JPanel(new GridBagLayout());
        evalButton = new JButton("Evaluate!");
        historyTextArea = new JTextArea();
        historyTextArea.add(scrollContainerPane);
        listPanel.add(jListInitialize(), constraints(0,0,1,1,GridBagConstraints.BOTH,new Insets(0,0,5,0)));
        add(formulaInputInitialize());
        mainFrameInitialize(this);
        setJMenuBar(glueMenuTogether());
        add(mainPanelInitialize());
        pack();
    }

    private JFrame mainFrameInitialize(myCalc mainJFrame){
        Dimension windowSize = new Dimension(500,500);
        Dimension userDisplayDimensions;
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        userDisplayDimensions = toolkit.getScreenSize();
        Point windowPos = new Point(userDisplayDimensions.width/2-windowSize.width/2,userDisplayDimensions.height/2-windowSize.height/2);
        mainJFrame.setSize(windowSize);
        mainJFrame.setPreferredSize(windowSize);
        mainJFrame.setLocation(windowPos);
        mainJFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        mainJFrame.setVisible(true);
        return mainJFrame;
    }
    private JPanel mainPanelInitialize(){
        mainPanel = new JPanel(new GridBagLayout());
        mainPanel.add(new JScrollPane(historyTextArea), constraints(0,0,1,1,GridBagConstraints.BOTH,new Insets(0,0,5,5)));
        mainPanel.add(formulaInput, constraints(0,1,0,0,GridBagConstraints.BOTH,new Insets(5,5,5,5)));
        mainPanel.add(listPanel, constraints(1,0,0,1,GridBagConstraints.BOTH,new Insets(0,5,0,0)));
        mainPanel.add(evalButton, constraints(1,1,0,0,GridBagConstraints.BOTH,new Insets(5,5,5,5)));
        return mainPanel;
    }

    private JList<String> jListInitialize(){
        defaultListModel = new DefaultListModel<>();
        defaultListModel.addElement(new Pair("Square root", "sqrt()"));
        defaultListModel.addElement(new Pair("Fibonacci number", "Fib()"));
        defaultListModel.addElement(new Pair("Absolute value", "abs()"));
        defaultListModel.addElement(new Pair("Binary Logarithm", "log2()"));
        defaultListModel.addElement(new Pair("Exponential", "exp()"));
        defaultListModel.addElement(new Pair("!", "!"));
        defaultListModel.addElement(new Pair("^", "^"));
        defaultListModel.addElement(new Pair("%", "#"));
        defaultListModel.addElement(new Pair("Omega Constant", "[Om]*3"));
        defaultListModel.addElement(new Pair("Sierpiński Constant", "2*[Ks]"));
        defaultListModel.addElement(new Pair("Euler-Mascheroni Constant", "2*[gam]"));
        defaultListModel.addElement(new Pair("Last result", lastResult));
        functionList = new JList(defaultListModel);

        functionList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(e.getClickCount()==2){
                    if(defaultListModel.get(functionList.getSelectedIndex())==defaultListModel.lastElement()){
                        if(lastFormulaInput.equals(""))
                            JOptionPane.showMessageDialog(null,"Historia operacji pusta","Error",JOptionPane.INFORMATION_MESSAGE);
                    }
                    String insertEquation = defaultListModel.getElementAt(functionList.getSelectedIndex()).getValue();
                    StringBuilder actualEquation = new StringBuilder(formulaInput.getText());

                    actualEquation.insert(formulaInput.getCaretPosition(),insertEquation);
                    formulaInput.setText(actualEquation.toString());

                    if(formulaInput.getText().contains(")"))
                        formulaInput.setCaretPosition(formulaInput.getText().indexOf(")"));
                    else if(formulaInput.getText().contains("!")) {
                        formulaInput.setCaretPosition(formulaInput.getText().indexOf('!'));
                    }
                    formulaInput.requestFocus();

                }
            }
        });

        return functionList;
    }

    private JTextField formulaInputInitialize(){
        formulaInput = new JTextField();
        formulaInput.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode()== KeyEvent.VK_UP){
                    if(lastFormulaInput.equals(""))
                        JOptionPane.showMessageDialog(null,"Historia operacji pusta","Error",JOptionPane.INFORMATION_MESSAGE);
                        else
                    formulaInput.setText(lastFormulaInput);
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
        formulaInput.addActionListener(e->{
            formulaInputCalculate();
        });
        evalButton.addActionListener(e->{
            formulaInputCalculate();
        });

        return formulaInput;
    }

    private void printResult(String equation,String results){
        String result = MessageFormat.format("{0} \n \t\t= {1}\n==========\n", equation, results);
        lastFormulaInput= equation;
        defaultListModel.lastElement().stringsPair.setValue(equation);
        formulaInput.setText(null);
        historyTextArea.append(result);
    }

    private void formulaInputCalculate(){
        String equation = formulaInput.getText();
        Expression exp = new Expression(equation);
        try {
            if (exp.checkSyntax()) {
                Double result = exp.calculate();
                if(result.isNaN())
                    throw new ArithmeticException("Not a number");
                printResult(equation,String.valueOf(result));
            } else {
                String errorMessage = exp.getErrorMessage();
                throw new IllegalArgumentException(errorMessage);
            }
        }catch(IllegalArgumentException e){
            JOptionPane.showMessageDialog(null,e,"ERROR",JOptionPane.ERROR_MESSAGE);
        }catch(ArithmeticException e){
            JOptionPane.showMessageDialog(null,e,"ERROR",JOptionPane.ERROR_MESSAGE);
        }


    }



    private final GridBagConstraints constraints(int x,int y,int wx,int wy, int gridBagConstraints, Insets insets){
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.weightx = wx;
        constraints.weighty = wy;
        constraints.fill = gridBagConstraints;
        constraints.insets = insets;
        return constraints;
    }
}
