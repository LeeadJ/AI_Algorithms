import java.util.ArrayList;
import java.util.HashMap;

public class Variable {
    /**
     * The Variable Class will hold five variables:
     * 1) _name: The name of the variable.
     * 2) _parents: An ArrayList of type Variable representing the current variables parents.
     * 3) _children: An ArrayList of type Variable representing the current variables children.
     * 4) _outcomes: An ArrayList of type String representing the current variables outcome probabilities.
     * 5) _cpt: The current variables CPT.
     */
    private String _name;
    private ArrayList<Variable> _parents;
    private ArrayList<Variable> _children;
    private ArrayList<String> _outcomes;
    public CPT _cpt;

    /**
     * Constructor for the Variable.
     *
     * @param name - Name of the event.
     */
    public Variable(String name) {
        _name = name;
        _parents = new ArrayList<>();
        _children = new ArrayList<>();
        _outcomes = new ArrayList<>();
        _cpt = new CPT();
    }

    /**
     * Copy Constructor for the Variable:
     *
     * @param other - The variable to be copied from.
     */
    public Variable(Variable other) {
        _name = other._name;

        _parents = new ArrayList<>();
        _parents.addAll(other._parents);

        _children = new ArrayList<>();
        _children.addAll(other._children);

        _outcomes = new ArrayList<>();
        _outcomes.addAll(other._outcomes);

        _cpt = new CPT(other._cpt);
    }

    /**
     * Getters:
     */
    public String getName() {
        return _name;
    }

    public ArrayList<Variable> getParents() {
        return _parents;
    }

    public ArrayList<Variable> getChildren() {
        return _children;
    }

    public ArrayList<String> getOutcomes() {
        return _outcomes;
    }

    public CPT getCPT() {
        return _cpt;
    }

    /**
     * Setters:
     */
    public void setName(String name) {
        _name = name;
    }

    public void setParents(ArrayList<Variable> parents) {
        _parents = parents;
    }

    public void setChildren(ArrayList<Variable> children) {
        _children = children;
    }

    public void set_outcomes(ArrayList<String> outcomes) {
        _outcomes = outcomes;
    }

    /** Functions: */

    /**
     * @return - String of the Variable.
     */
    @Override
    public String toString() {
        StringBuilder var = new StringBuilder("\nVariable Name: " + this._name);
        var.append("\nParents: [");
        for (Variable p : this._parents)
            var.append(p.getName()).append(", ");
        if (this._parents.size() != 0)
            var = new StringBuilder(var.substring(0, var.length() - 2));
        var.append("]\nChildren: [");
        for (Variable c : this._children)
            var.append(c.getName()).append(", ");
        if (this._children.size() != 0)
            var = new StringBuilder(var.substring(0, var.length() - 2));
        var.append("]\nOutcomes: ").append(this._outcomes);
        var.append("\n\tCPT: ").append(this._cpt.toString());
        return var.toString();
    }


    public void addParent(Variable p) {
        this._parents.add(p);
    }

    public void addChild(Variable c) {
        this._children.add(c);
    }

    public void addOutcomes(String out) {
        this._outcomes.add(out);
    }

    /**
     * @param values - the values of the probability from the xml file.
     *               Each index of the String array represents a row of the CPT table.
     */
    public void initCPT(String[] values) {
        //System.out.println("\t\tINIT CPT START: ");
        _cpt._row_size = values.length;
        //System.out.println("CPT ROW SIZE: "+ this._cpt._row_size);
        for (int i = 0; i < values.length; i++) {
            HashMap<String, String> rows = new HashMap<>();
            rows.put(_name, _outcomes.get(i % _outcomes.size()));
            int j = _parents.size() - 1;
            int outcome_size = _outcomes.size();
            while (j >= 0) {
                Variable temp = _parents.get(j);
                rows.put(temp.getName(), temp.getOutcomes().get((i / outcome_size % temp.getOutcomes().size())));
                j--;
                outcome_size *= temp.getOutcomes().size();
            }
            rows.put("Prob", values[i]);
            _cpt._cpt_table.add(rows);
        }
    }

    /**
     * Recursion Method to check if a current variable is a descendant of a given variable.
     *
     * @param other - potential ancestor variable.
     * @return - True or false.
     */
    public boolean isAncestor(Variable other) {
        if (other.getChildren().contains(this))
            return true;
        for (int i = 0; i < other.getChildren().size(); i++) {
            if (isAncestor(other.getChildren().get(i)))
                return true;
        }
        return false;
    }
}
