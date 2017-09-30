package gvs.ui.tree.view;

import gvs.interfaces.IBinaryNode;

import java.math.BigInteger;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class NodeLineImpl implements Line {

	private final int DEPTH;
	private final int DISTANCE;
	Vector<IBinaryNode> mArr;
	/**
	 * The width in percentage that can be used for Layouting.
	 */
	private final double mWidth;
	/**
	 * The size of steps for the x-direction of nodes. Remark: This is the
	 * distance from the edge to the first resp. last Node. Between two nodes
	 * are two such steps!
	 */
	final double mNodeStepDistance;
	private int mSize;
	Logger mTreeContLogger = null;

	NodeLineImpl(int pDepth, double pWidth, int pDistance) {
		DEPTH = pDepth;
		mWidth = pWidth;
		DISTANCE = pDistance;
		mNodeStepDistance = mWidth / Math.pow(2, DEPTH) / 2;
		mArr = new Vector<IBinaryNode>();
		mArr.setSize(((int) Math.pow(2, DEPTH)));
		// TODO: check Logger replacement
		// mTreeContLogger =
		// gvs.common.Logger.getInstance().getTreeControllerLogger();
		mTreeContLogger = LoggerFactory.getLogger(NodeLineImpl.class);
		mTreeContLogger.debug("NodeLineImpl::NodeLineImpl():  DISTANCE=" + DISTANCE + "  DEPTH=" + DEPTH);
	}

	public Cluster getNextCluster(int pSearchStartPos) {
		int startPos = -1;
		int endPos = -1;
		int len = mArr.size();
		for (int i = pSearchStartPos; i < len; i++) {
			startPos = -1;
			for (int j = i; j < len; j++) {
				if (mArr.get(j) != null) {
					startPos = j;
					break;
				}
			}
			if (startPos == -1) { // no Nodes
				return null;
			}
			endPos = len - 1;
			for (int j = startPos + 1; j < len; j++) {
				int nulls = 0;
				// are there Nodes in the Range of DISTANCE?
				for (int k = 0; k < DISTANCE; k++) {
					if (mArr.get(Math.min(j + k, mArr.size() - 1)) == null)
						nulls++;
				}
				if (nulls == DISTANCE) { // no Nodes inbetween DISTANCE: End
											// reached
					endPos = j - 1;
					break;
				}
			}
			if (endPos - startPos < 1) {
				continue;
			}
			// we found a Cluster:
			break;
		}
		if (endPos - startPos > 0) {
			return new NodeClusterImpl(this, startPos, endPos);
		} else {
			return null;
		}
	}

	public int length() {
		return mArr.size();
	}

	public void add(IBinaryNode pNode) {
		double xpos = pNode.getXPosition();
		int index = (int) (xpos / (mNodeStepDistance * 2));
		mArr.set(index, pNode);
		String str = String.format("NodeLineImpl::add(): >%s<  X-Pos: %f  Index: %d\n", pNode.getNodeLabel(),
				pNode.getXPosition(), index);
		mTreeContLogger.debug(str);
		mSize++;
	}

	public void print() {
		mTreeContLogger.debug("NodeLineImpl::print(): ");
		int len = mArr.size();
		for (int i = 0; i < len; i++) {
			IBinaryNode node = mArr.get(i);
			String str = String.format("  [%03d]: %s ", i, (mArr.get(i) != null) ? node.toString() : "null");
			mTreeContLogger.debug(str);
		}
		if (ClusterSplitterGVS.mPanel != null) {
			ClusterSplitterGVS.mPanel.repaint();
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
			}
		}
	}

	public int getNodeDistance() {
		return DISTANCE;
	}

	public BigInteger getBigIntegerInterpretation() {
		// Interpret pLine as Binary-Polynom:
		int nrOfBits = mArr.size();
		int nrOfBytes = nrOfBits / 8 + (((nrOfBits % 8) > 0) ? 1 : 0);
		byte[] bytes = new byte[nrOfBytes];
		for (int i = 0; i < nrOfBits; i++) {
			if (mArr.get(i) != null) {
				byte actualByte = bytes[bytes.length - 1 - i / 8];
				actualByte = (byte) (actualByte | (byte) (1 << (i % 8)));
				bytes[bytes.length - 1 - i / 8] = actualByte;
			}
		}
		BigInteger bigInt = new BigInteger(bytes);
		mTreeContLogger.debug("BigInteger = " + bigInt);
		return bigInt;
	}

	int getSize() {
		return mSize;
	}

}
