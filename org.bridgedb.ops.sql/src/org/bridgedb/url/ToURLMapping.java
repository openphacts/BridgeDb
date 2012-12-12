// BridgeDb,
// An abstraction layer for identifier mapping services, both local and online.
//
// Copyright      2012  Christian Y. A. Brenninkmeijer
// Copyright      2012  OpenPhacts
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//
package org.bridgedb.url;

import java.util.HashSet;
import java.util.Set;
import org.bridgedb.Xref;

/**
 * Contains the information held for a particular mapping.
 * <p>
 * @See getMethods for what is returned.
 * <p>
 * A few things that are not returned and why included:
 * <ul>
 * <li>UriSpace: 
 * @author Christian
 */
public class ToURLMapping {
 
    private Integer id;
    private Xref source;
    private Set<String> targetURLs;
    private Integer mappingSetId;
    private String predicate;
    
    public ToURLMapping (Integer id, Xref source, String predicate, String targetURL, Integer mappingSetId){
        this.id = id;
        this.source = source;
        this.targetURLs = new HashSet<String>();
        targetURLs.add(targetURL);
        this.mappingSetId = mappingSetId;
        this.predicate = predicate;
    }

    /**
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @return the target URLs
     */
    public Set<String> getTargetURLs() {
        return targetURLs;
    }

    public void addTargetURL(String targetURL){
        targetURLs.add(targetURL);
    }

    public String toString(){
        StringBuilder output = new StringBuilder("mapping ");
        output.append(this.id);
        output.append("\n\tSource: ");
        output.append(source);
        output.append("\n\tPredicate(): ");
        output.append(predicate);
        for (String targetURL:targetURLs){
            output.append("\n\tTargetURL: ");
            output.append(targetURL);
        }
        output.append("\n\tMappingSet(id): ");
        output.append(mappingSetId);
        return output.toString();
    }
   
    @Override
    public boolean equals(Object other){
        if (other == null) return false;
        if (other instanceof ToURLMapping){
            ToURLMapping otherMapping = (ToURLMapping)other;
            if (otherMapping.id != id) return false;
            if (!otherMapping.source.equals(source)) return false;
            if (!otherMapping.targetURLs.equals(targetURLs)) return false;
            if (!otherMapping.getMappingSetId().equals(getMappingSetId())) return false;
            //No need to check predicate as by defintion one id has one predicate
            return true;
         } else {
            return false;
        }
    }

    /**
     * @return the mappingSetId
     */
    public Integer getMappingSetId() {
        return mappingSetId;
    }

    /**
     * @return the predicate
     */
    public String getPredicate() {
        return predicate;
    }

    /**
     * @return the source
     */
    public Xref getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(Xref source) {
        this.source = source;
    }
}
