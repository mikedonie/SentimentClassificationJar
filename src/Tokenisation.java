import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;
import weka.core.Instances;
//import weka.core.stemmers.LovinsStemmer;
import weka.core.tokenizers.NGramTokenizer;
import weka.filters.unsupervised.attribute.StringToWordVector;

/**
 *
 * @author amin.abdaoui
 */
public class Tokenisation {
    public static StringToWordVector WordNgrams(String propPath) throws Exception{
        Properties prop = new Properties();
	InputStream input = new FileInputStream(propPath);
        prop.load(input);
        final StringToWordVector filter = new StringToWordVector();
        filter.setAttributeIndices("first-last");
        filter.setOutputWordCounts(false);
        filter.setTFTransform(false);
        filter.setIDFTransform(false);
        // ATTENTION, on a pas MotsVides.txt !!!
        //String filePath = Thread.currentThread().getContextClassLoader().getResource("MotsVides.txt").getPath();
        String filePath = "ressources/MotsVides.txt";
        if (prop.getProperty("Preprocessings.removeStopWords").equalsIgnoreCase("yes")) filter.setStopwords(new File(filePath));
        filter.setWordsToKeep(10000);
        filter.setMinTermFreq(1);
        NGramTokenizer tok = new NGramTokenizer();
        tok.setDelimiters(" \n 	.,;:'\"()?!-_/<>‘’“”…«»•&#{[|`^@]}$*%");
        tok.setNGramMinSize(Integer.parseInt(prop.getProperty("Ngrams.min")));
        tok.setNGramMaxSize(Integer.parseInt(prop.getProperty("Ngrams.max")));
        filter.setTokenizer(tok);
        
        return filter;
    }
    
}
