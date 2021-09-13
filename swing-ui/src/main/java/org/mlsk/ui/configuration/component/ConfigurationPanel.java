package org.mlsk.ui.configuration.component;

import com.google.common.annotations.VisibleForTesting;
import org.mlsk.ui.configuration.service.ConfigurationCommand;
import org.mlsk.ui.configuration.service.ConfigurationService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static org.mlsk.ui.component.builder.GridBagConstraintsBuilder.buildGridBagConstraints;
import static org.mlsk.ui.component.builder.JButtonBuilder.buildJButton;
import static org.mlsk.ui.component.popup.ErrorPopup.tryPopupVoid;
import static org.mlsk.ui.configuration.service.ConfigurationCommand.*;

public class ConfigurationPanel extends JPanel implements ActionListener {

  private final ServiceConfigurationPanel serviceConfigurationPanel;
  private final ConfigurationService configurationService;

  public ConfigurationPanel() {
    this(new ServiceConfigurationPanel(),
        new ConfigurationService());

    this.setLayout(new GridBagLayout());

    this.add(serviceConfigurationPanel, buildGridBagConstraints(0, 0));
    this.add(buildButtonsPanel(), buildGridBagConstraints(0, 1));

    configurationService.restore(serviceConfigurationPanel);
  }

  @VisibleForTesting
  ConfigurationPanel(ServiceConfigurationPanel serviceConfigurationPanel, ConfigurationService configurationService) {
    this.serviceConfigurationPanel = serviceConfigurationPanel;
    this.configurationService = configurationService;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    ConfigurationCommand configurationCommand = fromString(e.getActionCommand());
    if (SAVE == configurationCommand) {
      tryPopupVoid(() -> configurationService.save(serviceConfigurationPanel), "Saving Configuration");
    } else if (CANCEL == configurationCommand) {
      configurationService.restore(serviceConfigurationPanel);
    }
  }

  private JPanel buildButtonsPanel() {
    JPanel buttonsPanel = new JPanel();

    buttonsPanel.setLayout(new GridBagLayout());

    buttonsPanel.add(buildJButton(SAVE.getTitle(), this), buildGridBagConstraints(0, 0));
    buttonsPanel.add(buildJButton(CANCEL.getTitle(), this), buildGridBagConstraints(1, 0));

    return buttonsPanel;
  }
}
