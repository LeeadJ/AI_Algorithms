import java.util.ArrayList;
import java.util.HashMap;

public class Variable {
    private String _name;
    private ArrayList<Variable> _parents;
    private ArrayList<Variable> _children;
    private ArrayList<String> _outcomes;
    public CPT _cpt;

    /**
     * Constructor for the Variable.
     * @param name - Name of the event. */
    public Variable(String name){
        this._name = name;
        this._parents= new ArrayList<>();
        this._children = new ArrayList<>();
        this._outcomes = new ArrayList<>();
        this._cpt = new CPT();
    }

    /**
     * Copy Constructor for the Variable: */
    public Variable(Variable other){
        this._name = other._name;

        this._parents = new ArrayList<>();
        this._parents.addAll(other._parents);

        this._children = new ArrayList<>();
        this._children.addAll(other._children);

        this._outcomes = new ArrayList<>();
        this._outcomes.addAll(other._outcomes);

        this._cpt = new CPT(other._cpt);
    }

    /** Getters: */
    public String getName(){ return _name;}
    public ArrayList<Variable> getParents() {return _parents;}
    public ArrayList<Variable> getChildren() {return _children;}

    public ArrayList<String> getOutcomes() {return _outcomes;}
    public CPT getCPT() {return _cpt;}

    /** Setters: */
    public void setName(String name) {this._name = name;}
    public void setParents(ArrayList<Variable> parents) {this._parents = parents;}
    public void setChildren(ArrayList<Variable> children) {this._children = children;}
    public void set_outcomes(ArrayList<String> outcomes) {this._outcomes = outcomes;}

    /** Functions: */
    @Override
    public String toString(){
        String var = "\nVariable Name: " + this._name;
        var += "\nParents: [";
        for(Variable p : this._parents)
            var += p.getName() + ", ";
        if(this._parents.size() != 0)
            var = var.substring(0, var.length()-2);
        var += "]\nChildren: [";
        for(Variable c : this._children)
            var += c.getName() + ", ";
        if(this._children.size() != 0)
            var = var.substring(0, var.length()-2);
        var += "]\nOutcomes: " + this._outcomes;
        var += "\n\tCPT: " + this._cpt.toString();
        return var;
    }

    public void addParent(Variable p) {this._parents.add(p);}
    public void addChild(Variable c) {this._children.add(c);}
    public void addOutcomes(String out) {this._outcomes.add(out);}

    /**@param values - the values of the probability from the xml file.
     * Each index of the String array represents a row of the CPT table.*/
    public void initCPT(String[] values){
        //System.out.println("\t\tINIT CPT START: ");
        this._cpt._row_size = values.length;
        //System.out.println("CPT ROW SIZE: "+ this._cpt._row_size);
        for(int i=0; i<values.length; i++){
            HashMap<String, String> rows = new HashMap<>();
            rows.put(this._name, this._outcomes.get(i % this._outcomes.size()));
            int j = this._parents.size() -1;
            int outcome_size = this._outcomes.size();
            while(j >= 0){
                Variable temp = this._parents.get(j);
                rows.put(temp.getName(), temp.getOutcomes().get((i / outcome_size % temp.getOutcomes().size())));
                j--;
                outcome_size *= temp.getOutcomes().size();
            }
            rows.put("Pr", values[i]);
            this._cpt._cpt_table.add(rows);
        }

    }

    /**Recursion Method to check if a current variable is a descendant of a given variable.
     * @param other - potential ancestor variable.*/
    public boolean isAncestor(Variable other){
        if(other.getChildren().contains(this))
            return true;
        for(int i=0; i<other.getChildren().size(); i++){
            if(isAncestor(other.getChildren().get(i)))
                return true;
        }
        return false;
    }

    public static void main(String[] args) {
        Variable a = new Variable("A");
        Variable b = new Variable("B");
        Variable j = new Variable("J");
        Variable e = new Variable("E");
        Variable m = new Variable("M");
        b.addChild(a);
        e.addChild(a);
        a.addChild(j);
        a.addChild(m);
        a.addParent(e);
        a.addParent(b);
        j.addParent(a);
        m.addParent(a);

        System.out.println(b.toString());
        System.out.println(e.toString());
        System.out.println(a.toString());
        System.out.println(j.toString());
        System.out.println(m.toString());
    }

}
