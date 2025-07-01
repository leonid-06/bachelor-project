package com.leonid.workerbot.service;

import com.leonid.workerbot.config.SSHClientFactory;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.TransportException;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.UserAuthException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class SshService {
    private final SSHClientFactory sshClientFactory;

    @Value("${remote.username}")
    private String remoteUsername;

    @Value("${remote.home.dir}")
    private String remoteHomePath;

    @Value("${local.pem.key.path}")
    private String localPemKeyPath;

    @Value("${local.deploy.script.path}")
    private String deployScriptToUploadPath;

    @Value("${remote.deploy.script.path}")
    private String remoteScriptPath;

    private static final int PING_TIMEOUT = 2000;
    private static final int PING_COUNT = 10;


    public void sendSrcArchiveAndRunServer(String ipV4, String zipToUploadPath){
        System.out.println("Start of sendSrcArchive to remote ip: " + ipV4);
        SSHClient sshClient = sshClientFactory.create();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());

        try {
            sshClient.connect(ipV4);
        } catch (IOException e) {
            System.err.println("Chinazes with sshClient.connect");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        try {
            sshClient.authPublickey(remoteUsername, localPemKeyPath);
        } catch (UserAuthException e) {
            System.err.println("Chinazes with sshClient.authPublickey by reason of UserAuthException");
            throw new RuntimeException(e);
        } catch (TransportException e) {
            System.err.println("Chinazes with sshClient.authPublickey by reason of TransportException");
            throw new RuntimeException(e);
        }

        try {
            sshClient.newSCPFileTransfer().upload(zipToUploadPath, remoteHomePath);
            sshClient.newSCPFileTransfer().upload(deployScriptToUploadPath, remoteHomePath);
        } catch (IOException e) {
            System.err.println("Chinazes with sshClient.upload");
            throw new RuntimeException(e);
        }

        System.out.println("Start of script running");
        try (Session session = sshClient.startSession()) {
            Session.Command cmd = session.exec("chmod +x " + remoteScriptPath + " && " + remoteScriptPath);

            cmd.join(60, TimeUnit.SECONDS);

            Integer exitStatus = cmd.getExitStatus();
            if (exitStatus != 0) {
                System.out.println("Chinazes with sshClient.upload");
                throw new RuntimeException("Command failed with exit code: " + exitStatus);
            } else {
                System.out.println("deploy.sh successfully completed");
            }
        }catch (Exception e){
            System.out.println("Chinazes with sshClient.upload");
            throw new RuntimeException(e);
        }

        try {
            sshClient.disconnect();
            sshClient.close();
        } catch (IOException e) {
            System.err.println("Chinazes with sshClient.disconnect");
            throw new RuntimeException(e);
        }
    }

    public void waitForSshAvailable(String ipV4) {
        int attempts = 0;
        boolean isAvailable = false;

        while (attempts < PING_COUNT) {
            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(ipV4, SSHClient.DEFAULT_PORT), PING_TIMEOUT);
                isAvailable = true;
                System.out.println("SSH port is open on " + ipV4);
                break;
            } catch (IOException e) {
                attempts++;
                System.out.println("Attempt " + attempts + ": SSH not available yet...");
                try {
                    Thread.sleep(PING_TIMEOUT);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Thread interrupted while waiting for SSH", ie);
                }
            }
        }

        if (!isAvailable) {
            throw new RuntimeException("SSH is not available on " + ipV4 + " after " + PING_COUNT + " attempts");
        }
    }


}
