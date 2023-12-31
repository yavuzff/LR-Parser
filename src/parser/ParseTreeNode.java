package parser;

import lexer.SymbolName;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;


public class ParseTreeNode {

    private SymbolName name;
    private ArrayList<ParseTreeNode> children;
    private Optional<String> value;

    public ParseTreeNode(SymbolName name) {
        this.name = name;
        this.value = Optional.empty();
        this.children = new ArrayList<>();
    }

    public ParseTreeNode(SymbolName name, String value) {
        this.name = name;
        this.value = Optional.of(value);
        this.children = new ArrayList<>();
    }

    public Double evaluate(){
        if (children.size() == 1) return children.get(0).evaluate();
        switch (name){
            case E5: return children.get(1).evaluate(); //parentheses
            case E4: return Math.sqrt(children.get(0).evaluate()); //TODO: return factorial of this (instead of sqrt)
            case E3: return Math.cos(children.get(1).evaluate());
            case E2: return children.get(0).evaluate() * children.get(2).evaluate();
            case E1: return children.get(0).evaluate() - children.get(2).evaluate();
            case E0: return children.get(0).evaluate() + children.get(2).evaluate();
            case NUM: {
                long minus_count = 0;
                int i = 0;
                while (!Character.isDigit(value.get().charAt(i)) && value.get().charAt(i) != '.'){
                    if (value.get().charAt(i) == '-') minus_count ++;
                    i++;
                }
                return (minus_count % 2 == 0 ? 1: -1) * Double.parseDouble(value.get().substring(i));}
        }
        assert false; //evaluate shouldn't be called for terminals that are operations
        return 0.0;
    }


    public SymbolName getName() {return name;}

    public ArrayList<ParseTreeNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<ParseTreeNode> children) {
        this.children = children;
    }

    public Optional<String> getValue() {return value;}

    public void setValue(String value) {this.value = Optional.of(value);}

    @Override
    public  String toString(){
        StringBuilder buffer = new StringBuilder(50);
        print(buffer, "", "");
        return buffer.toString();
    }

    private void print(StringBuilder buffer, String prefix, String children_prefix){
        buffer.append(prefix);
        buffer.append(name + (value.map(s -> " " + s).orElse("")));
        buffer.append('\n');
        for (Iterator<ParseTreeNode> it = children.iterator(); it.hasNext();){
            ParseTreeNode next = it.next();
            if (it.hasNext()){
                next.print(buffer, children_prefix + "├── ", children_prefix + "│   ");
            } else {
                next.print(buffer, children_prefix + "└── ", children_prefix + "    ");
            }
        }
    }
}
