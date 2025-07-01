package com.leonid.workerbot.config;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.stereotype.Component;

@Component
public class SSHClientFactory {
    public SSHClient create() {
        SSHClient sshClient = new SSHClient();
        sshClient.addHostKeyVerifier(new PromiscuousVerifier());
        return sshClient;
    }
}

