/*
 * This file is part of GumTree.
 *
 * GumTree is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * GumTree is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with GumTree. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2011-2015 Jean-R茅my Falleri <jr.falleri@gmail.com>
 * Copyright 2011-2015 Flor茅al Morandat <florealm@gmail.com>
 */

package com.github.gumtreediff.gen.antlr3.xml;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.antlr.runtime.ANTLRReaderStream;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonToken;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.RuleReturnScope;
import org.antlr.runtime.Token;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

import com.example.demo.service.FileOperation;
import com.github.gumtreediff.gen.Register;
import com.github.gumtreediff.gen.antlr3.AbstractAntlr3TreeGenerator;
import com.github.gumtreediff.tree.EMFTreeContext;
import com.github.gumtreediff.tree.ITree;

@Register(id = "xml-antlr", accept = { "\\.xml$", "\\.xsd$", "\\.wadl$" })
public class EMFTreeGenerator extends AbstractAntlr3TreeGenerator<XMLLexer, XMLParser> {
	private Deque<ITree> trees = new ArrayDeque<>();

	public EMFTreeContext generateFromFile(String path) throws IOException {
		EMFTreeContext ctx = generateFromReader(Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8")));
		File file = new File(path);
		String version = FileOperation.getFileSha1(file);
		ctx.setVersion(version);
		return ctx;
	}

	public EMFTreeContext generateFromFile(String path, long time) throws IOException {
		EMFTreeContext ctx = generateFromReader(Files.newBufferedReader(Paths.get(path), Charset.forName("UTF-8")),
				time);
		File file = new File(path);
		String version = FileOperation.getFileSha1(file);
		ctx.setVersion(version);
		return ctx;
	}

	public EMFTreeContext generateFromReader(Reader r) throws IOException {
		EMFTreeContext ctx = generate(r);
		ctx.validate();
		return ctx;
	}

	public EMFTreeContext generateFromReader(Reader r, long time) throws IOException {
		EMFTreeContext ctx = generate(r, time);
		ctx.validate();
		return ctx;
	}

	@Override
	public EMFTreeContext generate(Reader file) throws IOException {

		EMFTreeContext ctx = new EMFTreeContext();

		try {
			CommonTree ct = getStartSymbol(file);
			long time = System.currentTimeMillis();
			buildTree(ctx, ct, time);
		} catch (RecognitionException e) {
			System.out.println("at " + e.line + ":" + e.charPositionInLine);
			e.printStackTrace();
			ctx = null;
		}
		ITree t = ctx.getRoot();
		for (ITree c : t.getTrees()) { // Prune top level empty pcdata
			if (c.getType() == XMLParser.PCDATA && c.getLabel().trim().equals("")) {
				c.setParentAndUpdateChildren(null);
			}
		}
		return ctx;
	}

	public EMFTreeContext generate(Reader file, long time) throws IOException {

		EMFTreeContext ctx = new EMFTreeContext();

		try {
			CommonTree ct = getStartSymbol(file);
			buildTree(ctx, ct, time);
		} catch (RecognitionException e) {
			System.out.println("at " + e.line + ":" + e.charPositionInLine);
			e.printStackTrace();
			ctx = null;
		}
		ITree t = ctx.getRoot();
		for (ITree c : t.getTrees()) { // Prune top level empty pcdata
			if (c.getType() == XMLParser.PCDATA && c.getLabel().trim().equals("")) {
				c.setParentAndUpdateChildren(null);
			}
		}
		return ctx;
	}

	protected CommonTree getStartSymbol(Reader r) throws RecognitionException, IOException {
		ANTLRStringStream stream = getAntlrStream(r);
		XMLLexer lexer = getLexer(stream);
		tokens = getTokenStream(lexer);
		XMLParser parser = getParser(tokens);
		return getTreeFromRule(getStartRule(parser));
	}

	protected ANTLRStringStream getAntlrStream(Reader r) throws IOException {
		return new ANTLRReaderStream(r);
	}

	@SuppressWarnings("unchecked")
	protected void buildTree(EMFTreeContext context, CommonTree ct, long time) {
		int type = ct.getType();
		String tokenName = getTokenName(type);
		String label = ct.getText();
		if (tokenName.equals(label))
			label = ITree.NO_LABEL;
		ITree t = context.createTree(type, label, tokenName, time);

		int start = startPos(ct.getTokenStartIndex());
		int stop = stopPos(ct.getTokenStopIndex());
		t.setPos(start);
		t.setLength(stop - start + 1); // FIXME check if this + 1 make sense ?

		if (trees.isEmpty())
			context.setRoot(t);
		else
			t.setParentAndUpdateChildren(trees.peek());

		if (ct.getChildCount() > 0) {
			trees.push(t);
			for (CommonTree cct : (List<CommonTree>) ct.getChildren())
				buildTree(context, cct, time);
			trees.pop();
		}
	}

	private int startPos(int tkPosition) {
		if (tkPosition == -1)
			return 0;
		Token tk = tokens.get(tkPosition);
		if (tk instanceof CommonToken)
			return ((CommonToken) tk).getStartIndex();
		return 0;
	}

	private int stopPos(int tkPosition) {
		if (tkPosition == -1)
			return 0;
		Token tk = tokens.get(tkPosition);
		if (tk instanceof CommonToken)
			return ((CommonToken) tk).getStopIndex();
		return 0;
	}

	@Override
	protected XMLLexer getLexer(ANTLRStringStream stream) {
		return new XMLLexer(stream);
	}

	@Override
	protected XMLParser getParser(TokenStream tokens) {
		return new XMLParser(tokens);
	}

	@Override
	protected RuleReturnScope getStartRule(XMLParser parser) throws RecognitionException {
		return parser.document();
	}

	@Override
	protected final String[] getTokenNames() {
		return XMLParser.tokenNames;
	}
}
