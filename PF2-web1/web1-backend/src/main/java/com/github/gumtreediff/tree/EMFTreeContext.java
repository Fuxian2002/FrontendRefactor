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

package com.github.gumtreediff.tree;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.gumtreediff.io.TreeIoUtils;
import com.github.gumtreediff.io.TreeIoUtils.MetadataSerializer;
import com.github.gumtreediff.io.TreeIoUtils.MetadataUnserializer;

public class EMFTreeContext extends TreeContext {

	private Map<Integer, String> typeLabels = new HashMap<>();

	private final Map<String, Object> metadata = new HashMap<>();

	private final MetadataSerializers serializers = new MetadataSerializers();

	private String version;
	private String lastVersion;
	private long t0 = 0;
	private long t1 = 0;
	private long t2 = 0;
	private long t3 = 0;
	private long t4 = 0;
	private long st0 = 0;
	private long st1 = 0;
	private long st2 = 0;
	private long st3 = 0;
	private ITree root;
	private int id = 0;
	private String name;
	private String email;
	private int flag1 = 0;
	private int flag2 = 0;
	private int flag3 = 0;
	private String from="";
	private String to="";
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String time() {
		return "t0="+t0+","+"t1="+t1+","+"t2="+t2+","+"t3="+t3+","+"st0="+st0+","+"st1="+st1+","+"st2="+st2+","+"st3="+st3;
	}
	
	public int getFlag1() {
		return flag1;
	}

	public void setFlag1(int flag1) {
		this.flag1 = flag1;
	}

	public int getFlag2() {
		return flag2;
	}

	public void setFlag2(int flag2) {
		this.flag2 = flag2;
	}

	public int getFlag3() {
		return flag3;
	}

	public void setFlag3(int flag3) {
		this.flag3 = flag3;
	}

	public long getT0() {
		return t0;
	}

	public void setT0(long t0) {
		this.t0 = t0;
	}

	public long getT1() {
		return t1;
	}

	public void setT1(long t1) {
		this.t1 = t1;
	}

	public long getT2() {
		return t2;
	}

	public void setT2(long t2) {
		this.t2 = t2;
	}

	public long getT3() {
		return t3;
	}

	public void setT3(long t3) {
		this.t3 = t3;
	}

	public long getT4() {
		return t4;
	}

	public void setT4(long t4) {
		this.t4 = t4;
	}

	public long getSt0() {
		return st0;
	}

	public void setSt0(long st0) {
		this.st0 = st0;
	}

	public long getSt1() {
		return st1;
	}

	public void setSt1(long st1) {
		this.st1 = st1;
	}

	public long getSt2() {
		return st2;
	}

	public void setSt2(long st2) {
		this.st2 = st2;
	}

	public long getSt3() {
		return st3;
	}

	public void setSt3(long st3) {
		this.st3 = st3;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return TreeIoUtils.toLisp(this).toString();
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getLastVersion() {
		return lastVersion;
	}

	public void setLastVersion(String lastVersion) {
		this.lastVersion = lastVersion;
	}

	public void setRoot(ITree root) {
		this.root = root;
	}

	public ITree getRoot() {
		return root;
	}

	public String getTypeLabel(ITree tree) {
		return getTypeLabel(tree.getType());
	}

	public String getTypeLabel(int type) {
		String tl = typeLabels.get(type);
		if (tl == null)
			tl = Integer.toString(type);
		return tl;
	}

	protected void registerTypeLabel(int type, String name) {
		if (name == null || name.equals(ITree.NO_LABEL))
			return;
		String typeLabel = typeLabels.get(type);
		if (typeLabel == null)
			typeLabels.put(type, name);
		else if (!typeLabel.equals(name))
			throw new RuntimeException(String.format("Redefining type %d: '%s' with '%s'", type, typeLabel, name));
	}

	public void importTypeLabels(EMFTreeContext ctx) {
		for (Map.Entry<Integer, String> label : ctx.typeLabels.entrySet()) {
			if (!typeLabels.containsValue(label.getValue())) {
				typeLabels.put(label.getKey(), label.getValue());
			}
		}
	}

	public ITree createTree(int type, String label, String typeLabel, long time) {
		registerTypeLabel(type, typeLabel);
		return new TimeTree(type, label, time, id++);
	}

	public ITree createTree(int type, String label, String typeLabel) {
		registerTypeLabel(type, typeLabel);
		return new Tree(type, label);
	}

	public ITree createTree(ITree... trees) {
		return new AbstractTree.FakeTree(trees);
	}

	public void validate() {
		root.refresh();
		TreeUtils.postOrderNumbering(root);
	}

	public boolean hasLabelFor(int type) {
		return typeLabels.containsKey(type);
	}

	/**
	 * Get a global metadata.
	 * There is no way to know if the metadata is really null or does not exists.
	 *
	 * @param key of metadata
	 * @return the metadata or null if not found
	 */
	public Object getMetadata(String key) {
		return metadata.get(key);
	}

	/**
	 * Get a local metadata, if available. Otherwise get a global metadata.
	 * There is no way to know if the metadata is really null or does not exists.
	 *
	 * @param key of metadata
	 * @return the metadata or null if not found
	 */
	public Object getMetadata(ITree node, String key) {
		Object metadata;
		if (node == null || (metadata = node.getMetadata(key)) == null)
			return getMetadata(key);
		return metadata;
	}

	/**
	 * Store a global metadata.
	 *
	 * @param key   of the metadata
	 * @param value of the metadata
	 * @return the previous value of metadata if existed or null
	 */
	public Object setMetadata(String key, Object value) {
		return metadata.put(key, value);
	}

	/**
	 * Store a local metadata
	 *
	 * @param key   of the metadata
	 * @param value of the metadata
	 * @return the previous value of metadata if existed or null
	 */
	public Object setMetadata(ITree node, String key, Object value) {
		if (node == null)
			return setMetadata(key, value);
		else {
			Object res = node.setMetadata(key, value);
			if (res == null)
				return getMetadata(key);
			return res;
		}
	}

	/**
	 * Get an iterator on global metadata only
	 */
	public Iterator<Entry<String, Object>> getMetadata() {
		return metadata.entrySet().iterator();
	}

	/**
	 * Get serializers for this tree context
	 */
	public MetadataSerializers getSerializers() {
		return serializers;
	}

	public EMFTreeContext export(MetadataSerializers s) {
		serializers.addAll(s);
		return this;
	}

	public EMFTreeContext export(String key, MetadataSerializer s) {
		serializers.add(key, s);
		return this;
	}

	public EMFTreeContext export(String... name) {
		for (String n : name)
			serializers.add(n, x -> x.toString());
		return this;
	}

	public EMFTreeContext deriveTree() { // FIXME Should we refactor TreeContext class to allow shared metadata etc ...
		EMFTreeContext newContext = new EMFTreeContext();
		newContext.version = version;
		newContext.lastVersion = lastVersion;
		newContext.setId(id);
		newContext.setT0(t0);
		newContext.setT1(t1);
		newContext.setT2(t2);
		newContext.setT3(t3);
		newContext.setT4(t4);
		newContext.setSt0(st0);
		newContext.setSt1(st1);
		newContext.setSt2(st2);
		newContext.setSt3(st3);
		newContext.setFlag1(flag1);
		newContext.setFlag2(flag2);
		newContext.setFlag3(flag3);
		newContext.name = name;
		newContext.email = email;
		newContext.setRoot(getRoot().deepCopy());
		newContext.typeLabels = typeLabels;
		newContext.metadata.putAll(metadata);
		newContext.serializers.addAll(serializers);
		return newContext;
	}

	/**
	 * Get an iterator on local and global metadata.
	 * To only get local metadata, simply use : `node.getMetadata()`
	 */
	public Iterator<Entry<String, Object>> getMetadata(ITree node) {
		if (node == null)
			return getMetadata();
		return new Iterator<Entry<String, Object>>() {
			final Iterator<Entry<String, Object>> localIterator = node.getMetadata();
			final Iterator<Entry<String, Object>> globalIterator = getMetadata();
			final Set<String> seenKeys = new HashSet<>();

			Iterator<Entry<String, Object>> currentIterator = localIterator;
			Entry<String, Object> nextEntry;

			{
				next();
			}

			@Override
			public boolean hasNext() {
				return nextEntry != null;
			}

			@Override
			public Entry<String, Object> next() {
				Entry<String, Object> n = nextEntry;
				if (currentIterator == localIterator) {
					if (localIterator.hasNext()) {
						nextEntry = localIterator.next();
						seenKeys.add(nextEntry.getKey());
						return n;
					} else {
						currentIterator = globalIterator;
					}
				}
				nextEntry = null;
				while (globalIterator.hasNext()) {
					Entry<String, Object> e = globalIterator.next();
					if (!(seenKeys.contains(e.getKey()) || (e.getValue() == null))) {
						nextEntry = e;
						seenKeys.add(nextEntry.getKey());
						break;
					}
				}
				return n;
			}
		};
	}

	public static class Marshallers<E> {
		Map<String, E> serializers = new HashMap<>();

		public static final Pattern valid_id = Pattern.compile("[a-zA-Z0-9_]*");

		public void addAll(Marshallers<E> other) {
			addAll(other.serializers);
		}

		public void addAll(Map<String, E> serializers) {
			serializers.forEach((k, s) -> add(k, s));
		}

		public void add(String name, E serializer) {
			if (!valid_id.matcher(name).matches()) // TODO I definitely don't like this rule, we should think twice
				throw new RuntimeException("Invalid key for serialization");
			serializers.put(name, serializer);
		}

		public void remove(String key) {
			serializers.remove(key);
		}

		public Set<String> exports() {
			return serializers.keySet();
		}
	}

	// public static class MetadataSerializers extends
	// Marshallers<MetadataSerializer> {
	//
	// public void serialize(TreeFormatter formatter, String key, Object value)
	// throws Exception {
	// MetadataSerializer s = serializers.get(key);
	// if (s != null)
	// formatter.serializeAttribute(key, s.toString(value));
	// }
	// }

	public static class MetadataUnserializers extends Marshallers<MetadataUnserializer> {

		public void load(ITree tree, String key, String value) throws Exception {
			MetadataUnserializer s = serializers.get(key);
			if (s != null) {
				if (key.equals("pos"))
					tree.setPos(Integer.parseInt(value));
				else if (key.equals("length"))
					tree.setLength(Integer.parseInt(value));
				else
					tree.setMetadata(key, s.fromString(value));
			}
		}
	}
}
