/*
 This will be the GUI that allows the user to interact with data.
 - Button to import data
 - Button to reset data
 - Button to begin algorithm
 */
package bayesclassifier;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

public class BayesWorkflow extends JFrame {

    private JButton btnImport;
    private JButton btnStart;
    private JButton btnReset;
    
    private JTextArea txtOutput;
    
    private JScrollPane scrlOutput;
    
    protected JLabel lblFile;
    
    protected JFileChooser load;
    
    //Holds all sample data
    protected ArrayList<BayesSample> samples = new ArrayList<>();
    
    //Holds normalized data
    protected ArrayList<BayesSample> samplesNorm = new ArrayList<>();
    
    //Header of feature names
    protected ArrayList<String> features = new ArrayList<>();
    
    //Store class set from samples to compare accuracy later
    protected ArrayList<String> classes = new ArrayList<>();
    
    //List to store names of classes only. Don't confuse with the above classes list,
    //which holds all classes to compare to later. This only holds the names of the classes
    protected ArrayList<String> className = new ArrayList<>();
    
    //Holds split data for training
    protected ArrayList<BayesSample> trainingSamples = new ArrayList<>();
    protected ArrayList<String> trainingClasses = new ArrayList<>();
    
    //Holds split data for testing
    protected ArrayList<BayesSample> testSamples = new ArrayList<>();
    protected ArrayList<String> testClasses = new ArrayList<>();
    
    
    
    // Used to hold the euclidean distances
    protected ArrayList<Double> distanceArray = new ArrayList<>();
    protected double euclidDist = 0;
    protected double finalDist = 0;
    protected int nearHit = 0;
    protected int nearMiss = 0;
    protected double min = 0;  
    protected int finalIndex = -1;
    
    protected double percentUsed = 0;
    
    protected int numFeatures = 0;
    protected int numSamples = 0;
    protected int numClasses;
    protected int correctClass;
    protected int initialSample;
    protected boolean done = false;
    protected Random newClass = new Random();
    
    //Array for min and max of feature
    protected double[] featureMin;
    protected double[] featureMax;
    
    //Arrays to hold probabilities, and number of occurrences
    protected double[] classProb;
    protected double[] sampleProb;
    protected double[] occurrences;
    protected int[] numberInClass;
    protected double[][] numberSampleInClass;
    
    protected double[] weightsHit;  
    protected double[] weightsMiss; 
    protected double[] weightsTotal; 
    
    DecimalFormat dec = new DecimalFormat("0.000");
    
    BayesWorkflow() {
        super("Relief & Bayes Classifier");
        
        JOptionPane.showMessageDialog(null,"Welcome!\nBefore importing your data file, please be sure to " +
                "verify your data follows these assumptions:"
                + "\n\n- Has an attribute header line"
                + "\n- All instances are comma separated" 
                + "\n- The class is in the LAST attribute column of the data"
                + "\n\nUsing Excel can help you meet these assumptions."
                + "\nAny other format may result in unexpected behavior!");
        buildFrame();
        buildButtons();
        buildDataOutput();

        setVisible(true);
    }
    //Create main window
    private void buildFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 650);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
    }
    //Add buttons to workflow
    private void buildButtons() {
        btnImport = new JButton("Import File");
        btnImport.setBounds(20, 40, 110, 30);
        btnImport.addActionListener(new importFile());

        btnStart = new JButton("Start");
        btnStart.setBounds(20, 80, 110, 30);
        btnStart.addActionListener(new beginAlgorithm());

        btnReset = new JButton("Reset");
        btnReset.setBounds(20, 120, 110, 30);
        btnReset.addActionListener(new startReset());

        add(btnImport);
        add(btnStart);
        add(btnReset);
    }
    //Add text pane to display data output
    private void buildDataOutput() {
        txtOutput = new JTextArea();
        txtOutput.setEditable(false);
        txtOutput.setWrapStyleWord(true);

        scrlOutput = new JScrollPane(txtOutput, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrlOutput.setBounds(150, 10, 520, 590);

        lblFile = new JLabel("File: None Selected");
        lblFile.setBounds(10, 5, 500, 30);
        lblFile.setFont(new Font("Dialog", Font.PLAIN, 16));
        lblFile.setVerticalTextPosition(SwingConstants.CENTER);

        add(scrlOutput);
        add(lblFile);
    }
    //Import file using file chooser, file must be .csv or .txt formats
    private class importFile implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            reset();
            load = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    ".CSV & .txt & .data", "csv", "txt", "data");
            load.setFileFilter(filter);
            int fileOpen = load.showOpenDialog(null);
            if (fileOpen == JFileChooser.APPROVE_OPTION) {
                lblFile.setText("File: " + load.getSelectedFile().getName());
                readData();
                txtOutput.append("----------------------------\nPre-Processing:\n----------------------------");
                txtOutput.append("\nNumber of Samples: " + (samples.size()-1));
                txtOutput.append("\n\nThere were " + className.size() + " classes discovered:");
                for(int i=0; i<className.size(); i++){
                    txtOutput.append("\nClass " + i + ": " + className.get(i));
                }
                txtOutput.append("\n\nThere were " + numFeatures + " features discovered:");
                for(int i=0; i<features.size()-1; i++){
                    txtOutput.append("\nFeature " + i + ": " + features.get(i));
                }
            }
        }
    }
    
    //Reset all data in workspace and free resources
    private class startReset implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            reset();
        }
    }
    //Begin the algorithm. Do Relief alg first, output results via txtOutput.append(" ").
    //Next, run Bayes Classifier. Might be easier to put each step of Bayes and Relief into
    //specialized functions. Then, just call them in order. 
    private class beginAlgorithm implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (load != null && !done) {
                try{
                normalizeData();
                calcRelief();
                splitData();
                classProbabilities();
                sampleProbabilities();
                totalProbabilities();
                
                //Completed all processing
                done = true;
                }
                catch(Exception ex){
                    JOptionPane.showMessageDialog(null,"There was an error while processing data."
                    + "\nDid you verify your data set follows the assumptions?\n" + ex);
                    reset();
                }
            }
        }
    }
    //Reset method for startReset (to make available to other windows)
    //Clear all data for next file set
    protected void reset() {
        lblFile.setText("File: None Selected");
        load = null;
        features.clear();
        samples.clear();
        samplesNorm.clear();
        classes.clear();
        className.clear();
        trainingSamples.clear();
        trainingClasses.clear();
        testSamples.clear();
        testClasses.clear();
        distanceArray.clear();
        classProb = null;
        numSamples = 0;
        numFeatures = 0;
        numClasses = 0;
        euclidDist = 0;
        finalDist = 0;
        done = false;
        txtOutput.setText("");
    }
    //Read in data from file that was imported
    private void readData() {
        try {
            Scanner fileScan = new Scanner(load.getSelectedFile());
            String[] items;
            int index = 0;
            while (fileScan.hasNextLine()) {
                //Take each line, divide into features, add each sample to list
                fileScan.useDelimiter("\n");
                String line = fileScan.nextLine();
                items = line.split(",");
                samples.add(new BayesSample());
                for (int i = 0; i < items.length; i++) {
                    if (index == 0) {
                        features.add(items[i]);
                        numFeatures = items.length - 1;
                    }
                    if (index > 0) {
                        //Mae sure features are numerical. Final is class, can be string/numerical
                        if ((isDouble(items[i]) || isInteger(items[i])) && i<numFeatures) {
                            samples.get(index).addFeatures(Double.parseDouble(items[i]));
                        }
                        if(i == numFeatures){
                            if(!classes.contains(items[i])){
                                //Add names of classes
                                className.add(items[i]);
                            }
                            //Add ALL classifications
                            classes.add(items[i]);
                        }
                    }
                }
                index++;
            }
            numSamples = index - 1;
            fileScan.close();
        } catch (FileNotFoundException ex) {
            JOptionPane.showMessageDialog(null, "There was a problem loading the file.");
        }
    }
    //Check if value is valid double value
    private boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    //Check if value is valid integer value
    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    //Manipulate numerical data within a common numerical range
    protected void normalizeData(){
        featureMin = new double[numFeatures];
        featureMax = new double[numFeatures];
        
        for(int a = 0; a<numFeatures; a++){
            featureMin[a] = Double.MAX_VALUE;
            featureMax[a] = Double.MIN_VALUE;
        }
        
        //Calculate min and max for all features
        for(int i=1; i<numSamples; i++){
            for(int j=0; j<numFeatures; j++){
                if(samples.get(i).getFeatures().get(j) < featureMin[j]){
                    featureMin[j] = samples.get(i).getFeatures().get(j);
                }
                if(samples.get(i).getFeatures().get(j) > featureMax[j]){
                    featureMax[j] = samples.get(i).getFeatures().get(j);
                }
            }
        }
        
        //Normalize data and add to samplesNorm arraylist
        for(int i=0; i<numSamples; i++){
            samplesNorm.add(new BayesSample());
            for(int j=0; j<numFeatures; j++){
                samplesNorm.get(i).addFeatures(Double.parseDouble(dec.format(
                        (samples.get(i+1).getFeatures().get(j)-featureMin[j])/(featureMax[j]-featureMin[j]))));
            }
        }
    }
    
    //Calculate sample distances and store the nearest hit and miss for each round until done
    //use full data set for relief
    //d(p,q) = sqrt(pow(p1-q1, 2) + pow(p2-q2, 2) + pow(pi-qi, 2) + pow(pn-qn, 2))
    protected void calcRelief(){
        weightsHit = new double[numFeatures];
        weightsMiss = new double[numFeatures];
        weightsTotal = new double[numFeatures];
        int count = 0;
        for(int i = 0; i < samplesNorm.size(); i++) {
            for(int j = i + 1; j < samplesNorm.size(); j++) {
                for(int k = 0; k < numFeatures; k++) {
                    // Sample One SHOULD be each feature from the first sample as it loops through the features k
                    // Sample Two SHOULD be each feature from the second sample as it loops through the features k
                    double sampleOne = samplesNorm.get(i).getFeatures().get(k);
                    double sampleTwo = samplesNorm.get(j).getFeatures().get(k);
                    // This is what would be under the square root: pow(p1-q1, 2) + pow(p2-q2, 2) + pow(pi-qi, 2) + pow(pn-qn, 2)
                    euclidDist += Math.pow(sampleOne - sampleTwo, 2);
                }
                // final evaluation of the function and add it to an array, then reset euclid dist for next sample comarison
                finalDist = Math.sqrt(euclidDist);
                distanceArray.add(finalDist);
                euclidDist = 0;
            }
            // find Min value in distancearray
            //Calc nearHit or nearMiss
            nearHit = 0;
            nearMiss = 0;
            min = Double.MAX_VALUE;
            for (int l = 0; l < distanceArray.size(); l++) {
                for (int m = 0; m < className.size(); m++) {
                    if (classes.get(l+count).equals(classes.get(m))) {
                        if (distanceArray.get(l) < min){
                            nearHit = l+count;
                            min = distanceArray.get(l);
                        }
                    }
                    else{
                        if (distanceArray.get(l) < min){
                            nearMiss = l+count;
                            min = distanceArray.get(l);
                        }
                    }
                }
            }
            distanceArray.clear();
            calcWeights(samplesNorm.get(i),samplesNorm.get(nearHit),samplesNorm.get(nearMiss));
            count++;
        }
        reliefOutput();
    }
    
    //Calculate weights for each samples features. At the end, output which feature is least weighted.
    protected void calcWeights(BayesSample curr, BayesSample hit, BayesSample miss){
        for(int p=0; p <weightsHit.length; p++){
            weightsHit[p] = Math.pow(curr.getFeatures().get(p) - hit.getFeatures().get(p), 2);
            weightsMiss[p] = Math.pow(curr.getFeatures().get(p) - miss.getFeatures().get(p), 2);
            weightsTotal[p] = weightsHit[p] + weightsMiss[p];
        }
        double weightMin = weightsTotal[0];
        for(int q =0; q< weightsTotal.length; q++){
            if(weightsTotal[q] < weightMin){
                finalIndex = q;
                weightMin = weightsTotal[q];
            }
        }
    }
    
    //Only display relief output at end of function
    protected void reliefOutput(){
        txtOutput.append("\n\n----------------------------\nRelief Algorithm (Feature Reduction)\n----------------------------");
        if(finalIndex != -1){
        txtOutput.append("\nThe Relief algorithm suggests you can disregard:");
        txtOutput.append("\nFeature " + finalIndex + ".\n");
        }
        else{
            txtOutput.append("\nThe Relief algorithm finds features to have the same weights.\n");
        }
    }
    
    //Split data for training and testing
    protected void splitData(){
        Random r = new Random();
        int i;
        percentUsed = Math.round(samplesNorm.size() * .7);   //trainingSamples list
        //Assign to training set
        while (trainingSamples.size() < percentUsed) {
            i = r.nextInt(samplesNorm.size());
                if (!trainingSamples.contains(samplesNorm.get(i))) {
                    trainingSamples.add(samplesNorm.get(i));
                    trainingClasses.add(classes.get(i));
                }
        }
        //If not in training set, put in test set
        for(int j=0; j<samplesNorm.size(); j++){
            if(!trainingSamples.contains(samplesNorm.get(j))){
                testSamples.add(samplesNorm.get(j));
                testClasses.add(classes.get(j));
            }
        }
        txtOutput.append("----------------------------\nNaive Bayes Classifier\n----------------------------");
        txtOutput.append("\nData Split:");
        txtOutput.append("\nTraining data contains " + trainingSamples.size() + " instances.");
        txtOutput.append("\nTesting data contains " + testSamples.size() + " instances.");
    }

    //Calculate probabilities of all classes (number of C, over the total number of samples)
    protected void classProbabilities(){
        classProb = new double[className.size()];
        numberInClass = new int[className.size()];
        
        for(int i=0; i<percentUsed; i++){
            for(int j=0; j<className.size(); j++){
                if (trainingClasses.get(i).equals(className.get(j))){
                    classProb[j]++;
                    numberInClass[j]++;
                }
            }
        }
        txtOutput.append("\n\n(70% of data used as training, 30% as testing.)");
        txtOutput.append("\nProbability of Class:");
        for(int i=0; i<className.size(); i++){
            classProb[i] /= percentUsed;
            classProb[i] = Double.parseDouble(dec.format(classProb[i]));
            txtOutput.append("\nClass " + i + ": " + classProb[i]);
        }
    }
    
    //Get probability of sample given class
    protected void sampleProbabilities(){
        //This mess will hopefully calculate probability of test samples, against the training data
        sampleProb = new double[testSamples.size()];
        numberSampleInClass = new double[className.size()][testSamples.size()];
        for (int w = 0; w < className.size(); w++) {
            for (int x = 0; x < testSamples.size(); x++) {
                for (int y = 0; y < trainingSamples.size(); y++) {
                    for (int z = 0; z < numFeatures; z++) {
                        if (testSamples.get(x).getFeatures().get(z).equals(trainingSamples.get(y).getFeatures().get(z))
                                && trainingClasses.get(y).equals(classes.get(w))) {
                            numberSampleInClass[w][x]++;
                        }
                    }
                }
            }
        }
        double[][] probs = new double[className.size()][testSamples.size()];
        for(int i=0;i<className.size();i++){
            for(int j=0; j<testSamples.size()-1; j++){
                probs[i][j] = (numberSampleInClass[i][j] * numberSampleInClass[i][j+1]) /
                        (Math.pow(numberInClass[i], numFeatures));
                if(probs[i][j] == 0){
                    probs[i][j] = .001;
                }
                //Because size is limited, allow the full number of probabilities per class to persist.
                if(j == testSamples.size()-1){
                    sampleProb[j] = probs[i][j+1];
                }
                else{
                    sampleProb[j] = probs[i][j];
                }
            }
        }
    }
    //Calculate probability of sample given class * class probability, then assign sample to class
    protected void totalProbabilities() {
        int[] counter = new int[className.size()];
        double maxClass = 0;
        maxClass = Double.MIN_VALUE;
        for(int i=0; i<testSamples.size(); i++){
            for(int j=0; j<className.size(); j++){
                correctClass = newClass.nextInt(className.size());
                if((sampleProb[i] * classProb[j]) > maxClass){
                    testSamples.get(i).assignToClass(correctClass);
                    maxClass = (sampleProb[i] * classProb[j]);
                }
            }
            counter[correctClass]++;
            maxClass = Double.MIN_VALUE;
        }
        //Display how many samples were assigned to each of the classes
        txtOutput.append("\n");
        for(int i=0; i<counter.length; i++){
            txtOutput.append("\n" + counter[i] + " samples were assigned to class " + i + ".");
        }
    }
}