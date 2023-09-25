package parser;

import lexer.SymbolName;

import java.util.ArrayList;
import java.util.Objects;

public class Production {
    SymbolName head;
    ArrayList<SymbolName> body;
    public Production(SymbolName head, ArrayList<SymbolName> body) {
        this.head = head;
        this.body = body;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Production that = (Production) o;
        return head == that.head && Objects.equals(body, that.body);
    }

    @Override
    public int hashCode() {
        return Objects.hash(head, body);
    }

    @Override
    public String toString() {
        return "Production{"+ head +
                "-->" + body +
                '}';
    }
}