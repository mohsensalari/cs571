package edu.emory.mathcs.nlp.component.dep.feature;

import edu.emory.mathcs.nlp.common.util.BinUtils;
import edu.emory.mathcs.nlp.component.dep.DEPFeatureTemplate;
import edu.emory.mathcs.nlp.component.util.feature.Direction;
import edu.emory.mathcs.nlp.component.util.feature.FeatureItem;
import edu.emory.mathcs.nlp.component.util.feature.Field;
import edu.emory.mathcs.nlp.component.util.feature.Relation;
import edu.emory.mathcs.nlp.component.util.feature.Source;


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


/**
 * @author Bonggun Shin ({@code bonggun.shin@emory.edu})
 */
public class BonggunMohsenFeatureTemplate extends DEPFeatureTemplate
{


    @Override
    protected void init()
    {
        BinUtils.LOG.info("FeatureTemplateStanford used\n");
     // lemma features 
     		add(new FeatureItem<>(Source.i, 0, Field.word_form));
     		add(new FeatureItem<>(Source.j, 0, Field.word_form));
     		add(new FeatureItem<>(Source.i, 0, Field.dependency_label));
     		
     		//template2
     		// lemma features 
    		add(new FeatureItem<>(Source.i, -1, Field.lemma));
    		add(new FeatureItem<>(Source.i,  0, Field.lemma));
    		add(new FeatureItem<>(Source.i,  1, Field.lemma));
    		
    		add(new FeatureItem<>(Source.j, -2, Field.lemma));
    		add(new FeatureItem<>(Source.j, -1, Field.lemma));
    		add(new FeatureItem<>(Source.j,  0, Field.lemma));
    		add(new FeatureItem<>(Source.j,  1, Field.lemma));
    		add(new FeatureItem<>(Source.j,  2, Field.lemma));
    		
    		add(new FeatureItem<>(Source.k,  1, Field.lemma));
    		
    		// pos features
    		add(new FeatureItem<>(Source.i, -2, Field.pos_tag));
    		add(new FeatureItem<>(Source.i, -1, Field.pos_tag));
    		add(new FeatureItem<>(Source.i,  0, Field.pos_tag));
    		add(new FeatureItem<>(Source.i,  1, Field.pos_tag));
    		add(new FeatureItem<>(Source.i,  2, Field.pos_tag));
    		
    		add(new FeatureItem<>(Source.j, -2, Field.pos_tag));
    		add(new FeatureItem<>(Source.j, -1, Field.pos_tag));
    		add(new FeatureItem<>(Source.j,  0, Field.pos_tag));
    		add(new FeatureItem<>(Source.j,  1, Field.pos_tag));
    		add(new FeatureItem<>(Source.j,  2, Field.pos_tag));
    		
    		add(new FeatureItem<>(Source.k,  1, Field.pos_tag));
    		add(new FeatureItem<>(Source.k,  2, Field.pos_tag));
    		
    		// valency features
    		add(new FeatureItem<>(Source.i, 0, Field.valency, Direction.all));
    		add(new FeatureItem<>(Source.j, 0, Field.valency, Direction.all));
    		
    		// 2nd-order features
    		add(new FeatureItem<>(Source.i, Relation.h  , 0, Field.lemma));
    		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.lemma));
    		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.lemma));
    		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.lemma));
    		
    		add(new FeatureItem<>(Source.i, Relation.h  , 0, Field.pos_tag));
    		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.pos_tag));
    		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.pos_tag));
    		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.pos_tag));
    		
    		add(new FeatureItem<>(Source.i,               0, Field.dependency_label));
    		add(new FeatureItem<>(Source.i, Relation.lns, 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.i, Relation.lmd, 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.i, Relation.rmd, 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.j, Relation.lmd, 0, Field.dependency_label));
    		
    		// 3rd-order features
    		add(new FeatureItem<>(Source.i, Relation.h2  , 0, Field.lemma));
    		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.lemma));
    		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.lemma));
    		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.lemma));
    		
    		add(new FeatureItem<>(Source.i, Relation.h2  , 0, Field.pos_tag));
    		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.pos_tag));
    		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.pos_tag));
    		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.pos_tag));
    		
    		add(new FeatureItem<>(Source.i, Relation.h   , 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.i, Relation.lns2, 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.i, Relation.lmd2, 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.i, Relation.rmd2, 0, Field.dependency_label));
    		add(new FeatureItem<>(Source.j, Relation.lmd2, 0, Field.dependency_label));
    		
    		// boolean features
    		addSet(new FeatureItem<>(Source.i, 0, Field.binary));
    		addSet(new FeatureItem<>(Source.j, 0, Field.binary));
     		
     		
     		//end template 2
     		
        // Single-word features
        // s1.w
        add(new FeatureItem<>(Source.i,  0, Field.lemma));
        // s1.t
        add(new FeatureItem<>(Source.i, 0, Field.pos_tag));
        // s1.wt
        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag));

        // s2.w
        add(new FeatureItem<>(Source.i,  1, Field.lemma));
        // s2.t
        add(new FeatureItem<>(Source.i,  1, Field.pos_tag));
        // s2.wt
        add(new FeatureItem<>(Source.i, 1, Field.lemma), new FeatureItem<>(Source.i, 1, Field.pos_tag));

        // b1.w
        add(new FeatureItem<>(Source.j,  0, Field.lemma));
        // b1.t
        add(new FeatureItem<>(Source.j,  0, Field.pos_tag));
        // b1.wt
        add(new FeatureItem<>(Source.j,  0, Field.lemma), new FeatureItem<>(Source.j,  0, Field.pos_tag));

        // Word-pair features
        // s1.wt◦s2.wt
        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i, 1, Field.lemma), new FeatureItem<>(Source.i, 1, Field.pos_tag));
        // s1.wt◦s2.w
        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i, 1, Field.lemma));
        // s1.wt◦s2.t
        add(new FeatureItem<>(Source.i, 0, Field.lemma), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i, 1, Field.pos_tag));
        // s1.w◦s2.wt
        add(new FeatureItem<>(Source.i, 0, Field.lemma),
                new FeatureItem<>(Source.i, 1, Field.lemma), new FeatureItem<>(Source.i, 1, Field.pos_tag));
        // s1.t◦s2.wt
        add(new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i, 1, Field.lemma), new FeatureItem<>(Source.i, 1, Field.pos_tag));
        // s1.w◦s2.w
        add(new FeatureItem<>(Source.i, 0, Field.lemma),
                new FeatureItem<>(Source.i, 1, Field.lemma));
        // s1.t◦s2.t
        add(new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i, 1, Field.pos_tag));
        // s1.t◦b1.t
        add(new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.j, 0, Field.pos_tag));

        // Three-word features
        // s2.t◦s1.t◦b1.t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.j, 0, Field.pos_tag));
        // s2.t◦s1.t◦lc1(s1).t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i,  Relation.lnd, 0, Field.pos_tag));
        // s2.t◦s1.t◦rc1(s1).t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i,  Relation.rnd, 0, Field.pos_tag));

        // s2.t◦s1.t◦lc1(s2).t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i,  Relation.lnd, 1, Field.pos_tag));
        // s2.t◦s1.t◦rc1(s2).t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.pos_tag),
                new FeatureItem<>(Source.i,  Relation.rnd, 1, Field.pos_tag));

        // s2.t◦s1.w◦rc1(s2).t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.lemma),
                new FeatureItem<>(Source.i,  Relation.rnd, 1, Field.pos_tag));
        // s2.t◦s1.w◦lc1(s1).t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.lemma),
                new FeatureItem<>(Source.i,  Relation.lnd, 0, Field.pos_tag));

        // s2.t◦s1.w◦b1.t
        add(new FeatureItem<>(Source.i, 1, Field.pos_tag), new FeatureItem<>(Source.i, 0, Field.lemma),
                new FeatureItem<>(Source.j,  0, Field.pos_tag));
    }
}
