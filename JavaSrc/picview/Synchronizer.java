package picview;

import com.jcraft.jsch.*;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Synchronizer {

    private static final String REMOTE_USER = Vars.getRemoteUser();
    private static final String LOCAL_USER = Vars.getLocalUser();
    private static final String REMOTE_HOST = Vars.getRemoteHost();
    private static final String FILE_KNOWN_HOSTS = Vars.getFileKnownHosts();
    public static final String LOCAL_DIR = Vars.getLocalDir();
    public static final String REMOTE_DIR = Vars.getRemoteDir();
    public boolean initialized;
    public boolean initFailed;
    public Session session;
    public ChannelSftp sftpChannel;

    public Synchronizer() {
    }

    public void init() {
        new LoginThread();
    }

    private void login() throws JSchException {
        JSch jsch = new JSch();
        jsch.setKnownHosts(FILE_KNOWN_HOSTS);

        session = jsch.getSession(REMOTE_USER, REMOTE_HOST);
        UserInfo userInfo = new MyUserInfo();
        session.setUserInfo(userInfo);

        session.connect();

        sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
    }

    public void sync() throws SftpException {
        if (initFailed) {
            System.out.println("Unable to synchronize with remote (" + REMOTE_USER + "@" + REMOTE_HOST + ")");
            return;
        }
        System.out.println("Synchronizing with remote (" + REMOTE_USER + "@" + REMOTE_HOST + ")");
        Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(REMOTE_DIR);
        List<String> fileNameList = new ArrayList<>();
        for (ChannelSftp.LsEntry entry :
                files) {
            if (!entry.getAttrs().isDir()) {
                fileNameList.add(entry.getFilename());
            }
        }
        String[] filenames = fileNameList.toArray(new String[0]);
        File localDir = new File(LOCAL_DIR);
        for (int i = 0; i < filenames.length; i++) {
            if (!(new File(localDir, filenames[i]).exists())) {
                if (!localDir.isDirectory()) {
                    localDir.mkdirs();
                    System.out.println("\t\tCreating path: " + localDir.getAbsolutePath());
                }
                System.out.println("\t\tCopying rem:" + REMOTE_DIR + filenames[i] + " to loc:" + LOCAL_DIR + filenames[i]);

                sftpChannel.get(REMOTE_DIR + filenames[i], LOCAL_DIR + filenames[i]);
            }
        }
        System.out.println("\tDone");
    }

    public boolean isInitialized() {
        return initialized;
    }

    public boolean isInitFailed() {
        return initFailed;
    }

    private class MyUserInfo implements UserInfo {

        private static final String TITLE = "JSch login (ssh/sftp)";
        private String passphrase;
        private String password;

        @Override
        public String getPassphrase() {
            return passphrase;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean promptPassword(String s) {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Passwort für " + REMOTE_USER + "@" + REMOTE_HOST + ": ");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            int option = JOptionPane.showConfirmDialog(null,
                    panel,
                    TITLE,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                password = new String(pass.getPassword());
            }
            return option == JOptionPane.OK_OPTION;
        }

        @Override
        public boolean promptPassphrase(String s) {
            JPanel panel = new JPanel();
            JLabel label = new JLabel("Passphrase (SSH-key): ");
            JPasswordField pass = new JPasswordField(10);
            panel.add(label);
            panel.add(pass);
            int option = JOptionPane.showConfirmDialog(null,
                    panel,
                    TITLE,
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                passphrase = new String(pass.getPassword());
            }
            return option == JOptionPane.OK_OPTION;
        }

        @Override
        public boolean promptYesNo(String s) {
            int answer = JOptionPane.showConfirmDialog(null,
                    s,
                    TITLE,
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.PLAIN_MESSAGE);
            return answer == JOptionPane.YES_OPTION;
        }

        @Override
        public void showMessage(String s) {
            JOptionPane.showMessageDialog(null,
                    s,
                    TITLE,
                    JOptionPane.PLAIN_MESSAGE);
        }
    }

    private class LoginThread extends Thread {
        public LoginThread() {
            start();
        }

        @Override
        public void run() {
            try {
                login();
                initialized = true;
                initFailed = false;
            } catch (JSchException e) {
                e.printStackTrace();
                initFailed = true;
            }
        }
    }
}
