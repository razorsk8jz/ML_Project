/*
Each sample is an object with dynamic size for # of features
 */
package bayesclassifier;

import java.util.ArrayList;

public class BayesSample {
    private ArrayList<Double> features = new ArrayList<>();
    
    int classType = -1;
    
    BayesSample(){
        
    }
    
    //After creating a new sample instance, add all of its features
    protected void addFeatures(double f){
        features.add(f);
    }
    
    //Return features list
    protected ArrayList<Double> getFeatures(){
        return features;
    }
    
    //Clear features?
    protected void clearFeatures(){
        features.clear();
    }
    
    //Assign sample to a cluster
    protected void assignToClass(int n){
        classType = n;
    }
    
    //Return which cluster sample belongs to
    protected int getClassType(){
        return classType;
    }
}
