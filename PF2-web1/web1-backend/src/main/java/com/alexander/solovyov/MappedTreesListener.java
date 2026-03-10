package com.alexander.solovyov;

import com.github.gumtreediff.tree.ITree;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

final public class MappedTreesListener implements TreeSelectionListener {
	final private JTree left;
	final private JTree right;
	final private GumTreeDataFetcher fetcher;

	MappedTreesListener(JTree leftTree, JTree rightTree, GumTreeDataFetcher fetcher) {
		left = leftTree;
		right = rightTree;
		left.addTreeSelectionListener(this);
		right.addTreeSelectionListener(this);
		this.fetcher = fetcher;
	}

	// On select in one tree we'll try to choose corresponding node in another tree
	// If the node is inserted/deleted selection from the other tree should be
	// removed
	// @Override
	public void valueChanged(TreeSelectionEvent event) {
		final JTree currentTree = (JTree) event.getSource();
		final boolean isSrc = currentTree == left;
		final JTree correspondingTree = isSrc ? right : left;

		if (currentTree.getSelectionPath() == null) {
			return;
		}

		final ITree node = (ITree) ((DefaultMutableTreeNode) currentTree.getLastSelectedPathComponent())
				.getUserObject();
		final DefaultMutableTreeNode correspondingNode = isSrc ? fetcher.getCorrespondingDstTreeNode(node)
				: fetcher.getCorrespondingSrcTreeNode(node);

		if (correspondingNode == null) {
			// Inserted/Deleted selected
			correspondingTree.clearSelection();
			return;
		}

		final TreePath path = new TreePath(correspondingNode.getPath());
		correspondingTree.scrollPathToVisible(path);
		correspondingTree.setSelectionPath(path);
	}
}
