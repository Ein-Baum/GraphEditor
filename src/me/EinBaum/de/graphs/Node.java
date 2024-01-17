package me.EinBaum.de.graphs;

import org.joml.Vector2f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import me.Mstudio.engine.GEngine;
import me.Mstudio.engine.graphics.ui.advancedComponents.PressableUi;
import me.Mstudio.engine.graphics.ui.advancedComponents.UiTextfield;
import me.Mstudio.engine.graphics.ui.constraints.AspectConstraint;
import me.Mstudio.engine.graphics.ui.constraints.CenterConstraint;
import me.Mstudio.engine.graphics.ui.constraints.PixelConstraint;
import me.Mstudio.engine.graphics.ui.constraints.PositionConstraint;
import me.Mstudio.engine.graphics.ui.constraints.RelativeConstraint;
import me.Mstudio.engine.graphics.ui.constraints.StickToCursorConstraint;
import me.Mstudio.engine.graphics.ui.text.Text;
import me.Mstudio.engine.graphics.ui.text.TextFormatting;
import me.Mstudio.engine.math.interpolation.EaseCubic;
import me.Mstudio.engine.utils.Color;

public class Node extends PressableUi{
	
	private Graph graph;
	private UiTextfield name;
	private boolean hidden = false;
	
	public Node(Graph graph, String name, PositionConstraint x, PositionConstraint y) {
		this.graph = graph;
		this.setPosition(x, y);
		this.setScale(new PixelConstraint(50), new AspectConstraint(1));
		this.setColor(new Color(1f,1f,1f,1f));
		this.setRimColor(new Color(0f,0f,0f,1f));
		this.setRimThickness(0.15f);
		this.setRimFalloff(0.8f);
		this.name = new UiTextfield(name, GEngine.resources().getRegistered("DefaultFont").asFont(), new CenterConstraint(true), Text.ONE_LINE_INFINITY, new RelativeConstraint(0.5f)) {
			@Override
			public boolean isSelected(int cause) {
				return false;
			}
		};
		this.name.setPosition(new CenterConstraint(true));
		this.name.setScale(new RelativeConstraint(1));
		this.name.getText().getFormat().setColor(new Vector4f(0,0,0,1f));
		this.name.getText().getFormat().setAlignment(TextFormatting.CENTER);
		this.name.setParent(this);
		this.name.setColor(new Color(0f,0f,0f,0f));
		this.name.block();
		this.setCornerRadius(0.5f);
	}
	
	@SuppressWarnings("unused")
	private long lastPressed = 0;
	private Vector2f pressedPos = new Vector2f();
	
	private boolean shouldMove() {
		return pressedPos.distance(getSystem().getCursorPosition()) > 20;
	}
	
	@Override
	public void click(int button, int mods) {
		lastPressed = System.currentTimeMillis();
		pressedPos = new Vector2f(getSystem().getCursorPosition());
		
		super.click(button, mods);
	}
	
	@Override
	public void release(int button, int mods) {
		
		if(!shouldMove()) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				graph.connect(this);
			}else if(button == GLFW.GLFW_MOUSE_BUTTON_RIGHT){
				if((mods & GLFW.GLFW_MOD_CONTROL) == 0) {
					graph.saveState(hidden ? "Show" : "Hide");
					hidden = !hidden;
					if(hidden) {
						this.setColor(new Color(0.1f,0.1f,0.1f,1f));
					}else {
						this.setColor(new Color(1f,1f,1f,1f));
					}
				}else {
					this.name.enter();	
				}
			}else if(button == GLFW.GLFW_MOUSE_BUTTON_MIDDLE){
				if((mods & GLFW.GLFW_MOD_CONTROL) == 0) {
					this.graph.deleteNode(this);
					this.name.exit();
				}else {
					this.graph.merge(this);
				}
			}
		}else {
		}
		this.setPositionInterpolated(new StickToCursorConstraint(false,  StickToCursorConstraint.CENTER), new EaseCubic(), 0.1f);
		moved = false;
		super.release(button, mods);
	}
	
	private boolean moved = false;
	
	@Override
	public void press(int button) {
		this.name.exit();
		if(shouldMove()) {
			if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
				if(!moved) {
					graph.saveState("Move");
					moved = true;
				}
				this.setPositionInterpolated(new StickToCursorConstraint(true,  StickToCursorConstraint.CENTER), new EaseCubic(), 0.1f);
			}
		}
	}

	@Override
	public void hover(boolean status) {
	}
	
	public String getName() {
		return name.getTextString();
	}
	
	public boolean isHidden() {
		return hidden;
	}
	
	public void setHidden(boolean hidden) {
		this.hidden = hidden;
	}

}
