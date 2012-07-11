package org.bridgedb.linkset;

import java.util.Set;
import java.util.Date;
import org.bridgedb.IDMapperException;
import org.bridgedb.IDMapperTest;
import org.bridgedb.IDMapperTestBase;
import org.bridgedb.url.URLMapperTestBase;
import static org.junit.Assert.*;

/**
 *
 * @author Christian
 */
public abstract class URLLinkLoaderTest extends URLMapperTestBase {
   
    //The Listener may be but needs not be the same as the IDMapper
    protected static URLLinkListener listener;
        
    /**
     * Suggested method to load the data.
     * 
     * Must be called or replaced.
     * @throws IDMapperException 
     */
    protected static void defaultLoadData() throws IDMapperException{
        listener.openInput();

        listener.registerLinkSet(link1to2, DataSource1, TEST_PREDICATE, DataSource2, false);
        listener.registerLinkSet(link2to1, DataSource2, TEST_PREDICATE, DataSource1, false);
        listener.insertLink(map1URL1, map1URL2, link1to2, link2to1);
        listener.insertLink(map2URL1, map2URL2, link1to2, link2to1);
        listener.insertLink(map3URL1, map3URL2, link1to2, link2to1);

        listener.registerLinkSet(link1to3, DataSource1, TEST_PREDICATE, DataSource3, true);
        listener.registerLinkSet(link3to1, DataSource3, TEST_PREDICATE, DataSource1, true);
        listener.insertLink(map1URL1, map1URL3, link1to3, link3to1);
        listener.insertLink(map2URL1, map2URL3, link1to3, link3to1);
        listener.insertLink(map3URL1, map3URL3, link1to3, link3to1);
        
        listener.registerLinkSet(link2to3, DataSource2, TEST_PREDICATE, DataSource3, false);
        listener.registerLinkSet(link3to2, DataSource3, TEST_PREDICATE, DataSource2, false);
        listener.insertLink(map1URL2, map1URL3, link2to3, link3to2);
        listener.insertLink(map2URL2, map2URL3, link2to3, link3to2);
        listener.insertLink(map3URL2, map3URL3, link2to3, link3to2);

        listener.closeInput();
 
        Set<String> results = listener.getLinkSetIds();
        assertTrue(results.contains(link1to2));
        assertTrue(results.contains(link1to3));
        assertTrue(results.contains(link2to1));
        assertTrue(results.contains(link2to3));
        assertTrue(results.contains(link3to1));
        assertTrue(results.contains(link3to2));
    }
    
}