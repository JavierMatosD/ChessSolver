import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class setChildrenTask<T> implements Runnable, Callable {

    MoveTreeParallel.Node n;
    ConcurrentLinkedQueue<T> allChildren;
    CountDownLatch latch;

    public setChildrenTask(MoveTreeParallel.Node n, ConcurrentLinkedQueue<T> allChildren, CountDownLatch latch){
        this.n = n;
        this.allChildren = allChildren;
        this.latch = latch;
    }

    @Override
    public void run() {
        n.setChildren();
//        System.out.println(n.getChildren());
        allChildren.addAll(n.getChildren()); //interesting. Accessing children directly threw NullPointerException. You'd think within a task it would run sequentially?
        latch.countDown();

    }

    @Override
    public Object call() throws Exception {
        return null;
    }
}
