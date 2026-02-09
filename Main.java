import java.util.Scanner;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        RPN calc = new RPN();
        double result = 0;
        
        // calc.setExpression("1.5;12;+;8;*;0.5;^;-1;+"); // 1 1 + 5 * 2 -
        // double result = calc.calculate();
        
        while (true) {
            System.out.println("Input your numbers and operands: ");
            
            // String currentExpression = "";
            
            calc.setExpression(keyboard.nextLine());
            result = calc.calculate();
            System.out.println("\n\nThe result of the calculation is: " + result);
            
            System.out.println("\n\n\n" + calc.exportRegisters());
        }
    }
}

class RPN {
    private String expression;
    private Register register = new Register();
    
    public RPN () {
        expression = "";
    }

    public RPN (String e) {
        expression = e;
    }
    
    public void setExpression (String e) {
        expression = e;
    }

    public double calculate () { // actually return the result of the full operation
        // System.out.println("Calc function start");
        StringList list = parse(expression);

        String[] parsedString = list.getElements();
        int numElements = list.getNumElements();
        int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};

        double num1 = 0;
        double num2 = 0;
        boolean num1Used = false;
        boolean isNum = false;
        boolean storeInRegister = false;
        double ans = 0;

        for (int i = 0; i < numElements; i++) {
            if (HelperFunctions.checkIfNumber(parsedString[i])) { // checks if the string is a number
                isNum = true;
                if (!num1Used) {
                    num1 = HelperFunctions.convertStringToDouble(parsedString[i]);
                    num1Used = true;
                }
                else {
                    num2 = HelperFunctions.convertStringToDouble(parsedString[i]);
                }
            }
            else { // it must be an operator
                isNum = false;
                
                if (parsedString[i].charAt(0) == '+') { // addition
                    ans = num1 + num2;
                    System.out.println("\nNum1: " + num1 + "\nNum2: " + num2 + "\nResult: " + ans);
                }
                if (parsedString[i].charAt(0) == '-') { // subtraction
                    ans = num1 - num2;
                    System.out.println("\nNum1: " + num1 + "\nNum2: " + num2 + "\nResult: " + ans);
                }
                else if (parsedString[i].charAt(0) == '*') { // multiplication
                    ans = num1 * num2;
                    System.out.println("\nNum1: " + num1 + "\nNum2: " + num2 + "\nResult: " + ans);
                }
                else if (parsedString[i].charAt(0) == '/') { // division
                    ans = num1 / num2;
                    System.out.println("\nNum1: " + num1 + "\nNum2: " + num2 + "\nResult: " + ans);
                }
                else if (parsedString[i].charAt(0) == '^') { // exponentiation
                    ans = Math.pow(num1, num2);
                    System.out.println("\nNum1: " + num1 + "\nNum2: " + num2 + "\nResult: " + ans);
                }
                else if (parsedString[i] == "Ans") { // Ans register
                    storeInRegister = true;
                }
                else if (parsedString[i].charAt(0) == 'A') { // A register
                   storeInRegister = true; 
                }
                else if (parsedString[i].charAt(0) == '=') { // store value in register
                    if (storeInRegister) {
                        if (!HelperFunctions.checkIfNumber(parsedString[i-2])) { // the register isn't just being set to a set value but instead results from Ans
                            register.setRegister(parsedString[i-1], register.getRegister("Ans"));
                        }
                        else { // the register is being set from a decimal value
                            register.setRegister(parsedString[i-1], HelperFunctions.convertStringToDouble(parsedString[i-2]));
                        }
                        storeInRegister = false;
                    }
                    else {
                        throw new IllegalArgumentException("Function caculate: no register defined for \'=\' operator");
                    }
                }
                else { // non valid operator defined
                    // fix this at some point
                    // throw new IllegalArgumentException("function calculate: undefined operator");
                }
                
                register.setRegister("Ans", ans);
                
                num1 = ans;
            }
        }
        
        System.out.println("\n\nans: " + ans);
        return ans;
    }

    private StringList parse (String input) { // convert input string into a list (String[]) of each number and operator as well as return the amount of numbers and operators in total (numE
        input += Defaults.expressionDelimiter;

        StringList list = new StringList();

        String[] parsedString = new String[Defaults.maxStringLength]; // output list of numbers and operators
        char[] temp = new char[Defaults.maxNumberLength]; 
        int parsedIdx = 0; // I end up using this to keep track of the number of total elements in the list
        int tempIdx = 0; // number of elements in temp (char[])

        for (int i = 0; i < input.length(); i++) {
            char currentChar = input.charAt(i);
            
            if (currentChar != Defaults.expressionDelimiter) {
                temp[tempIdx] = currentChar; 
                tempIdx++;
            }
            else {
                parsedString[parsedIdx] = "";
                for (int j = 0; j < tempIdx; j++) {
                    // System.out.println("char " + j + ": " + temp[j] + " at position: " + parsedIdx); // #debug
                    parsedString[parsedIdx] += temp[j];
                }
                // temp = new char[32];
                tempIdx = 0;
                parsedIdx++;
            }
        }

        System.out.print("\nParsed String: ");
        for (int i = 0; i < parsedIdx; i++) {
            System.out.print(parsedString[i] + " ");
        }
        System.out.println();

        return new StringList(parsedString, parsedIdx);
    }
    
    public String exportRegisters () {
        return register.exportRegisters();
    }
}

class StringList {
    private String[] list;
    private int elementsNum;

    public StringList () {
        list = new String[Defaults.maxStringLength];
        elementsNum = Defaults.maxStringLength;
    }
    
    public StringList (int elements) {
        list = new String[elements];
        elementsNum = elements;
    }

    public StringList (String[] string, int elements) {
        list = string;
        elementsNum = elements;
    }

    public int getNumElements () {
        return elementsNum;
    }

    public String[] getElements () {
        return list;
    }

    public void updateElements (String[] string) {
        list = string;
    }

    public void updateNumElements (int elements) {
        elementsNum = elements;
    }
}

class Register {
    private HashMap<String, Double> registers = new HashMap<String, Double>(); // variables
    private String[] registerNames = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "l", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "x", "y", "z", "w", "Ans"}; // length of 30

    public Register () {
        for (int i = 0; i < registerNames.length; i++) {
            registers.put(registerNames[i], 0.0);
        }
    }
    
    public Register(String input) {
        importRegisters(input);
    }
    
    public void setRegister (String name, double value) {
        registers.put(name, value);
    }
    
    public double getRegister (String name) {
        return registers.get(name);
    }
    
    public void importRegisters (String input) {
        String[] parsedInput = new String[registerNames.length]; // might have to manually set to 30 but idk
        String temp = "";
        int numInputs = 0;
        
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) != ',') {
                temp += input.charAt(i);
            }
            else {
                parsedInput[numInputs] = temp;
                temp = "";
                numInputs++;
            }
        }
        
        double[] parsedInputDouble = new double[registerNames.length];
        
        for (int i = 0; i < numInputs; i++) {
            parsedInputDouble[i] = HelperFunctions.convertStringToDouble(parsedInput[i]);
        }
        
        
        for (int i = 0; i < registerNames.length; i++) {
            registers.put(registerNames[i], parsedInputDouble[i]);
        }
    }
    
    public String exportRegisters () {
        String output = "";
        
        for (int i = 0; i < registerNames.length; i++) {
            output += registers.get(registerNames[i]) + ",";
        }
        
        return output;
    }
}

class HelperFunctions {
    private static char[] validCharacters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'};
    
    public HelperFunctions () {
        
    }
    
    /*
    public char[] getValidCharactersInADouble () {
        return validCharacers;
    }
    */
    
    public static boolean checkIfNumber (String input) {
        // check if the inputted string is actually a valid number and not something else
        // char[] validCharacters = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', '-'};
        boolean valid = false;

        // System.out.println("\n\nInput: " + input);

        for (int i = 0; i < input.length(); i++) {
            valid = false;
            for (int j = 0; j < validCharacters.length; j++) {
                if (input.charAt(i) == validCharacters[j]) {
                    valid = true;
                    break;
                }
            }
        }
        
        if (input.charAt(0) == '-' && input.length() == 1) { // just so it doesn't think the minus operand is actually a number and not an operand
            valid = false;
        }

        if (valid) {
            return true;
        }
        else {
            return false;
        }
    }

    public static double convertStringToDouble (String input) {
        // add ".0" to the end of a number if a decimal part is not already present
        boolean hasDecimal = false;
       
        for (int i = 0; i < input.length(); i++) {
            if (input.charAt(i) == '.') {
                hasDecimal = true;
            } 
        }
       
        if (!hasDecimal) {
           input += ".0";
        }
        
        // get the sign of the number
        boolean isNegative = false;
        
        if (input.charAt(0) == '-') {
            isNegative = true;
        }
       
        // split the input into its whole and decimal components eg. 18.15 = 18 | 15
        String wholeComponent = input.substring(0, input.indexOf('.')); 
        String decimalComponent = input.substring(input.indexOf('.') + 1, input.length());
        
        // System.out.println("Whole: " + wholeComponent);
        // System.out.println("Decimal: " + decimalComponent);
        
        // reverse the whole number string so the string goes from least significant number to greatest significant number, I don't have to do this to the decimal since it's already in the format I want eg. greatest to least significant number
        String temp = "";
        
        for (int i = wholeComponent.length(); i > 0; i--) {
            temp += wholeComponent.charAt(i - 1); // subtract 1 since wholeComponent.length() returns say 21 but since the string itself is indexed at 0, it would be out of bounds trying to index the 22nd element
        }
        
        wholeComponent = temp;
        
        

        // convert the whole number String into an array of ints
        int[] numbers = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        
        int[] whole = new int[Defaults.maxNumberLength];
        
        for (int i = 0; i < wholeComponent.length(); i++) {
            for (int j = 0; j < numbers.length; j++) {
                if (wholeComponent.charAt(i) == Character.forDigit(numbers[j], 10)) {
                    whole[i] = numbers[j];
                    break;
                    // System.out.println("Made a number match (whole)!");
                }
            }
        }
        
        // convert the decimal number String into an array of ints
        int[] decimal = new int[Defaults.maxNumberLength];
        
        for (int i = 0; i < decimalComponent.length(); i++) {
            for (int j = 0; j < numbers.length; j++) {
                if (decimalComponent.charAt(i) == Character.forDigit(numbers[j], 10)) {
                    decimal[i] = numbers[j];
                    break;
                    // System.out.println("Made a number match (decimal)!: " + numbers[j]);
                }
            }
        }
        
        // convert the whole number and decimal arrays into a final double
        double result = 0;
        
        for (int i = 0; i < wholeComponent.length(); i++) {
            result += whole[i] * Math.pow(10, i);
        }
        
        for (int i = 0; i < decimalComponent.length(); i++) {
            // System.out.println("Decimal added: " + decimal[i - 1]);
            // System.out.println("Place of that: " + Math.pow(10, -1 * i));
            result += decimal[i] * Math.pow(10, -1 * (i + 1)); // add 1 to i so the first decimal starts at 0.1 and not 1
        }
        
        if (isNegative) {
            result *= -1;
        }
        
        // System.out.println("Resultant double: " + result);

        return result;
    }
}

class Defaults {
    public static final int maxStringLength = 64; // used for storing the input sequence of inputs eg. "1;+;2;"
    public static final int maxNumberLength = 32; // used for storing the numbers in arrays
    public static final char expressionDelimiter = ' ';
}
