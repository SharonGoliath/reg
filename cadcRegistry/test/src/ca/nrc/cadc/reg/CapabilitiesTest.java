/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2011.                            (c) 2011.
*  Government of Canada                 Gouvernement du Canada
*  National Research Council            Conseil national de recherches
*  Ottawa, Canada, K1A 0R6              Ottawa, Canada, K1A 0R6
*  All rights reserved                  Tous droits réservés
*
*  NRC disclaims any warranties,        Le CNRC dénie toute garantie
*  expressed, implied, or               énoncée, implicite ou légale,
*  statutory, of any kind with          de quelque nature que ce
*  respect to the software,             soit, concernant le logiciel,
*  including without limitation         y compris sans restriction
*  any warranty of merchantability      toute garantie de valeur
*  or fitness for a particular          marchande ou de pertinence
*  purpose. NRC shall not be            pour un usage particulier.
*  liable in any event for any          Le CNRC ne pourra en aucun cas
*  damages, whether direct or           être tenu responsable de tout
*  indirect, special or general,        dommage, direct ou indirect,
*  consequential or incidental,         particulier ou général,
*  arising from the use of the          accessoire ou fortuit, résultant
*  software.  Neither the name          de l'utilisation du logiciel. Ni
*  of the National Research             le nom du Conseil National de
*  Council of Canada nor the            Recherches du Canada ni les noms
*  names of its contributors may        de ses  participants ne peuvent
*  be used to endorse or promote        être utilisés pour approuver ou
*  products derived from this           promouvoir les produits dérivés
*  software without specific prior      de ce logiciel sans autorisation
*  written permission.                  préalable et particulière
*                                       par écrit.
*
*  This file is part of the             Ce fichier fait partie du projet
*  OpenCADC project.                    OpenCADC.
*
*  OpenCADC is free software:           OpenCADC est un logiciel libre ;
*  you can redistribute it and/or       vous pouvez le redistribuer ou le
*  modify it under the terms of         modifier suivant les termes de
*  the GNU Affero General Public        la “GNU Affero General Public
*  License as published by the          License” telle que publiée
*  Free Software Foundation,            par la Free Software Foundation
*  either version 3 of the              : soit la version 3 de cette
*  License, or (at your option)         licence, soit (à votre gré)
*  any later version.                   toute version ultérieure.
*
*  OpenCADC is distributed in the       OpenCADC est distribué
*  hope that it will be useful,         dans l’espoir qu’il vous
*  but WITHOUT ANY WARRANTY;            sera utile, mais SANS AUCUNE
*  without even the implied             GARANTIE : sans même la garantie
*  warranty of MERCHANTABILITY          implicite de COMMERCIALISABILITÉ
*  or FITNESS FOR A PARTICULAR          ni d’ADÉQUATION À UN OBJECTIF
*  PURPOSE.  See the GNU Affero         PARTICULIER. Consultez la Licence
*  General Public License for           Générale Publique GNU Affero
*  more details.                        pour plus de détails.
*
*  You should have received             Vous devriez avoir reçu une
*  a copy of the GNU Affero             copie de la Licence Générale
*  General Public License along         Publique GNU Affero avec
*  with OpenCADC.  If not, see          OpenCADC ; si ce n’est
*  <http://www.gnu.org/licenses/>.      pas le cas, consultez :
*                                       <http://www.gnu.org/licenses/>.
*
*  $Revision: 5 $
*
************************************************************************
*/

package ca.nrc.cadc.reg;


import ca.nrc.cadc.reg.Capabilities;
import ca.nrc.cadc.reg.Capability;
import ca.nrc.cadc.util.Log4jInit;
import java.net.URI;
import java.util.List;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author yeunga
 */
public class CapabilitiesTest 
{
    private static final Logger log = Logger.getLogger(CapabilitiesTest.class);

    private URI STANDARD_ID = Standards.TAP_SYNC_11_URI;
    private URI STANDARD_ID_2 = Standards.TAP_ASYNC_11_URI;
    private URI STANDARD_ID_3= Standards.TAP_TABLES_11_URI;
    private static final URI TAP_RESOURCE_IDENTIFIER_URI = URI.create("ivo://cadc.nrc.ca/tap");
    
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.vosi", Level.INFO);
    }
    
    public CapabilitiesTest() { }
    
    @Test
    public void testNullResourceID()
    {
        try
        {
            new Capability(null);
            Assert.fail("expected IllegalArgumentException");
        }
        catch(IllegalArgumentException ex)
        {
        	// expected
        }
        catch(Throwable t)
        {
            Assert.fail("unexpected t: " + t);
        }
    }
    
    @Test
    public void testConstruction()
    {
    	try
    	{
    		Capabilities caps = new Capabilities(TAP_RESOURCE_IDENTIFIER_URI);
    		URI resourceID = caps.getResourceIdentifier();
    		Assert.assertNotNull("accessURL should not be null", resourceID);
    		Assert.assertEquals("resource identifier is corrupted", TAP_RESOURCE_IDENTIFIER_URI, resourceID);
    		Assert.assertNotNull("capabilities should not be null", caps.getCapabilities());
    		Assert.assertEquals("capabilities should be empty", 0, caps.getCapabilities().size());
    	}
    	catch (Throwable t)
    	{
            log.error("unexpected exception", t);
            Assert.fail("unexpected exception: " + t);
    	}
    }
    
    @Test
    public void testCapabilities()
    {
    	try
    	{
    		// construct a Capabilities object
    		Capabilities capabilities = new Capabilities(TAP_RESOURCE_IDENTIFIER_URI);
    		List<Capability> caps = capabilities.getCapabilities();
    		
    		// test correct capability is added
    		caps.add(new Capability(STANDARD_ID));
    		Assert.assertEquals("capability list should have one entry", 1, caps.size());
    		Capability[] capArray = caps.toArray(new Capability[caps.size()]);
    		Assert.assertEquals("capability list contains a different capability", STANDARD_ID, capArray[0].getStandardID());
    		
    		// test correct number of capability are added 
    		caps.add(new Capability(STANDARD_ID_2));
    		Assert.assertEquals("capability list should have one entry", 2, caps.size());
    		caps.add(new Capability(STANDARD_ID_3));
    		Assert.assertEquals("capability list should have one entry", 3, caps.size());
    	}
    	catch (Throwable t)
    	{
            log.error("unexpected exception", t);
    		Assert.fail("unexpected exception: " + t);
    	}
    }
}
