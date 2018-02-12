import jep.Jep;
import jep.JepException;

public class hello{
	public static void main(String[] args) throws Exception{
		Jep jep = new Jep(false);
		jep.eval("import sys");
		jep.eval("import numpy");
		jep.eval("import scipy");
		jep.eval("import pickle");
		jep.eval("print(\'Hello from Python\')");
		jep.eval("f =  open(\'sentiment_pipeline\', \'rb\')");
		jep.eval("print(f)");
		//jep.eval("d = pickle.loads(f, \'latin1\')");
		//jep.eval("f.close()");
		//jep.eval(" print(d.predict([\'I like it\']))");
		jep.eval("f.close()");
		System.out.println("Hello from Java");
	}
}
