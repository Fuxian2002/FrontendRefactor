package com.alexander.solovyov;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.ITree;
import com.github.gumtreediff.tree.EMFTreeContext;

final class GumTreeDataFetcher {
	final private HashMap<ITree, DefaultMutableTreeNode> sourceMap = new HashMap();
	final private HashMap<ITree, DefaultMutableTreeNode> destMap = new HashMap();

	// These are needed for drawing and selection
	final private HashSet<ITree> updatedSourceNodes = new HashSet();
	final private HashSet<ITree> movedSourceNodes = new HashSet();
	final private HashSet<ITree> deletedSourceNodes = new HashSet();

	final private HashSet<ITree> updatedDestNodes = new HashSet();
	final private HashSet<ITree> movedDestNodes = new HashSet();
	final private HashSet<ITree> insertedDestNodes = new HashSet();

	final private EMFTreeContext sourceContext;
	final private EMFTreeContext destContext;
	final private MappingStore mappings;
	final private MutableTreeNode sourceNode;
	final private MutableTreeNode destNode;

	GumTreeDataFetcher(final EMFTreeContext sourceContext,
			final EMFTreeContext destContext, List<Action> actions,
			MappingStore mappings) throws IOException {
		this.sourceContext = sourceContext;
		final ITree sourceRoot = sourceContext.getRoot();
		this.destContext = destContext;
		final ITree destRoot = destContext.getRoot();
		this.mappings = mappings;
		for (Action action : actions) {
			if (action instanceof Update) {
				updatedSourceNodes.add(action.getNode());
				updatedDestNodes.add(mappings.getDst(action.getNode()));
			} else if (action instanceof Move) {
				movedSourceNodes.add(action.getNode());
				movedDestNodes.add(mappings.getDst(action.getNode()));
			} else if (action instanceof Insert) {
				insertedDestNodes.add(action.getNode());
			} else if (action instanceof Delete) {
				deletedSourceNodes.add(action.getNode());
			}
		}
		sourceNode = buildNode(sourceRoot, sourceMap);
		destNode = buildNode(destRoot, destMap);
	}

	// GumTreeDataFetcher(final String src, final String dst) throws IOException
	// {
	// Run.initGenerators();
	// sourceContext = Generators.getInstance().getTree(src);
	// final ITree sourceRoot = sourceContext.getRoot();
	// destContext = Generators.getInstance().getTree(dst);
	// final ITree destRoot = destContext.getRoot();
	// final Matcher matcher = Matchers.getInstance().getMatcher(sourceRoot,
	// destRoot);
	// matcher.match();
	// mappings = matcher.getMappings();
	// final ActionGenerator actionGenerator = new ActionGenerator(sourceRoot,
	// destRoot, mappings);
	// for (Action action : actionGenerator.generate()) {
	// if (action instanceof Update) {
	// updatedSourceNodes.add(action.getNode());
	// updatedDestNodes.add(mappings.getDst(action.getNode()));
	// } else if (action instanceof Move) {
	// movedSourceNodes.add(action.getNode());
	// movedDestNodes.add(mappings.getDst(action.getNode()));
	// } else if (action instanceof Insert) {
	// insertedDestNodes.add(action.getNode());
	// } else if (action instanceof Delete) {
	// deletedSourceNodes.add(action.getNode());
	// }
	// }
	// sourceNode = buildNode(sourceRoot, sourceMap);
	// destNode = buildNode(destRoot, destMap);
	// }

	private MutableTreeNode buildNode(final ITree node,
			final HashMap<ITree, DefaultMutableTreeNode> hashMap) {
		final DefaultMutableTreeNode mutableNode = new DefaultMutableTreeNode(
				node);
		hashMap.put(node, mutableNode);
		if (node != null)
			for (final ITree childNote : node.getChildren()) {
				mutableNode.add(buildNode(childNote, hashMap));
			}
		return mutableNode;
	}

	MutableTreeNode getSourceNode() {
		return sourceNode;
	}

	MutableTreeNode getDestNode() {
		return destNode;
	}

	String getSourcePrettyName(ITree node) {
		return node.toPrettyString(sourceContext);
		// return node.toShortString();
	}

	String getDestPrettyName(ITree node) {
		if (node != null)
			return node.toPrettyString(destContext);
		else
			return "";
		// return node.toShortString();
	}

	boolean isDeletedSource(ITree node) {
		return deletedSourceNodes.contains(node);
	}

	boolean isUpdatedSource(ITree node) {
		return updatedSourceNodes.contains(node);
	}

	boolean isMovedSource(ITree node) {
		return movedSourceNodes.contains(node);
	}

	boolean isInsertedDest(ITree node) {
		return insertedDestNodes.contains(node);
	}

	boolean isUpdatedDest(ITree node) {
		return updatedDestNodes.contains(node);
	}

	boolean isMovedDest(ITree node) {
		return movedDestNodes.contains(node);
	}

	DefaultMutableTreeNode getCorrespondingDstTreeNode(ITree srcNode) {
		ITree dst = mappings.getDst(srcNode);
		return dst == null ? null : destMap.get(dst);
	}

	DefaultMutableTreeNode getCorrespondingSrcTreeNode(ITree dstNode) {
		ITree src = mappings.getSrc(dstNode);
		return src == null ? null : sourceMap.get(src);
	}
}
