/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2010.                            (c) 2010.
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

package ca.nrc.cadc.reg.client;

import ca.nrc.cadc.util.Log4jInit;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author pdowler
 */
public class RegistryClientTest 
{
private static Logger log = Logger.getLogger(RegistryClientTest.class);
    static
    {
        Log4jInit.setLevel("ca.nrc.cadc.reg", Level.INFO);
    }


    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception
    {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception
    {
    }

    static String GMS_URI = "ivo://cadc.nrc.ca/gms";
    static String GMS_HTTP = "http://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/gms";
    static String GMS_HTTPS = "https://www.cadc-ccda.hia-iha.nrc-cnrc.gc.ca/gms";
    
    static String DUMMY_URI = "ivo://example.com/srv";
    static String DUMMY_URL = "http://example.com/current/path/to/my/service";

    @Test
    public void testNotFound() throws Exception
    {
        try
        {
            RegistryClient rc = new RegistryClient();

            URL url = rc.getServiceURL(new URI("ivo://foo/bar"));
            Assert.assertNull(url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            Assert.fail("unexpected exception: " + t);
        }
    }

    @Test
    public void testFound() throws Exception
    {
        try
        {
            RegistryClient rc = new RegistryClient();

            URL expected = new URL(DUMMY_URL);
            URL url = rc.getServiceURL(new URI(DUMMY_URI));
            Assert.assertEquals(expected, url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            Assert.fail("unexpected exception: " + t);
        }
    }
    @Test
    public void testFoundAltURL() throws Exception
    {
        try
        {
            // TODO: verify that this works with an arbitrary URL (eg http)
            RegistryClient rc = new RegistryClient(
                    RegistryClient.class.getClassLoader().getResource(
                        RegistryClient.class.getSimpleName() + ".properties") );

            URL expected = new URL(DUMMY_URL);
            URL url = rc.getServiceURL(new URI(DUMMY_URI));
            Assert.assertEquals(expected, url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            Assert.fail("unexpected exception: " + t);
        }
    }


    
    @Test
    public void testFoundWithProtocol() throws Exception
    {
        try
        {
            RegistryClient rc = new RegistryClient();

            URL expected = new URL(GMS_HTTP);
            URL url = rc.getServiceURL(new URI(GMS_URI), "http");
            Assert.assertEquals(expected, url);

            expected = new URL(GMS_HTTPS);
            url = rc.getServiceURL(new URI(GMS_URI), "https");
            Assert.assertEquals(expected, url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            Assert.fail("unexpected exception: " + t);
        }
    }

    @Test
    public void testFoundLocal() throws Exception
    {
        try
        {
            System.setProperty(RegistryClient.class.getName() + ".local", "true");
            RegistryClient rc = new RegistryClient();

            String localhost = InetAddress.getLocalHost().getCanonicalHostName();
            URL expected = new URL("http://" + localhost + "/current/path/to/my/service");

            URL url = rc.getServiceURL(new URI(DUMMY_URI));
            Assert.assertEquals(expected, url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            Assert.fail("unexpected exception: " + t);
        }
        finally
        {
            System.setProperty(RegistryClient.class.getName() + ".local", "false");
        }
    }

    @Test
    public void testFoundHost() throws Exception
    {
        try
        {
            System.setProperty(RegistryClient.class.getName() + ".host", "cadcweb0");
            RegistryClient rc = new RegistryClient();

            String localhost = InetAddress.getLocalHost().getCanonicalHostName();
            URL expected = new URL("http://cadcweb0/current/path/to/my/service");

            URL url = rc.getServiceURL(new URI(DUMMY_URI));
            Assert.assertEquals(expected, url);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            Assert.fail("unexpected exception: " + t);
        }
        finally
        {
            System.setProperty(RegistryClient.class.getName() + ".host", "");
        }
    }
}
