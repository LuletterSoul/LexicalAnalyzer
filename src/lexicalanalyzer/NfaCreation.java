package lexicalanalyzer;

import java.util.Stack;

public class NfaCreation {

    Stack<Nfa> nfaStack = new Stack<Nfa>();

    Nfa buildfNfa(String postfix, String expression) {
        int flag = 0;     //flag to differentiate between zerp or more operation and the mult operation.
                          ////flag to differentiate between one or more operation and the addition operation.
        for (int i = 0; i < postfix.length(); i++) {
            char input = postfix.charAt(i);
            //for handling brackets
            if(postfix.length()==1){
                 nfaStack.push(Nfa.AddTransition(input, expression));
            }
           else if (isInput(input)) {
                nfaStack.push(Nfa.AddTransition(input, expression));
            } else if (input == '.') {
                Nfa b = nfaStack.pop();
                Nfa a = nfaStack.pop();
                nfaStack.push(Concat(a, b, expression));
            } else if (input == '*') {

                if (i == 0) {
                    nfaStack.push(Nfa.AddTransition(input, expression));
                    flag = 1;
                } else if (i != postfix.length() - 1) {
                    if (postfix.charAt(i + 1) == '|') {
                        flag = 1;
                        nfaStack.push(Nfa.AddTransition(input, expression));
                    }
                }

                if (flag == 0) {
                    Nfa top = nfaStack.pop();
                    nfaStack.push(Kleene(top, expression));
                }
            } else if (input == '+') {

                if (i == 0) {
                    nfaStack.push(Nfa.AddTransition(input, expression));
                    flag = 1;
                } else if (i != postfix.length() - 1) {

                    if (postfix.charAt(i + 1) == '|') {
                        nfaStack.push(Nfa.AddTransition(input, expression));
                        flag = 1;
                    }
                }

                if (flag == 0) {
                    Nfa top = nfaStack.pop();
                    nfaStack.push(Plus(top, expression));
                }
            } else if (input == '|') {
                Nfa b = nfaStack.pop();
                Nfa a = nfaStack.pop();
                nfaStack.push(Union(a, b, expression));
            }
            flag = 0;

        }
        //a part is added here to get complete nfa
        Nfa completeNfa = nfaStack.pop();
        return completeNfa;
    }
    // ~ is epsilon

    public static Nfa Concat(Nfa a, Nfa b, String expression) {
        Nfa.connectStates(a.getEnd(), b.getStart(), '~');
        a.getEnd().setIsAccepting(false);
        Nfa ret = new Nfa(a.getStart(), b.getEnd());
        return ret;
    }

    public static Nfa Kleene(Nfa a, String expression) {
        State new_start = new State(Nfa.last_id++, false);
        //
        Nfa.states.add(new_start);
        //
        State new_end = new State(Nfa.last_id++, true);
        new_end.setPattern(expression);
        //
        Nfa.states.add(new_end);
        //
        a.getEnd().setIsAccepting(false);
        Nfa.connectStates(new_start, a.getStart(), '~');
        Nfa.connectStates(a.getEnd(), new_end, '~');
        Nfa.connectStates(new_start, new_end, '~');
        Nfa.connectStates(new_end, new_start, '~');
        Nfa ret = new Nfa(new_start, new_end);
        return ret;
    }

    public static Nfa Plus(Nfa a, String expression) {
        State new_start = new State(Nfa.last_id++, false);
        //
        Nfa.states.add(new_start);
        //
        State new_end = new State(Nfa.last_id++, true);
        new_end.setPattern(expression);
        //
        Nfa.states.add(new_end);
        //
        a.getEnd().setIsAccepting(false);
        Nfa.connectStates(new_start, a.getStart(), '~');
        Nfa.connectStates(a.getEnd(), new_end, '~');
        Nfa.connectStates(new_end, new_start, '~');
        Nfa ret = new Nfa(new_start, new_end);
        return ret;
    }

    public static Nfa Union(Nfa a, Nfa b, String expression) {
        State new_start = new State(Nfa.last_id++, false);
        //
        Nfa.states.add(new_start);
        //
        State new_end = new State(Nfa.last_id++, true);
        new_end.setPattern(expression);
        //
        Nfa.states.add(new_end);
        //
        a.getEnd().setIsAccepting(false);
        b.getEnd().setIsAccepting(false);
        Nfa.connectStates(new_start, a.getStart(), '~');
        Nfa.connectStates(new_start, b.getStart(), '~');
        Nfa.connectStates(a.getEnd(), new_end, '~');
        Nfa.connectStates(b.getEnd(), new_end, '~');
        Nfa ret = new Nfa(new_start, new_end);
        return ret;
    }

    boolean isInput(char c) {
        if (Character.isAlphabetic(c)) {
            return true;
        }

        if (Character.isDigit(c)) {
            return true;
        }
        if (c == '<' || c == '>' || c == '=' || c == '!' || c == '{' || c == '}' || c == 59 || c == 44 || c == '(' || c == ')' || c == '/' || c == '-') {
            return true;
        }
        return false;

    }
}
