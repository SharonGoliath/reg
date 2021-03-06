/*
************************************************************************
*******************  CANADIAN ASTRONOMY DATA CENTRE  *******************
**************  CENTRE CANADIEN DE DONNÉES ASTRONOMIQUES  **************
*
*  (c) 2019.                            (c) 2019.
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

package ca.nrc.cadc.vosi;

import ca.nrc.cadc.auth.AuthenticationUtil;
import ca.nrc.cadc.log.ServletLogInfo;
import ca.nrc.cadc.log.WebServiceLogInfo;
import ca.nrc.cadc.reg.CapabilitiesReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.Subject;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.filter.Filters;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;

/**
 * Servlet implementation class CapabilityServlet.
 * 
 * @deprecated use CapInitAction and CapGetAction with cadc-rest library
 */
@Deprecated
public class CapabilitiesServlet extends HttpServlet {

    private static Logger log = Logger.getLogger(CapabilitiesServlet.class);
    private static final long serialVersionUID = 201003131300L;

    private String capTemplate;

    /**
     * Enable transformation of the capabilities template (default: true). Subclasses
     * may disable this according to some policy. The current transform is to change
     * the hostname in every accessURL in the capabilities to match the hostname used
     * in the request to th servlet. This works fine in most cases but would not work
     * if some accessURL(s) within an application are deployed on a different host.
     * For example, if the VOSI-availability endpoint is deployed on an separate host
     * so it can probe the service from the outside, then capabilities transform
     * would need to be disabled.
     */
    protected boolean doTransform = true;

    @Override
    public void init(ServletConfig config)
            throws ServletException {
        super.init(config);
        String str = config.getInitParameter("input");

        if (str == null) {
            throw new ExceptionInInitializerError("Missing capabilities input");
        }

        log.info("static capabilities: " + str);
        try {
            URL resURL = config.getServletContext().getResource(str);
            CapabilitiesReader cr = new CapabilitiesReader(true);
            InputStream in = resURL.openStream();

            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = in.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String xml = result.toString("UTF-8");

            // validate
            cr.read(xml);
            this.capTemplate = xml;
        } catch (Throwable t) {
            log.error("CONFIGURATION ERROR: failed to read capabilities template: " + str, t);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        WebServiceLogInfo logInfo = new ServletLogInfo(request);
        long start = System.currentTimeMillis();

        try {
            Subject subject = AuthenticationUtil.getSubject(request);
            logInfo.setSubject(subject);
            log.info(logInfo.start());

            if (doTransform) {
                StringReader sr = new StringReader(capTemplate);
                CapabilitiesParser cp = new CapabilitiesParser(false);
                Document doc = cp.parse(sr);

                transformCapabilities(doc, request);
                doOutput(doc, response);
            } else {
                doOutput(capTemplate, response);
            }
        } catch (JDOMException ex) {
            logInfo.setSuccess(false);
            logInfo.setMessage(ex.toString());
            log.error("BUG: failed to rewrite hostname in accessURL elements", ex);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, ex.getMessage());
        } finally {
            logInfo.setElapsedTime(System.currentTimeMillis() - start);
            log.info(logInfo.end());
        }
    }

    private void transformCapabilities(Document doc, HttpServletRequest request)
            throws IOException, JDOMException {
        URL rurl = new URL(request.getRequestURL().toString());
        String hostname = rurl.getHost();

        Element root = doc.getRootElement();
        List<Namespace> nsList = new ArrayList<Namespace>();
        nsList.addAll(root.getAdditionalNamespaces());
        nsList.add(root.getNamespace());

        String xpath = "/vosi:capabilities/capability/interface/accessURL";
        XPathFactory xf = XPathFactory.instance();
        XPathExpression<Element> xp = xf.compile(xpath, Filters.element(),
                null, nsList);
        List<Element> accessURLs = xp.evaluate(doc);
        log.debug("xpath[" + xpath + "] found: " + accessURLs.size());
        for (Element e : accessURLs) {
            String surl = e.getTextTrim();
            log.debug("accessURL: " + surl);
            URL url = new URL(surl);
            URL nurl = new URL(url.getProtocol(), hostname, url.getPath());
            log.debug("accessURL: " + surl + " -> " + nurl);
            e.setText(nurl.toExternalForm());
        }

    }

    private void doOutput(String xml, HttpServletResponse response) throws IOException {
        response.setContentType("text/xml");
        PrintWriter w = response.getWriter();
        w.write(xml);
        w.flush();
    }

    private void doOutput(Document doc, HttpServletResponse response) throws IOException {
        XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
        response.setContentType("text/xml");
        out.output(doc, response.getOutputStream());
    }
}
