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
    
    //Only has number of classes
    protected ArrayList<BayesSample> classes = new ArrayList<>();
    
    int numFeatures = 0;
    int numSamples = 0;
    int numClasses;
    int initialSample;
    boolean done = false;
    
    //Array for min and max of feature
    double[] featureMin;
    double[] featureMax;
    
    DecimalFormat dec = new DecimalFormat("0.000");
    
    BayesWorkflow() {
        super("Relief & Bayes Classifier");
        
        JOptionPane.showMessageDialog(null,"Welcome!\nBefore importing your data file, please be sure to " +
                "verify your data has an\nattribute header line, and that all instances are " + 
                "seperated by commas.\n\nAny other format may result in unexpected behavior!");
        buildFrame();
        buildButtons();
        buildDataOutput();

        setVisible(true);
    }
    //Create main window
    private void buildFrame() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(700, 500);
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
        scrlOutput.setBounds(150, 40, 520, 410);

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
    private class beginAlgorithm implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            if(load != null && !done){
                normalizeData();
                
                //Completed all processing
                done = true;
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
        numSamples = 0;
        numFeatures = 0;
        numClasses = 0;
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
                    if (index == 0){
                        features.add(items[i]);
                        numFeatures = items.length - 1;
                    }
                    if (index > 0) {
                        //Ignore string data for class info. Make sure file format matches!
                        if (isDouble(items[i]) || isInteger(items[i])) {
                            samples.get(index).addFeatures(Double.parseDouble(items[i]));
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
    
    /*
    Group project collaboration for RELIEF:
    - Function to normalize data. Must be done FIRST. Norm = (x-min)/(max-min)
    - Function to randomly select initial sample
    - Function to calculate distances between samples, store nearest sample in same class (near hit), store
            nearest sample from different class (near miss)
    - Function to calculate weights of each sample. Weight = W - diff(x,near hit)^2 + diff(x,near miss)^2
    - Output which feature has the least weight
    */
    
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
    
    //For Relief alg, select a random sample to begin
    protected void chooseInitialSample(){
        Random r = new Random();
        initialSample = r.nextInt(numSamples); //sample index
    }
    
    //Calculate sample distances and store the nearest hit and miss for each round until done
    protected void calcDistances(){
        
    }
    
    //Calculate weights for each samples features. At the end, output which feature is least weighted.
    protected void calcWeights(){
        
    }
    
    /*
    Group project collaboration for BAYES:
    - Function to calculate probabilities of all recognized classes
    - Function to calculate probability of each sample and each class
    - Function to multiply P(C) * P(x|C), and assign sample to class with highest probability
    */
    
    //Calculate probabilities of all classes (number of C, over the total number of samples)
    protected void classProbabilities(){
        
    }
    
    //Get probability of sample given class
    protected void sampleProbabilities(){
        
    }
    
    //Calculate probability of sample given class * class probability, then assign sample to class
    protected void totalProbabilities(){
        
    }
}
