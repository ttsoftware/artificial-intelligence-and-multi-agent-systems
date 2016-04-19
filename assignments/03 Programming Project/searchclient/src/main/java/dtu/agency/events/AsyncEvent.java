package dtu.agency.events;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * An AsyncEvent is an event which asynchronously returns a response
 *
 * @param <T>
 */
public class AsyncEvent<T> extends Event {

    private T response;
    private CountDownLatch latch = new CountDownLatch(1);

    /**
     * Default overloaded getResponse with 2^32-1 millisecond.
     * @return
     */
    public T getResponse() {
        return getResponse(Integer.MAX_VALUE);
    }

    /**
     * Will wait until setResponse is called before returning or timeout.
     * @param timeout
     * @return response T after waiting for a maximum of timeout milliseconds.
     */
    public T getResponse(long timeout) {
        try {
            latch.await(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }
        return response;
    }

    public void setResponse(T response) {
        this.response = response;
        latch.countDown();
    }
}