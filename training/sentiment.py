from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split
from sklearn.naive_bayes import MultinomialNB
from pandas import read_csv
import csv
import pickle
from pandas import crosstab
from sklearn.feature_extraction.text import CountVectorizer
from sklearn.metrics import classification_report
from sklearn import metrics

if __name__ == '__main__':
    #read data
    pos_df = read_csv("data/pos.csv", delimiter='\t', quoting=csv.QUOTE_NONE)
    neg_df = read_csv("data/neg.csv", delimiter='\t', quoting=csv.QUOTE_NONE)
    df = pos_df.append(neg_df)
    X = df['text']
    y = df['sentiment']

    #constructing a pipeline
    count_vect = CountVectorizer()
    nb_clf = MultinomialNB()

    pipeline = Pipeline([
        ('vectorizer', count_vect),
        ('classifier', nb_clf)
    ])

    #training
    X_train, X_test, y_train, y_test = train_test_split(df['text'], y, stratify=y, test_size=0.2)

    pipeline.fit(X_train, y_train)
    preds = pipeline.predict(X_test)

    mat = crosstab(y_test, preds, rownames=['Actual Classes'], colnames=['Predicted Classes'])
    print(mat)
    print(classification_report(y_test, preds))
    print('Acuracy Score: ', metrics.accuracy_score(y_test, preds))

    pickle.dump(pipeline, open("target/sentiment_pipeline", 'wb'))