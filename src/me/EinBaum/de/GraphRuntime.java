package me.EinBaum.de;

import org.joml.Vector2f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL40;

import me.EinBaum.de.graphs.Graph;
import me.EinBaum.de.graphs.Node;
import me.EinBaum.de.graphs.ui.Actions;
import me.EinBaum.de.graphs.ui.Timeline;
import me.Mstudio.engine.GEngine;
import me.Mstudio.engine.RenderEngine;
import me.Mstudio.engine.Runtime;
import me.Mstudio.engine.graphics.io.KeyInputReciever;
import me.Mstudio.engine.graphics.raw.RawPen;
import me.Mstudio.engine.graphics.ui.constraints.RelativeToScreen;
import me.Mstudio.engine.graphics.ui.constraints.StickToScreenConstraint;
import me.Mstudio.engine.graphics.wrapper.Framebuffer;
import me.Mstudio.engine.graphics.wrapper.Texture;
import me.Mstudio.engine.utils.Color;
import me.Mstudio.engine.utils.mss.io.InputEvents;

public class GraphRuntime implements Runtime, KeyInputReciever{

	private static Graph graph;
	
	private Framebuffer buffer,outBuffer;
	private Texture texture;
	
	private InputEvents inputHandler;
	private Timeline timeline;
	
	@Override
	public boolean step(double deltaTime) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	private void init() {
		if(buffer==null) {
			
			graph = new Graph();
			
			timeline = new Timeline(graph);
			timeline.setPosition(new StickToScreenConstraint(StickToScreenConstraint.BOTTOM_LEFT));
			timeline.setScale(new RelativeToScreen(1), new RelativeToScreen(0.05f));
			timeline.activate();
			
			graph.setTimeline(timeline);
			graph = graph.makeCopy();
			
			inputHandler = new Actions("", this);
			inputHandler.start();
			
			buffer = new Framebuffer(true, RenderEngine.getPrimaryWindow(), 10);
			buffer.addTextureAttachment(Framebuffer.COLOR, 0, GL40.GL_RGBA, GL40.GL_RGBA, GL40.GL_FLOAT, GL40.GL_LINEAR, GL40.GL_REPEAT);
			
			outBuffer = new Framebuffer(true, RenderEngine.getPrimaryWindow(), 0);
			texture = outBuffer.addTextureAttachment(Framebuffer.COLOR, 0, GL40.GL_RGBA, GL40.GL_RGBA, GL40.GL_FLOAT, GL40.GL_LINEAR, GL40.GL_REPEAT);
			
			GEngine.getInputHandler().addKeyInputReciever(this);
			RenderEngine.getPrimaryWindow().getScreenComponent().setColor(new Color(0.5f,0.5f,0.5f,0f));
		}
	}

	@Override
	public int[] render(double deltaTime, RenderEngine engineInstance) {
		
		init();
		
		RawPen pen = engineInstance.getPen();
		
		buffer.bind();
		
		GL40.glClearColor(0,0,0, 1f);
		GL40.glClear(GL40.GL_COLOR_BUFFER_BIT);
		
		graph.drawConnections(pen);
		
		buffer.unbind();
		
		
		outBuffer.bind(GL40.GL_DRAW_FRAMEBUFFER);
		buffer.bind(GL40.GL_READ_FRAMEBUFFER);
		
		GL40.glBlitFramebuffer(0, 0, RenderEngine.getPrimaryWindow().getWidth(), RenderEngine.getPrimaryWindow().getHeight(), 0, 0, RenderEngine.getPrimaryWindow().getWidth(), RenderEngine.getPrimaryWindow().getHeight(), GL40.GL_COLOR_BUFFER_BIT, GL40.GL_LINEAR);
		
		buffer.unbind();
		outBuffer.unbind();
		
		return new int[] {texture.getID()};
	}
	
	public Timeline getTimeline() {
		return timeline;
	}

	@Override
	public boolean recieveKey(int key, int scancode, int action, int mods) {
		return false;
	}

	@Override
	public boolean recieveButton(int button, int action, int mods) {
		if(button == GLFW.GLFW_MOUSE_BUTTON_LEFT && action == GLFW.GLFW_PRESS) {
			if(RenderEngine.getPrimaryUiSystem().getSelectedID() == 0) {
				Node node = graph.addNode(graph.getSize()+"", RenderEngine.getPrimaryWindow().getUiSystem().getCursorPosition().sub(25, 25, new Vector2f()));
				if(graph.isConnecting()) {
					graph.connect(node);
				}
			}
		}
		return false;
	}
	
	public void undo() {
		graph.deactivate();
		graph = timeline.rollback(1);
		graph.activate();
	}
	
	public void redo() {
		graph.deactivate();
		graph = timeline.rollForwards(1);
		graph.activate();
	}
	
	public static void setGraph(Graph graph) {
		GraphRuntime.graph.deactivate();
		GraphRuntime.graph = graph;
		GraphRuntime.graph.activate();
	}

}
