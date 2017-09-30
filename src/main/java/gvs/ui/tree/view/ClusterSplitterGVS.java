package gvs.ui.tree.view;

import gvs.interfaces.IBinaryNode;
import gvs.ui.tree.model.BinaryNode;

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClusterSplitterGVS extends ClusterSplitter implements Runnable {

	private static boolean mEnabled = false;
	
	static VisualizationTreePanel mPanel;
	
	Logger mTreeContLogger;

	ClusterSplitterGVS() {
		//TODO: check Logger replacement
		//mTreeContLogger = gvs.common.Logger.getInstance().getTreeControllerLogger();
		mTreeContLogger = LoggerFactory.getLogger(ClusterSplitterGVS.class);
	}
	
	ClusterSplitterGVS(VisualizationTreePanel pPanel) {
		this();
		mPanel = pPanel;
	}

	
	public void run() {
		
		int depth;
		NodeLineImpl nodeLine = null;
		Vector<IBinaryNode> sortedNodes = null;
		double width = mPanel.getTreeLayoutController().getMaxDimensionWidth();
		int result = 0;

		depth = 5;
		nodeLine = new NodeLineImpl(depth, width, 1);
		sortedNodes = mPanel.getTreeLayoutController().getSortedNodes();
		for (IBinaryNode node: sortedNodes) {
			if (node.getMyTreeLevel() == depth) {
				nodeLine.add(node);
			}
		}
		if (nodeLine.getSize() > 0) {
			nodeLine.print();
			result = split(nodeLine);
			if (result != 0) {
				mTreeContLogger.warn("ClusterSplitterGVS::run(): no Solution for Depth = "+depth);
				mTreeContLogger.warn("  Reason: "+ ((result == -1) ? "Same Line" : "No Actions"));
			}
		}
		
		
		depth = 6;
		nodeLine = new NodeLineImpl(depth, width, 3);
		sortedNodes = mPanel.getTreeLayoutController().getSortedNodes();
		for (IBinaryNode node: sortedNodes) {
			if (node.getMyTreeLevel() == depth) {
				nodeLine.add(node);
			}
		}
		if (nodeLine.getSize() > 0) {
			nodeLine.print();
			result = split(nodeLine);
			if (result != 0) {
				mTreeContLogger.warn("ClusterSplitterGVS::run(): no Solution for Depth = "+depth);
				mTreeContLogger.warn("  Reason: "+ ((result == -1) ? "Same Line" : "No Actions"));
			}
		}
		
		depth = 7;
		nodeLine = new NodeLineImpl(depth, width, 5);
		sortedNodes = mPanel.getTreeLayoutController().getSortedNodes();
		for (IBinaryNode node: sortedNodes) {
			if (node.getMyTreeLevel() == depth) {
				nodeLine.add(node);
			}
		}
		if (nodeLine.getSize() > 0) {
			nodeLine.print();
			result = split(nodeLine);
			if (result != 0) {
				mTreeContLogger.warn("ClusterSplitterGVS::run(): no Solution for Depth = "+depth);
				mTreeContLogger.warn("  Reason: "+ ((result == -1) ? "Same Line" : "No Actions"));
			}
		}
		
		mPanel.repaint();
		try { Thread.sleep(2000); } catch (InterruptedException e) {}
		
		mPanel.setClusterSplitter(null);
		
	}
	
	public static void main(String[] args) {
		new ClusterSplitterGVS().test();
	}
	
	
	public void test() {
		setEnabled(true);
		final int DEPTH = 3;
		double width = 95; // Width in Percents
		NodeLineImpl nodeLine = null;
		int result = 0;
		
		nodeLine = new NodeLineImpl(DEPTH, width, 1);
		for (int i = 0; i < 4; i++) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		split(nodeLine);
		
		nodeLine = new NodeLineImpl(DEPTH, width, 1);
		for (int i = 1; i < 4; i++) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		result = split(nodeLine);
		nodeLine.mTreeContLogger.debug("split(): " + result);
		
		nodeLine = new NodeLineImpl(DEPTH, width, 2);
		for (int i = 1; i < 4; i++) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		result = split(nodeLine);
		nodeLine.mTreeContLogger.debug("split(): " + result);
		
		nodeLine = new NodeLineImpl(DEPTH, width, 3);
		for (int i = 1; i < 3; i++) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		result = split(nodeLine);
		nodeLine.mTreeContLogger.debug("split(): " + result);
		
		nodeLine = new NodeLineImpl(DEPTH, width, 6);
		for (int i = 3; i <= 4; i++) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		result = split(nodeLine);
		nodeLine.mTreeContLogger.debug("split(): " + result);
		
		nodeLine = new NodeLineImpl(DEPTH, width, 2);
		for (int i = 4; i <= 6; i++) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		result = split(nodeLine);
		nodeLine.mTreeContLogger.debug("split(): " + result);
		
		// No Solution:
		nodeLine = new NodeLineImpl(DEPTH, width, 1);
		int[] iarr = {0, 2, 4, 6, 7};
		for (int i: iarr) {
			double xPos = width / 16 * (i*2+1);
			IBinaryNode binaryNode = new BinaryNode(0, ""+(8+1), null, null, null, 0, 0, xPos, 0);
			nodeLine.add(binaryNode);
		}
		nodeLine.print();
		result = split(nodeLine);
		nodeLine.mTreeContLogger.debug("split(): " + result);

		
	}

	public static boolean isEnabled() {
		return mEnabled;
	}

	public static void setEnabled(boolean enabled) {
		mEnabled = enabled;
	}

}
