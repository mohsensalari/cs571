package edu.emroy.mathcs.nlp.component.ner;

import java.util.Arrays;
import edu.emory.mathcs.nlp.component.ner.NERNode;

public class NERtest {

	public double getF1(NERNode[] gold, NERNode[] sys) {
		int true_positive = 0;
		boolean crossed[] = new boolean[gold.length];
		Arrays.fill(crossed, false);
		for (NERNode sysNode : sys) {
			for (int i = 0; i < gold.length; i++) {
				if ((!crossed[i]) && (sysNode.isExactMatch(gold[i]))) {
					true_positive++;
					crossed[i] = true;
					break;
				}

			}

		}
		double precision = true_positive / sys.length;
		double recall = true_positive / gold.length;
		double f1_score = 2 * (precision * recall) / (precision + recall);

		return f1_score;
	}

}
