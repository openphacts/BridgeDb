package org.bridgedb.url;

import org.bridgedb.mapping.MappingListener;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTestBase;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author Christian
 */
public abstract class URLListenerTest extends URLMapperTestBase{
        
    protected static URLListener listener;
    protected static final String TEST_PREDICATE = "http://www.bridgedb.org/test#testPredicate";
    private static final boolean SYMETRIC = true;
    private static final boolean ORIGINAL = false;
    private static final boolean TRANSATIVE = true;

    @Test
    public void testRegisterUriSpace() throws IDMapperException{
        listener.registerUriSpace(DataSource1, URISpace1);
        listener.registerUriSpace(DataSource2, URISpace2);
        listener.registerUriSpace(DataSource3, URISpace3);

        listener.openInput();
        int mappingSet = listener.registerMappingSet(URISpace1, URISpace2, TEST_PREDICATE, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL1, map1URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL2, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL2, mappingSet, SYMETRIC);
        
        mappingSet = listener.registerMappingSet(URISpace2, URISpace3, TEST_PREDICATE, SYMETRIC, ORIGINAL);
        listener.insertURLMapping(map1URL2, map1URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL2, map2URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL2, map3URL3, mappingSet, SYMETRIC);

        mappingSet = listener.registerMappingSet(URISpace1, URISpace3, TEST_PREDICATE, SYMETRIC, TRANSATIVE);
        listener.insertURLMapping(map1URL1, map1URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map2URL1, map2URL3, mappingSet, SYMETRIC);
        listener.insertURLMapping(map3URL1, map3URL3, mappingSet, SYMETRIC);

        listener.closeInput();
    }


}
