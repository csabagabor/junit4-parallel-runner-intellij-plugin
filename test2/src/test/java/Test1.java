import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import com.intellij.debugger.impl.GenericDebuggerRunner;
public class Test1 {
    @Test
    public void a() throws InterruptedException {
        assertTrue(10 == 5 + 5);

        assertFalse(11 == 5 + 5);
        //while(true);
    }
}
