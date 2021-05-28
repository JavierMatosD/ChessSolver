import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicReferenceArray;
//task that takes a node and sets its children, adding those children to the allChildren shared queue and counting down the latch.
//the adding to the shared data structure is commented out to demonstrate that it's not the step slowing down this implementation.
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
        //check root.checkmate?
//        allChildren.addAll(n.getChildren()); //interesting. Accessing children directly threw NullPointerException. You'd think within a task it would run sequentially?
        latch.countDown();

    }

    @Override
    public Object call() throws Exception {
        return null;
    }
}
