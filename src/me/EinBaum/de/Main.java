package me.EinBaum.de;

import me.Mstudio.engine.GEngine;
import me.Mstudio.engine.RenderEngine;
import me.Mstudio.engine.Runtime;
import me.Mstudio.engine.graphics.io.Window;
import me.Mstudio.engine.utils.ThreadLock;

public class Main{

	public static Runtime graph;
	
	
	public static void main(String[] args) {
		
		GEngine.initialize("/logs/");
		
		graph = new GraphRuntime();
		
		GEngine.start(graph);
		
		Window window = new Window(1000, 1000, "");
		window.setResizable(true);
		
		RenderEngine.createWindow(window);
		
		while(!RenderEngine.isReady()) {
			ThreadLock.waitFor(0.0001f);
		}
		
	}

}
