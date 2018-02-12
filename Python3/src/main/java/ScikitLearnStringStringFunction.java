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
import jep.JepConfig;
import jep.JepException;
import org.apache.asterix.external.api.IExternalScalarFunction;
import org.apache.asterix.external.api.IFunctionHelper;
import org.apache.asterix.external.library.java.JObjects;
import org.apache.asterix.external.library.java.JTypeTag;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ScikitLearnStringStringFunction implements IExternalScalarFunction {

    private static Jep jep;

    @Override
    public void deinitialize() {
        jep.close();
    }

    @Override
    public void evaluate(IFunctionHelper functionHelper) throws Exception {

        //-------------Get all features----------------//
        JObjects.JString inputText = ((JObjects.JString) functionHelper.getArgument(0));

        JObjects.JString output = (JObjects.JString) functionHelper.getObject(JTypeTag.STRING);

        //Getting model result
        String[] testArray = new String[]{inputText.getValue()};
        String result = getResult(testArray);

        output.setValue(result);
        functionHelper.setResult(output);
    }

    @Override
    public void initialize(IFunctionHelper functionHelper)  throws Exception{

        String modelPath = "sentiment_pipeline";
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(modelPath);
        byte[] byteArray = IOUtils.toByteArray(is);

        jep = new Jep();
        jep.eval("import pickle");

        jep.set("f", byteArray);
        jep.eval("pipeline = pickle.loads(bytes(f),encoding=\"latin1\")");

    }


    public static String getResult(String[] text) {
        try {

            jep.set("data", text);
            jep.eval("result = pipeline.predict(data)[0]");

            String ret = jep.getValue("result").toString();
            return ret;
        }
        catch (JepException e){

            System.out.println(e.getMessage());
        }

        return null;
    }

}
