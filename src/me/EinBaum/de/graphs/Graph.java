package me.EinBaum.de.graphs;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.joml.Vector2f;

import me.EinBaum.de.graphs.ui.Timeline;
import me.Mstudio.engine.RenderEngine;
import me.Mstudio.engine.graphics.raw.RawPen;
import me.Mstudio.engine.graphics.ui.animation.transitions.AnimationDriver;
import me.Mstudio.engine.graphics.ui.animation.transitions.Transition;
import me.Mstudio.engine.graphics.ui.constraints.PixelConstraint;
import me.Mstudio.engine.math.interpolation.EaseCubic;
import me.Mstudio.engine.math.interpolation.SmoothInterpolator;

public class Graph {
	
	private static final Transition NODE_IN = new Transition().addDriver(Transition.ALPHA, Transition.MIN_TO_MAX, new AnimationDriver(0, 1f, 0.5f, new EaseCubic()));
	private static final Transition NODE_OUT = new Transition().addDriver(Transition.ALPHA, Transition.MAX_TO_MIN, new AnimationDriver(0, 1f, 0.5f, new SmoothInterpolator()));
	
	class Connection{
		private Node node1, node2;
		
		public Connection(Node node1, Node node2) {
			this.node1 = node1;
			this.node2 = node2;
		}
		
		public boolean constains(Node node) {
			return node1 == node || node2 == node;
		}
		
		public Node getNode1() {
			return node1;
		}
		
		public Node getNode2() {
			return node2;
		}
		
		public Node getOtherNode(Node node) {
			if(node1 == node)return node2;
			if(node2 == node)return node1;
			return node;
		}
		
	}
	
	private Node firstNode = null, firstMerge = null;
	private Set<Node> nodes = new HashSet<>();
	private Set<Connection> connections = new HashSet<>();
	
	private Timeline timeline;
	
	public Graph(Timeline timeline) {
		this.timeline = timeline;
	}
	
	public Graph() {
	}
	
	public void activate() {
		nodes.forEach((N) -> N.activate());
	}
	
	public void deactivate() {
		nodes.forEach((N) -> N.deactivate());
	}
	
	public void setTimeline(Timeline timeline) {
		this.timeline = timeline;
	}
	
	public Timeline getTimeline() {
		return timeline;
	}
	
	public void drawConnections(RawPen pen) {
		
		for(Iterator<Connection> iter = connections.iterator(); iter.hasNext();) {
			Connection C = iter.next();
			
			if(C.node1.isHidden() || C.node2.isHidden()) {
				pen.setStrokeSize(2);
				pen.setColor(0.1f, 0.1f, 0.1f, 1f);
			}else {
				pen.setStrokeSize(8);
				pen.setColor(1, 1, 1, 1f);
			}
			
			pen.begin();
			pen.moveTo(C.getNode1().getPosition().x + C.getNode1().getScale().x/2f, C.getNode1().getPosition().y + C.getNode1().getScale().y/2f, 0);
			pen.moveTo(C.getNode2().getPosition().x + C.getNode2().getScale().x/2f, C.getNode2().getPosition().y + C.getNode2().getScale().y/2f, 0);
			pen.end(false,false,false);
			
			
		};
		
		if(firstNode != null) {
			
			pen.setStrokeSize(2);
			pen.setColor(0.1f, 0.1f, 0.1f, 1f);
			
			pen.drawLine(firstNode.getPosition().x + firstNode.getScale().x/2f, firstNode.getPosition().y + firstNode.getScale().y/2f, 0, RenderEngine.getPrimaryUiSystem().getCursorPosition().x, RenderEngine.getPrimaryUiSystem().getCursorPosition().y, 0);
			
		}
		
	}
	
	public void saveState(String reason) {
		timeline.save(this,reason);
	}
	
	public Graph makeCopy() {
		Graph graph = new Graph(timeline);
		Map<Node, Node> nodeReplacements = new HashMap<>();
		for(Node node : nodes) {
			Node newNode = new Node(graph, node.getName(), new PixelConstraint((int) node.getPosition().x), new PixelConstraint((int) node.getPosition().y));
			graph.nodes.add(newNode);
			nodeReplacements.put(node, newNode);
		}
		for(Connection connection : connections) {
			graph.connections.add(new Connection(nodeReplacements.get(connection.node1), nodeReplacements.get(connection.node2)));
		}
		return graph;
	}
	
	public Node addNode(String name, Vector2f position) {
		timeline.save(this.makeCopy(), "Add");
		Node node = new Node(this, name, new PixelConstraint((int)position.x), new PixelConstraint((int)position.y));
		nodes.add(node);
		node.activate();
		node.getAnimator().applyAnimationOnce(NODE_IN, 0);
		return node;
	}
	
	public void deleteNode(Node node) {
		timeline.save(this.makeCopy(), "Delete");
		node.getAnimator().applyAnimationOnce(NODE_OUT, 0);
		node.deactivate();
		nodes.remove(node);
		connections.removeIf((C) -> C.constains(node));
	}
	
	public Set<Node> getNeighborsOf(Node node){
		Set<Node> neighbors = new HashSet<>();
		for(Connection c : connections) {
			if(c.constains(node)) {
				neighbors.add(c.getOtherNode(node));
			}
		}
		return neighbors;
	}
	
	public Node addNodeSilently(String name, Vector2f position) {
		Node node = new Node(this, name, new PixelConstraint((int)position.x), new PixelConstraint((int)position.y));
		nodes.add(node);
		node.activate();
		return node;
	}
	
	public void deleteNodeSilently(Node node) {
		node.getAnimator().applyAnimationOnce(NODE_OUT, 0);
		node.deactivate();
		nodes.remove(node);
		connections.removeIf((C) -> C.constains(node));
	}
	
	public boolean mergeNodes(Node node1, Node node2) {
		timeline.save(this.makeCopy(), "Merge");
		Set<Node> nodesToConnect = getNeighborsOf(node1);
		nodesToConnect.addAll(getNeighborsOf(node2));
		if(nodesToConnect.contains(node1) && nodesToConnect.contains(node2)) {
			
			deleteNodeSilently(node2);
			deleteNodeSilently(node1);
			nodesToConnect.remove(node1);
			nodesToConnect.remove(node2);
			
			Node newNode = addNodeSilently(node1.getName()+":"+node2.getName(), node1.getPosition().add(node2.getPosition(), new Vector2f()).mul(0.5f));
			
			for(Node node : nodesToConnect) {
				connections.add(new Connection(node, newNode));
			}
			
			return true;
		}
		return false;
	}
	
	public boolean hasConnection(Node node1, Node node2) {
		
		for(Connection c : connections) {
			if(c.constains(node2) && c.constains(node1)) {
				return true;
			}
		}
		
		return false;
	}
	
	public void removeConnection(Node node1, Node node2) {
		timeline.save(this.makeCopy(), "Deconnect");
		for(Iterator<Connection> iter = connections.iterator(); iter.hasNext();) {
			Connection c = iter.next();
			if(c.constains(node2) && c.constains(node1)) {
				iter.remove();
			}
		}
		
	}
	
	public void addConnection(Node node1, Node node2) {
		timeline.save(this.makeCopy(), "Connect");
		connections.add(new Connection(node1, node2));
	}
	
	public boolean isConnecting() {
		return firstNode != null;
	}
	
	public void connect(Node node) {
		
		if(node == null) {
			firstNode = null;
		}else if(firstNode == null) {
			firstNode = node;
		}else {
			if(hasConnection(firstNode, node)) {
				removeConnection(firstNode, node);
			}else {
				addConnection(firstNode, node);
			}
			firstNode = null;
		}
	}
	
	public void merge(Node node) {
		if(node == null) {
			firstMerge = null;
		}else if(firstMerge == null) {
			firstMerge = node;
		}else {
			if(hasConnection(firstMerge, node)) {
				mergeNodes(firstMerge, node);
			}
			firstMerge = null;
		}
	}
	
	public int getSize() {
		return nodes.size();
	}

}
