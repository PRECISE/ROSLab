/**
 *
 */
package roslab.processors.verification;

import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Z3Exception;

/**
 * @author Peter Gebhard
 */
public class Z3Handler {

    Context ctx;
    Solver solver;

    /**
     *
     */
    public Z3Handler() {
        try {
            ctx = new Context();
            solver = ctx.mkSolver();
        }
        catch (Z3Exception e) {
            e.printStackTrace();
        }
    }
}
