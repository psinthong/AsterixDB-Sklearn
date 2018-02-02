# Installing Scikit-Learn UDFs

This document describes a complete cycle for social data analysis using Scikit-Learn package in AsterixDB. We assume you have followed the [installation instructions](http://asterixdb.apache.org/docs/0.9.2/install.html) to set up a running AsterixDB instance.

## How To Use
* Clone this repository onto your local machine.
* [Install necessary libraries](#install)
* [Train a machine learning model](#training) 
* [Select a predefined UDF](#udf)
* [Compile and launch an AsterixDB instance](#asterix)
* [Call the UDF](#apply)

## <a name="install">Dependencies</a>
* Python >=2.7 or >= 3.3
* [Pip](https://pip.pypa.io/en/stable/)
* Java >= 1.7
* [Scikit-Learn](http://scikit-learn.org/stable/install.html)
* [Jep](https://github.com/ninia/jep)

## Installing Packages

	python --version

Typing in the above command should print out a correct python version. If Python version is compatibled, you can proceed on with the following commands.

	python -m pip install --upgrade pip

	sudo pip install numpy

	sudo pip install matplotlib

	sudo pip install -U scikit-learn

	sudo pip install -U nltk

Install Java Embedded Python support. This is to enable a Java UDF to exchange information with python libraries(Scikit-Learn).

	sudo -H pip install jep

Make sure jep is accessible by a jvm. Drop jep jar file into Java library path.

	sudo cp <path to your python's site-packages that contains jep> /Library/Java/Extensions

	sudo cp /usr/local/lib/python3.6/site-packages/jep/ /Library/Java/Extensions

Note: If JEP is installed correctly, typing in 'jep' in your terminal should bring up a JEP shell. If there is an error, it is possible that your system's python interpreter does not find jep package in its search path. You would then need to set a path variable.


## <a name="training">Train a Machine Learning Model</a>
Use one of the provided sample files in the training folder to train a machine learning model. In this example we illustrate steps for training a sentiment analysis pipeline using Scikit-Learn.

	cd training

	python sentiment.py

If successfully, you can find a serialized package 'sentiment_pipeline' under target folder


Copy the serialized package into AsterixDB-Sklearn folder.
	
	cd ..

	cp training/target/sentiment_pipeline AsterixDB-Sklearn/src/main/resources/

## <a name="udf">Select a Predefined UDF</a>

- Open the 'library_descriptor.xml' file (under AsterixDB-Sklearn/src/main/resources/).
- Select a corresponding UDF to match the expected input/output (for this example, it is string/int)
- Edit the model name to match the trained model which is 'sentiment_pipeline'.
- Edit the function name. This will be the name you will use to call the function from AsterixDB (For our case, lets change it to 'SentimentScore').


## <a name="asterix">Compile and launch an AsterixDB instance</a>

	cd AsterixDB-Sklearn

	mvn package -DskipTests

Maven will create a zipped package under target folder. This is your udf path.

Use ansible to install the packaged udf and launch AsterixDB.

	cd ..

	cd asterix-server-0.9.3-SNAPSHOT-binary-assembly/opt/ansible/bin

	./deploy.sh

Install udf package using ansible. Put in the absolute path to the packaged udf. For example: replace UDF\_PACKAGE\_PATH with ../home/user/AsterixDB-Sklearn/AsterixDB-Sklearn/target/asterix-sklearn-udf-0.1-SNAPSHOT-testlib.zip. Replace DATAVERSE\_NAME with the name you would like to use with this udf. Note: This DATAVERSE_NAME if does not exist in your AsterixDB environment, it will be created.

	./udf.sh -m i -d DATAVERSE_NAME -l LIBRARY_NAME -p UDF_PACKAGE_PATH


Start your instance.

	./start.sh
	


## <a name="apply">Call the UDF</a>
	
Bring up the AsterixDB web interface by going to http://localhost:19001/

The below example uses a local file adapter to load a sample of 1000 twitter texts(provided with this repository) and call the sentiment udf. Replace the path to the twitter_1000.txt file with your absolute path. Edit the DATAVERSE\_NAME and and LIBRARY\_NAME to match the ones you specify while installing the udf package.

	USE DATAVERSE_NAME;

	create type Tweet as open{
	    id: int64,
	    text : string
	};

	CREATE DATASET TweetItems(Tweet)
		PRIMARY KEY id;


	LOAD DATASET TweetItems USING localfs
	    (("path"="127.0.0.1:///home/user/AsterixDB-Sklearn/twitter_1000.txt"),("format"="adm"));

	select tweet.text as text, LIBRARY_NAME#SentimentScore(tweet.text) as sentiment
	    from TweetItems tweet
	    limit 100;

To stop the instance :

	./stop.sh

