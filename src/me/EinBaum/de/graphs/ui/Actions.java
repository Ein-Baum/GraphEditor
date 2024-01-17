package me.EinBaum.de.graphs.ui;

import org.lwjgl.glfw.GLFW;

import me.EinBaum.de.GraphRuntime;
import me.Mstudio.engine.utils.mss.io.InputEvents;
import me.Mstudio.engine.utils.mss.io.Keymapping;
import me.Mstudio.engine.utils.mss.io.types.KeyInput;
import me.Mstudio.engine.utils.mss.io.types.UserInput;

public class Actions extends InputEvents {

	public Actions(String name, GraphRuntime runtime) {
		super(name);
		
		add(new Keymapping("Undo", new KeyInput(GLFW.GLFW_KEY_Z, GLFW.GLFW_RELEASE).setCtrl(true)) {
			
			@Override
			protected void run(UserInput input) {
				
				runtime.undo();
				
			}
		});
		
		add(new Keymapping("Redo", new KeyInput(GLFW.GLFW_KEY_Y, GLFW.GLFW_RELEASE).setCtrl(true)) {
			
			@Override
			protected void run(UserInput input) {
				
				runtime.redo();
				
			}
		});
		
		
	}

}
