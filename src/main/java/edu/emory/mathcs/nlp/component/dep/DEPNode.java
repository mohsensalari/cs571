/**
 * Copyright 2015, Emory University
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.emory.mathcs.nlp.component.dep;

import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.common.collection.list.SortedArrayList;
import edu.emory.mathcs.nlp.common.constant.StringConst;
import edu.emory.mathcs.nlp.common.util.DSUtils;
import edu.emory.mathcs.nlp.component.pos.POSNode;
import edu.emory.mathcs.nlp.component.util.feature.Field;
import edu.emory.mathcs.nlp.component.util.node.DirectionType;
import edu.emory.mathcs.nlp.component.util.node.FeatMap;


/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPNode extends POSNode implements Comparable<DEPNode>
{
	private static final long serialVersionUID = 3794720014142939766L;
	static DEPNode ROOT = new DEPNode(0, "@#r$%", "@#r$%", "@#r$%", new FeatMap(), null, "@#r$%");

	/** The dependency label of this node. */
	protected String dependency_label;
	/** The dependency head of this node. */
	protected DEPNode head_node;
	/** The sorted list of all dependents of this node (default: empty). */
	protected SortedArrayList<DEPNode> dependent_list;
	/** The ID of this node among its sibling (starting with 0). */
	protected int sibling_id;
	
//	====================================== Constructors ======================================
	
	public DEPNode(int id, String form)
	{
		super(id, form, null, null, new FeatMap());
		dependent_list = new SortedArrayList<>();
	}
	
	public DEPNode(int id, String form, String lemma, String posTag, FeatMap feats)
	{
		super(id, form, lemma, posTag, feats);
		dependent_list = new SortedArrayList<>();
	}
	
	public DEPNode(int id, String form, String lemma, String posTag, FeatMap feats, DEPNode head, String label)
	{
		super(id, form, lemma, posTag, feats);
		dependent_list = new SortedArrayList<>();
		setHead(head);
		setLabel(label);
	}
	
	/** Clear all dependencies(head, label, and sibling relations) of the node. */
	void clearDependencies()
	{
		head_node  = null;
		dependency_label = null;
		sibling_id = 0;
		dependent_list.clear();
	}
	
//	====================================== GETTERS ======================================
	
	public String getLabel()
	{
		return dependency_label;
	}
	
	public DEPNode getHead()
	{
		return head_node;
	}

	/** @return the dependency grand-head of the node if exists; otherwise, {@code null}. */
	public DEPNode getGrandHead()
	{
		DEPNode head = getHead();
		return (head == null) ? null : head.getHead();
	}
	
	/** Calls {@link #getLeftNearestSibling(int)}, where {@code order=0}. */
	public DEPNode getLeftNearestSibling()
	{
		return getLeftNearestSibling(0);
	}
	
	/**
	 * Get the left sibling node with input displacement (0 - leftmost, 1 - second leftmost, etc.).
	 * @param order left displacement
	 * @return the left sibling node with input displacement
	 */
	public DEPNode getLeftNearestSibling(int order)
	{
		if (head_node != null)
		{
			order = sibling_id - order - 1;
			if (order >= 0) return head_node.getDependent(order);
		}
		
		return null;
	}
	
	public DEPNode getLeftNearestSibling(String label)
	{
		if (head_node != null)
		{
			DEPNode node;
			
			for (int i=sibling_id-1; i>=0; i--)
			{	
				node = head_node.getDependent(i);
				if (node.isLabel(label)) return node;
			}
		}
		
		return null;
	}

	/**
	 * Get the right nearest sibling node of the node.
	 * Calls {@link #getRightNearestSibling(int)}, where {@code order=0}.
	 * @return the right nearest sibling node
	 */
	public DEPNode getRightNearestSibling()
	{
		return getRightNearestSibling(0);
	}
	
	/**
	 * Get the right sibling node with input displacement (0 - rightmost, 1 - second rightmost, etc.).
	 * @param order right displacement
	 * @return the right sibling node with input displacement
	 */
	public DEPNode getRightNearestSibling(int order)
	{
		if (head_node != null)
		{
			order = sibling_id + order + 1;
			if (order < head_node.getDependentSize()) return head_node.getDependent(order);
		}
		
		return null;
	}
	
	public DEPNode getRightNearestSibling(String label)
	{
		if (head_node != null)
		{
			int i, size = head_node.getDependentSize();
			DEPNode node;
			
			for (i=sibling_id+1; i<size; i++)
			{	
				node = head_node.getDependent(i);
				if (node.isLabel(label)) return node;
			}
		}
		
		return null;
	}
	
	/**
	 * Get the left most dependency node of the node.
	 * Calls {@link #getLeftMostDependent(int)}, where {@code order=0}
	 * @return the left most dependency node of the node
	 */
	public DEPNode getLeftMostDependent()
	{
		return getLeftMostDependent(0);
	}
	
	/**
	 * Get the left dependency node with input displacement (0 - leftmost, 1 - second leftmost, etc.).
	 * The leftmost dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the leftmost dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getLeftMostDependent(int order)
	{
		if (DSUtils.isRange(dependent_list, order))
		{
			DEPNode dep = getDependent(order);
			if (dep.id < id) return dep;
		}

		return null;
	}
	
	/** 
	 * Get the right most dependency node of the node.
	 * Calls {@link #getRightMostDependent(int)}, where {@code order=0}. 
	 * @return the right most dependency node of the node
	 */
	public DEPNode getRightMostDependent()
	{
		return getRightMostDependent(0);
	}
	
	/**
	 * Get the right dependency node with input displacement (0 - rightmost, 1 - second rightmost, etc.).
	 * The rightmost dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the rightmost dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getRightMostDependent(int order)
	{
		order = getDependentSize() - 1 - order;
		
		if (DSUtils.isRange(dependent_list, order))
		{
			DEPNode dep = getDependent(order);
			if (dep.id > id) return dep;
		}

		return null;
	}
	
	/** 
	 * Get the left nearest dependency node.
	 * Calls {@link #getLeftNearestDependent(int)}, where {@code order=0}.
	 * @return the left nearest dependency node
	 */
	public DEPNode getLeftNearestDependent()
	{
		return getLeftNearestDependent(0);
	}
	
	/**
	 * Get the left nearest dependency node with input displacement (0 - left-nearest, 1 - second left-nearest, etc.).
	 * The left nearest dependent must be on the left-hand side of this node.
	 * @param order left displacement
	 * @return the left-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getLeftNearestDependent(int order)
	{
		int index = dependent_list.getInsertIndex(this) - order - 1;
		return (index >= 0) ? getDependent(index) : null;
	}
	
	/**
	 * Get the right nearest dependency node.
	 * Calls {@link #getRightNearestDependent(int)}, where {@code order=0}. 
	 * @return the right nearest dependency node
	 */
	public DEPNode getRightNearestDependent()
	{
		return getRightNearestDependent(0);
	}
	
	/**
	 * Get the right nearest dependency node with input displacement (0 - right-nearest, 1 - second right-nearest, etc.).
	 * The right-nearest dependent must be on the right-hand side of this node.
	 * @param order right displacement
	 * @return the right-nearest dependent of this node if exists; otherwise, {@code null}
	 */
	public DEPNode getRightNearestDependent(int order)
	{
		int index = dependent_list.getInsertIndex(this) + order;
		return (index < getDependentSize()) ? getDependent(index) : null;
	}
	
	public DEPNode getFirstDependent(BiPredicate<DEPNode,String> p, String tag)
	{
		for (DEPNode node : dependent_list)
		{
			if (p.test(node, tag))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the first dependency node of the node by label.
	 * @param label string label of the first-dependency node
	 * @return the first-dependency node of the specific label
	 */
	public DEPNode getFirstDependentByLabel(String label)
	{
		return getFirstDependent((n, t) -> n.isLabel(t), label);
	}
	
	public DEPNode getFirstDependentByPOS(String label)
	{
		return getFirstDependent((n, t) -> n.isPOSTag(t), label);
	}
	
	public DEPNode getFirstDependentByLemma(String lemma)
	{
		return getFirstDependent((n, t) -> n.isLemma(t), lemma);
	}
	
	/**
	 * Get the first dependency node of the node by label.
	 * @param pattern pattern label of the first-dependency node
	 * @return the first-dependency node of the specific label
	 */
	public DEPNode getFirstDependentByLabel(Pattern pattern)
	{
		for (DEPNode node : dependent_list)
		{
			if (node.isLabel(pattern))
				return node;
		}
		
		return null;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node.
	 * @return list of all the dependency nodes of the node
	 */
	public List<DEPNode> getDependentList()
	{
		return dependent_list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label.
	 * @param label string label
	 * @return list of all the dependency nodes of the node by label
	 */
	public List<DEPNode> getDependentListByLabel(String label)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.isLabel(label))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by labels set.
	 * @param label labels set
	 * @return list of all the dependency nodes of the node by labels set
	 */
	public List<DEPNode> getDependentListByLabel(Set<String> labels)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (labels.contains(node.getLabel()))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the dependency nodes of the node by label pattern.
	 * @param label label pattern
	 * @return list of all the dependency nodes of the node by label pattern
	 */
	public List<DEPNode> getDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.isLabel(pattern))
				list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the left dependency nodes of the node.
	 * @return list of all the left dependency nodes of the node
	 */
	public List<DEPNode> getLeftDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id > id) break;
			list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the left dependency nodes of the node by label pattern.
	 * @param label label pattern
	 * @return list of all the left dependency nodes of the node by label pattern
	 */
	public List<DEPNode> getLeftDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id > id) break;
			if (node.isLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the right dependency nodes of the node.
	 * @return list of all the right dependency nodes of the node
	 */
	public List<DEPNode> getRightDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id < id) continue;
			list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all the right dependency nodes of the node by label pattern.
	 * @param label label pattern
	 * @return list of all the right dependency nodes of the node by label pattern
	 */
	public List<DEPNode> getRightDependentListByLabel(Pattern pattern)
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
		{
			if (node.id < id) continue;
			if (node.isLabel(pattern)) list.add(node);
		}
		
		return list;
	}
	
	/**
	 * Get the list of all grand-dependents of the node. 
	 * @return an unsorted list of grand-dependents of the node
	 */
	public List<DEPNode> getGrandDependentList()
	{
		List<DEPNode> list = new ArrayList<>();
		
		for (DEPNode node : dependent_list)
			list.addAll(node.getDependentList());
	
		return list;
	}
	
	/**
	 * Get the list of all descendant nodes of the node with specified height.
	 * If {@code height == 1}, return {@link #getDependentList()}.
	 * If {@code height > 1} , return all descendants within the depth.
	 * If {@code height < 1} , return an empty list.
	 * @param height height level of the descendant nodes
	 * @return an unsorted list of descendants.
	 */
	public List<DEPNode> getDescendantList(int height)
	{
		List<DEPNode> list = new ArrayList<>();
	
		if (height > 0)
			getDescendantListAux(this, list, height-1);
		
		return list;
	}
	
	private void getDescendantListAux(DEPNode node, List<DEPNode> list, int height)
	{
		list.addAll(node.getDependentList());
		
		if (height > 0)
		{
			for (DEPNode dep : node.getDependentList())
				getDescendantListAux(dep, list, height-1);
		}
	}
	
	/**
	 * Get any descendant node with POS tag.
	 * @param tag POS tag
	 * @return s descendant node with the POS tag
	 */
	public DEPNode getAnyDescendantByPOSTag(String tag)
	{
		return getAnyDescendantByPOSTagAux(this, tag);
	}
	
	private DEPNode getAnyDescendantByPOSTagAux(DEPNode node, String tag)
	{
		for (DEPNode dep : node.getDependentList())
		{
			if (dep.isPOSTag(tag)) return dep;
			
			dep = getAnyDescendantByPOSTagAux(dep, tag);
			if (dep != null) return dep;
		}
		
		return null;
	}

	/**
	 * Get the sorted list of all the nodes in the subtree of the node.
	 * @return a sorted list of nodes in the subtree of this node (inclusive)
	  */
	public List<DEPNode> getSubNodeList()
	{
		List<DEPNode> list = new ArrayList<>();
		getSubNodeCollectionAux(list, this);
		Collections.sort(list);
		return list;
	}
	
	/**
	 * Get a set of all the nodes is the subtree of the node.
	 * @return a set of nodes in the subtree of this node (inclusive)
	 */
	public Set<DEPNode> getSubNodeSet()
	{
		Set<DEPNode> set = new HashSet<>();
		getSubNodeCollectionAux(set, this);
		return set;
	}
	
	private void getSubNodeCollectionAux(Collection<DEPNode> col, DEPNode node)
	{
		col.add(node);
		
		for (DEPNode dep : node.getDependentList())
			getSubNodeCollectionAux(col, dep);
	}
	
	/**
	 * Get the IntHashSet of all the nodes in the subtree (Node ID -> DEPNode).
	 * @return the ntHashSet of all the nodes in the subtree (inclusive)
	 */
	public IntSet getSubNodeIDSet()
	{
		IntSet set = new IntOpenHashSet();
		getSubNodeIDSetAux(set, this);
		return set;
	}

	private void getSubNodeIDSetAux(IntSet set, DEPNode node)
	{
		set.add(node.id);
		
		for (DEPNode dep : node.getDependentList())
			getSubNodeIDSetAux(set, dep);
	}
	
	/** 
	 * Get a sorted array of IDs of all the nodes in the subtree of the node.
	 * @return a sorted array of IDs from the subtree of the node (inclusive) 
	 */
	public int[] getSubNodeIDSortedArray()
	{
		IntSet set = getSubNodeIDSet();
		int[] list = set.toIntArray();
		Arrays.sort(list);
		return list;
	}
	
	/**
	 * Get the dependency node with specific index.
	 * @return the dependency node of the node with the specific index if exists; otherwise, {@code null}.
	 * @throws IndexOutOfBoundsException
	 */
	public DEPNode getDependent(int index)
	{
		return dependent_list.get(index);
	}
	
	/**
	 * Get the index of the dependency node of a specified DEPNode.
	 * If the specific node is not a dependent of this node, returns a negative number.
	 * @return the index of the dependent node among other siblings (starting with 0).
	 */
	public int getDependentIndex(DEPNode node)
	{
		return dependent_list.indexOf(node);
	}
	
	/**
	 * Get the size of the dependents of the node.
	 * @return the number of dependents of the node 
	 */
	public int getDependentSize()
	{
		return dependent_list.size();
	}
	
	/**
	 * Get the the valency of the node.
	 * @param direction DirectionType of l, r, a 
	 * @return "0" - no dependents, "<" - left dependents, ">" - right dependents, "<>" - left and right dependents. 
	 */
	public String getValency(DirectionType direction)
	{
		switch (direction)
		{
		case  l: return getLeftValency();
		case  r: return getRightValency();
		case  a: return getLeftValency()+"-"+getRightValency();
		default: return null;
		}
	}
	
	/**
	 * Get the left valency of the node.
	 * @return "<" - left dependents
	 */
	public String getLeftValency()
	{
		StringBuilder build = new StringBuilder();
		
		if (getLeftMostDependent() != null)
		{
			build.append(StringConst.LESS_THAN);
			
			if (getLeftMostDependent(1) != null)
				build.append(StringConst.LESS_THAN);
		}
		
		return build.toString();
	}
	
	/**
	 * Get the right valency of the node.
	 * @return ">" - right dependents
	 */
	public String getRightValency()
	{
		StringBuilder build = new StringBuilder();
		
		if (getRightMostDependent() != null)
		{
			build.append(StringConst.GREATER_THAN);
			
			if (getRightMostDependent(1) != null)
				build.append(StringConst.GREATER_THAN);
		}
		
		return build.toString();
	}
	
	/**
	 * Get sub-categorization of the node.
	 * @param direction direction DirectionType of l, r, a
	 * @param field Field of tag feature
	 * @return "< {@code TagFeature}" for left sub-categorization, "> {@code TagFeature}" for right-categorization, and {@code null} if not exist
	 */
	public String getSubcategorization(DirectionType direction, Field field)
	{
		switch (direction)
		{
		case l: return getLeftSubcategorization (field);
		case r: return getRightSubcategorization(field);
		case a:
			String left = getLeftSubcategorization(field);
			if (left == null) return getRightSubcategorization(field);
			String right = getRightSubcategorization(field);
			return  (right == null) ? left : left+right;
		default: return null; 
		}
	}
	
	/**
	 * Get left sub-categorization of the node.
	 * @param field Field of tag feature 
	 * @return "< {@code TagFeature}" for left sub-categorization, {@code null} if not exist. 
	 */
	public String getLeftSubcategorization(Field field)
	{
		StringBuilder build = new StringBuilder();
		int i, size = getDependentSize();
		DEPNode node;
		
		for (i=0; i<size; i++)
		{
			node = getDependent(i);
			if (node.getID() > id) break;
			build.append(StringConst.LESS_THAN);
			build.append(node.getValue(field));
		}
		
		return build.length() > 0 ? build.toString() : null;
	}
	
	/**
	 * Get right sub-categorization of the node.
	 * @param field Field of tag feature 
	 * @return "> {@code TagFeature}" for right sub-categorization, {@code null} if not exist. 
	 */
	public String getRightSubcategorization(Field field)
	{
		StringBuilder build = new StringBuilder();
		int i, size = getDependentSize();
		DEPNode node;
		
		for (i=size-1; i>=0; i--)
		{
			node = getDependent(i);
			if (node.getID() < id) break;
			build.append(StringConst.GREATER_THAN);
			build.append(node.getValue(field));
		}
		
		return build.length() > 0 ? build.toString() : null;
	}
	
	
	/**
	 * Find the path of between this nodes and the input DEPNode.
	 * @param node the node that you want to find the path from this node
	 * @param field Field of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(DEPNode node, Field field)
	{
		DEPNode lca = getLowestCommonAncestor(node);
		return (lca != null) ? getPath(node, lca, field) : null;
	}
	
	/**
	 * Find the path of between this nodes and the input DEPNode with the lowest common ancestor specified.
	 * @param node the node that you want to find the path from this node
	 * @param lca the lowest common ancestor DEPNode that you specified for the path
	 * @param field Field of the the node for search
	 * @return the path between the two nodes
	 */
	public String getPath(DEPNode node, DEPNode lca, Field field)
	{
		if (node == lca)
			return getPathAux(lca, this, field, "^", true);
		
		if (this == lca)
			return getPathAux(lca, node, field, "|", true);
		
		return getPathAux(lca, this, field, "^", true) + getPathAux(lca, node, field, "|", false);
	}
	
	private String getPathAux(DEPNode top, DEPNode bottom, Field field, String delim, boolean includeTop)
	{
		StringBuilder build = new StringBuilder();
		DEPNode node = bottom;
		int dist = 0;
		String s;
		
		do
		{
			s = node.getValue(field);
			
			if (s != null)
			{
				build.append(delim);
				build.append(s);
			}
			else
			{
				dist++;
			}
		
			node = node.getHead();
		}
		while (node != top && node != null);
		
		if (field == Field.distance)
		{
			build.append(delim);
			build.append(dist);
		}
		else if (field != Field.dependency_label && includeTop)
		{
			build.append(delim);
			build.append(top.getValue(field));
		}
		
		return build.length() == 0 ? null : build.toString();
	}
	
	/**
	 * Get a set of all the ancestor nodes of the node (ie. Parent node, Grandparent node, etc.).
	 * @return set of all the ancestor nodes
	 */
	public Set<DEPNode> getAncestorSet()
	{
		Set<DEPNode> set = new HashSet<>();
		DEPNode node = getHead();
		
		while (node != null)
		{
			set.add(node);
			node = node.getHead();
		}
		
		return set;
	}
	
	/**
	 * Get the first/lowest common ancestor of the two given nodes (this node and the input DEPNode).
	 * @param node the node that you want to find the lowest common ancestor with the node with
	 * @return the lowest common ancestor of the node and the specified node
	 */
	public DEPNode getLowestCommonAncestor(DEPNode node)
	{
		Set<DEPNode> set = getAncestorSet();
		set.add(this);
		
		while (node != null)
		{
			if (set.contains(node)) return node;
			node = node.getHead();
		}
		
		return null;
	}
	
	@Override
	public String getValue(Field field)
	{
		switch (field)
		{
		case dependency_label: return getLabel();
		default: return super.getValue(field);
		}
	}
	
//	====================================== Setters ======================================

	/** 
	 * Sets the dependency label of this node with the specific label.
	 * @param label label of the node 
	 */
	public void setLabel(String label)
	{
		dependency_label = label;
	}
	
	/** 
	 * Sets the dependency head of this node with the specific node.
	 * @param node head node of the node 
	 */
	public void setHead(DEPNode node)
	{
		if (hasHead())
			head_node.dependent_list.remove(this);
		
		if (node != null)
			sibling_id = node.dependent_list.addItem(this);
		
		head_node = node;
	}
	
	/** 
	 * Sets the dependency head of this node with the specific node and the label.
	 * @param node head node of the node
	 * @param label label of the node 
	 */
	public void setHead(DEPNode node, String label)
	{
		setHead (node);
		setLabel(label);
	}
	
	/**
	 * Add the node as a dependent to a specified node.
	 * @param node head node that you wish to add the node as a dependent to
	 */
	public void addDependent(DEPNode node)
	{
		node.setHead(this);
	}
	
	/**
	 * Add the node as a dependent to a specified node and set the label of the node.
	 * @param node head node that you wish to add the node as a dependent to
	 * @param label label of the node
	 */
	public void addDependent(DEPNode node, String label)
	{
		node.setHead(this, label);
	}
	
//	====================================== Booleans ======================================
	
	/**
	 * Check if the node has a head node.
	 * @return {@code true} if this node has the dependency head; otherwise {@code false} if head is {@code null}. 
	 */
	public boolean hasHead()
	{
		return head_node != null;
	}
	
	/**
	 * Check if the node contain another as dependent.
	 * @param node dependent code for check
	 * @return {@code true} if the node has the input DEPNode as a dependent
	 */
	public boolean containsDependent(DEPNode node)
	{
		return dependent_list.contains(node);
	}
	
	/**
	 * Check if the node has the label for its first dependent.
	 * @param label label of the node for check
	 * @return {@code true} if the node's first dependent has the input label
	 */
	public boolean containsDependent(String label)
	{
		return getFirstDependentByLabel(label) != null;
	}
	
	public boolean containsDependentPOS(String tag)
	{
		return getFirstDependentByPOS(tag) != null;
	}
	
	public boolean containsDependentLemma(String lemma)
	{
		return getFirstDependentByLemma(lemma) != null;
	}
	
	/**
	 * Check if the node has the pattern for its first dependent.
	 * @param pattern pattern of the node for check
	 * @return {@code true} if the node's first dependent has the input pattern
	 */
	public boolean containsDependent(Pattern pattern)
	{
		return getFirstDependentByLabel(pattern) != null;
	}
	
	/**
	 * Check if the node has the label as the input string.
	 * @param label label string for check
	 * @return {@code true} if the dependency label of this node equals to the specific label 
	 */
	public boolean isLabel(String label)
	{
		return label.equals(dependency_label);
	}
	
	/**
	 * Check if the node has the label as any label in the input strings array.
	 * @param labels label string array for check
	 * @return {@code true} if the dependency label of this node equals to any of the specific labels
	 */
	public boolean isLabelAny(String... labels)
	{
		for (String label : labels)
		{
			if (label.equals(dependency_label))
				return true;
		}
		
		return false;
	}
	
	/**
	 * Check if the node has the label as the input label pattern.
	 * @param pattern label pattern for check
	 * @return {@code true} if the dependency label of this node matches the specific pattern
	 */
	public boolean isLabel(Pattern pattern)
	{
		return pattern.matcher(dependency_label).find();
	}
	
	/** 
	 * Check if the node has the input dependent node. 
	 * @param node dependent node for check
	 * @return {@code true} if this node is a dependent of the specific node 
	 */
	public boolean isDependentOf(DEPNode node)
	{
		return head_node == node;
	}
	
	/**
	 * Check if the node has the input dependent node and the input label string. 
	 * @param node dependent node for check
	 * @param label label string for check
	 * @return @return {@code true} if the node has the specific dependent node and the specific label string
	 */
	public boolean isDependentOf(DEPNode node, String label)
	{
		return isDependentOf(node) && isLabel(label);
	}
	
	/**
	 * Check if the node is the descendant of the input head node. 
	 * @param label label string for check
	 * @return {@code true} if the node is the dependent of the specific node
	 */
	public boolean isDescendantOf(DEPNode node)
	{
		DEPNode head = getHead();
		
		while (head != null)
		{
			if (head == node)	return true;
			head = head.getHead();
		}
		
		return false;
	}
	
	/**
	 * Check if the node has the sibling node.
	 * @param node sibling node of the node for check
	 * @return {@code true} if the node has the sibling node
	 */
	public boolean isSiblingOf(DEPNode node)
	{
		return hasHead() && node.isDependentOf(head_node);
	}

//	====================================== Helpers ======================================
	
	@Override
	public String toString()
	{
		StringJoiner join = new StringJoiner(StringConst.TAB);
		
		join.add(Integer.toString(id));
		join.add(word_form);
		join.add(lemma);
		join.add(pos_tag);
		join.add(feat_map.toString());
		
		if (hasHead())
		{
			join.add(Integer.toString(head_node.id));
			join.add(dependency_label);
		}
		else
		{
			join.add(StringConst.UNDERSCORE);
			join.add(StringConst.UNDERSCORE);
		}
		
		return join.toString();
	}
	
	@Override
	public int compareTo(DEPNode node)
	{
		return id - node.id;
	}
}