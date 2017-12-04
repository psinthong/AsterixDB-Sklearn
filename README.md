# Installing Scikit-Learn UDFs

This document describes a complete cycle for social data analysis using Scikit-Learn package in AsterixDB. We assume you have followed the [installation instructions](http://asterixdb.apache.org/docs/0.9.0/install.html) to set up a running AsterixDB instance.

## Dependencies
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

	sudo pip install jep

Make sure jep is accessible by a jvm. Drop jep jar file into Java library path.

	sudo cp lib/libjep.jnilib /Library/Java/Extensions


## How To Use
* Install necessary libraries.
* Clone this repository onto your local machine.
* [Train a machine learning model](#training) 
* [Select a predefined UDF](#udf)
* [Compile and launch an AsterixDB instance](#asterix)
* [Call the UDF](#apply)

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

	./udf.sh -m i -d DATAVERSE_NAME -l LIBRARY_NAME -p UDF_PACKAGE_PATH

The UDF\_PACKAGE\_PATH = ../AsterixDB-Sklearn/target/asterix-sklearn-udf-0.1-SNAPSHOT-testlib.zip


Start your instance.

	./start.sh
	


## <a name="apply">Call the UDF</a>
	
	USE DATAVERSE_NAME;

	create type Tweet as open{
	    id: int64,
	    text : string
	};

	CREATE DATASET TweetItems(Tweet)
		PRIMARY KEY id;


	LOAD DATASET TweetItems USING localfs
	    (("path"="127.0.0.1:///home/user/AsterixDB-Sklearn/tweets_1000.txt"),("format"="adm"));

	select tweet.text as text, skl#SentimentScore(tweet.text) as sentiment
	    from TweetItems tweet
	    limit 100;
