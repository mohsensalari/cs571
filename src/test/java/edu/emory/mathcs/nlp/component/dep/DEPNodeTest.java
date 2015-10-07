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

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.util.List;
import java.util.regex.Pattern;

import edu.emory.mathcs.nlp.component.util.feature.Direction;
import edu.emory.mathcs.nlp.component.util.feature.Field;
import org.junit.Ignore;
import org.junit.Test;

import edu.emory.mathcs.nlp.common.util.Joiner;
import edu.emory.mathcs.nlp.component.util.node.FeatMap;
import edu.emory.mathcs.nlp.component.util.reader.TSVReader;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPNodeTest
{
	@Test
//	@Ignore
	public void test() throws Exception
	{
		TSVReader<DEPNode> reader = new TSVReader<>(new DEPIndex(1, 2, 3, 4, 5, 6));
		reader.open(new FileInputStream("src/main/resources/dat/wsj_0001.dep"));
		DEPNode[] nodes = reader.next();
		
		// TODO:
//		System.out.println(Joiner.join(nodes, "\n"));

        assertEquals(nodes[4].getGrandHead(), nodes[6]);

        assertEquals(nodes[2].getLeftNearestSibling(), null);
        assertEquals(nodes[3].getLeftNearestSibling(), nodes[1]);
        assertEquals(nodes[6].getLeftNearestSibling(), nodes[3]);

        assertEquals(nodes[1].getRightNearestSibling(), nodes[3]);
        assertEquals(nodes[2].getRightNearestSibling(), nodes[8]);
        assertEquals(nodes[3].getRightNearestSibling(), nodes[6]);


        assertEquals(nodes[2].getLeftMostDependent(), nodes[1]); //1
        assertEquals(nodes[5].getLeftMostDependent(), nodes[4]); //4
        assertEquals(nodes[6].getLeftMostDependent(), nodes[5]); //5

        assertEquals(nodes[2].getRightMostDependent(), nodes[7]); //7
        assertEquals(nodes[9].getRightMostDependent(), nodes[18]); //18
        assertEquals(nodes[12].getRightMostDependent(), nodes[15]); //15


        assertEquals(nodes[2].getLeftNearestDependent(), nodes[1]); //1
        assertEquals(nodes[5].getLeftNearestDependent(), nodes[4]); //4
        assertEquals(nodes[6].getLeftNearestDependent(), nodes[5]);//5
        assertEquals(nodes[9].getLeftNearestDependent(), nodes[8]); //8
        assertEquals(nodes[11].getLeftNearestDependent(), nodes[10]); //10

        assertEquals(nodes[2].getRightNearestDependent(), nodes[3]); //3
        assertEquals(nodes[9].getRightNearestDependent(), nodes[11]); //11
        assertEquals(nodes[12].getRightNearestDependent(), nodes[15]); //15


        assertEquals(nodes[2].getDependentList().get(0), nodes[1]); //1
        assertEquals(nodes[5].getDependentList().get(0), nodes[4]); //4
        assertEquals(nodes[6].getDependentList().get(0), nodes[5]); //5


        assertEquals(nodes[2].getDependentListByLabel("nn").get(0), nodes[1]); //1
        assertEquals(nodes[9].getDependentListByLabel("prep").get(0), nodes[12]); //12


        assertEquals(nodes[2].getLeftDependentList().get(0), nodes[1]); //1
        assertEquals(nodes[5].getLeftDependentList().get(0), nodes[4]); //4
        assertEquals(nodes[6].getLeftDependentList().get(0), nodes[5]); //5

        String pattern = "(p.*)";
        Pattern r = Pattern.compile(pattern);

        assertEquals(nodes[6].getLeftDependentListByLabel(r).get(0), nodes[5]);



        assertEquals(nodes[2].getRightDependentList().get(0).getID(), 3);
        assertEquals(nodes[9].getRightDependentList().get(0).getID(), 11);

        assertEquals(nodes[2].getRightDependentListByLabel(r).get(0), nodes[3]);
        assertEquals(nodes[9].getRightDependentListByLabel(r).get(0), nodes[12]);

        assertEquals(nodes[2].getGrandDependentList().get(0), nodes[5]);
        assertEquals(nodes[6].getGrandDependentList().get(0), nodes[4]);

        assertEquals(nodes[2].getDescendantList(0).size(),0);
        assertEquals(nodes[2].getDescendantList(1).get(0), nodes[1]);
        assertEquals(nodes[2].getDescendantList(1).get(1), nodes[3]);
        assertEquals(nodes[2].getDescendantList(1).get(2), nodes[6]);
        assertEquals(nodes[2].getDescendantList(1).get(3), nodes[7]);

        assertEquals(nodes[2].getDescendantList(2).get(0), nodes[1]);
        assertEquals(nodes[2].getDescendantList(2).get(1), nodes[3]);
        assertEquals(nodes[2].getDescendantList(2).get(2), nodes[6]);
        assertEquals(nodes[2].getDescendantList(2).get(3), nodes[7]);
        assertEquals(nodes[2].getDescendantList(2).get(4), nodes[5]);



        assertEquals(nodes[2].getAnyDescendantByPOSTag("NNP"), nodes[1]);

        assertEquals(nodes[5].getSubNodeList().get(0), nodes[4]);
        assertEquals(nodes[5].getSubNodeList().get(1), nodes[5]);

        assertEquals(nodes[5].getSubNodeIDSet().contains(4), true);
        assertEquals(nodes[5].getSubNodeIDSet().contains(5), true);


        assertEquals(nodes[5].getSubNodeIDSortedArray()[0], 4);


        assertEquals(nodes[2].getDependentSize(), 4);
        assertEquals(nodes[5].getDependentSize(), 1);

        assertEquals(nodes[2].getDependent(3), nodes[7]);
        assertEquals(nodes[5].getDependent(0), nodes[4]);

        assertEquals(nodes[2].getDependentIndex(nodes[7]), 3);
        assertEquals(nodes[5].getDependentIndex(nodes[4]), 0);


        assertEquals(nodes[2].getValency(Direction.right), ">>");
        assertEquals(nodes[2].getValency(Direction.left), "<");
        assertEquals(nodes[1].getValency(Direction.all), "-");

        assertEquals(nodes[2].getSubcategorization(Direction.all, Field.lemma), "<pierre>,>old>,");

        assertEquals(nodes[5].getPath(nodes[2], Field.lemma), "^year^old^vinken");


        assertEquals(nodes[4].getAncestorSet().contains(nodes[2]), true);
        assertEquals(nodes[4].getAncestorSet().contains(nodes[6]), true);

        assertEquals(nodes[4].getLowestCommonAncestor(nodes[3]), nodes[2]);

	}
	
	@Test
//	@Ignore
	public void testBasicFields()
	{
		DEPNode node = new DEPNode(1, "Jinho");
		
		assertEquals(1, node.getID());
		assertEquals("Jinho", node.getWordForm());
		
		node = new DEPNode(1, "Jinho", "jinho", "NNP", new FeatMap("fst=jinho|lst=choi"));
		
		assertEquals(1       , node.getID());
		assertEquals("Jinho" , node.getWordForm());
		assertEquals("jinho", node.getLemma());
		assertEquals("NNP"   , node.getPOSTag());
		
		node.removeFeat("fst");
		assertEquals(null, node.getFeat("fst"));
		assertEquals("choi", node.getFeat("lst"));
		
		node.putFeat("fst", "Jinho");

		assertEquals("Jinho", node.getFeat("fst"));


	}

	@Test
	public void testSetters()
	{
		DEPNode node1 = new DEPNode(1, "He");
		DEPNode node2 = new DEPNode(2, "bought");
		DEPNode node3 = new DEPNode(3, "a");
		DEPNode node4 = new DEPNode(4, "car");

        DEPNode[] nodes;
        nodes = new DEPNode[4];
        nodes[0] = node1;
        nodes[1] = node2;
        nodes[2] = node3;
        nodes[3] = node4;


		node2.addDependent(node4, "dobj");
		node2.addDependent(node1, "nsubj");
		node4.addDependent(node3, "det");
		
		List<DEPNode> list = node2.getDependentList();
		assertEquals(node1, list.get(0));
		assertEquals(node4, list.get(1));

        node1.setLabel("nsubj2");
        assertEquals(node1.getLabel(), "nsubj2");
        node1.setLabel("nsubj");
        assertEquals(node1.getLabel(), "nsubj");

        node3.setHead(node1);
        assertEquals(node3.getHead(), node1);
        node3.setHead(node4);
        assertEquals(node3.getHead(), node4);

        assertEquals(node3.hasHead(), true);

        node3.clearDependencies();
        assertEquals(node3.hasHead(), false);
        node4.addDependent(node3, "det");
        assertEquals(node3.hasHead(), true);
    }

}