package me.EinBaum.de.graphs.ui;

import org.lwjgl.glfw.GLFW;

import me.EinBaum.de.GraphRuntime;
import me.EinBaum.de.graphs.Graph;
import me.Mstudio.engine.GEngine;
import me.Mstudio.engine.graphics.ui.advancedComponents.ClickableUi;
import me.Mstudio.engine.graphics.ui.animation.transitions.AnimationDriver;
import me.Mstudio.engine.graphics.ui.animation.transitions.Transition;
import me.Mstudio.engine.graphics.ui.constraints.PixelConstraint;
import me.Mstudio.engine.graphics.ui.constraints.RelativeConstraint;
import me.Mstudio.engine.graphics.ui.constraints.ScaleConstraint;
import me.Mstudio.engine.graphics.ui.constraints.StickToConstraint;
import me.Mstudio.engine.graphics.ui.constraints.StickToParentConstraint;
import me.Mstudio.engine.graphics.ui.text.Text;
import me.Mstudio.engine.math.interpolation.SmoothInterpolator;
import me.Mstudio.engine.utils.Color;

public class GraphRollblack extends ClickableUi {
	
	public static ScaleConstraint WIDTH = new RelativeConstraint(1f/Timeline.maxCards), HEIGHT = new RelativeConstraint(1f);
	private static Transition hover = new Transition().addDriver(Transition.Y_POSITION, new AnimationDriver(new PixelConstraint(0), new RelativeConstraint(0.1f), 0.1f, new SmoothInterpolator()).setMode(AnimationDriver.ADD));
	private static final Transition fadeOut = new Transition().addDriver(Transition.ALPHA, Transition.MAX_TO_MIN, new AnimationDriver(0, 1, 0.5f, new SmoothInterpolator())).addDriver(Transition.ALPHA, Transition.MIN_TO_MAX, new AnimationDriver(1, 1, 0.5f, new SmoothInterpolator()));
	
	private Graph graph;
	private String cause;
	
	public GraphRollblack(Graph graph, String description) {
		this.graph = graph;
		this.cause = description;
		this.setScale(WIDTH, HEIGHT);
		this.setCornerRadius(new RelativeConstraint(0.25f));
		this.setColor(new Color(0,0,0, 1f));
		this.setRimColor(new Color(1,1,1, 1f));
		this.setRimFalloff(0.75f);
		this.setRimThickness(0.1f);
		this.addText(new Text(GEngine.resources().getRegistered("DefaultFont").asFont(), description, new RelativeConstraint(0.5f)), new StickToParentConstraint(StickToConstraint.LEFT, new RelativeConstraint(0.1f)).setInverse(true));
		this.getText().getFormat().setColor(new Color(1,1,1,1f));
		this.getText().setMaxLineAmount(2);
		this.getText().setWrapMethod(Text.WRAP_WORDS);
		this.getText().setScale(new RelativeConstraint(1), new RelativeConstraint(5));
		this.getAnimator().addTransition(fadeOut, 0, 0);
	}
	
	public String getCause() {
		return cause;
	}
	
	public Graph getGraph() {
		return graph;
	}
	
	@Override
	public String toString() {
		return graph.toString();
	}

	@Override
	public void click(int button, int mods) {
	}

	@Override
	public void release(int button, int mods) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
			GraphRuntime.setGraph(graph.getTimeline().rollbackTo(this));
		}
	}

	@Override
	public void hover(boolean status) {
		getAnimator().applyAnimation(hover, 0, !status);
	}

}
