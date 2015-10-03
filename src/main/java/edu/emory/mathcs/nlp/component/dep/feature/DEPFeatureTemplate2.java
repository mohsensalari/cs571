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
package edu.emory.mathcs.nlp.component.dep.feature;

import edu.emory.mathcs.nlp.component.dep.DEPFeatureTemplate;
import edu.emory.mathcs.nlp.component.util.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.util.feature.Field;
import edu.emory.mathcs.nlp.component.util.feature.Source;

/**
 * @author Jinho D. Choi ({@code jinho.choi@emory.edu})
 */
public class DEPFeatureTemplate2 extends DEPFeatureTemplate
{
	private static final long serialVersionUID = 4717085054409332081L;

	@Override
	protected void init()
	{
		// lemma features 
		add(new FeatureItem<>(Source.i, 0, Field.word_form));
		add(new FeatureItem<>(Source.j, 0, Field.word_form));
		add(new FeatureItem<>(Source.i, 0, Field.dependency_label));
	}
}
