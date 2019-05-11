package sync;

import com.jcraft.jsch.*;

import javax.swing.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class SyncThread extends Thread {

    private static final String REMOTE_USER = "jzbor";
    private static final String LOCAL_USER = "jzbor";
    private static final String REMOTE_HOST = "localhost";
    private static final String FILE_KNOWN_HOSTS = "/home/" + LOCAL_USER + "/.ssh/known_hosts";
    private static final String LOCAL_DIR = "./test/out/";
    private static final String REMOTE_DIR = "/home/jzbor/Pictures/Silvester/MEDIUM/";
    private static final int SLEEP = 2000;
    private boolean end;
    private Session session;
    private ChannelSftp sftpChannel;

    public SyncThread() {
        start();
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

    @Override
    public void run() {
        super.run();

        try {
            login();
        } catch (JSchException e) {
            e.printStackTrace();
            return;
        }

        while (!end) {
            try {
                Vector<ChannelSftp.LsEntry> files = sftpChannel.ls(REMOTE_DIR);
                List<String> fileNameList = new ArrayList<>();
                for (ChannelSftp.LsEntry entry :
                        files) {
                    if (!entry.getAttrs().isDir()) {
                        fileNameList.add(entry.getFilename());
                    }
                }
                String[] filenames = fileNameList.toArray(new String[0]);
                for (int i = 0; i < filenames.length; i++) {
                    if (!(new File(LOCAL_DIR + filenames[i]).exists())) {
                        System.out.println("Copying rem:" + REMOTE_DIR + filenames[i] + " to loc:" + LOCAL_DIR + filenames[i]);
                        sftpChannel.get(REMOTE_DIR + filenames[i], LOCAL_DIR + filenames[i]);
                    }
                }
            } catch (SftpException e) {
                e.printStackTrace();
            }

            try {
                Thread.sleep(SLEEP);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void quit() {
        end = true;
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
            JLabel label = new JLabel("Enter a password: ");
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
            JLabel label = new JLabel("Enter a passphrase (SSH-key): ");
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
}
