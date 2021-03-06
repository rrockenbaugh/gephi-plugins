/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.ui.importer.plugin;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.gephi.io.importer.api.ContainerLoader;
import org.gephi.io.importer.api.EdgeDraft;
import org.gephi.io.importer.api.ElementDraft;
import org.gephi.io.importer.api.Issue;
import org.gephi.io.importer.api.NodeDraft;
import org.gephi.io.importer.api.PropertiesAssociations;
import org.gephi.io.importer.api.PropertiesAssociations.EdgeProperties;
import org.gephi.io.importer.api.PropertiesAssociations.NodeProperties;
import org.gephi.io.importer.api.Report;
import org.gephi.io.importer.spi.WizardImporter;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;

/**
 *
 * @author ec2-user
 */
public class SolrWizardImporter implements WizardImporter, LongTask {
       
    private Report report;
    private ProgressTicket progressTicket;
    private EdgeListSolrImpl solrInfo = new EdgeListSolrImpl();
    private ContainerLoader container;
    private SolrClient client;
    private boolean cancel = false;
       
    //TempData
    private String timeIntervalStart;
    private String timeIntervalEnd;

    @Override
    public boolean execute(ContainerLoader container) {
        this.container = container;
        this.report = new Report();
        try {
            importData();
        } catch (Exception e) {
            close();
            throw new RuntimeException(e);
        }
        close();
        return !cancel;
    }

    private void close() {
        //Close connection
        if (client != null) {
            try {
                client.close();
                report.log("Solr connection terminated");
            } catch (Exception e) { /* ignore close errors */ }
        }
    }

    private SolrClient getSolrClient(String url) {
        SolrClient solrClient = null;
        try {
            report.log("Try to connect at " + url);
            solrClient = new HttpSolrClient.Builder(url).build();
            solrClient.ping();
            report.log("Solr connection established");
            
        } catch (SolrServerException | IOException ex) {
            if (solrClient != null) {
                try {
                    solrClient.close();
                    report.log("Solr connection terminated");
                } catch (Exception e) { /* ignore close errors */ }
            }
            report.logIssue(new Issue("Failed to connect at " + url, Issue.Level.CRITICAL, ex));
        }
        if (solrClient == null) {
            report.logIssue(new Issue("Failed to connect at " + url, Issue.Level.CRITICAL));
        }
        return solrClient;
    }
    
    private void importData() {

        report.log(solrInfo.getPropertiesAssociations().getInfos());
        getNodes();
        getEdges();
    }
    
    private void getNodes() {

        client = getSolrClient(this.getSolrInfo().getNodeUrl());
        //Factory
        ElementDraft.Factory factory = container.factory();

        //Properties
        PropertiesAssociations properties = solrInfo.getPropertiesAssociations();

        SolrQuery solrQuery = null;
        QueryResponse response = null;
        try {
            solrQuery = getSolrQuery(solrInfo.getNodeQuery());
            response = client.query(solrQuery);
        } catch (SolrServerException | IOException ex) {
            report.logIssue(new Issue("Failed to execute Node query", Issue.Level.SEVERE, ex));
            return;
        }

        SolrDocumentList list = response.getResults();
        
        for (SolrDocument doc : list) {
            String id = (String) doc.getFirstValue("id");
            NodeDraft node = factory.newNodeDraft(id);
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                NodeProperties p = properties.getNodeProperty(entry.getKey());
                if (p != null) {
                    injectNodeProperty(p, entry.getValue(), node);
                } else {
                    node.setValue(entry.getKey(), entry.getValue());
                }
            }
            injectTimeIntervalProperty(node);
            container.addNode(node);
        }
    }

    private void getEdges() {

        client = getSolrClient(solrInfo.getEdgeUrl());
        
        //Factory
        ElementDraft.Factory factory = container.factory();

        //Properties
        PropertiesAssociations properties = solrInfo.getPropertiesAssociations();

        SolrQuery solrQuery = null;
        QueryResponse response = null;
        try {
            solrQuery = getSolrQuery(solrInfo.getEdgeQuery());
            response = client.query(solrQuery);
        } catch (SolrServerException | IOException ex) {
            report.logIssue(new Issue("Failed to execute Node query", Issue.Level.SEVERE, ex));
            return;
        }

        SolrDocumentList list = response.getResults();
        
        for (SolrDocument doc : list) {
            String id = (String) doc.getFirstValue("id");
            EdgeDraft edge = factory.newEdgeDraft(id);
            for (Map.Entry<String, Object> entry : doc.entrySet()) {
                EdgeProperties p = properties.getEdgeProperty(entry.getKey());
                if (p != null) {
                    injectEdgeProperty(p, entry.getValue(), edge);
                } else {
                    edge.setValue(entry.getKey(), entry.getValue());
                }
            }
            injectTimeIntervalProperty(edge);
            container.addEdge(edge);
        }
    }

    private void injectNodeProperty(NodeProperties p, Object value, NodeDraft nodeDraft) {
        switch (p) {
            case LABEL:
                String label = (String) value;
                if (label != null) {
                    nodeDraft.setLabel(label);
                }
                break;
            case X:
                float x = (Float) value;
                if (x != 0) {
                    nodeDraft.setX(x);
                }
                break;
            case Y:
                float y = (Float) value;
                if (y != 0) {
                    nodeDraft.setY(y);
                }
                break;
            case Z:
                float z = (Float) value;
                if (z != 0) {
                    nodeDraft.setZ(z);
                }
                break;
            case COLOR:
                String color = (String) value;
                if (color != null) {
                    String[] rgb = color.replace(" ", "").split(",");
                    if (rgb.length == 3) {
                        nodeDraft.setColor(rgb[0], rgb[1], rgb[2]);
                    } else {
                        nodeDraft.setColor(color);
                    }
                }
                break;
            case SIZE:
                float size = (Float) value;
                if (size != 0) {
                    nodeDraft.setSize(size);
                }
                break;
            case START:
                String start = (String) value;
                if (start != null) {
                    timeIntervalStart = start;
                }
                break;
            case START_OPEN:
                String startOpen = (String) value;
                if (startOpen != null) {
                    timeIntervalStart = startOpen;
                }
                break;
            case END:
                String end = (String) value;
                if (end != null) {
                    timeIntervalEnd = end;
                }
                break;
            case END_OPEN:
                String endOpen = (String) value;
                if (endOpen != null) {
                    timeIntervalEnd = endOpen;
                }
                break;
        }
    }


    private void injectTimeIntervalProperty(ElementDraft elementDraft) {
        if (timeIntervalStart != null || timeIntervalEnd != null) {
            container.setTimeFormat(solrInfo.getTimeFormat());
            elementDraft.addInterval(timeIntervalStart, timeIntervalEnd);
        }

        //Reset temp data
        timeIntervalStart = null;
        timeIntervalEnd = null;
    }

    private void injectEdgeProperty(EdgeProperties p, Object value, EdgeDraft edgeDraft) {
        report.log("injectEdgeProperty:value=" + value);
        switch (p) {
            case LABEL:
                String label = (String) value;
                if (label != null) {
                    edgeDraft.setLabel(label);
                }
                break;
            case SOURCE:
                String source = value.toString();
                if (source != null && !source.isEmpty()) {
                    NodeDraft sourceNode = container.getNode(source);
                    edgeDraft.setSource(sourceNode);
                }
                break;
            case TARGET:
                String target = value.toString();
                if (target != null && !target.isEmpty()) {
                    NodeDraft targetNode = container.getNode(target);
                    edgeDraft.setTarget(targetNode);
                }
                break;
            case WEIGHT:
                Double weight = new Double(value.toString());
                if (weight != 0) {
                    edgeDraft.setWeight(weight);
                }
                break;
            case COLOR:
                String color = value.toString();
                if (color != null) {
                    String[] rgb = color.split(",");
                    if (rgb.length == 3) {
                        edgeDraft.setColor(rgb[0], rgb[1], rgb[2]);
                    } else {
                        edgeDraft.setColor(color);
                    }
                }
                break;
            case START:
                String start = value.toString();
                if (start != null) {
                    timeIntervalStart = start;
                }
                break;
            case START_OPEN:
                String startOpen = value.toString();
                if (startOpen != null) {
                    timeIntervalStart = startOpen;
                }
                break;
            case END:
                String end = value.toString();
                if (end != null) {
                    timeIntervalEnd = end;
                }
                break;
            case END_OPEN:
                String endOpen = value.toString();
                if (endOpen != null) {
                    timeIntervalEnd = endOpen;
                }
                break;

        }
    }

    @Override
    public ContainerLoader getContainer() {
        return container;
    }

    @Override
    public Report getReport() {
        return report;
    }

    @Override
    public boolean cancel() {
        this.cancel = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket pt) {
        this.progressTicket = pt;
    }

    public EdgeListSolrImpl getSolrInfo() {
        return solrInfo;
    }

    public void setSolrInfo(EdgeListSolrImpl solrInfo) {
        this.solrInfo = solrInfo;
    }
    
    private SolrQuery getSolrQuery(String queryString) {
        SolrQuery solrQuery = new SolrQuery();
        if (queryString != null && !queryString.isEmpty()) {
            List<NameValuePair> list = URLEncodedUtils.parse(queryString, Charset.defaultCharset());
            list.stream().forEach((pair) -> {
                solrQuery.add(pair.getName(), pair.getValue());
            });
        }
        return solrQuery;
    }
    
    
}
