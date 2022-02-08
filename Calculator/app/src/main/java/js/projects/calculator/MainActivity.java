package js.projects.calculator;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import js.projects.calculator.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    boolean isDecimal= false;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        binding.one.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"1"));
        binding.two.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"2"));
        binding.three.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"3"));
        binding.four.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"4"));
        binding.five.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"5"));
        binding.six.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"6"));
        binding.seven.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"7"));
        binding.eight.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"8"));
        binding.nine.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"9"));
        binding.zero.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"0"));
        binding.pi.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"π"));
        binding.period.setOnClickListener(v -> {
            if(!isDecimal){
                if(binding.ioMain.getText().equals("")) binding.ioMain.setText("0.");
                else binding.ioMain.setText(binding.ioMain.getText().toString()+".");
                isDecimal = true;
            }
        });
        binding.ac.setOnClickListener(v -> {
            binding.ioMain.setText("");
            binding.output.setText("");
        });
        binding.delete.setOnClickListener(v -> {
            String str = binding.ioMain.getText().toString();
            if(str.charAt(str.length()-1) == '.') isDecimal = false;
            str = str.substring(0, str.length()-1);
            binding.ioMain.setText(str);
        });

        binding.bracketOpen.setOnClickListener(v -> binding.ioMain.setText(binding.ioMain.getText().toString()+"("));
        binding.bracketClose.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty()){
            binding.ioMain.setText(binding.ioMain.getText().toString()+")");
            }
        });
        binding.caret.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty()) binding.ioMain.setText(binding.ioMain.getText().toString()+"^");
        });
        binding.fac.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty())
            binding.ioMain.setText(binding.ioMain.getText().toString()+"!");
        });
        binding.plus.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty())
            binding.ioMain.setText(binding.ioMain.getText().toString()+"+");
        });
        binding.minus.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty())
            binding.ioMain.setText(binding.ioMain.getText().toString()+"-");
        });
        binding.multiply.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty())
            binding.ioMain.setText(binding.ioMain.getText().toString()+"x");
        });
        binding.divide.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty())
            binding.ioMain.setText(binding.ioMain.getText().toString()+"÷");
        });
        binding.percent.setOnClickListener(v -> {
            if(!binding.ioMain.getText().toString().isEmpty())
            binding.ioMain.setText(binding.ioMain.getText().toString()+"%");
        });

        binding.equals.setOnClickListener(v ->{
            binding.ioMain.setText(evaluate());
            binding.output.setText("");
        });
        binding.ioMain.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String str = binding.ioMain.getText().toString();
                str = str.replace('÷','/').replace('x','*');
                if(str.length()>0){
                    str = Character.toString(str.charAt(str.length() - 1));
                    if (str.equals("!") || str.equals(")") || !isOperator(str)) {
                        binding.output.setText(evaluate());
                    }
                }
            }
        });

    }

    private String evaluate(){
        String s = binding.ioMain.getText().toString();
        s = s.replace('÷','/').replace('x','*');
        List<String> postfix;
        postfix = infix2postfix(s);
        if(postfix.size() == 1){
            return postfix.get(0);
        }
        return evaluatePostfix(postfix);
    }
    final static double PI = Math.PI;
    static List<String> infix2postfix(String infix){
        List<String> postfix = new ArrayList<>();
        Stack<Character> operators = new Stack<>();
        for (int i = 0; i < infix.length(); i++) {
            StringBuilder operand = new StringBuilder();
            while(i<infix.length()&&!isOperator(Character.toString(infix.charAt(i)))){
                operand.append(infix.charAt(i++));
            }
            if(!operand.toString().isEmpty()){
                if(operand.toString().equals("π")) postfix.add(Double.toString(PI));
                else postfix.add(operand.toString());
            }
            if(i>=infix.length()) break;
            if (isOperator(Character.toString(infix.charAt(i)))) {
                if(infix.charAt(i) == ')'){
                    try{
                        char ch = operators.pop();
                        while(ch!='('){
                            postfix.add(Character.toString(ch));
                            ch = operators.pop();
                        }
                    }
                    catch (Exception e){
                        List<String> error = new ArrayList<>();
                        error.add("Invalid expression");
                        return error;
                    }
                }
                else if (operators.isEmpty()) {
                    operators.push(infix.charAt(i));
                } else {
                    if (operators.peek() != '('&&operatorPresidency(operators.peek()) > operatorPresidency(infix.charAt(i))) {
                        postfix.add(Character.toString(operators.pop()));
                    }
                    operators.push(infix.charAt(i));
                }
            }
            else{
                List<String> error = new ArrayList<>();
                error.add("Invalid operator");
                return error;
            }
        }
        while(!operators.isEmpty()){
            postfix.add(Character.toString(operators.pop()));
        }
        return postfix;
    }
    static String evaluatePostfix(List<String> postfix){
        int i=0;
        if(postfix.isEmpty()){
            return "";
        }
        Stack<String> eval = new Stack<>();
        while(i<postfix.size()){
            if(isOperator(postfix.get(i))){
                if(postfix.get(i).equals("!")){
                    double op = Double.parseDouble(eval.pop());
                    try{
                        eval.push(Integer.toString((int) fact((int)op)));
                        i++;
                    }catch (Exception e){
                        return "invalid expression";
                    }
                }else {
                    double op2 = Double.parseDouble(eval.pop());
                    double op1;
                    try {
                        op1 = Double.parseDouble(eval.pop());
                    }catch (Exception e){
                        return "invalid expression";
                    }
                    eval.push(Double.toString(operation(op1, op2, postfix.get(i))));
                    i++;
                }
            }
            else {
                eval.push(postfix.get(i++));
            }
        }
        return String.valueOf(eval.pop());
    }
    static int operatorPresidency(char operator){
        switch (operator){
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            case '^':
            case '%':
            case '!':
                return 3;
            case '(':
                return 4;
            default:
                return -1;
        }
    }
    static boolean isOperator(String ch){
        switch (ch){
            case "+":
            case "-":
            case "*":
            case "/":
            case "^":
            case "(":
            case ")":
            case "%":
            case "!":
                return true;
            default:
                return false;
        }
    }
    static double operation(double op1, double op2, String operator){
        switch (operator){
            case "+":
                return op1+op2;
            case "-":
                return op1-op2;
            case "*":
                return op1*op2;
            case "/":
                return op1/op2;
            case "^":
                return Math.pow(op1,op2);
            case "%":
                return op1*op2/100;
            default:
                return -1;
        }
    }
    static double fact(double n){
        if(n==0) return 1;
        if(n==1) return 1;
        return n*fact(n-1);
    }

}