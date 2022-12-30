package dev.lastknell.core.manager;

public class ConnectionsInfo {
    public int secondsElapsed;
    public int totalConnections;
    public int openedCPS;
    public int successfulCPS;

    public ConnectionsInfo() {
        successfulCPS = 0;
        openedCPS = 0;
        totalConnections = 0;
        secondsElapsed = 0;
    }
}
