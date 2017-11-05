package de.misi.idea.plugins.jenkins.dialog.settings;

import com.intellij.openapi.ui.DialogBuilder;
import de.misi.idea.plugins.jenkins.settings.AudioSettings;
import de.misi.idea.plugins.jenkins.settings.Authentication;
import de.misi.idea.plugins.jenkins.settings.PasswordManager;
import de.misi.idea.plugins.jenkins.settings.URLSettings;
import de.misi.idea.plugins.jenkins.settings.WorkspaceSettings;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Arrays;

import static de.misi.idea.plugins.jenkins.JenkinsStateUtilitiesKt.playMediaFile;
import static de.misi.idea.plugins.jenkins.dialog.settings.AuthenticationHelperKt.enableAuthenticationComponents;

public class JenkinsSettingsPanel {
    private JPanel rootPanel;
    private JCheckBox enableJenkinsState;
    private JTextField textURL;
    private JButton btnShowURLHelper;
    private JTextField checkInterval;
    private JCheckBox blueIsGreen;
    private JCheckBox showBlueBalloon;
    private JCheckBox showYellowBalloon;
    private JCheckBox showRedBalloon;
    private JCheckBox playAudio;
    private JTextField textAudioFile;
    private JButton btnBrowseAudio;
    private JButton btnPlayAudio;
    private JCheckBox requiresAuthentication;
    private JTextField username;
    private JPasswordField apitoken;
    private JCheckBox storeAPIToken;

    private boolean modified = false;

    public JenkinsSettingsPanel() {
        PropertyChangeListener modifyPropertyChangeListener = e -> modified = true;
        ActionListener modifyActionListener = e -> modified = true;
        ActionListener modifyAndEnableActionLstener = e -> {
            modified = true;
            enableComponents();
        };
        enableJenkinsState.addActionListener(e -> enableComponents());
        textURL.addPropertyChangeListener(modifyPropertyChangeListener);
        btnShowURLHelper.addActionListener(e -> showURLHelperDialog());
        requiresAuthentication.addActionListener(modifyAndEnableActionLstener);
        username.addPropertyChangeListener(modifyPropertyChangeListener);
        storeAPIToken.addActionListener(modifyAndEnableActionLstener);
        apitoken.addPropertyChangeListener(modifyPropertyChangeListener);
        checkInterval.addActionListener(modifyActionListener);
        blueIsGreen.addActionListener(modifyActionListener);
        showBlueBalloon.addActionListener(modifyActionListener);
        showYellowBalloon.addActionListener(modifyActionListener);
        showRedBalloon.addActionListener(modifyActionListener);
        playAudio.addActionListener(modifyAndEnableActionLstener);
        textAudioFile.addPropertyChangeListener(modifyPropertyChangeListener);
        btnBrowseAudio.addActionListener(e -> browseAudioFile());
        btnPlayAudio.addActionListener(e -> playAudioFile());
    }

    private void enableComponents() {
        enableComponents(enableJenkinsState.isSelected());
    }

    private void enableComponents(boolean enabled) {
        textURL.setEnabled(enabled);
        btnShowURLHelper.setEnabled(enabled);
        requiresAuthentication.setEnabled(enabled);
        enableAuthenticationComponents(enabled, requiresAuthentication, username, storeAPIToken, apitoken);
        checkInterval.setEnabled(enabled);
        blueIsGreen.setEnabled(enabled);
        showBlueBalloon.setEnabled(enabled);
        showYellowBalloon.setEnabled(enabled);
        showRedBalloon.setEnabled(enabled);
        playAudio.setEnabled(enabled);
        boolean enableAudio = enabled && playAudio.isSelected();
        textAudioFile.setEnabled(enableAudio);
        btnBrowseAudio.setEnabled(enableAudio);
        btnPlayAudio.setEnabled(enableAudio);
    }

    private void showURLHelperDialog() {
        DialogBuilder builder = new DialogBuilder();
        Authentication authentication = new Authentication();
        readAuthentication(authentication);
        URLHelperDialogPanel panel = new URLHelperDialogPanel(authentication, new String(apitoken.getPassword()));
        builder.setCenterPanel(panel.getRootPanel());
        if (builder.showAndGet()) {
            textURL.setText(panel.getRestUrl());
        }
    }

    public JPanel getRootPanel() {
        return rootPanel;
    }

    public boolean isModified() {
        return modified;
    }

    public WorkspaceSettings createSettings() {
        WorkspaceSettings workspaceSettings = new WorkspaceSettings();
        boolean enableJenkinsStateSelected = enableJenkinsState.isSelected();
        boolean requiresAuthenticationSelected = requiresAuthentication.isSelected();
        boolean storePasswordSelected = storeAPIToken.isSelected();
        URLSettings urlSettings = workspaceSettings.getUrlSettings();
        urlSettings.setUrl(textURL.getText());
        Authentication authentication = urlSettings.getAuthentication();
        AudioSettings audioSettings = workspaceSettings.getAudioSettings();

        workspaceSettings.setEnableJenkinsCheck(enableJenkinsStateSelected);
        readAuthentication(authentication);
        workspaceSettings.setWaitingSeconds(Integer.parseInt(checkInterval.getText()));
        workspaceSettings.setBlueIsGreen(blueIsGreen.isSelected());
        workspaceSettings.setShowBalloonOnBlue(showBlueBalloon.isSelected());
        workspaceSettings.setShowBalloonOnYellow(showYellowBalloon.isSelected());
        workspaceSettings.setShowBalloonOnRed(showRedBalloon.isSelected());
        audioSettings.setPlayAudioFile(playAudio.isSelected());
        audioSettings.setAudioFile(textAudioFile.getText());
        if (enableJenkinsStateSelected && requiresAuthenticationSelected && storePasswordSelected) {
            PasswordManager.INSTANCE.storePassword(username.getText(), String.copyValueOf(apitoken.getPassword()));
        }
        return workspaceSettings;
    }

    private void readAuthentication(Authentication authentication) {
        authentication.setRequired(requiresAuthentication.isSelected());
        authentication.setUsername(username.getText());
        authentication.setStoreAPIToken(storeAPIToken.isSelected());
    }

    public void reset(WorkspaceSettings settings) {
        URLSettings urlSettings = settings.getUrlSettings();
        Authentication authentication = urlSettings.getAuthentication();
        AudioSettings audioSettings = settings.getAudioSettings();

        enableJenkinsState.setSelected(settings.getEnableJenkinsCheck());
        textURL.setText(urlSettings.getUrl());
        requiresAuthentication.setSelected(authentication.getRequired());
        username.setText(authentication.getUsername());
        storeAPIToken.setSelected(authentication.getStoreAPIToken());
        apitoken.setText(PasswordManager.INSTANCE.readPassword(username.getText()));
        checkInterval.setText(Integer.toString(settings.getWaitingSeconds()));
        blueIsGreen.setSelected(settings.getBlueIsGreen());
        showBlueBalloon.setSelected(settings.getShowBalloonOnBlue());
        showYellowBalloon.setSelected(settings.getShowBalloonOnYellow());
        showRedBalloon.setSelected(settings.getShowBalloonOnRed());
        playAudio.setSelected(audioSettings.getPlayAudioFile());
        textAudioFile.setText(audioSettings.getAudioFile());

        enableComponents();
        modified = false;
    }

    private void browseAudioFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileTypeFilter("Audio-Files", ".mp3", ".wav", ".ogg", ".au"));
        if (fileChooser.showOpenDialog(rootPanel) == JFileChooser.APPROVE_OPTION) {
            textAudioFile.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void playAudioFile() {
        playMediaFile(textAudioFile.getText());
    }

    private static class FileTypeFilter extends FileFilter {

        private final String[] suffixes;
        private final String description;

        private FileTypeFilter(String description, String... suffixes) {
            this.suffixes = suffixes;
            this.description = description;
        }

        @Override
        public boolean accept(File f) {
            String filename = f.getName();
            return f.isDirectory() || Arrays.stream(suffixes).anyMatch(filename::endsWith);
        }

        @Override
        public String getDescription() {
            return description;
        }
    }
}
