package org.jenkinsci.test.acceptance.docker.fixtures;

import java.io.IOException;
import java.net.URL;
import org.jenkinsci.test.acceptance.docker.Docker;
import org.jenkinsci.test.acceptance.docker.DockerContainer;
import org.jenkinsci.test.acceptance.docker.DockerFixture;

/**
 * Runs gitserver container.
 */
@DockerFixture(id = "git", ports = 22)
public class GitContainer extends DockerContainer {
    protected static final String REPO_DIR = "/home/git/gitRepo";
    public static final String REPO_NAME = "gitRepo";

    public String host() {
        return ipBound(22);
    }
    
    public int port() {
        return port(22);
    }

    public URL getUrl() throws IOException {
        String host = host();
        if (ipv6Enabled()) {
            host = String.format("[%s]", host());
        }
        return new URL("http://" + host + ":" + port());
    }

    /** URL visible from the host. */
    public String getRepoUrl() {
        String host = host();
        if (ipv6Enabled()) {
            host = String.format("[%s]", host());
        }
        return "ssh://git@" + host + ":" + port() + REPO_DIR;
    }

    @Deprecated
    public String getRepoUrlInsideDocker() throws IOException {
        String ipAddress = getIpAddress();
        if (ipv6Enabled()) {
            ipAddress = String.format("[%s]", getIpAddress());
        }
        return "ssh://git@" + ipAddress + REPO_DIR;
    }

    /**
     * URL visible from other Docker containers.
     * @param alias an alias for this container’s {@link #getCid} passed to {@code --link}
     */
    public String getRepoUrlInsideDocker(String alias) throws IOException {
        return "ssh://git@" + alias + REPO_DIR;
    }

    /**
     * Add an additional certificate to {@code ~/.ssh/authorized_keys}
     * @param pubKey the certificate public key
     */
    public void addSSHCertificate(String pubKey) throws IOException, InterruptedException {
        Docker.cmd("exec", getCid()).add("/bin/bash",  "-c",  "echo " + pubKey + " >> /home/git/.ssh/authorized_keys")
                .popen()
                .verifyOrDieWith("Unable to add SSH public key to authorized keys");
    }

}
