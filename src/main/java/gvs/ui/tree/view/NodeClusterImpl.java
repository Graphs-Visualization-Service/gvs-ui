package gvs.ui.tree.view;

class NodeClusterImpl implements Cluster {

	private NodeLineImpl mLine;
	private int mStartPos;
	private int mEndPos;

	public NodeClusterImpl(NodeLineImpl pLine) {
		mLine = pLine;
	}

	public NodeClusterImpl(NodeLineImpl pLine, int pStartPos,
			int pEndPos) {
		this(pLine);
		mStartPos = pStartPos;
		mEndPos = pEndPos;
		print();
	}

	public int getLeftNodePos() {
		return mStartPos;
	}

	public int getRightNodePos() {
		return mEndPos;
	}

	public int length() {
		int nodesOfCluster = 0;
		for (int i = mStartPos; i <= mEndPos; i++) {
			if (mLine.mArr.get(i) != null) {
				nodesOfCluster++;
			}
		}
		return nodesOfCluster;
	}

	public void moveLeftNode() {
		int leftPost = mStartPos-1;
		mLine.mArr.set(leftPost, mLine.mArr.get(mStartPos));
		mLine.mArr.set(mStartPos, null);
		mLine.mArr.get(leftPost).setXPosition((leftPost*2+1)*mLine.mNodeStepDistance);
		while(mLine.mArr.get(++mStartPos) == null) {}
		mLine.print();
	}

	public void moveRightNode() {
		int rightPos = mEndPos+1;
		mLine.mArr.set(rightPos, mLine.mArr.get(mEndPos));
		mLine.mArr.set(mEndPos, null);
		mLine.mArr.get(rightPos).setXPosition((rightPos*2+1)*mLine.mNodeStepDistance);
		while(mLine.mArr.get(--mEndPos) == null) {}
		mLine.print();
	}
	
	public void print() {
		mLine.mTreeContLogger.debug("NodeClusterImpl: " + mStartPos + "/" + mEndPos);
	}

}
