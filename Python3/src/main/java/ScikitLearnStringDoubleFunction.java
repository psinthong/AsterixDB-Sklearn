/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import jep.Jep;
import jep.JepException;
import org.apache.asterix.external.api.IExternalScalarFunction;
import org.apache.asterix.external.api.IFunctionHelper;
import org.apache.asterix.external.library.java.JObjects;
import org.apache.asterix.external.library.java.JTypeTag;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ScikitLearnStringDoubleFunction implements IExternalScalarFunction {

    private static Jep jep;

    @Override
    public void deinitialize() {
        jep.close();
    }

    @Override
    public void evaluate(IFunctionHelper functionHelper) throws Exception {

        //-------------Get all features----------------//
        JObjects.JString inputText = ((JObjects.JString) functionHelper.getArgument(0));

        JObjects.JDouble output = (JObjects.JDouble) functionHelper.getObject(JTypeTag.DOUBLE);

        //Getting model result
        String[] testArray = new String[]{inputText.getValue()};
        Double result = getResult(testArray);

        output.setValue(result);
        functionHelper.setResult(output);
    }

    @Override
    public void initialize(IFunctionHelper functionHelper)  throws Exception{

        String modelPath = "sentiment_pipeline3";

        InputStream is = this.getClass().getClassLoader().getResourceAsStream(modelPath);

        byte[] byteArray = IOUtils.toByteArray(is);

        String path = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
        String parent = new File(path).getParentFile().getPath();
        String newFilePath = parent+"/"+modelPath;
        FileOutputStream fos = new FileOutputStream(newFilePath);
        fos.write(byteArray);

        jep = new Jep();
        jep.eval("import pickle");

        jep.set("fname", newFilePath);
        jep.eval("f = open(fname,\'rb\')");
        jep.eval("pipeline = pickle.load(f)");
        jep.eval("f.close()");

    }


    public static Double getResult(String[] text) throws JepException{
        jep.set("data", text);
        jep.eval("result = pipeline.predict(data)[0]");

        double ret = Double.parseDouble(jep.getValue("result").toString());
        return ret;

    }

}
