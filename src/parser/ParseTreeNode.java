package parser;

import lexer.SymbolName;

import java.util.ArrayList;
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
    public String toString() {
        if (value.isPresent()) return "Node{"+ name + ", children=" + children + ", val=" + value.get() + '}';
        else return "Node{"+ name + ", children=" + children + '}';
    }
}
