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
}
