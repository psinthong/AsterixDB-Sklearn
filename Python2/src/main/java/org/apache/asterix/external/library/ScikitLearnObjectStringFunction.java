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
package org.apache.asterix.external.library;

import jep.Jep;
import jep.JepException;
import org.apache.asterix.external.api.IExternalScalarFunction;
import org.apache.asterix.external.api.IFunctionHelper;
import org.apache.asterix.external.api.IJObject;
import org.apache.asterix.external.library.java.JObjects;
import org.apache.commons.io.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;

public class ScikitLearnObjectStringFunction implements IExternalScalarFunction {

    private static Jep jep;

    @Override
    public void deinitialize() {
        jep.close();
    }

    @Override
    public void evaluate(IFunctionHelper functionHelper) throws Exception {

        //-------------Get all features----------------//
        JObjects.JRecord inputRecord = (JObjects.JRecord) functionHelper.getArgument(0);
        JObjects.JString outputRecord = (JObjects.JString) functionHelper.getResultObject();


        IJObject inputRecordFields[] = inputRecord.getFields();
        Object[] testArray = new Object[inputRecordFields.length];
        for (int i = 0; i < inputRecordFields.length; i++) {
            testArray[i] = inputRecordFields[i].getIAObject();
        }

        //Getting model result
        String result = getResult(testArray);

        outputRecord.setValue(result);
        functionHelper.setResult(outputRecord);
    }

    @Override
    public void initialize(IFunctionHelper functionHelper) throws Exception {
        
        String modelPath = "sentiment_pipeline";
        InputStream in = this.getClass().getClassLoader().getResourceAsStream(modelPath);
        byte[] byteArray = IOUtils.toByteArray(in);
        String byteString =  new String(byteArray);

        jep = new Jep();
        jep.eval("import pickle");
        jep.set("bytes", byteString);
        jep.eval("pipeline = pickle.loads(bytes)");
    }


    public static String getResult(Object[] text) {
        try {

            jep.set("data", text);
            jep.eval("result = rdf.predict(data).tolist()");

            ArrayList<String> retArray = (ArrayList<String>)jep.getValue("result");

            return retArray.get(0);
        }
        catch (JepException e){

            System.out.println(e.getMessage());
        }

        return null;
    }

}
