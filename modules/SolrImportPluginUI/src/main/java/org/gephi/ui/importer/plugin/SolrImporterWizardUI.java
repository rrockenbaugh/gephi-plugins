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
    
    public String getDisplayName() {
        return "Solr Importer";
    }

    public String getCategory() {
        return "Solr";
    }

    public String getDescription() {
        return "Import Solr Data with Solr Query URL";
    }

    public WizardDescriptor.Panel[] getPanels() {
       if (panels == null) {
           panels = new Panel[1];
           panels[0] = new SolrImportWizardPanel1();
       } 
       return panels;
    }

    public void setup(WizardDescriptor.Panel pnl) {
        
    }

    public void unsetup(WizardImporter importer, Panel Panel) {
        ((SolrImportWizardPanel1)((Panel) panels[0]).getComponent()).unsetup((SolrWizardImporter) importer);
    }

    public boolean isUIForImporter(Importer imprtr) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
