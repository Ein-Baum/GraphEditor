package me.EinBaum.de.graphs.ui;

import java.util.Stack;

import me.EinBaum.de.graphs.Graph;
import me.Mstudio.engine.graphics.ui.advancedComponents.UiBlock;
import me.Mstudio.engine.graphics.ui.constraints.CenterConstraint;
import me.Mstudio.engine.graphics.ui.constraints.RelativeToValueConstraint;
import me.Mstudio.engine.graphics.ui.constraints.StickToConstraint;
import me.Mstudio.engine.graphics.ui.constraints.StickToParentConstraint;
import me.Mstudio.engine.math.interpolation.SmoothInterpolator;
import me.Mstudio.engine.utils.Color;

public class Timeline extends UiBlock{
	
	protected static int maxCards = 7;
	
	private Stack<GraphRollblack> graphStack;
	private Stack<GraphRollblack> undidStack;
	
	public Timeline(Graph graph) {
		graphStack = new Stack<>();
		undidStack = new Stack<>();
		save(graph, "Root");
		setColor(new Color(0,0,0,0f));
	}
	
	private void relocateFirstElement() {
		graphStack.get(0).setPositionInterpolated(new StickToParentConstraint(StickToConstraint.LEFT, new RelativeToValueConstraint(GraphRollblack.WIDTH, (Math.min(0, maxCards - graphStack.size())))).setInverse(true), new CenterConstraint(true), new SmoothInterpolator(), 0.1f);
	}
	
	public Graph save(Graph graph) {
		return save(graph, "Unspecified action");
	}
	
	public Graph saveCurrent() {
		return saveCurrent("Unspecified action");
	}
	
	public Graph save(Graph graph, String cause) {
		
		GraphRollblack card = new GraphRollblack(graph, cause);
		card.setParent(this);
		card.activate();
		
		if(!graphStack.empty()) {
			card.setPosition(new StickToConstraint(graphStack.peek(), StickToConstraint.RIGHT), new CenterConstraint(true));
		}
		
		graphStack.push(card);
		relocateFirstElement();
		
		undidStack.clear();
		
		return graph;
	}
	
	public Graph saveCurrent(String cause) {
		
		GraphRollblack card = new GraphRollblack(graphStack.peek().getGraph().makeCopy(), cause);
		card.setParent(this);
		card.activate();
		
		if(!graphStack.empty()) {
			card.setPosition(new StickToConstraint(graphStack.peek(), StickToConstraint.RIGHT), new CenterConstraint(true));
		}
		
		graphStack.push(card);
		relocateFirstElement();
		
		undidStack.clear();
		
		return card.getGraph();
	}
	
	public Graph rollbackTo(GraphRollblack rollback) {
		return rollback(graphStack.size() - graphStack.indexOf(rollback));
	}
	
	public Graph rollback(int n) {
		
		GraphRollblack toReturn = null;
		
		while(n > 0 && graphStack.size() > 0) {
			toReturn = graphStack.pop();
			toReturn.deactivate();
			undidStack.push(toReturn);
			n--;
		}

		if(graphStack.isEmpty()) {
			graphStack.push(toReturn);
			toReturn.activate();
		}
		
		relocateFirstElement();
		
		return toReturn.getGraph();
	}
	
	public Graph rollForwards(int n) {
		GraphRollblack toReturn = null;
		
		while(n > 0 && undidStack.size() > 0) {
			toReturn = undidStack.pop();
			graphStack.push(toReturn);
			toReturn.activate();
			n--;
		}
		System.out.println(toReturn);

		relocateFirstElement();
		
		return toReturn == null ? graphStack.peek().getGraph() : toReturn.getGraph();
	}
	

}
