import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import weka.core.DenseInstance;
import weka.core.Instance;

public class Foo
{
    public static void main(String[] args) throws Exception
    {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new java.io.FileInputStream("t.txt"), "utf8"));
        String line;
        //System.out.println("J'étais à la bourre et jéééé");
        while ((line=reader.readLine())!=null){
            System.out.println("M: "+line);
        }
    }
}