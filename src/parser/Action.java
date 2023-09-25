package parser;

abstract class Action {
    static class Shift extends Action{
        State state;

        Shift(State state){
            this.state = state;
        }

        @Override
        public String toString() {
            return "Shift{" + state + "} ";
        }
    }

    static class Reduce extends Action{
        Production production;
        Reduce (Production prod){
            this.production = prod;
        }

        @Override
        public String toString() {
            return "Reduce{" + production + "} ";
        }
    }

    static class Accept extends Action{
        @Override
        public String toString() {
            return "Accept{} ";
        }
    }


}
