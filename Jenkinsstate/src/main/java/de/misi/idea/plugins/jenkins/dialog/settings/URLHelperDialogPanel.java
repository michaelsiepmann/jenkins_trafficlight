package de.misi.idea.plugins.jenkins.dialog.settings;

import de.misi.idea.plugins.jenkins.dialog.ArrayListModel;
import de.misi.idea.plugins.jenkins.parser.data.views.Hudson;
import de.misi.idea.plugins.jenkins.parser.data.views.View;
import de.misi.idea.plugins.jenkins.settings.Authentication;
import de.misi.idea.plugins.jenkins.settings.PasswordManager;

import javax.swing.*;

import static de.misi.idea.plugins.jenkins.dialog.settings.AuthenticationHelperKt.enableAuthenticationComponents;
import static de.misi.idea.plugins.jenkins.parser.JenkinsXMLParserKt.createRestURL;
import static de.misi.idea.plugins.jenkins.parser.JenkinsXMLParserKt.parseHudson;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

class URLHelperDialogPanel {
    private JPanel rootPanel;
    private JTextField baseUrl;
    private JButton fetchMainData;
    private JList<View> dashboards;
    private JCheckBox requiresAuthentication;
    private JTextField username;
    private JPasswordField apitoken;
    private JCheckBox storeAPIToken;

    private final Authentication authentication;

    URLHelperDialogPanel(Authentication authentication, String apitoken) {
        this.authentication = authentication;
        fetchMainData.addActionListener(e -> fetchMainDataFromURL());
        requiresAuthentication.addActionListener(e -> enableComponents());
        storeAPIToken.addActionListener(e -> enableComponents());
        requiresAuthentication.setSelected(authentication.getRequired());
        String username = authentication.getUsername();
        this.username.setText(username);
        storeAPIToken.setSelected(authentication.getStoreAPIToken());
        if (isNotEmpty(username)) {
            this.apitoken.setText(isNotEmpty(apitoken) ? apitoken : PasswordManager.INSTANCE.readPassword(username));
        }
        enableComponents();
    }

    private void enableComponents() {
        enableAuthenticationComponents(requiresAuthentication, username, storeAPIToken, apitoken);
    }

    private void fetchMainDataFromURL() {
        String restUrl = createRestURL(baseUrl.getText());
        Hudson hudson = parseHudson(restUrl, authentication);
        dashboards.setModel(new ArrayListModel<>(hudson.getViews()));
        if (!hudson.getViews().isEmpty()) {
            dashboards.setSelectedIndex(0);
        }
    }

    JPanel getRootPanel() {
        return rootPanel;
    }

    String getRestUrl() {
        return createRestURL(dashboards.getSelectedValue().getUrl());
    }
}
