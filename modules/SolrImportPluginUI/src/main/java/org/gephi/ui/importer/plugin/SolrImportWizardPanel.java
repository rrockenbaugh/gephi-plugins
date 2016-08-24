/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.ui.importer.plugin;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.event.ChangeListener;
import org.openide.WizardDescriptor;
import org.openide.util.HelpCtx;

/**
 *
 * @author ec2-user
 */
public class SolrImportWizardPanel implements org.openide.WizardDescriptor.Panel {

    private SolrImportPanel panel = new SolrImportPanel();
    private List<ChangeListener> listeners;
    
    @Override
    public Component getComponent() {
        return panel;
    }

    @Override
    public HelpCtx getHelp() {
        return HelpCtx.DEFAULT_HELP;
    }

    @Override
    public void readSettings(Object settings) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void storeSettings(Object settings) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public void addChangeListener(ChangeListener cl) {
        if (listeners == null) {
            listeners = new ArrayList<>();
        }
        
        listeners.add(cl);
    }

    @Override
    public void removeChangeListener(ChangeListener cl) {
        listeners.remove(cl);
    }

}
