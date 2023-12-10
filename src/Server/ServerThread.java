package Server;

import Network.DataTranslator;

import java.util.concurrent.CyclicBarrier;

public abstract class ServerThread extends Thread {

    protected DataTranslator dataTranslator;
    protected CyclicBarrier cyclicBarrier;

    public ServerThread(DataTranslator dataTranslator, CyclicBarrier cyclicBarrier) {
        this.dataTranslator = dataTranslator;
        this.cyclicBarrier = cyclicBarrier;
    }
}
