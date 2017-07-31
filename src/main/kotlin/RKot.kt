import org.renjin.eval.SessionBuilder
import org.renjin.primitives.matrix.Matrix
import org.renjin.script.RenjinScriptEngine
import org.renjin.script.RenjinScriptEngineFactory
import org.renjin.sexp.*
import org.renjin.sexp.Function

typealias R_Function = Function
typealias R_Null = Null
typealias SessionID = Int


//convenience lambda extensions for type conversion using the R engine
fun SEXP.logical(): LogicalVector = this as LogicalVector
fun SEXP.integer(): IntVector = this as IntVector
fun SEXP.double(): DoubleVector = this as DoubleVector
fun SEXP.character(): StringVector  = this as StringVector
fun SEXP.complex(): ComplexVector  = this as ComplexVector
fun SEXP.raw(): RawVector = this as RawVector
fun SEXP.list(): ListVector = this as ListVector
fun SEXP.function(): R_Function = this as R_Function
fun SEXP.env(): Environment = this as Environment
fun SEXP.NULL(): R_Null = this as R_Null

//wrapper for Renjin API in Kotlin
object R {

    //TODO: create Coroutines that can manage multiple sessions concurrently
    private val activeSessions: MutableMap<SessionID, RenjinScriptEngine> =
            mutableMapOf(Pair(1, RenjinScriptEngineFactory().scriptEngine))
    private var activeSession: RenjinScriptEngine =  RenjinScriptEngineFactory().scriptEngine

    //FIXME: perhaps better to let sessions invoke rather than singleton?
    operator fun invoke(expression: String): SEXP {
        return activeSession.eval(expression) as SEXP
    }

    fun startEngine(): RenjinScriptEngine {
        //TODO: add default system manager,
        //TODO: package loader,
        //TODO: and class loader methods BEFORE starting engine
        val session = SessionBuilder()
                .withDefaultPackages()
                .build()

        activeSessions.put(1 + activeSessions.size,
                RenjinScriptEngineFactory().getScriptEngine(session))

        activeSessions[activeSessions.size + 1]?.let { setActiveSession(it) }
        return activeSession

    }

    fun howManySessionsActive() {
        println(activeSessions)

    }

    private fun setActiveSession(rse: RenjinScriptEngine) {
        this.activeSession = rse
    }

    fun print(expression: String) {
        activeSession.eval("print($expression)")
    }


    //TODO: change to observable for better error handling purposes.
    fun matrix(seq: Int, row: Int): Matrix {
        try {
            val vector =activeSession.eval("matrix(seq($seq), nrow = $row)") as Vector
            return Matrix(vector)
        }  catch (iae: IllegalArgumentException){
            print(iae.message)
            return Matrix(activeSession.eval("matrix(seq(0), nrow=0)") as Vector)
        }
    }
    fun matrix(r_vector: Vector): Matrix {
        return Matrix(r_vector)
    }

    fun put(variable: String, value: Any) {
        activeSession.put(variable, value)

    }
}

fun main(args: Array<String>) {

    R("df <- data.frame(x=1:10, y=(1:10)+rnorm(n=10))")
    R.print("df")
    val R2 = R.startEngine()
    R.howManySessionsActive()
    println(R2.eval("df <- data.frame(x=1:15, y=(1:15)+rnorm(n=15))"))

}