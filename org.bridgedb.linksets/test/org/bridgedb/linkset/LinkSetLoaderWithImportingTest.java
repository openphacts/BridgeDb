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
package org.bridgedb.linkset;

import org.bridgedb.rdf.RdfReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.bridgedb.IDMapperException;
import org.bridgedb.metadata.MetaDataException;
import org.bridgedb.metadata.validator.ValidationType;
import org.bridgedb.mysql.MySQLSpecific;
import org.bridgedb.sql.BridgeDbSqlException;
import org.bridgedb.sql.SQLAccess;
import org.bridgedb.sql.SQLUrlMapper;
import org.bridgedb.sql.TestSqlFactory;
import org.bridgedb.statistics.MappingSetInfo;
import org.bridgedb.utils.Reporter;
import org.bridgedb.utils.StoreType;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openrdf.OpenRDFException;
import static org.junit.Assert.*;
import static org.hamcrest.Matchers.*;

/**
 * @author Christian
 */
public class LinkSetLoaderWithImportingTest {
       
    private static final boolean LOAD_DATA = true;
    
    //Unsure if this is still needed or even desirable!
    @BeforeClass
    public static void testLoader() throws IDMapperException, IOException, OpenRDFException, BridgeDbSqlException, IDMapperLinksetException, FileNotFoundException, MetaDataException  {
        //Check database is running and settup correctly or kill the test. 
        TestSqlFactory.createTestSQLAccess();
        
        LinksetLoader.clearExistingData(StoreType.TEST);
        LinksetLoader.parse("../org.bridgedb.metadata/test-data/chemspider-void.ttl", StoreType.TEST, 
                ValidationType.DATASETVOID, LOAD_DATA);
        LinksetLoader.parse("../org.bridgedb.metadata/test-data/chembl-rdf-void.ttl", StoreType.TEST, 
                ValidationType.DATASETVOID, LOAD_DATA);
        LinksetLoader.parse("test-data/chemspider2chemblrdf-linkset.ttl", StoreType.TEST, 
                ValidationType.LINKS, LOAD_DATA);
	}

    @Test
    public void testMappingInfo() throws IDMapperException {
        Reporter.report("MappingInfo");
        SQLAccess sqlAccess = TestSqlFactory.createTestSQLAccess();
        SQLUrlMapper sqlUrlMapper = new SQLUrlMapper(false, sqlAccess, new MySQLSpecific());
        
        MappingSetInfo info = sqlUrlMapper.getMappingSetInfo(1);
        assertEquals ("http://linkedchemistry.info/chembl/molecule/", info.getSourceSysCode());
        assertEquals ("ChemSpider", info.getTargetSysCode());
        assertEquals ("http://www.w3.org/2004/02/skos/core#exactMatch", info.getPredicate());
        //ystem.out.println(info);
    }
    

}