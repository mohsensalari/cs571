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
package edu.emory.mathcs.nlp.bin;

import java.util.List;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.common.util.IOUtils;
import edu.emory.mathcs.nlp.component.pos.AmbiguityClassMap;
import edu.emory.mathcs.nlp.component.pos.POSConfig;
import edu.emory.mathcs.nlp.component.pos.POSNode;
import edu.emory.mathcs.nlp.component.pos.POSState;
import edu.emory.mathcs.nlp.component.pos.POSTagger;
import edu.emory.mathcs.nlp.component.pos.feature.POSFeatureTemplate1;
import edu.emory.mathcs.nlp.component.pos.feature.POSFeatureTemplate0;
import edu.emory.mathcs.nlp.component.util.NLPComponent;
import edu.emory.mathcs.nlp.component.util.config.NLPConfig;
import edu.emory.mathcs.nlp.component.util.eval.AccuracyEval;
import edu.emory.mathcs.nlp.component.util.eval.Eval;
import edu.emory.mathcs.nlp.component.util.feature.FeatureTemplate;
import edu.emory.mathcs.nlp.component.util.reader.TSVReader;
import edu.emory.mathcs.nlp.component.util.train.NLPTrain;
import edu.emory.mathcs.nlp.learn.model.StringModel;
import edu.emory.mathcs.nlp.learn.weight.MultinomialWeightVector;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class POSTrain extends NLPTrain<POSNode,POSState<POSNode>>
{
	public POSTrain(String[] args)
	{
		super(args);
	}

	@Override
	protected NLPConfig<POSNode> createConfiguration(String filename)
	{
		return new POSConfig(IOUtils.createFileInputStream(filename));
	}
	
	@Override
	protected Eval createEvaluator()
	{
		return new AccuracyEval();
	}
	
	@Override
	protected NLPComponent<POSNode,POSState<POSNode>> createComponent()
	{
		return new POSTagger<>(new StringModel(new MultinomialWeightVector()));
	}
	
	@Override
	protected FeatureTemplate<POSNode,POSState<POSNode>> createFeatureTemplate()
	{
		switch (feature_template)
		{
		case 0: return new POSFeatureTemplate0();
		case 1: return new POSFeatureTemplate1();
		default: throw new IllegalArgumentException("Unknown feature template: "+feature_template);
		}
	}
	
	@Override
	public void collect(TSVReader<POSNode> reader, List<String> inputFiles, NLPComponent<POSNode,POSState<POSNode>> component, NLPConfig<POSNode> configuration)
	{
		POSTagger<POSNode> tagger = (POSTagger<POSNode>)component;
		POSConfig config = (POSConfig)configuration;
		AmbiguityClassMap ac = new AmbiguityClassMap();
		
		iterate(reader, inputFiles, nodes -> ac.add(nodes));
		ac.expand(config.getAmbiguityClassThreshold());
		tagger.setAmbiguityClassMap(ac);
		
		BinUtils.LOG.info(String.format("- # of ambiguity classes: %d\n", ac.size()));
	}
	
	static public void main(String[] args)
	{
		new POSTrain(args).train();
	}
}
