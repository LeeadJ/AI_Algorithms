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

}
