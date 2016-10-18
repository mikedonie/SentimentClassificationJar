import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.SMO;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.supervised.instance.Resample;  //pourquoi pas unservised


public class LearnModels
{
    private  int nbFolds=0;
    private  ArrayList<Object> MiP=new ArrayList<>();
    private  ArrayList<Object> MiR=new ArrayList<>();
    private  ArrayList<Object> MiF=new ArrayList<>();
    private  ArrayList<Object> MaP=new ArrayList<>();
    private  ArrayList<Object> MaR=new ArrayList<>();
    private  ArrayList<Object> MaF=new ArrayList<>();
  
  public void learn(String propPath)
    throws Exception
  {     
        Properties prop = new Properties();
	InputStream input = new FileInputStream(propPath);
        prop.load(input);
        
        BufferedReader r , rt;
        Instances train, test = null;
        
        r = new BufferedReader(new InputStreamReader(new FileInputStream(prop.getProperty("Data.trainPath")), "UTF-8"));
        train = new Instances(r);
        train.setClassIndex(train.numAttributes() - 1);
        CalculAttributs ca = new CalculAttributs(propPath);
        ConstructionARFF obj = new ConstructionARFF(propPath,ca);
        train = obj.ConstructionInstances(train);
        
        if(prop.getProperty("Data.resample").equalsIgnoreCase("yes")){
            // Resampling
            Resample filt = new Resample();
            filt.setBiasToUniformClass(0.8);
            try {
                    filt.setInputFormat(train);
                    filt.setNoReplacement(false);
                    filt.setSampleSizePercent(100);
                    train = Filter.useFilter(train, filt);
            } catch (Exception e) {
                    System.out.println("Error when resampling input data!");
                    e.printStackTrace();
            }
        }
        
        if (prop.getProperty("Data.testPath").length()>0){
            rt = new BufferedReader(new InputStreamReader(new FileInputStream(prop.getProperty("Data.testPath")), "UTF-8"));
            test = new Instances(rt);
            test.setClassIndex(test.numAttributes() - 1);
            test = obj.ConstructionInstances(test);
            rt.close();
        }
        else nbFolds=Integer.parseInt(prop.getProperty("Data.nbFolds"));
        System.out.println("  Number of selected attributes = " + train.numAttributes());
        
        if (nbFolds==0) Run(train,test,propPath);
        else{
            Instances data=train;
            Random rand = new Random();   // create seeded number generator
            data.randomize(rand);        // randomize data with number generator
            data.setClass(data.attribute("_class"));
            data.stratify(nbFolds);
            for (int f=0; f<nbFolds; f++){
                System.out.println();
                System.out.println("######## Fold"+(f+1)+" ########");
                train = data.trainCV(nbFolds,f);
                test = data.testCV(nbFolds,f);
                Run(train,test,propPath);
            }
            System.out.println();
            System.out.println("######## Global results ########");
            double mip=0, mir=0, mif=0, map=0, mar=0, maf=0;
            for (int i=0;i<nbFolds;i++){
                mip+=(Double) MiP.get(i);
                mir+=(Double) MiR.get(i);
                mif+=(Double) MiF.get(i);
                map+=(Double) MaP.get(i);
                mar+=(Double) MaR.get(i);
                maf+=(Double) MaF.get(i);
            }
            
            System.out.println("    miP="+roundTwoDecimals(mip/nbFolds));
            System.out.println("    miR="+roundTwoDecimals(mir/nbFolds));
            System.out.println("    miF="+roundTwoDecimals(mif/nbFolds));
            System.out.println("    maP="+roundTwoDecimals(map/nbFolds));
            System.out.println("    maR="+roundTwoDecimals(mar/nbFolds));
            System.out.println("    maF="+roundTwoDecimals(maf/nbFolds));
            
        }
        r.close();
        try {
             input.close();
        } catch (IOException e) {
            System.out.println(e);
	}
  }
  
  public void Run(Instances train, Instances test, String propPath) throws Exception{
        Properties prop = new Properties();
	InputStream input = new FileInputStream(propPath);
        prop.load(input);
        StringToWordVector filter = Tokenisation.WordNgrams(propPath);
        filter.setInputFormat(train);
        train = Filter.useFilter(train, filter);
        test = Filter.useFilter(test, filter);
        train.setClass(train.attribute("_class"));
        test.setClass(train.attribute("_class"));
        
        double macroPrecision, macroRappel, macroFmesure;
        
        AttributeSelection f = new AttributeSelection();
        if (prop.getProperty("FeatureSelection.perform").equalsIgnoreCase("Yes")){
            f = SelectionAttributs.InfoGainAttributeEval(train);
            f.setInputFormat(train);
            train = Filter.useFilter(train, f);
            test =  Filter.useFilter(test, f);
        }
        System.out.println("  Number of attributes after Feature Selection = " + train.numAttributes());
        
        SMO classifier = new SMO();
        double c = Double.parseDouble(prop.getProperty("SVM.CompexityParameter"));
        classifier.setC(c);
        classifier.buildClassifier(train);
        
        // Save the models
        /*weka.core.SerializationHelper.write(path+"/SMO.model", classifier);
        weka.core.SerializationHelper.write(path+"/STW.model", filter);
        weka.core.SerializationHelper.write(path+"/IG.model", f);*/
        weka.core.SerializationHelper.write(prop.getProperty("StringToWordVector"), filter);
        weka.core.SerializationHelper.write(prop.getProperty("AttributeSelection"), f);
        weka.core.SerializationHelper.write(prop.getProperty("Classifier"), classifier);
        
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
        System.out.println("    miP="+roundTwoDecimals(eTest.weightedPrecision()));
        MiP.add(eTest.weightedPrecision());
        System.out.println("    miR="+roundTwoDecimals(eTest.weightedRecall()));
        MiR.add(eTest.weightedRecall());
        System.out.println("    miF="+roundTwoDecimals(eTest.weightedFMeasure()));
        MiF.add(eTest.weightedFMeasure());
        System.out.println("    maP="+roundTwoDecimals(macroPrecision));
        MaP.add(macroPrecision);
        System.out.println("    maR="+roundTwoDecimals(macroRappel));
        MaR.add(macroRappel);
        System.out.println("    maF="+roundTwoDecimals(macroFmesure));
        MaF.add(macroFmesure);
        
  }
  
  public String Lemmatiser(String tweet, LemmatiseurHandler lm)
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
  
  public void Save(Instances data, String file) throws IOException{
      ArffSaver saver = new ArffSaver();
      saver.setInstances(data);
      saver.setFile(new File(file));
      saver.writeBatch();
  }
  
  public String roundTwoDecimals(double d) {
    double r=d*100;
    DecimalFormat twoDForm = new DecimalFormat("#.#");
    return twoDForm.format(r).replaceAll(",", ".");
  }
  
}