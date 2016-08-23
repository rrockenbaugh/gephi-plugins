/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.ui.importer.plugin;

import org.gephi.io.importer.spi.WizardImporter;
import org.gephi.io.importer.spi.WizardImporterBuilder;
import org.openide.util.lookup.ServiceProvider;
/**
 *
 * @author ec2-user
 */
@ServiceProvider(service = WizardImporterBuilder.class)
public class SolrWizardImporterBuilder implements WizardImporterBuilder {

    public WizardImporter buildImporter() {
        return new SolrWizardImporter();
    }

    public String getName() {
        return "Solr Wizard";
    }
    
}
