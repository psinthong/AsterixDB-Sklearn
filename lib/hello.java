import jep.Jep;
import jep.JepException;

public class hello{
	public static void main(String[] args) throws Exception{
		Jep jep = new Jep(false);
		jep.eval("import sys");
		jep.eval("import numpy");
		jep.eval("import scipy");
		jep.eval("import sklearn");
		jep.eval("import pickle");
		jep.eval("print(\'Hello from Python\')");
		System.out.println("Hello from Java");
	}
}
