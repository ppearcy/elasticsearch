/*
 * Licensed to Elastic Search and Shay Banon under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.index.analysis;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.KeywordMarkerFilter;
import org.apache.lucene.analysis.TokenStream;
import org.elasticsearch.ElasticSearchIllegalArgumentException;
import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.common.inject.assistedinject.Assisted;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.env.Environment;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.settings.IndexSettings;
import org.apache.lucene.util.Version;

import java.util.Set;

@AnalysisSettingsRequired
public class KeywordMarkerTokenFilterFactory extends AbstractTokenFilterFactory {

    private final CharArraySet keywordLookup;

    @Inject public KeywordMarkerTokenFilterFactory(Index index, @IndexSettings Settings indexSettings, Environment env, @Assisted String name, @Assisted Settings settings) {
        super(index, indexSettings, name, settings);

        boolean ignoreCase = settings.getAsBoolean("ignore_case", false);
        Set<String> rules = Analysis.getWordSet(env, settings, "keywords");
        if (rules == null) {
            throw new ElasticSearchIllegalArgumentException("keyword filter requires either `keywords` or `keywords_path` to be configured");
        }
        keywordLookup = new CharArraySet(Version.LUCENE_32, rules, ignoreCase);
    }

    @Override public TokenStream create(TokenStream tokenStream) {
        return new KeywordMarkerFilter(tokenStream, keywordLookup);
    }
}
