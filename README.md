# Source Code Update 11/27/17 (Brennan Lefler)
We have 3 classes implemented. The main thread, the sample data structure, and the project workflow. The workflow is where most of the implementation will go. I have added comments and function prototypes for the group to guide us along, but feel free to add or remove functions as needed. 

I also went ahead and implemented a function to normalize the data, which successfully converts all of the data to values between 0..1. IMPORTANT: The normalized data we will be using is in an ArrayList named "samplesNorm". Any processing of the data must be done on this list, and not any of the others. The "features" ArrayList holds the attribute names, so if you need to get those header names, call that list. 

Unless absolutely necessary, please do not make any changes to any of the functions that import the data or read the data. Everything works as needed to bring in all of the samples and there corresponding features. I know there will be questions about the code as we move forward, so feel free to ask me about it.

As of right now, the GUI is working with a file chooser to select the data. It will only read .CSV and .txt files, which are the normal file types in this area of study. Feel free to mess around with it and identify any issues you find so we can fix them.

# Code Update 11/30/17 (Brennan Lefler)
I updated assumptions about how data will be loaded, to make it easier on us. The user is required to make sure the class is the final attribute in the samples. The sample list does not read in the class; the class is added to a separate list, that we will compare classification outcomes later on (This gives us the confusion matrix, I think...) There is aother list, which contains the names of the classes. So we can assume their indexes as numerical representations of the class. 

What I mean: (iris data example)
flower1 = 0
flower2 = 1
flower3 = 2

So, we have 3 classes, and when we assign a sample to a class, it will be much easier to assign it to '0', rather than comparing to a string ('flower1'). As usual, let me know if you guys need clarification on anything. Feel free to try implementing some of the functions soon. I will continue working on the Bayes portion.
