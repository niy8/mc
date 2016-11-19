package formula.pathFormula;

import formula.*;
import formula.stateFormula.*;

import java.util.ArrayList;
import java.util.Set;

import tsmodel.TSModel;
import tsmodel.TSState;
import tsmodel.TSTransition;

public class Until extends PathFormula {
    public final StateFormula left;
    public final StateFormula right;
    private Set<String> leftActions;
    private Set<String> rightActions;
    private boolean isValid = true;

    public Until(StateFormula left, StateFormula right, Set<String> leftActions, Set<String> rightActions) {
        super();
        this.left = left;
        this.right = right;
        this.leftActions = leftActions;
        this.rightActions = rightActions;
    }

    public Set<String> getLeftActions() {
        return leftActions;
    }

    public Set<String> getRightActions() {
        return rightActions;
    }

    @Override
    public void writeToBuffer(StringBuilder buffer) {
        buffer.append("(");
        left.writeToBuffer(buffer);
        buffer.append(" " + FormulaParser.UNTIL_TOKEN + " ");
        right.writeToBuffer(buffer);
        buffer.append(")");
    }

	@Override
	public boolean isValidState(TSState state, StateFormula sf) {
		boolean[] visited = new boolean[TSModel.numberOfStates];
		if(ForAll.class.isInstance(sf)){
			recursiveTraversal(state, visited, true);
			return isValid;
		} else if(ThereExists.class.isInstance(sf)){
			///recursiveTraversal(state, visited);
			return true;
		} else {
			return false;
		}
	}
		
	public void recursiveTraversal(TSState state, boolean[] visited, boolean currentLeft) {
		if (visited[state.getIndex()]) {
			return;
		}		
		if(left.isValidState(state)){
			isValid = true;
		} else {
			currentLeft = false;
			if((!currentLeft) && right.isValidState(state)){
				return;
			} else {
				isValid = false;
			}
		}
		visited[state.getIndex()] = true;
		ArrayList<TSTransition> transitions = state.getTransitions();
		for (int i = 0; i < transitions.size(); i++) {
			TSTransition currentT = transitions.get(i);
			TSState futureState = currentT.getTarget();
			recursiveTraversal(futureState, visited, currentLeft);
		}
	} 
	

}
