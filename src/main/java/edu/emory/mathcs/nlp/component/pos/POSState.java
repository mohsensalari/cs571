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
package edu.emory.mathcs.nlp.component.pos;

import edu.emory.mathcs.nlp.component.util.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.state.L2RState;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSState<N extends POSNode> extends L2RState<N>
{
	AmbiguityClassMap ambiguity_class_map;
	
	public POSState(N[] nodes, AmbiguityClassMap map)
	{
		super(nodes, N::getPOSTag, N::setPOSTag);
		ambiguity_class_map = map;
	}

	@Override
	public void evaluate(Eval eval)
	{
		evaluateTokens((AccuracyEval)eval);
	}
	
	public String getAmbiguityClass(N node)
	{
		return ambiguity_class_map.get(node);
	}
}
