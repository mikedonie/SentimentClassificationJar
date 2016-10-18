
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.StringTokenizer;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;

public class main
{
  private static int folds = 10;
  //private static int nch=6;
  
  public static void main(String[] args)
    throws Exception
  {   
    //int fold = Integer.parseInt(args[0]);
    String benchmark="DebatPolitique";
    //BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("data//"+benchmark+"//train"+/*fold+*/".arff"), "UTF-8"));
    //BufferedReader rt = new BufferedReader(new InputStreamReader(new FileInputStream("data//"+benchmark+"//test"+/*fold+*/".arff"), "UTF-8"));
    
    StringToWordVector filter = (StringToWordVector) weka.core.SerializationHelper.read("models//DEFT15_T2.2_STW.model");
    AttributeSelection f = (AttributeSelection)weka.core.SerializationHelper.read("models//DEFT15_T2.2_IG.model");
    Classifier classifier = (Classifier) weka.core.SerializationHelper.read("models//DEFT15_T2.2_SMO.model");
    
    BufferedReader rt = new BufferedReader(new InputStreamReader(new FileInputStream("Test//Test2.2.arff"), "UTF-8"));
    
    //BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("DebatTrainFinalFs.arff"), "UTF-8"));
    //BufferedReader rt = new BufferedReader(new InputStreamReader(new FileInputStream("DebatTestFinalFs.arff"), "UTF-8"));
        
    //Instances train = new Instances(r);
    Instances test = new Instances(rt);
    
    //SMO classifier = new SMO();
    
    //train.setClass(train.attribute("_class"));
    test.setClass(test.attribute("_class"));
    
    /*train.setClassIndex(train.numAttributes() - 1);
    test.setClassIndex(test.numAttributes() - 1);
    */
    System.out.println("Construction objet ConstructionARFF..");
    ConstructionARFF_Old obj = new ConstructionARFF_Old();
    System.out.println("Construction train ARFF..");
    //train = obj.ConstructionInstances(train);
    System.out.println("Construction test ARFF..");
    test = obj.ConstructionInstances(test,"emotion");
   // System.out.println("  Train.numAttributes = " + train.numAttributes());
    System.out.println("  Test.numAttributes = " + test.numAttributes());
    
    //StringToWordVector filter = Tokenisation.WordNgrams(1,1);
    //filter.setInputFormat(train);
    //train = Filter.useFilter(train, filter);
    test = Filter.useFilter(test, filter);
    //train.setClass(train.attribute("_class"));
    test.setClass(test.attribute("_class"));
    System.out.println("  numAttributes Après StringToWordVector = " + test.numAttributes());
    
    double macroPrecision, macroRappel, macroFmesure;
    DecimalFormat twoDForm = new DecimalFormat("#.##");
    
    /*Save(train,"JeuxTrain_Avg.arff");
    Save(test,"JeuxTest_Avg.arff");*/
    
    //AttributeSelection f = SelectionAttributs.InfoGainAttributeEval(train);
    //f.setInputFormat(train);
    //train = Filter.useFilter(train, f);
    test =  Filter.useFilter(test, f);
    
    /*Save(train,"DebatTrainFinalFs_WE.arff");
    Save(test,"DebatTestFinalFs_WE.arff");*/
    
    //classifier.setC(0.05);
    //classifier.buildClassifier(train);
       
    // Calculer les micro et macro mesures    
    Evaluation eTest = new Evaluation(test);
    eTest.evaluateModel(classifier, test);
    macroPrecision=0; macroRappel=0; macroFmesure=0;
    for (int i=0; i<test.attribute("_class").numValues(); i++){
        macroPrecision+=eTest.precision(i);
        macroRappel+=eTest.recall(i);
        macroFmesure+=eTest.fMeasure(i);
    }
    macroPrecision=macroPrecision/test.attribute("_class").numValues();
    macroRappel=macroRappel/test.attribute("_class").numValues();
    macroFmesure=macroFmesure/test.attribute("_class").numValues();
    // Enregistrer les résultats
    //PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("results//"+benchmark+"//FinalTest"+/*(fold-1)+*/".txt")));
    PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("ResultatT22"+/*(fold-1)+*/".txt")));
    w.println(roundTwoDecimals(eTest.weightedPrecision())+" & "+roundTwoDecimals(eTest.weightedRecall())+" & "+roundTwoDecimals(eTest.weightedFMeasure())+" & "+roundTwoDecimals(macroPrecision)+" & "+roundTwoDecimals(macroRappel)+" & "+roundTwoDecimals(macroFmesure)+" & "+eTest.unweightedMicroFmeasure());
    w.flush();
    w.close();/*
    
    // Estimation du paramètre C
    
    for (double c=0; c<=2; c=c+0.01){
        // Contstruire le classifieur
        classifier.setC(c);
        classifier.buildClassifier(train);
        // Calculer les micro et macro mesures    
        Evaluation eTest = new Evaluation(train);
        eTest.evaluateModel(classifier, test);
        macroPrecision=0; macroRappel=0; macroFmesure=0;
        for (int i=0; i<train.attribute("_class").numValues(); i++){
            macroPrecision+=eTest.precision(i);
            macroRappel+=eTest.recall(i);
            macroFmesure+=eTest.fMeasure(i);
        }
        macroPrecision=macroPrecision/train.attribute("_class").numValues();
        macroRappel=macroRappel/train.attribute("_class").numValues();
        macroFmesure=macroFmesure/train.attribute("_class").numValues();
        // Enregistrer les résultats
        PrintWriter w = new PrintWriter(new BufferedWriter(new FileWriter("results//"+benchmark+"//"+c+".txt", true)));
        w.println(roundTwoDecimals(eTest.weightedPrecision())+" & "+roundTwoDecimals(eTest.weightedRecall())+" & "+roundTwoDecimals(eTest.weightedFMeasure())+" & "+roundTwoDecimals(macroPrecision)+" & "+roundTwoDecimals(macroRappel)+" & "+roundTwoDecimals(macroFmesure));
        w.flush();
        w.close();
    }*/
    
  }
  
  
  public static String Lemmatiser(String tweet, LemmatiseurHandler lm)
    throws Exception
  {
    String tweet_lem = "";
    lm.clear();
    lm.setTermes(tweet);
    lm.process();
    for (String t : lm.getListTermeLem())
    {
      if (tweet_lem.length() > 1) {
        tweet_lem = tweet_lem + " ";
      }
      if ((t.contains("|")) && (t.length() > 1)) {
        try
        {
          t = t.split("\\|")[0];
        }
        catch (Exception e) {}
      }
      tweet_lem = tweet_lem + t;
    }
    return tweet_lem;
  }
  
  public static void Save(Instances data, String file) throws IOException{
      ArffSaver saver = new ArffSaver();
      saver.setInstances(data);
      saver.setFile(new File(file));
      saver.writeBatch();
  }
  
  
  public static String roundTwoDecimals(double d) {
    double r=d*100;
    DecimalFormat twoDForm = new DecimalFormat("#.#");
    return twoDForm.format(r).replaceAll(",", ".");
  }
  
}