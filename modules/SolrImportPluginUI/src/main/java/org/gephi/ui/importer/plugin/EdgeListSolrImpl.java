/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.ui.importer.plugin;

import java.nio.charset.Charset;
import org.apache.http.client.utils.URLEncodedUtils;
import org.gephi.graph.api.TimeFormat;
import org.gephi.io.importer.api.PropertiesAssociations;
import org.gephi.io.importer.api.PropertiesAssociations.EdgeProperties;
import org.gephi.io.importer.api.PropertiesAssociations.NodeProperties;

/**
 *
 * @author ec2-user
 */
public class EdgeListSolrImpl {
    //protected String solrCollectionUrl = null;
    
    protected PropertiesAssociations properties = new PropertiesAssociations();
    
    protected String edgeUrl = null;
    protected String edgeAttributesQuery = null;
    protected String edgeQuery = null;
    protected String nodeUrl = null;
    protected String nodeAttributesQuery = null;
    protected String nodeQuery = null;
    
    protected TimeFormat timeFormat = TimeFormat.DATE;
    
    
    public EdgeListSolrImpl() {

        //Default node associations
        properties.addNodePropertyAssociation(NodeProperties.ID, "id");
        properties.addNodePropertyAssociation(NodeProperties.LABEL, "label");
        properties.addNodePropertyAssociation(NodeProperties.X, "x");
        properties.addNodePropertyAssociation(NodeProperties.Y, "y");
        properties.addNodePropertyAssociation(NodeProperties.SIZE, "size");
        properties.addNodePropertyAssociation(NodeProperties.COLOR, "color");
        properties.addNodePropertyAssociation(NodeProperties.START, "start");
        properties.addNodePropertyAssociation(NodeProperties.END, "end");
        properties.addNodePropertyAssociation(NodeProperties.START, "start_open");
        properties.addNodePropertyAssociation(NodeProperties.END_OPEN, "end_open");

        //Default edge associations
        properties.addEdgePropertyAssociation(EdgeProperties.ID, "id");
        properties.addEdgePropertyAssociation(EdgeProperties.SOURCE, "source");
        properties.addEdgePropertyAssociation(EdgeProperties.TARGET, "target");
        properties.addEdgePropertyAssociation(EdgeProperties.LABEL, "label");
        properties.addEdgePropertyAssociation(EdgeProperties.WEIGHT, "weight");
        properties.addNodePropertyAssociation(NodeProperties.COLOR, "color");
        properties.addEdgePropertyAssociation(EdgeProperties.START, "start");
        properties.addEdgePropertyAssociation(EdgeProperties.END, "end");
        properties.addEdgePropertyAssociation(EdgeProperties.START, "start_open");
        properties.addEdgePropertyAssociation(EdgeProperties.END_OPEN, "end_open");
    }

    public String getEdgeUrl() {
        return edgeUrl;
    }

    public void setEdgeUrl(String edgeUrl) {
        this.edgeUrl = edgeUrl;
    }

    public String getNodeUrl() {
        return nodeUrl;
    }

    public void setNodeUrl(String nodeUrl) {
        this.nodeUrl = nodeUrl;
    }
    
    public PropertiesAssociations getPropertiesAssociations() {
        return properties;
    }

    public String getEdgeAttributesQuery() {
        return edgeAttributesQuery;
    }

    public void setEdgeAttributesQuery(String edgeAttributesQuery) {
        this.edgeAttributesQuery = edgeAttributesQuery;
    }

    public String getEdgeQuery() {
        return edgeQuery;
    }

    public void setEdgeQuery(String edgeQuery) {
        this.edgeQuery = edgeQuery;
    }

    public String getNodeAttributesQuery() {
        return nodeAttributesQuery;
    }

    public void setNodeAttributesQuery(String nodeAttributesQuery) {
        this.nodeAttributesQuery = nodeAttributesQuery;
    }

    public String getNodeQuery() {
        return nodeQuery;
    }

    public void setNodeQuery(String nodeQuery) {
        this.nodeQuery = nodeQuery;
    }  

    public TimeFormat getTimeFormat() {
        return this.timeFormat;
    }
    
    public void setTimeFormat(TimeFormat timeFormat) {
        this.timeFormat = timeFormat;
    }
}
