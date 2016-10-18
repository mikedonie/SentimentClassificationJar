import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.MultilayerPerceptron;
import weka.classifiers.functions.SMO;
import weka.classifiers.lazy.IBk;
import weka.classifiers.meta.AdaBoostM1;
import weka.classifiers.meta.FilteredClassifier;
import weka.classifiers.trees.J48;
import weka.classifiers.trees.RandomForest;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.supervised.attribute.AttributeSelection;
import weka.filters.unsupervised.attribute.StringToWordVector;
import weka.filters.supervised.instance.Resample;
import weka.filters.unsupervised.attribute.Remove;


public class LearnSeveralModels {
    
     public static void main(String[] args) throws UnsupportedEncodingException, FileNotFoundException, IOException, Exception{
        BufferedReader r = new BufferedReader(new InputStreamReader(new FileInputStream("Train//T2.2.arff"), "UTF-8"));
        //BufferedReader rt = new BufferedReader(new InputStreamReader(new FileInputStream("Test//Test1.arff"), "UTF-8"));

        Instances train = new Instances(r);
        //Instances test = new Instances(rt);
        
        /**************************************************************************************
        *   FILTRE
        ****************************************************************************************/ 
        train.setClassIndex(train.numAttributes() - 1);
        //test.setClassIndex(test.numAttributes() - 1);
        
        System.out.println("Construction objet ConstructionARFF..");
        ConstructionARFF_Old obj = new ConstructionARFF_Old();
        System.out.println("Construction train ARFF..");
        train = obj.ConstructionInstances(train,"emotion");
        System.out.println("Construction test ARFF..");
        //test = obj.ConstructionInstances(test);
        System.out.println("  Train.numAttributes = " + train.numAttributes());
        //System.out.println("  Test.numAttributes = " + test.numAttributes());

        String propPath = "";
        StringToWordVector filter = Tokenisation.WordNgrams(propPath);
        filter.setInputFormat(train);
        train = Filter.useFilter(train, filter);
        //test = Filter.useFilter(test, filter);
        train.setClass(train.attribute("_class"));
        //test.setClass(train.attribute("_class"));
        System.out.println("  numAttributes Après StringToWordVector = " + train.numAttributes());
        System.out.println("  numInstances = " + train.numInstances());
        
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
        
        System.out.println("  numInstances = " + train.numInstances());
            
        // Selection d'attributs
        AttributeSelection as = SelectionAttributs.InfoGainAttributeEval(train);
        as.setInputFormat(train);
        train = Filter.useFilter(train, as);
        //test =  Filter.useFilter(test, as);
        
        
        /**************************************************************************************
        *   SVM
        ****************************************************************************************/ 
        //SVM Classifier
        SMO classifierSVM = new SMO();
        classifierSVM.setC(0.2D);
        // Contstruire le classifieur
        classifierSVM.buildClassifier(train);
        // Serialisation
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_SMO.model", classifierSVM);
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_STW.model", filter);
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_IG.model", as);
        
        /**************************************************************************************
        *   RANDOM FOREST
        ****************************************************************************************/ 
    /*    int numFolds = 10;
        //Instances trainData = new Instances(r);
        train.setClassIndex(train.numAttributes() - 1);
        RandomForest classifierRF = new RandomForest();
        classifierRF.setNumTrees(100);
         
        classifierRF.buildClassifier(train);
        Evaluation evaluation = new Evaluation(train);
        evaluation.crossValidateModel(classifierRF, train, numFolds, new Random(1));
        
        System.out.println(evaluation.toSummaryString("\nResults\n======\n", true));
        System.out.println(evaluation.toClassDetailsString());
        System.out.println("Results For Class -1- ");
        System.out.println("Precision=  " + evaluation.precision(0));
        System.out.println("Recall=  " + evaluation.recall(0));
        System.out.println("F-measure=  " + evaluation.fMeasure(0));
        System.out.println("Results For Class -2- ");
        System.out.println("Precision=  " + evaluation.precision(1));
        System.out.println("Recall=  " + evaluation.recall(1));
        System.out.println("F-measure=  " + evaluation.fMeasure(1));
        
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_RF.model", classifierRF);
        System.out.println("RF terminée");
        
        /**************************************************************************************
        *   J48
        ****************************************************************************************/ 
         // filter
    /*    Remove rm = new Remove();
        rm.setAttributeIndices("1");  // remove 1st attribute
        // classifier
        J48 classifierJ48 = new J48();
        classifierJ48.setUnpruned(true);        // using an unpruned J48
        // meta-classifier
        FilteredClassifier fc = new FilteredClassifier();
        fc.setFilter(rm);
        fc.setClassifier(classifierJ48);
        //train and make predictions
        fc.buildClassifier(train);
        
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_J48.model", classifierJ48);
        System.out.println("J48 terminée");
        
        /**************************************************************************************
        *   KNN
        ****************************************************************************************/ 
    /*    IBk classifierIBk = new IBk();		
	classifierIBk.buildClassifier(train);
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_KNN.model", classifierIBk);
        System.out.println("KNN terminée");
        /**************************************************************************************
        *   Neural Network Classifier
        ****************************************************************************************/ 
    /*    MultilayerPerceptron classifierMLP = new MultilayerPerceptron();
        classifierMLP.setLearningRate(0.1);
        classifierMLP.setMomentum(0.2);
        classifierMLP.setTrainingTime(2000);
        classifierMLP.setHiddenLayers("3");
        classifierMLP.buildClassifier(train);
        //mlp.setOptions(Utils.splitOptions(“-L 0.1 -M 0.2 -N 2000 -V 0 -S 0 -E 20 -H 3”));  //https://floatcode.wordpress.com/2015/06/22/perceptron-neural-network-in-java-using-weka-library/
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_MNN.model", classifierMLP);
        System.out.println("Neural Network terminée");
        
        //http://www.programcreek.com/java-api-examples/index.php?api=weka.classifiers.meta.AdaBoostM1
        /**************************************************************************************
        *   AdaBoostM1
        ****************************************************************************************/ 
    /*    AdaBoostM1 classifierAda = new AdaBoostM1();
        classifierAda.setNumIterations(20);
        classifierAda.setUseResampling(true);
        classifierAda.buildClassifier(train);
        weka.core.SerializationHelper.write("models/DEFT15_T2.2_Ada.model", classifierAda);
        System.out.println("AdaBoost terminée");
        
        /*for (int i = 0; i < test.numInstances(); i++) {
          double pred = fc.classifyInstance(test.instance(i));
          System.out.print("ID: " + test.instance(i).value(0));
          System.out.print(", actual: " + test.classAttribute().value((int) test.instance(i).classValue()));
          System.out.println(", predicted: " + test.classAttribute().value((int) pred));
        }*/
        
        
        r.close();

    }
    
}
