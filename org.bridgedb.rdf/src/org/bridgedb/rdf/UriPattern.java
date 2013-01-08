/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bridgedb.rdf;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.bridgedb.DataSource;
import org.bridgedb.rdf.constants.BridgeDBConstants;
import org.bridgedb.rdf.constants.RdfConstants;
import org.bridgedb.rdf.constants.VoidConstants;
import org.bridgedb.utils.BridgeDBException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.impl.LiteralImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;

/**
 *
 * @author Christian
 */
public class UriPattern extends RdfBase {

    private final String nameSpace;
    private final String postfix;
    private DataSourceUris dataSourceUris;
    private HashSet<DataSourceUris> isUriPatternOf = new HashSet<DataSourceUris>();
    
    private static HashMap<Resource, UriPattern> register = new HashMap<Resource, UriPattern>();
    private static HashMap<String,UriPattern> byNameSpaceOnly = new HashMap<String,UriPattern>();
    private static HashMap<String,HashMap<String,UriPattern>> byNameSpaceAndPostFix = 
            new HashMap<String,HashMap<String,UriPattern>> ();  
    private static HashSet<URI> expectedPredicates = new HashSet<URI>(Arrays.asList(new URI[] {
        BridgeDBConstants.POSTFIX_URI,
        VoidConstants.URI_SPACE_URI,
        RdfConstants.TYPE_URI,
        BridgeDBConstants.HAS_DATA_SOURCE,
        BridgeDBConstants.IS_URI_PATTERN_OF
    }));
            
    private UriPattern(String namespace){
        this.nameSpace = namespace;
        this.postfix = null;
        byNameSpaceOnly.put(namespace, this);
        register.put(getResourceId(), this);
    } 
    
    private UriPattern(String namespace, String postfix){
        this.nameSpace = namespace;
        if (postfix == null || postfix.isEmpty()){
            this.postfix = null;
            byNameSpaceOnly.put(namespace, this);    
        } else {
            this.postfix = postfix;
            HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(namespace);
            if (postFixMap == null){
                postFixMap = new HashMap<String,UriPattern>();
            }
            postFixMap.put(postfix, this);
            byNameSpaceAndPostFix.put(namespace, postFixMap);
        }
        register.put(getResourceId(), this);
    }
   
    public static UriPattern byNameSpace(String nameSpace){
        UriPattern result = byNameSpaceOnly.get(nameSpace);
        if (result == null){
            result = new UriPattern(nameSpace);
        }
        return result;
    }
    
    private static UriPattern byNameSpaceAndPostfix(String nameSpace, String postfix) {
        HashMap<String,UriPattern> postFixMap = byNameSpaceAndPostFix.get(nameSpace);
        if (postFixMap == null){
            return new UriPattern(nameSpace, postfix);
        }
        UriPattern result = postFixMap.get(postfix);
        if (result == null){
            return new UriPattern(nameSpace, postfix);
        }
        return result;
    }
                
    public static UriPattern byPattern(String urlPattern) throws BridgeDBException{
        int pos = urlPattern.indexOf("$id");
        if (pos == -1) {
            throw new BridgeDBException("Urlpattern should have $id in it");
        }
        String nameSpace = urlPattern.substring(0, pos);
        String postfix = urlPattern.substring(pos + 3);
        return byNameSpaceAndPostFix(nameSpace, postfix);
    }

    public static UriPattern byNameSpaceAndPostFix(String nameSpace, String postfix) throws BridgeDBException{
        if (postfix.isEmpty()){
            return byNameSpace(nameSpace);
        } else {
            return byNameSpaceAndPostfix(nameSpace, postfix);
        }
    }
    
    public void setPrimaryDataSource(DataSourceUris dsu) throws BridgeDBException{
        if (dataSourceUris ==  null) {       
            dataSourceUris = dsu;
            isUriPatternOf.remove(dsu);
        } else if (dataSourceUris.equals(dsu)){
            //Do nothing;
        } else {
            throw new BridgeDBException("Illegal attempt to set primary DataSource of " + this + " to " + dsu 
                    + " has it was already set to " + dataSourceUris);
        }  
    }
       
    public void setDataSource(DataSourceUris dsu) throws BridgeDBException{
        if (dsu == null){
            //ignore request
        } else if (dsu.equals(dataSourceUris)){
            //Do nothing;
        } else {
            isUriPatternOf.add(dsu);
        } 
     }
    
    public DataSource getDataSource(){
        if (dataSourceUris == null){
            return null;
        }
        return dataSourceUris.getDataSource();
    }
    
    public DataSourceUris getMainDataSourceUris() {
        if (dataSourceUris != null){
            return dataSourceUris;
        }
        if (isUriPatternOf.size() == 1){
            return isUriPatternOf.iterator().next();
        }
        return null;
    }
  

    public final URI getResourceId(){
        return new URIImpl(getUriPattern());
    }
    
    public String getUriPattern() {
        if (postfix == null){
            return nameSpace + "$id";
        } else {
            return nameSpace + "$id" + postfix;
        }
    }

    public static void addAll(RepositoryConnection repositoryConnection, boolean addPrimaries) 
            throws IOException, RepositoryException, BridgeDBException {
        for (UriPattern uriPattern:register.values()){
            uriPattern.add(repositoryConnection, addPrimaries);
        }        
    }
    
    public void add(RepositoryConnection repositoryConnection, boolean addPrimaries) throws RepositoryException{
        URI id = getResourceId();
        repositoryConnection.add(id, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI);
        repositoryConnection.add(id, VoidConstants.URI_SPACE_URI,  new LiteralImpl(nameSpace));
        if (postfix != null){
            repositoryConnection.add(id, BridgeDBConstants.POSTFIX_URI,  new LiteralImpl(postfix));
        }
        if (addPrimaries){
            DataSourceUris primary = getMainDataSourceUris();
            if (primary != null){
                repositoryConnection.add(id, BridgeDBConstants.HAS_DATA_SOURCE,  this.getMainDataSourceUris().getResourceId());            
            }        
            for (DataSourceUris dsu:isUriPatternOf){
                if (!dsu.equals(primary)){
                    repositoryConnection.add(id, BridgeDBConstants.IS_URI_PATTERN_OF,  dsu.getResourceId()); 
                }
            }
            
        } else {
            if (dataSourceUris != null){
                repositoryConnection.add(id, BridgeDBConstants.HAS_DATA_SOURCE,  dataSourceUris.getResourceId());            
            }
            for (DataSourceUris dsu:isUriPatternOf){
                 repositoryConnection.add(id, BridgeDBConstants.IS_URI_PATTERN_OF,  dsu.getResourceId()); 
            }
        }
    }        
    
    public static void readAllUriPatterns(RepositoryConnection repositoryConnection) throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(null, RdfConstants.TYPE_URI, BridgeDBConstants.URI_PATTERN_URI, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            UriPattern pattern = readUriPattern(repositoryConnection, statement.getSubject());
        }
    }

    public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource dataSourceId, DataSourceUris parent, 
            URI primary, URI shared) throws RepositoryException, BridgeDBException{
        Resource primaryId = getPossibleSingletonResource(repositoryConnection, dataSourceId, primary);
        Resource sharedId = getPossibleSingletonResource(repositoryConnection, dataSourceId, shared);
        if (primaryId == null){
            if (sharedId == null){
                //nothing found
                return null;
            } else {
                UriPattern result = readUriPattern(repositoryConnection, sharedId);
                result.setDataSource(parent);
                return result;
            } 
        } else { 
            if (sharedId == null){
                UriPattern result = readUriPattern(repositoryConnection, primaryId);
                result.setPrimaryDataSource(parent);
                return result;
            } else {
                throw new BridgeDBException(parent.getResourceId() + " can not have both a " 
                        + primary + " and a " + shared + " predicate.");
            }
        }        
    }
    
   public static UriPattern readUriPattern(RepositoryConnection repositoryConnection, Resource uriPatternId) 
            throws BridgeDBException, RepositoryException{
        checkStatements(repositoryConnection, uriPatternId);
        UriPattern pattern;      
        String uriSpace = getPossibleSingletonString(repositoryConnection, uriPatternId, VoidConstants.URI_SPACE_URI);
        if (uriSpace == null){
            pattern = byPattern(uriPatternId.stringValue());
        } else {
            String postfix = getPossibleSingletonString(repositoryConnection, uriPatternId, BridgeDBConstants.POSTFIX_URI);
            if (postfix == null){
                pattern = UriPattern.byNameSpace(uriSpace);
            } else {
                pattern = UriPattern.byNameSpaceAndPostFix(uriSpace, postfix);
            }
        }
        //Constructor registers with standard recource this register with used resource
        register.put((URI)uriPatternId, pattern);
/*        Resource dataSourceID = getPossibleSingletonResource(repositoryConnection, uriPatternId, BridgeDBConstants.HAS_DATA_SOURCE);
        System.out.println(dataSourceID);
        if (dataSourceID != null){
            DataSourceUris dsu = DataSourceUris.readDataSourceUris(repositoryConnection, dataSourceID);
            pattern.setParentDataSource(dsu);
        }
        Set<Resource> resources = getAllResources(repositoryConnection, uriPatternId, BridgeDBConstants.IS_URI_PATTERN_OF);
        for (Resource dataSource:resources){
            System.out.println(dataSourceID);
            DataSourceUris dsu = DataSourceUris.readDataSourceUris(repositoryConnection, dataSource);
            pattern.setNonParentDataSource(dsu);           
        }
*/        return pattern;
    }

    private final static void checkStatements(RepositoryConnection repositoryConnection, Resource dataSourceId) 
            throws BridgeDBException, RepositoryException{
        RepositoryResult<Statement> statements = 
                repositoryConnection.getStatements(dataSourceId, null, null, true);
        while (statements.hasNext()) {
            Statement statement = statements.next();
            try{
                if (!expectedPredicates.contains(statement.getPredicate())){
                    System.err.println("unexpected predicate in statement " + statement);
                }
            } catch (Exception e){
                throw new BridgeDBException ("Error processing statement " + statement, e);
            }
        }
    }
    
    @Override
    public String toString(){
        return getUriPattern();      
    }

    public boolean hasPostfix(){
        return postfix != null;
    }
    
    public String getUriSpace() throws BridgeDBException {
        if (postfix != null){
            throw new BridgeDBException("UriPattern " + this + " has a postfix");
        }
        return nameSpace;
    }

    public static void checkAllDataSourceUris() throws BridgeDBException{
        for (UriPattern uriPattern:register.values()){
            uriPattern.checkDataSourceUris();
        }        
    }
    
    private void checkDataSourceUris() throws BridgeDBException{
        if (dataSourceUris != null){
            return;  //DataSource has been Set
        }
        if (isUriPatternOf.size() <= 1){
            return;  //Singleton can be used and empty is fine too
        }
        DataSourceUris parent = null;
        for (DataSourceUris dsu:isUriPatternOf){
            if (dsu.getUriParent() != null){
                if (parent == null){
                    parent = dsu.getUriParent();
                } else {
                    System.out.println(this + " has two primary DataSources " + parent + " and " + dsu 
                            + " please delcare one formally");
                }
            }
        }
        if (parent != null){
            dataSourceUris = parent;
            System.out.println("�" + parent);
        } else {
            String uriPattern = getUriPattern();        
            DataSource dataSource = DataSource.register(uriPattern, uriPattern).urlPattern(uriPattern).asDataSource();
            dataSourceUris = DataSourceUris.byDataSource(dataSource);
        }
        for (DataSourceUris dsu:isUriPatternOf){
            dsu.setUriParent(dataSourceUris); 
        }
    }
 }
