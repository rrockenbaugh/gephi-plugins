/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.ui.importer.plugin;

import org.gephi.io.importer.spi.Importer;
import org.gephi.io.importer.spi.ImporterWizardUI;
import org.gephi.io.importer.spi.WizardImporter;
import org.openide.WizardDescriptor;
import org.openide.WizardDescriptor.Panel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author ec2-user
 */
@ServiceProvider(service = ImporterWizardUI.class)
public class SolrImporterWizardUI implements ImporterWizardUI {

    
    private Panel[] panels = null;
    
    @Override
    public String getDisplayName() {
        return "Solr Importer";
    }

    @Override
    public String getCategory() {
        return "Solr";
    }

    @Override
    public String getDescription() {
        return "Import Solr Data with Solr Query URL";
    }

    @Override
    public WizardDescriptor.Panel[] getPanels() {
       if (panels == null) {
           panels = new Panel[1];
           panels[0] = new SolrImportWizardPanel();
       } 
       return panels;
    }

    @Override
    public void setup(WizardDescriptor.Panel panel) {
        
    }

    @Override
    public void unsetup(WizardImporter importer, WizardDescriptor.Panel panel) {
        SolrWizardImporter solrImporter = (SolrWizardImporter) importer;
        SolrImportWizardPanel wizardPanel = (SolrImportWizardPanel) panel;
        SolrImportPanel solrPanel = (SolrImportPanel) wizardPanel.getComponent();
        
        String nodeUrl = solrPanel.getNodeUrl();
        String nodeQuery = solrPanel.getNodeQuery();
        String edgeUrl = solrPanel.getEdgeUrl();
        String edgeQuery = solrPanel.getEdgeQuery();        
        
        solrImporter.getSolrInfo().setNodeUrl(nodeUrl);
        solrImporter.getSolrInfo().setNodeQuery(nodeQuery);
        solrImporter.getSolrInfo().setEdgeUrl(edgeUrl);
        solrImporter.getSolrInfo().setEdgeQuery(edgeQuery);
        
        //((SolrImportWizardPanel)((Panel) panels[0]).getComponent()).unsetup((SolrWizardImporter) importer);
    }

    @Override
    public boolean isUIForImporter(Importer importer) {
        return importer instanceof SolrWizardImporter;
    }
    
}
